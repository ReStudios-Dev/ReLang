package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.nodes.extra.LoadedAnnotation;
import org.restudios.relang.parser.ast.types.nodes.statements.BlockStatement;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.exceptions.RLException;

import java.util.ArrayList;
import java.util.List;

public class ConstructorMethod extends FunctionMethod {
    List<Expression> callSuper;
    @SuppressWarnings("unused")
    public ConstructorMethod(FunctionMethod original) {
        super(original);
    }

    public ConstructorMethod(List<CustomTypeValue> customTypes, List<FunctionArgument> arguments, Type returnType,
                             String name, List<Visibility> visibility, BlockStatement code,
                             boolean isAbstract, List<Expression> callSuper, boolean isNative,
                             List<LoadedAnnotation> annotations) {
        super(customTypes, arguments, returnType, name, visibility, code, isAbstract, isNative, annotations);
        this.callSuper = callSuper;
    }
    @Override
    public Value execute(Context context, Context callContext,  Value... values) {
        if(getArguments().size() != values.length){
            throw new RLException("Method receiving "+getArguments().size()+" arguments, but got "+values.length, Type.internal(context), context);
        }
        Variable[] variables = new Variable[values.length];
        for (int i = 0; i < getArguments().size(); i++) {
            FunctionArgument fa = getArguments().get(i);
            Value val = CastExpression.cast(fa.type, values[i], context);
            Variable variable = new Variable(fa.type, fa.name, val, new ArrayList<>());
            variables[i] = variable;

        }
        Context con = new Context(context);
        for (Variable variable : variables) {
            con.putVariable(variable);
        }
        ClassInstance ci = con.thisClass();
        if(callSuper != null) {
            if (ci.getRLClass().getSuperClass() == null) {
                throw new RLException("Cannot call super method", Type.internal(context), context);
            }
            Value[] vals = new Value[callSuper.size()];
            for (int i = 0; i < callSuper.size(); i++) {
                vals[i] = callSuper.get(i).eval(con).finalExpression();
            }

            RLClass clazz = ci.getRLClass().getSuperClass();

            clazz.callConstructor(context, ci.getContext(), vals);
        }
        con.setInConstructor(true);
        if(isNative && code == null) return ci;
        code.execute(con);
        return ci;
    }
}
