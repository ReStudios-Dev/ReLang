package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.BooleanValue;
import org.restudios.relang.parser.ast.types.values.values.FloatValue;
import org.restudios.relang.parser.ast.types.values.values.IntegerValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class UnaryStatement extends Statement {
    public final String operator;
    public final Expression expression;
    public final boolean prefix;

    public UnaryStatement(Token token, String operator, Expression expression, boolean prefix) {
        super(token);
        this.operator = operator;
        this.expression = expression;
        this.prefix = prefix;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        switch (operator){
            case "-":
            case "~":
            case "++":
            case "--":
                return expression.predictType(c);
            case "!":
                return Primitives.BOOL.type();

        }
        throw new AnalyzerError("Invalid unary operator", token);
    }

    @Override
    public void analyze(AnalyzerContext context) {
        predictType(context);
    }

    @Override
    public Value eval(Context context) {
        Value val;
        Variable v;
        switch (operator){
            case "-":
                if(prefix){
                    val = expression.eval(context).finalExpression();
                    Value res = new IntegerValue(-val.intValue());
                    if(val instanceof FloatValue){
                        res = new FloatValue(-val.floatValue());
                    }
                    return res;
                }
                throw new RLException("Unsupported operation", Type.internal(context), context);

            case "!":
                if(prefix){
                    return new BooleanValue(!expression.eval(context).finalExpression().booleanValue());
                }
                throw new RLException("Unsupported operation", Type.internal(context), context);

            case "~":
                if(prefix){
                    val = expression.eval(context).finalExpression();
                    return new IntegerValue(~val.intValue());
                }
                throw new RLException("Unsupported operation", Type.internal(context), context);
            case "++":
                val = expression.eval(context);
                if(!(val instanceof Variable)){
                    throw new RLException("Unsupported operation", Type.internal(context), context);
                }
                v = (Variable) val;
                if(v.getType().like(Type.primitive(Primitives.INTEGER))){
                    int va = v.getValue().intValue();
                    int ret;
                    if(prefix) ret = ++va; else ret = va++;
                    v.setValue(new IntegerValue(va), context);
                    return new IntegerValue(ret);
                }else if(v.getType().like(Type.primitive(Primitives.FLOAT))){
                    double va = v.getValue().floatValue();
                    double ret;
                    if(prefix) ret = ++va; else ret = va++;
                    v.setValue(new FloatValue(va), context);
                    return new FloatValue(ret);
                }
                throw new RLException("Unsupported operation: increment on "+v.getType().displayName(), context);
                //throw new RLException("Unsupported operation: increment on "+v.getType().displayName(), Type.internal(context));
            case "--":
                val = expression.eval(context);
                if(!(val instanceof Variable)){
                    throw new RLException("Unsupported operation", Type.internal(context), context);
                }
                v = (Variable) val;
                if(v.getType().like(Type.primitive(Primitives.INTEGER))){
                    int va = v.getValue().intValue();
                    int ret;
                    if(prefix) ret = --va; else ret = va--;
                    v.setValue(new IntegerValue(va), context);
                    return new IntegerValue(ret);
                }else if(v.getType().like(Type.primitive(Primitives.FLOAT))){
                    double va = v.getValue().floatValue();
                    double ret;
                    if(prefix) ret = --va; else ret = va--;
                    v.setValue(new FloatValue(va), context);
                    return new FloatValue(ret);
                }
                throw new RLException("Unsupported operation: decrement on "+v.getType().displayName(), Type.internal(context), context);
        }
        return super.eval(context);
    }

    @Override
    public void execute(Context context) {
        eval(context);
    }

    @Override
    public String toString() {
        return "UnaryStatement{" +
                "operator='" + operator + '\'' +
                ", expression=" + expression +
                ", prefix=" + prefix +
                ", token=" + token +
                '}';
    }
}
