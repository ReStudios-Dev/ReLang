package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.ConstructorMethod;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class ConstructorDeclarationStatement extends MethodDeclarationStatement{
    final List<Expression> callSuper;
    public ConstructorDeclarationStatement(Token token, String name, Type returning, ArrayList<Visibility> visibility, ArrayList<VariableDeclarationStatement> arguments, BlockStatement code, List<Expression> callSuper) {
        super(token, name, returning, visibility, arguments, code, false, false);
        this.callSuper = callSuper;
    }

    @Override
    public FunctionMethod method() {

        ArrayList<FunctionArgument> args = new ArrayList<>();
        if(arguments != null)
            for (VariableDeclarationStatement argument : arguments) {
                args.add(new FunctionArgument(argument.variable, argument.type));
            }
        return new ConstructorMethod(new ArrayList<>(), args, returning, name, visibility, code, isAbstract, callSuper);
    }
}
