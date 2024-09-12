package org.restudios.relang.parser.ast.types.nodes.expressions.implicit;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.UnaryImplicitOperationType;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.ImplicitExpression;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class UnaryImplicitExpression extends ImplicitExpression {
    public final Expression expression;
    public final UnaryImplicitOperationType operation;
    public final Expression value;

    public UnaryImplicitExpression(Token token, Expression expression, UnaryImplicitOperationType operation, Expression value) {
        super(token);
        this.expression = expression;
        this.operation = operation;
        this.value = value;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        // TODO PREDICT TYPE
        Type t = expression.predictType(c);
        if(t.isArray()){
            return t.subTypes.get(0);
        }else{
            throw new AnalyzerError("Can't unary get item of non array", expression.token);
        }
        //return Primitives.NULL.type();
    }

    @Override
    public Value eval(Context context) {
        Value v = expression.eval(context);
        if(v instanceof Variable) v = ((Variable) v).absoluteValue();
        Value i =value.eval(context).finalExpression();

        if(v instanceof RLArray){
            int item = i.intValue();
            RLArray l = (RLArray)v;
            if(item < 0 || item >= l.getValuesPointers().size()){
                throw new RLException("Array index out of range", Type.arrayOutOfBounds(context), context);
            }
            return l.getValuesPointers().get(item);
        }
        throw new RLException("Cannot get item from non collection", Type.internal(context), context);
    }
}
