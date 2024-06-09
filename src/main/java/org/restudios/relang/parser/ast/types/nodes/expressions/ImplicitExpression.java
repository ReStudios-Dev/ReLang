package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.tokens.Token;

public abstract class ImplicitExpression extends Expression {
    public ImplicitExpression(Token token) {
        super(token);
    }
}
