package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.DeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;

public class MethodDeclarationStatement extends DeclarationStatement {
    public final String name;
    public final Type returning;
    public final ArrayList<Visibility> visibility;
    public final ArrayList<VariableDeclarationStatement> arguments;
    public final BlockStatement code;
    public final boolean isAbstract;
    public final boolean isNative;

    public MethodDeclarationStatement(Token token, String name, Type returning, ArrayList<Visibility> visibility, ArrayList<VariableDeclarationStatement> arguments, BlockStatement code, boolean isAbstract, boolean isNative) {
        super(token);
        this.name = name;
        this.returning = returning;
        this.visibility = visibility;
        this.arguments = arguments;
        this.code = code;
        this.isAbstract = isAbstract;
        this.isNative = isNative;
    }

    public FunctionMethod method() {
        ArrayList<FunctionArgument> args = new ArrayList<>();
        if(arguments != null) {
            for (VariableDeclarationStatement argument : arguments) {
                args.add(new FunctionArgument(argument.variable, argument.type));
            }
        }
        return new FunctionMethod(new ArrayList<>(), args, returning, name, visibility, code, isAbstract, isNative);
    }

    @Override
    public void execute(Context context) {
        FunctionMethod m = method();
        context.putMethod(m);
    }

    @Override
    public void prepare(Context context) {

    }

    @Override
    public void validate(Context context) {
        execute(context);
    }
}
