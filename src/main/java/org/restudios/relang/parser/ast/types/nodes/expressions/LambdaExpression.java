package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.statements.BlockStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.VariableDeclarationStatement;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.ReFunction;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLRunnable;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;
import java.util.Collections;

public class LambdaExpression extends Expression {
    public final ArrayList<VariableDeclarationStatement> arguments;
    public final Statement body;

    public LambdaExpression(Token token, ArrayList<VariableDeclarationStatement> arguments, Statement body) {
        super(token);
        this.arguments = arguments;
        this.body = body;
    }

    @Override
    public Value eval(Context context) {
        ArrayList<FunctionArgument> args = new ArrayList<>();
        for (VariableDeclarationStatement argument : arguments) {
            args.add(new FunctionArgument(argument.variable, argument.type));
        }
        BlockStatement statement;
        if(this.body instanceof BlockStatement){
            statement = (BlockStatement) this.body;
        }else{
            statement = new BlockStatement(token, new ArrayList<>(Collections.singletonList(body)));
        }
        ReFunction reFunction = new FunctionMethod(new ArrayList<>(), args, Primitives.VOID.type(), "<lambda>", new ArrayList<>(Collections.singletonList(Visibility.READONLY)), statement, false, false);
        return new RLRunnable(reFunction, context);
    }
}
