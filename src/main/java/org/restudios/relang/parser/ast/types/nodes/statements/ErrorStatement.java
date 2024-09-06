package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.tokens.Token;

public class ErrorStatement extends Statement {
    public final Expression expression;

    public ErrorStatement(Token token, Expression expression) {
        super(token);
        this.expression = expression;
    }
    public ErrorStatement(Token token) { // just a new line
        super(token);
        expression = null;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        if(expression != null) expression.predictType(context);
    }

    @Override
    public void execute(Context context) {
        if (expression == null) {
            context.getOutput().errorStream.println();
        } else {
            context.getOutput().errorStream.println(expression.eval(context).initContext(context).finalExpression().stringValue());
        }
    }
}
