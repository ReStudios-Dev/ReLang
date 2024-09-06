package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.BooleanValue;
import org.restudios.relang.parser.ast.types.values.values.TBooleanValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class LogicalExpression extends Expression {
    public final Expression left;
    public final String operator;
    public final Expression right;

    public LogicalExpression(Token token, Expression left, String operator, Expression right) {
        super(token);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Type predictType(AnalyzerContext context) {
        return Primitives.BOOL.type();
    }

    @Override
    public Value eval(Context context) {
        Value l = left.eval(context).initContext(context).finalExpression();
        switch (operator) {
            case ">":
                return parseGreater(l, context);
            case "<":
                return parseLess(l, context);
            case "||":
                return parseOr(l, context);
            case "&&":
                return parseAnd(l, context);
            case ">=":
                return parseGreaterOrEqual(l, context);
            case "<=":
                return parseLessOrEqual(l, context);
            case "==":
                return parseEquality(l, context);
            case "!=":
                return parseNotEquality(l, context);
            case "instanceof":
                return parseInstanceOf(l, context);
        }
        return null;
    }
    private Value parseInstanceOf(Value l, Context context){
        Value r = right.eval(context).initContext(context).finalExpression();

        if(!(r instanceof RLClass) || !(l instanceof ClassInstance)) {
            throw new RLException("The instanceof operation can only be performed using the form: <instance> instanceof <class reference>", Type.internal(context), context);
        }
        RLClass assignable = ((RLClass) r);
        ClassInstance ci = (ClassInstance) l;
        return Value.value(ci.getRLClass().isInstanceOf(assignable));
    }
    private Value parseEquality(Value l, Context context) {
        Value r = right.eval(context).initContext(context).finalExpression();
        if(l.type().isPrimitive && r.type().isPrimitive){
            Type left = l.type();
            Type right = r.type();
            switch (left.primitive){
                case BOOL:
                    if(r instanceof BooleanValue) return new BooleanValue(l.booleanValue() == r.booleanValue());
                case TBOOL:
                    if(r instanceof TBooleanValue) return new BooleanValue(((TBooleanValue) l).value == ((TBooleanValue) r).value);

                case INTEGER:
                case CHAR:
                    int leftInt = l.intValue();
                    switch (right.primitive){
                        case INTEGER:
                        case CHAR:
                            int rightInt = r.intValue();
                            return new BooleanValue(leftInt == rightInt);
                        case FLOAT:
                            double rightFloat = r.floatValue();
                            return new BooleanValue(leftInt == rightFloat);
                        default:
                            throw new RLException("Could operate integer with "+right.displayName(), Type.internal(context), context);
                    }
                case FLOAT:
                    double leftFloat = l.floatValue();
                    switch (right.primitive){
                        case INTEGER:
                        case CHAR:
                            int rightInt = r.intValue();
                            return new BooleanValue(leftFloat == rightInt);
                        case FLOAT:
                            double rightFloat = r.floatValue();
                            return new BooleanValue(leftFloat == rightFloat);
                        default:
                            return new BooleanValue(false);
                            //throw new RLException("Could operate integer with "+right.displayName(), Type.internal(context));
                    }
                default:
                    return new BooleanValue(false);
                    //throw new RLException("Could operate "+left.displayName()+" with "+right.displayName(), Type.internal(context));
            }
        }else{
            if(l instanceof ClassInstance && r instanceof ClassInstance){
                FunctionMethod fm = ((ClassInstance) l).findMethodFromNameAndArguments(context,"equals", r);

                Value bv = fm.runMethod(((ClassInstance) l).getContext(), context, r);
                return new BooleanValue(bv.booleanValue());
            }
            throw new RLException("Could operate "+l.type().displayName()+" with "+r.type().displayName(), Type.internal(context), context);

        }
    }

    private Value parseNotEquality(Value l, Context context) {
        Value r = right.eval(context).finalExpression();
        if(l.type().primitive == Primitives.NULL){
            return new BooleanValue(r.type().primitive != Primitives.NULL);
        }
        if(r.type().primitive == Primitives.NULL){
            return new BooleanValue(l.type().primitive != Primitives.NULL);
        }

        if(l.type().isPrimitive && r.type().isPrimitive){
            Type left = l.type();
            Type right = r.type();
            switch (left.primitive){
                case BOOL:
                    if(r instanceof BooleanValue) return new BooleanValue(l.booleanValue() != r.booleanValue());
                case TBOOL:
                    if(r instanceof TBooleanValue) {
                        return new BooleanValue(((TBooleanValue) l).value != ((TBooleanValue) r).value);
                    }

                case INTEGER:
                case CHAR:
                    int leftInt = l.intValue();
                    switch (right.primitive){
                        case INTEGER:
                        case CHAR:
                            int rightInt = r.intValue();
                            return new BooleanValue(leftInt != rightInt);
                        case FLOAT:
                            double rightFloat = r.floatValue();
                            return new BooleanValue(leftInt != rightFloat);
                        default:
                            throw new RLException("Could operate integer with "+right.displayName(), Type.internal(context), context);
                    }
                case FLOAT:
                    double leftFloat = l.floatValue();
                    switch (right.primitive){
                        case INTEGER:
                        case CHAR:
                            int rightInt = r.intValue();
                            return new BooleanValue(leftFloat != rightInt);
                        case FLOAT:
                            double rightFloat = r.floatValue();
                            return new BooleanValue(leftFloat != rightFloat);
                        default:
                            throw new RLException("Could operate integer with "+right.displayName(), Type.internal(context), context);
                    }
                case NULL:
                    return new BooleanValue(right.primitive != Primitives.NULL);
                default:
                    throw new RLException("Could operate "+left.displayName()+" with "+right.displayName(), Type.internal(context), context);
            }
        }else{
            throw new RLException("Could operate "+l.type().displayName()+" with "+r.type().displayName(), Type.internal(context), context);
        }
    }
    private Value parseLessOrEqual(Value l, Context context) {
        double leftSide = l.floatValue();
        double rightSide = right.eval(context).floatValue();
        return new BooleanValue(leftSide <= rightSide);

    }
    private Value parseGreaterOrEqual(Value l, Context context) {
        double leftSide = l.floatValue();
        double rightSide = right.eval(context).floatValue();
        return new BooleanValue(leftSide >= rightSide);

    }
    private Value parseAnd(Value l, Context context) {
        boolean leftSide = l.booleanValue();
        if(!leftSide) return new BooleanValue(false);
        boolean rightSide = right.eval(context).booleanValue();
        return new BooleanValue(rightSide);
    }
    private Value parseOr(Value l, Context context) {
        boolean leftSide = l.booleanValue();
        if(leftSide) return new BooleanValue(true);
        boolean rightSide = right.eval(context).booleanValue();
        return new BooleanValue(rightSide);

    }
    private Value parseLess(Value l, Context context) {
        double leftSide = l.floatValue();
        double rightSide = right.eval(context).floatValue();
        return new BooleanValue(leftSide < rightSide);
    }
    private Value parseGreater(Value l, Context context) {
        double leftSide = l.floatValue();
        double rightSide = right.eval(context).floatValue();
        return new BooleanValue(leftSide > rightSide);

    }
}
