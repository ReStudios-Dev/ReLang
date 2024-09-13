package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.MemberStatement;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.FloatValue;
import org.restudios.relang.parser.ast.types.values.values.IntegerValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLStr;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class BinaryExpression extends Statement {
    public final Expression left;
    public final String operator;
    public final Expression right;

    public BinaryExpression(Token token, Expression left, String operator, Expression right) {
        super(token);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        Type l = left.predictType(c);
        Type r = right.predictType(c);
        l.initClassOrType(c);
        r.initClassOrType(c);
        if(l.isCustomType() && r.isCustomType()){
            RLClass li = l.clazz;
            RLClass ri = r.clazz;
            if(li.equals(ri)){
                FunctionMethod fm = li.findBinaryOperator(operator, li, li);
                if(fm != null){
                    return fm.getReturnType();
                }
            }else{
                FunctionMethod fm = li.findBinaryOperator(operator, li, ri);
                FunctionMethod fm2 = ri.findBinaryOperator(operator, li, ri);
                if(fm != null){
                    return fm.getReturnType();
                }
                if(fm2 != null){
                    return fm2.getReturnType();
                }
            }
        }else if(l.isCustomType() && l.primitive != Primitives.TYPE){
            RLClass li = l.clazz;
            FunctionMethod fm = li.findBinaryOperator(operator, Type.clazz(li), r);
            if(fm != null){
                return fm.getReturnType();
            }
        }else if(r.isCustomType() && r.primitive != Primitives.TYPE){
            RLClass ri = r.clazz;
            FunctionMethod fm = ri.getRLClass().findBinaryOperator(operator, l, Type.clazz(ri));
            if(fm != null){
                return fm.getReturnType();
            }
        }
        if(operator.equals("+")){
            if(l.isString()) return l.clazz.type();
            if(r.isString()) return r.clazz.type();
        }
        List<Primitives> compatible = new ArrayList<>(Arrays.asList(Primitives.FLOAT, Primitives.INTEGER, Primitives.CHAR));
        switch (operator) {
            case "+":
            case "-":
            case ">>":
            case ">>>":
            case "<<":
            case "^":
            case "&":
            case "|":
            case "%":
            case "**":
            case "/":
            case "*":
                for (Primitives primitives : compatible) {
                    if(l.primitive == primitives){
                        for (Primitives primitives1 : compatible) {
                            if(r.primitive == primitives1){
                                return r.primitive.type();
                            }
                        }
                    }
                }
                throw new AnalyzerError("Invalid binary operation", token);
        }
        throw new AnalyzerError("Unsupported operation", token);
    }

    @Override
    public void analyze(AnalyzerContext context) {
        predictType(context);
    }

    public static Value operate(Context context, Value l, String operator, Value r) {
        l = l.finalExpression();
        r = r.finalExpression();
        if(l instanceof ClassInstance && r instanceof ClassInstance){
            ClassInstance li = (ClassInstance) l;
            ClassInstance ri = (ClassInstance) r;
            if(li.getRLClass().equals(ri.getRLClass())){
                FunctionMethod fm = li.getRLClass().findBinaryOperator(operator, li.getRLClass(), li.getRLClass());
                if(fm != null){
                    return fm.runMethod(li.getRLClass().getStaticContext(), context, li, ri);
                }
            }else{
                FunctionMethod fm = li.getRLClass().findBinaryOperator(operator, li.getRLClass(), ri.getRLClass());
                FunctionMethod fm2 = ri.getRLClass().findBinaryOperator(operator, li.getRLClass(), ri.getRLClass());
                if(fm != null){
                    Type lt = fm.getArguments().get(0).type;
                    Type rt = fm.getArguments().get(1).type;
                    return fm.runMethod(li.getRLClass().getStaticContext(), context, CastExpression.cast(lt, li, context), CastExpression.cast(rt, ri, context));
                }
                if(fm2 != null){
                    Type lt = fm2.getArguments().get(0).type;
                    Type rt = fm2.getArguments().get(1).type;
                    return fm2.runMethod(li.getRLClass().getStaticContext(), context, CastExpression.cast(lt, li, context), CastExpression.cast(rt, ri, context));
                }
            }
        }else if(l instanceof ClassInstance){
            ClassInstance li = (ClassInstance) l;
            FunctionMethod fm = li.getRLClass().findBinaryOperator(operator, Type.clazz(li.getRLClass()), r.type());
            if(fm != null){
                Type lt = fm.getArguments().get(0).type;
                Type rt = fm.getArguments().get(1).type;
                return fm.runMethod(li.getRLClass().getStaticContext(), context, CastExpression.cast(lt, li, context), CastExpression.cast(rt, r, context));
            }
        }else if(r instanceof ClassInstance){
            ClassInstance ri = (ClassInstance) r;
            FunctionMethod fm = ri.getRLClass().findBinaryOperator(operator, l.type(), Type.clazz(ri.getRLClass()));
            if(fm != null){
                Type lt = fm.getArguments().get(0).type;
                Type rt = fm.getArguments().get(1).type;
                return fm.runMethod(ri.getRLClass().getStaticContext(), context, CastExpression.cast(lt, l, context), CastExpression.cast(rt, ri, context));
            }
        }
        switch (operator) {
            case "+":
                return additionOperation(l, r, context);
            case "-":
                return subtractionOperation(l, r, context);
            case "*":
                return multiplicationOperation(l, r, context);
            case "/":
                return divisionOperation(l, r, context);
            case "**":
                return powOperation(l, r, context);
            case "%":
                return moduloOperation(l, r, context);
            case "|":
                return bitwiseOr(l, r, context);
            case "&":
                return bitwiseAnd(l, r, context);
            case "^":
                return bitwiseXor(l, r, context);
            case "<<":
                return bitwiseLeftShiftSigned(l, r, context);
            case ">>":
                return bitwiseRightShiftSigned(l, r, context);
            case ">>>":
                return bitwiseRightShiftUnsigned(l, r, context);
        }
        throw new RuntimeException("Unsupported operation");
    }

    @Override
    public Value eval(Context context) {
        return operate(context, left.eval(context).finalExpression(), operator, right.eval(context).finalExpression());
    }

    @Override
    public void execute(Context context) {
        eval(context);
    }

    public static Value bitwiseRightShiftUnsigned(Value l, Value r, Context context) {

        if(!l.type().isPrimitive || !r.type().isPrimitive){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue(leftInt >>> rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue((int)leftFloat >>> rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public static Value bitwiseRightShiftSigned(Value l, Value r, Context context) {

        if(!l.type().isCustomType() || r.type().isCustomType()){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue(leftInt >> rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue((int)leftFloat >> rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public static Value bitwiseLeftShiftSigned(Value l, Value r, Context context) {

        if(l.type().isCustomType() || r.type().isCustomType()){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue(leftInt << rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue((int)leftFloat << rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public static Value bitwiseXor(Value l, Value r, Context context) {

        if(l.type().isCustomType() || r.type().isCustomType()){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue(leftInt ^ rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue((int)leftFloat ^ rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public static Value bitwiseAnd(Value l, Value r, Context context) {

        if(l.type().isCustomType() || r.type().isCustomType()){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue(leftInt & rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue((int)leftFloat & rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public static Value bitwiseOr(Value l, Value r, Context context) {

        if(l.type().isCustomType() || r.type().isCustomType()){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue(leftInt | rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue((int)leftFloat | rightInt);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public static Value moduloOperation(Value l, Value r, Context context) {

        if(l.type().isCustomType() || r.type().isCustomType()){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue(leftInt % rightInt);
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        return new FloatValue((double) leftInt % rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue((int)leftFloat % rightInt);
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        return new FloatValue( leftFloat % rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public static Value powOperation(Value l, Value r, Context context) {

        if(l.type().isCustomType() || r.type().isCustomType()){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new FloatValue(Math.pow(leftInt, rightInt));
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        return new FloatValue(Math.pow(leftInt, rightFloat));
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new FloatValue(Math.pow(leftFloat, rightInt));
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        return new FloatValue(Math.pow(leftFloat, rightFloat));
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public  static Value divisionOperation(Value l, Value r, Context context) {

        if(l.type().isCustomType() || r.type().isCustomType()){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        if(rightInt == 0)throw new RLException("Division by zero", Type.internal(context), context);
                        return new IntegerValue(leftInt / rightInt);
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        if(rightFloat == 0)throw new RLException("Division by zero", Type.internal(context), context);
                        return new FloatValue((double) leftInt / rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        if(rightInt == 0)throw new RLException("Division by zero", Type.internal(context), context);
                        return new IntegerValue((int)leftFloat / rightInt);
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        if(rightFloat == 0)throw new RLException("Division by zero", Type.internal(context), context);
                        return new FloatValue( leftFloat / rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public static Value multiplicationOperation(Value l, Value r, Context context) {

        Type left = l.type();
        Type right = r.type();
        if(left.clazz != null && left.clazz.getName().equals(DynamicSLLClass.STRING)){
            if(right.primitive == Primitives.INTEGER){
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < r.intValue(); i++) {
                    s.append(l.stringValue());
                }
                return new RLStr(s.toString(), context);
            }
        }
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue(leftInt * rightInt);
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        return new FloatValue((double) leftInt * rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue((int)leftFloat * rightInt);
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        return new FloatValue( leftFloat * rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }

            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }

    }
    public static Value subtractionOperation(Value l, Value r, Context context) {
        if(l.type().isCustomType() || r.type().isCustomType()){
            throw new RLException("Could not operate classes", Type.internal(context), context);
        }
        Type left = l.type();
        Type right = r.type();
        switch (left.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = l.intValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue(leftInt - rightInt);
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        return new FloatValue((double) leftInt - rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = r.floatValue();
                switch (right.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = r.intValue();
                        return new IntegerValue((int)leftFloat - rightInt);
                    case FLOAT:
                        double rightFloat = r.floatValue();
                        return new FloatValue( leftFloat - rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }
    public static Value additionOperation(Value left, Value right, Context context) {
        if(left.type().isCustomType()){
            left = new RLStr(left.finalExpression().stringValue(), context);
        }
        if(right.type().isCustomType()){
            left = new RLStr(left.finalExpression().stringValue(), context);
        }
        Type l = left.type();
        Type r = right.type();
        if(left instanceof RLStr){
            RLStr val = ((RLStr) left);
            val.value += right.finalExpression().stringValue();
            return val;
        }
        if(right instanceof RLStr){
            RLStr val = ((RLStr) right);
            val.value = left.finalExpression().stringValue()+val.value;
            return val;
        }
        switch (l.primitive){
            case INTEGER:
            case CHAR:
                int leftInt = left.intValue();
                switch (r.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = right.intValue();
                        return new IntegerValue(leftInt+rightInt);
                    case FLOAT:
                        double rightFloat = right.floatValue();
                        return new FloatValue((double) leftInt+rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            case FLOAT:
                double leftFloat = left.floatValue();
                switch (r.primitive){
                    case INTEGER:
                    case CHAR:
                        int rightInt = right.intValue();
                        return new IntegerValue((int)leftFloat+rightInt);
                    case FLOAT:
                        double rightFloat = right.floatValue();
                        return new FloatValue( leftFloat+rightFloat);
                    default:
                        throw new RLException("Unsupported operation", Type.internal(context), context);
                }
            default:
                throw new RLException("Unsupported operation", Type.internal(context), context);
        }
    }

    @Override
    public String toString() {
        return "BinaryExpression{" +
                "left=" + left +
                ", operator='" + operator + '\'' +
                ", right=" + right +
                ", token=" + token +
                '}';
    }
}
