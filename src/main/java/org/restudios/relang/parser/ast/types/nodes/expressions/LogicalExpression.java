package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Type left = this.left.predictType(context);
        Type right = this.right.predictType(context);

        left.initClassOrType(context);
        right.initClassOrType(context);

        switch (operator) {
            case ">":
            case "<":
            case ">=":
            case "<=":
                if(!left.isCustomType() && !right.isCustomType()){
                    if(left.primitive == Primitives.INTEGER){
                        if(right.primitive == Primitives.INTEGER || right.primitive == Primitives.FLOAT) return Primitives.BOOL.type();
                    }else if(left.primitive == Primitives.FLOAT){
                        if(right.primitive == Primitives.INTEGER || right.primitive == Primitives.FLOAT) return Primitives.BOOL.type();
                    }
                }else{
                    if(left.isInstance() && right.isInstance()){
                        FunctionMethod fm = left.clazz.findBinaryOperator(operator, left, right);
                        if(fm != null) return fm.getReturnType();
                        fm = right.clazz.findBinaryOperator(operator, left, right);
                        if(fm != null) return fm.getReturnType();
                    }else{
                        throw new AnalyzerError("Cannot process non instances", token);
                    }
                }
            case "||":
            case "&&":
                if(left.primitive != Primitives.BOOL || right.primitive != Primitives.BOOL){
                    throw new AnalyzerError("Cannot compare non boolean values", token);
                }
            case "==":
            case "!=":
                return Primitives.BOOL.type();
            case "instanceof":
                if(!left.isCustomType() || !left.isInstance() || !right.isCustomType() || right.isInstance()){
                    throw new AnalyzerError("Instanceof can compare instance with class (myInstance instanceof MyClass)", left.token);
                }
        }
        return Primitives.BOOL.type();
    }

    @Override
    public Value eval(Context context) {
        Value l = left.eval(context).initContext(context).finalExpression();
        Value r = right.eval(context).initContext(context).finalExpression();
        List<String> overloadable = Arrays.asList(">", "<", ">=", "<=");
        if(overloadable.contains(this.operator)){
            if(l instanceof ClassInstance && r instanceof ClassInstance){
                ClassInstance lci = (ClassInstance) l;
                ClassInstance rci = (ClassInstance) r;
                FunctionMethod fm = lci.getRLClass().findBinaryOperator(operator, lci.type(), rci.type());
                if(fm != null){
                    return fm.runMethod(lci.getContext(), context, l, r);
                }
                fm = rci.getRLClass().findBinaryOperator(operator, lci.type(), rci.type());
                if(fm != null){
                    return fm.runMethod(lci.getContext(), context, l, r);
                }
            }
        }
        switch (operator) {
            case ">":
                return parseGreater(l, r, context);
            case "<":
                return parseLess(l, r, context);
            case ">=":
                return parseGreaterOrEqual(l, r, context);
            case "<=":
                return parseLessOrEqual(l, r, context);
            case "||":
                return parseOr(l, context);
            case "&&":
                return parseAnd(l, context);
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
                            return Value.value(false);
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
                            return Value.value(false);
                            //throw new RLException("Could operate integer with "+right.displayName(), Type.internal(context));
                    }
                default:
                    return Value.value(false);
                    //throw new RLException("Could operate "+left.displayName()+" with "+right.displayName(), Type.internal(context));
            }
        }else{
            if(l instanceof ClassInstance && r instanceof ClassInstance){
                FunctionMethod fm = ((ClassInstance) l).findMethodFromNameAndArguments(context,"equals", r);

                Value bv = fm.runMethod(((ClassInstance) l).getContext(), context, r);
                return new BooleanValue(bv.booleanValue());
            }
            return Value.value(false);

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
                            return Value.value(false);
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
                            return Value.value(false);
                    }
                case NULL:
                    return new BooleanValue(right.primitive != Primitives.NULL);
                default:
                    return Value.value(false);
            }
        }else{
            return Value.value(false);
        }
    }
    private Value parseLessOrEqual(Value l, Value right, Context context) {
        double leftSide = l.floatValue();
        double rightSide = right.floatValue();
        return new BooleanValue(leftSide <= rightSide);

    }
    private Value parseGreaterOrEqual(Value l, Value right, Context context) {
        double leftSide = l.floatValue();
        double rightSide = right.floatValue();
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
    private Value parseLess(Value l, Value right, Context context) {
        double leftSide = l.floatValue();
        double rightSide = right.floatValue();
        return new BooleanValue(leftSide < rightSide);
    }
    private Value parseGreater(Value l, Value right, Context context) {
        double leftSide = l.floatValue();
        double rightSide = right.floatValue();
        return new BooleanValue(leftSide > rightSide);

    }
}
