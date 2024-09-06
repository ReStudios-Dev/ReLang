package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.extra.AnnotationDefinition;
import org.restudios.relang.parser.ast.types.nodes.extra.LoadedAnnotation;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.ConstructorMethod;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConstructorDeclarationStatement extends MethodDeclarationStatement{
    final List<Expression> callSuper;
    public ConstructorDeclarationStatement(Token token, String name, Type returning, ArrayList<Visibility> visibility, ArrayList<VariableDeclarationStatement> arguments, BlockStatement code, List<Expression> callSuper, boolean isNative, List<AnnotationDefinition> annotationDefinitions) {
        super(token, name, returning, visibility, arguments, code, false, isNative, annotationDefinitions);
        this.callSuper = callSuper;
    }

    @Override
    public FunctionMethod method(Context context) {

        ArrayList<FunctionArgument> args = new ArrayList<>();
        if(arguments != null)
            for (VariableDeclarationStatement argument : arguments) {
                args.add(new FunctionArgument(argument.variable, argument.type, argument.varArgs));
            }

        List<LoadedAnnotation> la = annotationDefinitions.stream().map(annotationDefinition -> annotationDefinition.eval(context)).collect(Collectors.toList());
        return new ConstructorMethod(new ArrayList<>(), args, returning, name, visibility, code, isAbstract, callSuper, isNative, la);
    }

    @Override
    public Value eval(Context context) {
        return super.eval(context);
    }
}
