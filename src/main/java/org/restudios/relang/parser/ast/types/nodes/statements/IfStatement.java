package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.tokens.Token;

public class IfStatement extends Statement {
    public final Expression condition;
    public final Statement body, elseBody;

    public IfStatement(Token token, Expression condition, Statement body, Statement elseBody) {
        super(token);
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        Type t = condition.predictType(context);
        if(t.primitive != Primitives.BOOL) throw new AnalyzerError("Condition need to be logical", condition.token);
        body.analyze(context.create());
        if(elseBody != null) body.analyze(context.create());
    }

    @Override
    public void execute(Context context) {
        if(condition.eval(context).booleanValue()){
            body.execute(context);
        }else{
            if(elseBody != null){
                elseBody.execute(context);
            }
        }
    }
}
