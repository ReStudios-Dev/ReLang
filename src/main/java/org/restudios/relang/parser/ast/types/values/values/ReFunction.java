package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLRunnable;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;

import java.util.ArrayList;
import java.util.List;

public abstract class ReFunction {
    private List<FunctionArgument> arguments;
    private Type returnType;
    private List<CustomTypeValue> typeValues;



    public ReFunction(List<FunctionArgument> arguments, Type returnType, List<CustomTypeValue> typeValues) {
        this.arguments = arguments;
        this.returnType = returnType;
        this.typeValues = typeValues;
    }

    public static ReFunction emptyVoidFunction() {
        return new ReFunction(new ArrayList<>(), Type.primitive(Primitives.VOID), new ArrayList<>()) {
            @Override
            public Value handle(Context context, Context callContext) {
                return Value.voidValue();
            }
        };
    }

    public List<FunctionArgument> getArguments() {
        return arguments;
    }

    public ReFunction setArguments(List<FunctionArgument> arguments) {
        this.arguments = arguments;
        return this;
    }

    public Type getReturnType() {
        return returnType;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ReFunction setReturnType(Type returnType) {
        this.returnType = returnType;
        return this;
    }
   @SuppressWarnings("UnusedReturnValue")
    public Value runMethod(Context createdContext, Context callContext, Value... arguments){
        return execute(createdContext, callContext, arguments);
    }
    public Value execute(Context context, Context callContext, Value... values){
        if(arguments.size() != values.length){
            throw new RLException("Method receiving "+arguments.size()+" arguments, but got "+values.length, Type.internal(context), context);
        }
        Variable[] variables = new Variable[values.length];
        for (int i = 0; i < arguments.size(); i++) {
            FunctionArgument fa = arguments.get(i);
            Value val = CastExpression.cast(fa.type, values[i], context);

            Variable variable = new Variable(fa.type, fa.name, val, new ArrayList<>());
            variables[i] = variable;

        }
        Context con = new Context(context);
        for (Variable variable : variables) {
            con.putVariable(variable);
        }
        Value returned = handle(con, callContext).finalExpression();
        returned.type().init(context);
        returnType.init(context);
        returnType.initClassOrType(context);
        if(returned instanceof RLRunnable){
            RLRunnable r = (RLRunnable) returned;
            if(returnType.like(Type.clazz(DynamicSLLClass.RUNNABLE, context))){
                r.setReturn(returnType.firstTypeOrVoid());
            }
        }
        if (!returned.type().canBe(returnType)){
            throw new RLException("Returned incorrect type: " + returned.type().displayName(), Type.internal(context), callContext);

        }
        return returned;
    }
    public abstract Value handle(Context context, Context callContext);

    public List<CustomTypeValue> getCustomTypes() {
        return this.typeValues;
    }
    public void setCustomTypes(List<CustomTypeValue> value){
        this.typeValues = value;
    }
}
