package org.restudios.relang.parser.ast.types.nodes;

import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

public abstract class Statement extends Expression {
    public Statement(Token token) {
        super(token);
    }

    @Override
    public Value eval(Context context) {
        return new NullValue();
    }
    public abstract void execute(Context context);
}
