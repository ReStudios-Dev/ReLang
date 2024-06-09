package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.BooleanValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class WhileStatement extends Statement {
    public final Expression condition;
    public final Statement body;

    public WhileStatement(Token token, Expression condition, Statement body) {
        super(token);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void execute(Context context) {
        Context sub = new Context(context);
        while (true){
            Value cond = condition.eval(sub).finalExpression();
            if (cond instanceof BooleanValue){
                if(!cond.booleanValue())break;
            }else{
                throw new RLException("While loop accepts only boolean expressions", Type.internal(context), context);
            }
            body.execute(sub);
        }
    }
}
