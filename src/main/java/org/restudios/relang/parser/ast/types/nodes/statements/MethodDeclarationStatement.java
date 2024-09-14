package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.DeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.extra.AnnotationDefinition;
import org.restudios.relang.parser.ast.types.nodes.extra.LoadedAnnotation;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MethodDeclarationStatement extends DeclarationStatement {
    public final String name;
    public final Type returning;
    public final ArrayList<Visibility> visibility;
    public final ArrayList<VariableDeclarationStatement> arguments;
    public final List<AnnotationDefinition> annotationDefinitions;
    public final BlockStatement code;
    public final boolean isAbstract;
    public final boolean isNative;

    @Override
    public void analyze(AnalyzerContext context) {
        if(code != null){
            AnalyzerContext nc = context.create();
            for (VariableDeclarationStatement argument : arguments) {
                argument.type.initClassOrType(context);
                if(argument.varArgs){
                    Type array = context.getClass(DynamicSLLClass.ARRAY).type();
                    array.applySubTypes(new ArrayList<>(Collections.singletonList(argument.type)));
                    nc.putVariable(argument.variable, array);
                    continue;
                }
                nc.putVariable(argument.variable, argument.type);
            }

            Type before = context.setMustReturn(returning == null ? Primitives.VOID.type() : returning);

            code.analyze(nc);

            context.setMustReturn(before);

        }
        if(returning != null) returning.initClassOrType(context);
        if(returning != null && returning.primitive != Primitives.VOID && returning.primitive != Primitives.NULL){
            if(!isNative) if(code == null || !code.hasReturnStatement()){
                throw new AnalyzerError("The method must return "+returning, token);
            }
        }
        context.putMethod(this);
    }

    @Override
    public void analyzerPrepare(AnalyzerContext context) {
        context.putMethod(this);
    }

    public MethodDeclarationStatement(Token token, String name, Type returning, ArrayList<Visibility> visibility, ArrayList<VariableDeclarationStatement> arguments, BlockStatement code, boolean isAbstract, boolean isNative, List<AnnotationDefinition> annotationDefinitions) {
        super(token);
        this.name = name;
        this.returning = returning;
        this.visibility = visibility;
        this.arguments = arguments;
        this.code = code;
        this.isAbstract = isAbstract;
        this.isNative = isNative;
        this.annotationDefinitions = annotationDefinitions;
    }

    public FunctionMethod method(Context context) {
        ArrayList<FunctionArgument> args = new ArrayList<>();
        if(arguments != null) {
            for (VariableDeclarationStatement argument : arguments) {
                args.add(new FunctionArgument(argument.variable, argument.type, argument.varArgs));
            }
        }
        List<LoadedAnnotation> la = annotationDefinitions.stream().map(annotationDefinition -> annotationDefinition.eval(context)).collect(Collectors.toList());
        return new FunctionMethod(new ArrayList<>(), args, returning, name, visibility, code, isAbstract, isNative, la);
    }

    @Override
    public void execute(Context context) {
        FunctionMethod m = method(context);
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
