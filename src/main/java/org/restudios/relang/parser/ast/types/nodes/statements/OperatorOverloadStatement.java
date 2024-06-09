package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.tokens.Token;

public abstract class OperatorOverloadStatement extends Statement {

    public final BlockStatement body;

    public OperatorOverloadStatement(Token token, BlockStatement body) {
        super(token);
        this.body = body;
    }

    public abstract FunctionMethod method(Context context);
}
