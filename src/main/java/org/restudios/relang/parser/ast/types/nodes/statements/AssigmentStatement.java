package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.BinaryExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLRunnable;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

import java.util.Objects;

public class AssigmentStatement extends Statement {
    public final Expression key;
    public final String operator;
    public final Expression value;

    public AssigmentStatement(Token token, Expression key, String operator, Expression value) {
        super(token);
        this.key = key;
        this.operator = operator;
        this.value = value;
    }


    @Override
    public Value eval(Context context) {
        Value variableToAssign = key.eval(context);
        if(!(variableToAssign instanceof Variable)){
            throw new RLException("Cannot assign non variable", Type.internal(context), context);
        }
        Variable variable = (Variable) variableToAssign;
        Value valueToAssign = value.eval(context);
        if(variable.getType().isRunnable()){
            if(valueToAssign.type().isRunnable()){
                ((RLRunnable) valueToAssign).setReturn(variable.getType().firstTypeOrVoid());
            }
        }
        if(operator.equals("??=")){
            if(variableToAssign.value() instanceof NullValue){
                variable.setValue(CastExpression.cast(variable.getType(), valueToAssign, context), context);
            }
            return variable.absoluteValue();
        }
        if (Objects.equals(operator, "=")){
            Value cv = variable.getValue().finalExpression();
            if(cv instanceof NullValue && context.isInConstructor()){
                variable.setValueForce(CastExpression.cast(variable.getType(), valueToAssign, context));
            }else{
                variable.setValue(CastExpression.cast(variable.getType(), valueToAssign, context), context);
            }
        }else{
            String binaryOperator = operator.substring(0, operator.length() - 1);
            variable.setValue(CastExpression.cast(variable.getType(), BinaryExpression.operate(context, variableToAssign.finalExpression(), binaryOperator, valueToAssign.finalExpression()), context), context);
        }
        return variable.absoluteValue();
    }

    @Override
    public void execute(Context context) {
        eval(context);

    }

    @Override
    public String toString() {
        return "AssigmentStatement{" +
                "key=" + key +
                ", operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }
}
