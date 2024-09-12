package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.ReturnExp;
import org.restudios.relang.parser.tokens.Token;

public class ReturnStatement extends Statement {
    public final Expression expression;

    public ReturnStatement(Token token, Expression expression) {
        super(token);
        this.expression = expression;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        if(context.getMustReturn() == null){
            throw new AnalyzerError("There shouldn't be a return here.", token);
        }
        Type mustReturn = context.getMustReturn();
        mustReturn.initClassOrType(context);
        Type returned = expression == null ? Primitives.VOID.type() : expression.predictType(context);
        returned.initClassOrType(context);
        if(returned.primitive == Primitives.VOID || returned.primitive == Primitives.NULL) return;
        if(!returned.canBe(mustReturn, true)){
            throw new AnalyzerError("The return type must be "+mustReturn, expression == null ? token : expression.token);
        }
    }

    @Override
    public void execute(Context context) {
        throw new ReturnExp(expression == null ? Value.voidValue() : expression.eval(context));
    }

    @Override
    public boolean hasReturnStatement() {
        return true;
    }
}
