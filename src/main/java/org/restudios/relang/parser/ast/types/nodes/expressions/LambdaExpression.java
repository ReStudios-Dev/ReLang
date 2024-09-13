package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.BlockStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.VariableDeclarationStatement;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.ReFunction;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLRunnable;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LambdaExpression extends Statement {
    public final ArrayList<VariableDeclarationStatement> arguments;
    public final Statement body;
    public final Expression expression;
    public final boolean byBody;

    public LambdaExpression(Token token, ArrayList<VariableDeclarationStatement> arguments, Statement body) {
        super(token);
        this.arguments = arguments;
        this.body = body;
        this.expression = null;
        this.byBody = true;
    }
    public LambdaExpression(Token token, ArrayList<VariableDeclarationStatement> arguments, Expression exp) {
        super(token);
        this.arguments = arguments;
        this.expression = exp;
        this.body = null;
        this.byBody = false;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        AnalyzerContext nc = c.create();
        Type apply = c.getShouldBe();
        Type before = nc.setMustReturn(Primitives.VOID.type());
        if(apply != null){
            if(apply.isRunnable()){
                nc.setMustReturn(apply.subTypes.get(0));
            }else if(apply.clazz != null && apply.clazz.isInterface()){
                RLClass clazz = apply.clazz;
                if(clazz.getAllDeclaredMethods().size() == 1){
                    FunctionMethod fm = isCompatible(clazz, c);
                    if(fm == null){
                        throw new AnalyzerError("Can't extend interface " + clazz.getName() + " because arguments of method " + fm + " is incompatible", token);

                    }

                    nc.setMustReturn(fm.getReturnType());
                }
            }
        }

        for (VariableDeclarationStatement argument : arguments) {
            nc.putVariable(argument);
        }
        if(body != null) body.analyze(nc);
        if(expression != null) expression.predictType(nc);
        nc.setMustReturn(before);

        Type t = c.getClass(DynamicSLLClass.RUNNABLE).type();

        t.lambda = this;
        t.setInstance(true);
        return t;
    }

    public FunctionMethod isCompatible(RLClass castInterface, AnalyzerContext c){
        FunctionMethod fm = castInterface.getAllDeclaredMethods().get(0);
        List<VariableDeclarationStatement> fromArg = arguments;
        List<FunctionArgument> toArg = fm.getArguments();
        if(!fromArg.isEmpty() && !toArg.isEmpty()) {
            for (int i = 0; i < fromArg.size(); i++) {
                VariableDeclarationStatement from = fromArg.get(i);
                FunctionArgument to = toArg.get(i);
                if(c != null) from.type.initClassOrType(c);
                if(c != null) to.type.initClassOrType(c);
                //if(to.type.primitive == Primitives.TYPE) continue;
                if (!from.type.canBe(to.type)) {
                    return null;
                }
            }
        }else return fromArg.isEmpty() == toArg.isEmpty() ? fm : null;
        return fm;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        predictType(context);
    }

    public Value typeEval(Context context, Type type){
        ArrayList<FunctionArgument> args = new ArrayList<>();
        for (VariableDeclarationStatement argument : arguments) {
            args.add(new FunctionArgument(argument.variable, argument.type, false));
        }
        if(byBody){
            BlockStatement statement;
            if(this.body instanceof BlockStatement){
                statement = (BlockStatement) this.body;
            }else{
                statement = new BlockStatement(token, new ArrayList<>(Collections.singletonList(body)));
            }
            ReFunction reFunction = new FunctionMethod(new ArrayList<>(), args,type, "<lambda>", new ArrayList<>(Collections.singletonList(Visibility.READONLY)), statement, false, false, new ArrayList<>());
            return new RLRunnable(reFunction, context);
        } else {
            ReFunction reFunction = new FunctionMethod(new ArrayList<>(), args, type, "<lambda>", new ArrayList<>(Collections.singletonList(Visibility.READONLY)), BlockStatement.returnStatement(expression, expression.token), false, false, new ArrayList<>());
            return new RLRunnable(reFunction, context);
        }
    }
    @Override
    public Value eval(Context context) {
        return typeEval(context, Primitives.VOID.type());
    }

    @Override
    public void execute(Context context) {

    }
}
