package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.exceptions.ReturnExp;
import org.restudios.relang.parser.tokens.Token;

public class ReturnStatement extends Statement {
    public final Expression exception;

    public ReturnStatement(Token token, Expression exception) {
        super(token);
        this.exception = exception;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        exception.predictType(context);
    }

    @Override
    public void execute(Context context) {
        throw new ReturnExp(exception.eval(context));
    }
}
