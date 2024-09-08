package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.BooleanValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class InlineIfExpression extends Expression {
    public final Expression condition;
    public final Expression trueCase;
    public final Expression falseCase;

    public InlineIfExpression(Token token, Expression condition, Expression trueCase, Expression falseCase) {
        super(token);
        this.condition = condition;
        this.trueCase = trueCase;
        this.falseCase = falseCase;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        Type t = condition.predictType(c);
        t.initClassOrType(c);
        if(t.primitive != Primitives.BOOL) throw new AnalyzerError("Condition receives boolean only", condition.token);

        Type trueCase = this.trueCase.predictType(c);
        Type falseCase = this.falseCase.predictType(c);
        if(!trueCase.canBe(falseCase) || !falseCase.canBe(trueCase)){
            throw new AnalyzerError("Both cases must be of the same type", token);
        }
        return trueCase;
    }



    @Override
    public Value eval(Context context) {
        Value v = condition.eval(context).finalExpression();
        if(!(v instanceof BooleanValue)){
            throw new RLException("Condition receives boolean value only", Type.internal(context), context);
        }
        return v.booleanValue() ? trueCase.eval(context).finalExpression() : falseCase.eval(context).finalExpression();
    }
}
