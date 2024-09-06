package org.restudios.relang.parser.ast.types.nodes.expressions.literals;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.LiteralExpression;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.BooleanValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

public class BooleanExpression extends LiteralExpression {
    public final boolean value;

    public BooleanExpression(Token token, boolean value) {
        super(token);
        this.value = value;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        return Primitives.BOOL.type();
    }

    @Override
    public Value eval(Context context) {
        return new BooleanValue(value);
    }
}
