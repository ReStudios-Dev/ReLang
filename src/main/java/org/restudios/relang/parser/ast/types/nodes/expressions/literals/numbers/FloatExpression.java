package org.restudios.relang.parser.ast.types.nodes.expressions.literals.numbers;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.literals.NumberExpression;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.FloatValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

public class FloatExpression extends NumberExpression {
    public final double value;

    public FloatExpression(Token token, double value) {
        super(token);
        this.value = value;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        return Primitives.FLOAT.type();
    }

    @Override
    public Value eval(Context context) {
        return new FloatValue(value);
    }
}
