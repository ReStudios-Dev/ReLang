package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.IdentifierExpression;
import org.restudios.relang.parser.ast.types.values.*;
import org.restudios.relang.parser.ast.types.values.values.EnumItemValue;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class MemberStatement extends Statement  {
    public final Expression left;
    public final Expression right;

    public MemberStatement(Token token, Expression left, Expression right) {
        super(token);
        this.left = left;
        this.right = right;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        predictType(context);
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        Type l = left.predictType(c);
        l.initClassOrType(c);
        if(l.primitive == Primitives.NULL || l.primitive == Primitives.VOID){
            throw new AnalyzerError("null value", left.token);
        }
        if(!l.isCustomType()){
            throw new AnalyzerError("Cannot access non class", left.token);
        }
        if(right instanceof MethodCallStatement) {
            MethodCallStatement mcs = (MethodCallStatement) right;
            List<Expression> arguments = mcs.arguments;
            List<Type> types = new ArrayList<>();
            for (Expression argument : arguments) {
                types.add(argument.predictType(c));
            }
            Type t = ClassInstance.findMethodFromNameAndArguments(mcs.method.token.string, types, l.clazz.getAllMethodsOriginal(true, false, true), l, c);
            if(t != null) return t;
            throw new AnalyzerError("Method "+mcs.method.token.string+" not found", mcs.method.token);
        }
        IdentifierExpression variable = (IdentifierExpression) right;
        for (UnInitializedVariable clazzVariable : l.clazz.getVariables()) {
            boolean staticVariable = clazzVariable.getVisibilities().contains(Visibility.STATIC);
            boolean staticCall = !l.isInstance();
            if(staticVariable != staticCall){
                continue;
            }
            if(clazzVariable.getName().equals(variable.value)){
                return clazzVariable.getType();
            }
        }
        for (RLClass subClass : l.clazz.getSubClasses()) {
            if(variable.value.equals(subClass.getName())){
                return subClass.type();
            }
        }
        throw new AnalyzerError("Variable "+variable.value+" not found", variable.token);
    }

    @Override
    public Value eval(Context context) {
        Value l = left.eval(context).initContext(context).finalExpression();
        if(l instanceof NullValue){
            throw new RLException("null value", Type.nullPointer(context), context);
        }
        if(!(l instanceof ClassInstance) && !(l instanceof RLClass) && !(l instanceof EnumItemValue)){
            throw new RLException("Cannot access non class", Type.internal(context), context);
        }
        if(right instanceof MethodCallStatement){
            MethodCallStatement mcs = (MethodCallStatement) right;
            if(l instanceof ClassInstance){
                ClassInstance ci = (ClassInstance) l;
                return mcs.fromClassInstance(right.token, context, ci);
            }
            if(l instanceof EnumItemValue){
                EnumItemValue etv = (EnumItemValue) l;
                return mcs.fromClassInstance(right.token, context, ((RLEnumClass) etv.getRLClass()).instantiateEnumeration(context, etv));
            }

            RLClass clazz = (RLClass) l;
            clazz.initializeStaticContext();
            return mcs.fromStatic(clazz, context);
        }
        IdentifierExpression variable = (IdentifierExpression) right;
        if(l instanceof ClassInstance){
            return variable.fromClassInstance(((ClassInstance) l));
        }
        if(!(l instanceof RLClass)){
            throw new RLException("Cannot operate", Type.internal(context), context);
        }
        RLClass clazz = (RLClass) l;
        clazz.initializeStaticContext();
        if (variable.value.equals("class")) {
            return clazz.getReflectionClass(context);
        }
        return right.eval(clazz.getStaticContext());
    }


    @Override
    public void execute(Context context) {
        eval(context);
    }
}
