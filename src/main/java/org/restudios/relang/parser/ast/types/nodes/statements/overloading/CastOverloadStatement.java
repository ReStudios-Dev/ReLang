package org.restudios.relang.parser.ast.types.nodes.statements.overloading;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.BlockStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.OperatorOverloadStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.VariableDeclarationStatement;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.CastOperatorOverloadFunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;

public class CastOverloadStatement extends OperatorOverloadStatement {
    public final boolean implicit;
    public final VariableDeclarationStatement from;
    public final Type to;

    public CastOverloadStatement(Token token, BlockStatement body, boolean implicit, VariableDeclarationStatement from, Type to) {
        super(token, body);
        this.implicit = implicit;
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute(Context context) {

    }

    @Override
    public String toString() {
        return "cast operator ["+ (implicit ? "implicit" : "explicit")+"]";
    }

    @Override
    public FunctionMethod method(Context context) {
        FunctionArgument from = new FunctionArgument(this.from.variable, this.from.type);
        return new CastOperatorOverloadFunctionMethod(new ArrayList<>(), body, from, to, implicit);
    }
}
