package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.tokens.Token;

public class ForStatement extends Statement {
    public final Statement initialization;
    public final Expression condition;
    public final Statement updating;
    public final Statement body;

    public ForStatement(Token token, Statement initialization, Expression condition, Statement updating, Statement body) {
        super(token);
        this.initialization = initialization;
        this.condition = condition;
        this.updating = updating;
        this.body = body;
    }

    @Override
    public void execute(Context context) {
        Context sub = new Context(context);
        initialization.execute(sub);
        while (condition.eval(sub).booleanValue()) {
            body.execute(sub);
            updating.execute(sub);
        }
    }
}
