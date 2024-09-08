package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.values.*;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLRunnable;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;
import org.restudios.relang.parser.utils.RLStackTrace;
import org.restudios.relang.parser.utils.RLStackTraceElement;

import java.util.ArrayList;
import java.util.List;

public class MethodCallStatement extends Statement {
    public final Expression method;
    public final ArrayList<Expression> arguments;

    public MethodCallStatement(Token token, Expression method, ArrayList<Expression> arguments) {
        super(token);
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        List<Type> types  = new ArrayList<>();
        for (Expression argument : arguments) {
            types.add(argument.predictType(c));
        }

        Type t = c.getMethod(method.token.string, types);
        if(t == null){
            if(c.getHandlingClass() != null){
                t = ClassInstance.findMethodFromNameAndArguments(method.token.string, types, c.getHandlingClass().getAllMethods(true, false, true), null, c);

                if(t == null){
                    t = ClassInstance.findMethodFromNameAndArguments(method.token.string, types, c.getHandlingClass().getStaticMethods(), null, c);
                }
            }
        }
        if(t == null) throw new AnalyzerError("Method "+method.token.string+" not found", method.token);
        t.setInstance(true);
        return t;
    }

    @Override
    public void analyze(AnalyzerContext context) {

    }

    public Value fromClassInstance(Token token, Context context, ClassInstance ci){
        RLStackTraceElement elem = context.putTrace(this, token);
        Value[] values = new Value[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            values[i] = arguments.get(i).eval(context).initContext(context).finalExpression();
        }

        FunctionMethod fm = ci.findMethodFromNameAndArguments(context, method.token.string, values);
        ClassInstance ca = context.thisClass();
        if(fm == null) {
            throw new RLException("Method " + method + " not found in class "+ci.getRLClass().getName(), Type.internal(context), context);
        }
        if(fm.visibility.contains(Visibility.PRIVATE)){
            if(ca == null){
                throw new RLException("Cannot access private method "+ method +" from outside class "+ci.getRLClass().getName(), Type.internal(context), context);
            }
            if(!ca.getRLClass().equals(ci.getRLClass())){
                throw new RLException("Cannot access private method "+ method +" from outside class "+ci.getRLClass().getName(), Type.internal(context), context);
            }
        }
        RLClass beff = context.setStaticCall(null);
        RLStackTrace bef = ci.getContext().getTrace();
        ci.getContext().setTrace(context.getTrace());
        Value v =  fm.runMethod(ci.getContext(), context, values);
        ci.getContext().setTrace(bef);
        context.removeTrace(elem);
        context.setStaticCall(beff);
        return v;
    }
    public Value fromStatement(Token token, Context context){
        RLStackTraceElement elem = context.putTrace(this, token);
        Value[] values = new Value[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            values[i] = arguments.get(i).eval(context);
        }
        Value va = null;
        try {
            va = method.eval(context).finalExpression();
        }catch (RLException e){
            if(!e.getMessage().startsWith("Could not find")){
                throw e;
            }
        }
        if(va != null){
            if(va instanceof RLRunnable){

                RLClass bef = context.setStaticCall(null);
                Value v = ((RLRunnable) va).getFunction().execute(((RLRunnable) va).getContext(), context, values);
                context.setStaticCall(bef);
                return v;

            }
        }
        for (FunctionMethod method : context.getMethods(method.token.string)) {

            if(method.canBeExecuted(values, context)) {
                RLClass bef = context.setStaticCall(null);
                Value v =  method.runMethod(context, context, values);
                context.removeTrace(elem);
                context.setStaticCall(bef);
                return v;
            }
        }
        RLClass ci = context.thisClass() != null ? context.thisClass().getRLClass() : context.getStaticCall();

        if(ci != null){
            for (FunctionMethod staticMethod : ci.getStaticMethods()) {
                if(staticMethod.canBeExecuted(values, context)){
                    RLClass bef = context.setStaticCall(null);
                    Value v =  staticMethod.runMethod(ci.getStaticContext(), context, values);
                    context.removeTrace(elem);
                    context.setStaticCall(bef);
                    return v;
                }
            }
        }
        throw new RLException("Method " + method.token.string + " not found", Type.internal(context), context);

    }

    @Override
    public Value eval(Context context) {
        return fromStatement(token, context);
    }

    @Override
    public void execute(Context context) {
        eval(context);
    }

    public Value fromStatic(RLClass clazz, Context context) {
        Value[] values = new Value[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            values[i] = arguments.get(i).eval(context);
        }
        FunctionMethod fm = clazz.findStaticMethodFromNameAndArguments(method.token.string, values, context);
        if(fm == null){
            throw new RLException("Method " + method.token.string + " not found", Type.internal(context), context);
        }
        ClassInstance ca = context.thisClass();
        if(fm.visibility.contains(Visibility.PRIVATE)){
            if(ca == null){
                throw new RLException("Cannot access private method "+ method.token.string +" from outside class "+clazz.getName(), Type.internal(context), context);
            }
            if(!ca.getRLClass().equals(clazz)){
                throw new RLException("Cannot access private method "+ method.token.string +" from outside class "+clazz.getName(), Type.internal(context), context);
            }
        }
        RLClass bef = context.setStaticCall(clazz);
        Value v = fm.runMethod(context, context, values);
        context.setStaticCall(bef);
        return v;
    }
}
