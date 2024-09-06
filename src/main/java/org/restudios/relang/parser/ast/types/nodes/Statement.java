package org.restudios.relang.parser.ast.types.nodes;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

public abstract class Statement extends Expression {
    public Statement(Token token) {
        super(token);
    }

    public static Statement ofExpression(Expression exp) {
        return new Statement(exp.token) {
            @Override
            public void execute(Context context) {
                exp.eval(context);
            }

            @Override
            public void analyze(AnalyzerContext context) {
                
            }
        };
    }

    @Override
    public Value eval(Context context) {
        return new NullValue();
    }
    public abstract void execute(Context context);
    
    @Override
    public Type predictType(AnalyzerContext c) {
        return Primitives.VOID.type();
    }
    public abstract void analyze(AnalyzerContext context);
}

