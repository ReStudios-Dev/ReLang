package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.IntegerValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.ExitExp;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class ExitStatement extends Statement {
    public final Expression code;

    public ExitStatement(Token token, Expression code) {
        super(token);
        this.code = code;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        if(code.predictType(context).primitive != Primitives.INTEGER) throw new AnalyzerError("Exit statement receives integer value only", code.token);
    }

    @Override
    public void execute(Context context) {
        Value v = code.eval(context).finalExpression();
        if(!(v instanceof IntegerValue)){
            throw new RLException("Exit statement receives integer value only", Type.internal(context), context);
        }
        throw new ExitExp(v.intValue());
    }
}
