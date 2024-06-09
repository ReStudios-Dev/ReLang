package org.restudios.relang.parser.ast.types.nodes.expressions.literals;

import org.restudios.relang.parser.ast.types.TBoolean;
import org.restudios.relang.parser.ast.types.nodes.expressions.LiteralExpression;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.TBooleanValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

public class TBooleanExpression extends LiteralExpression {
    public final TBoolean value;

    public TBooleanExpression(Token token, TBoolean value) {
        super(token);
        this.value = value;
    }

    @Override
    public Value eval(Context context) {
        return new TBooleanValue(value);
    }
}
