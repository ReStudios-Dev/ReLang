package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.BooleanValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class DoWhileStatement extends WhileStatement{
    public DoWhileStatement(Token token, Expression condition, Statement body) {
        super(token, condition, body);
    }

    @Override
    public void execute(Context context) {
        Value cond;
        do {
            Context sub = new Context(context);
            body.execute(sub);
        } while ((cond = condition.eval(context).finalExpression()) instanceof BooleanValue && cond.booleanValue());
    }

    @Override
    public boolean hasReturnStatement() {
        return body.hasReturnStatement();
    }
}
