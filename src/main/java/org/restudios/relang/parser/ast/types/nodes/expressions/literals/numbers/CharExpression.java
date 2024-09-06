package org.restudios.relang.parser.ast.types.nodes.expressions.literals.numbers;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.literals.NumberExpression;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.CharValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

public class CharExpression extends NumberExpression {
    public final char value;

    public CharExpression(Token token, char value) {
        super(token);
        this.value = value;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        return Primitives.CHAR.type();
    }

    @Override
    public Value eval(Context context) {
        return new CharValue(value);
    }
}
