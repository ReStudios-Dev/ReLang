package org.restudios.relang.parser.ast.types.nodes;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.Node;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

public abstract class Expression extends Node {
    public Expression(Token token) {
        super(token);
    }

    @SuppressWarnings("unused")
    public static Expression nullExpression(Token pf) {
        return new Expression(pf) {
            @Override
            public Value eval(Context context) {
                return Value.nullValue();
            }

            @Override
            public Type predictType(AnalyzerContext c) {
                return Primitives.NULL.type();
            }
        };
    }

    public abstract Value eval(Context context);

    public abstract Type predictType(AnalyzerContext context);
}
