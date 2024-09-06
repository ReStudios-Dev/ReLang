package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.tokens.Token;

public class EmptyStatement extends Statement {
    public EmptyStatement(Token token) {
        super(token);
    }

    @Override
    public void analyze(AnalyzerContext context) {

    }

    @Override
    public void execute(Context context) {

    }

}
