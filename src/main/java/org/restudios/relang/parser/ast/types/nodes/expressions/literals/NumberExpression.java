package org.restudios.relang.parser.ast.types.nodes.expressions.literals;

import org.restudios.relang.parser.ast.types.nodes.expressions.LiteralExpression;
import org.restudios.relang.parser.tokens.Token;

public abstract class NumberExpression extends LiteralExpression {
    public NumberExpression(Token token) {
        super(token);
    }
}
