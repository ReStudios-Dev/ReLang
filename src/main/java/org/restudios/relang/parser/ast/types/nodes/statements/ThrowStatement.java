package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class ThrowStatement extends Statement {
    private final Expression expression;

    public ThrowStatement(Token token, Expression expression) {
        super(token);
        this.expression = expression;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        Type t = expression.predictType(context);
        if(!t.isCustomType()) throw new AnalyzerError("Cannot throw primitive type", expression.token);
        if(!t.clazz.isThrowable(context)){
            throw new AnalyzerError("Cannot throw non exception", expression.token);
        }
    }

    @Override
    public void execute(Context context) {

        context.putTrace(this);
        String currentMethod = context.setCurrentMethod("<throw>");
        Value e = expression.eval(context).initContext(context).finalExpression();
        context.setCurrentMethod(currentMethod);
        if(!(e instanceof ClassInstance)){
            throw new RLException("Can not throw non class", Type.internal(context), context);
        }
        ClassInstance ci = (ClassInstance) e;
        if(!ci.isExceptionClass()){
            throw new RLException("Can not throw non exception", Type.internal(context), context);
        }
        RLException rle = new RLException(ci.getContext().getVariable("message").absoluteValue().stringValue(), ci);
        rle.setTrace(context.getTrace());
        throw rle;
    }

    @Override
    public boolean hasReturnStatement() {
        return true;
    }
}
