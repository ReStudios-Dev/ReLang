package org.restudios.relang.parser.ast.types.nodes.expressions.literals;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.LiteralExpression;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

public class NullExpression extends LiteralExpression {
    public NullExpression(Token token) {
        super(token);
    }

    @Override
    public Type predictType(AnalyzerContext context) {
        return Primitives.NULL.type();
    }

    @Override
    public Value eval(Context context) {
        return new NullValue();
    }
}
