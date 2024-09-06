package org.restudios.relang.parser.ast.types.nodes.statements.overloading;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.BlockStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.OperatorOverloadStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.VariableDeclarationStatement;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;

public class ArithmeticOverloadStatement extends OperatorOverloadStatement {
    private final Type returingType;
    private final String operator;
    private final ArrayList<VariableDeclarationStatement> arguments;

    public ArithmeticOverloadStatement(Token token, BlockStatement body, Type returingType, String operator, ArrayList<VariableDeclarationStatement> arguments) {
        super(token, body);
        this.returingType = returingType;
        this.operator = operator;
        this.arguments = arguments;
    }

    @Override
    public void execute(Context context) {

    }

    @Override
    public void analyze(AnalyzerContext context) {
        AnalyzerContext nc = context.create();
        for (VariableDeclarationStatement argument : arguments) {
            nc.putVariable(argument.variable, argument.type);
        }
        body.analyze(nc);
    }

    @Override
    public String toString() {
        return "arithmetic operator [" + operator + "]";
    }

    @Override
    public FunctionMethod method(Context context) {

        ArrayList<FunctionArgument> args = new ArrayList<>();
        if(arguments != null)
            for (VariableDeclarationStatement argument : arguments) {
                args.add(new FunctionArgument(argument.variable, argument.type, false));
            }
        return new FunctionMethod(new ArrayList<>(), args, returingType, operator, new ArrayList<>(), body, false, false, new ArrayList<>());
    }
}
