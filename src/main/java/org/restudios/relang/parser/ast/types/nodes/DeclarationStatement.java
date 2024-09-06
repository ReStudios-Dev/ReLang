package org.restudios.relang.parser.ast.types.nodes;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.tokens.Token;

public abstract class DeclarationStatement extends Statement {
    public DeclarationStatement(Token token) {
        super(token);
    }

    public abstract void analyzerPrepare(AnalyzerContext context);
    public abstract void prepare(Context context);
    public abstract void validate(Context context);
}
