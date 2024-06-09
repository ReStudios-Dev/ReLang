package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;
import org.restudios.relang.parser.utils.RLStackTrace;
import org.restudios.relang.parser.utils.RLStackTraceElement;

import java.util.ArrayList;

public class MethodCallStatement extends Statement {
    public final String name;
    public final ArrayList<Expression> arguments;

    public MethodCallStatement(Token token, String name, ArrayList<Expression> arguments) {
        super(token);
        this.name = name;
        this.arguments = arguments;
    }

    public Value fromClassInstance(Token token, Context context, ClassInstance ci){
        RLStackTraceElement elem = context.putTrace(this, token);
        Value[] values = new Value[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            values[i] = arguments.get(i).eval(context).initContext(context).finalExpression();
        }

        FunctionMethod fm = ci.findMethodFromNameAndArguments(context, name, values);
        ClassInstance ca = context.thisClass();
        if(fm == null) {
            throw new RLException("Method " + name + " not found in class "+ci.getRLClass().getName(), Type.internal(context), context);
        }
        if(fm.visibility.contains(Visibility.PRIVATE)){
            if(ca == null){
                throw new RLException("Cannot access private method "+name+" from outside class "+ci.getRLClass().getName(), Type.internal(context), context);
            }
            if(!ca.getRLClass().equals(ci.getRLClass())){
                throw new RLException("Cannot access private method "+name+" from outside class "+ci.getRLClass().getName(), Type.internal(context), context);
            }
        }
        RLStackTrace bef = ci.getContext().getTrace();
        ci.getContext().setTrace(context.getTrace());
        Value v =  fm.runMethod(ci.getContext(), context, values);
        ci.getContext().setTrace(bef);
        context.removeTrace(elem);
        return v;
    }
    public Value fromStatement(Token token, Context context){
        RLStackTraceElement elem = context.putTrace(this, token);
        Value[] values = new Value[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            values[i] = arguments.get(i).eval(context);
        }
        for (FunctionMethod method : context.getMethods(name)) {
            if(method.canBeExecuted(values, context)) {
                Value v =  method.runMethod(context, context, values);
                context.removeTrace(elem);
                return v;
            }
        }
        throw new RLException("Method " + name + " not found", Type.internal(context), context);

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
        FunctionMethod fm = clazz.findStaticMethodFromNameAndArguments(name, values, context);
        if(fm == null){
            throw new RLException("Method " + name + " not found", Type.internal(context), context);
        }
        ClassInstance ca = context.thisClass();
        if(fm.visibility.contains(Visibility.PRIVATE)){
            if(ca == null){
                throw new RLException("Cannot access private method "+name+" from outside class "+clazz.getName(), Type.internal(context), context);
            }
            if(!ca.getRLClass().equals(clazz)){
                throw new RLException("Cannot access private method "+name+" from outside class "+clazz.getName(), Type.internal(context), context);
            }
        }
        return fm.runMethod(context, context, values);
    }
}
