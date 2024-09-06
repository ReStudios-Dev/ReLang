package org.restudios.relang.parser.ast.types.nodes.expressions.literals;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.LiteralExpression;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLStr;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.tokens.Token;

public class StringExpression extends LiteralExpression {
    public final String value;

    public StringExpression(Token token, String value) {
        super(token);
        this.value = value;
    }

    @Override
    public Type predictType(AnalyzerContext context) {
        return context.getClass(DynamicSLLClass.STRING).type();
    }

    @Override
    public Value eval(Context context) {
        return new RLStr(value, context);
    }
}
