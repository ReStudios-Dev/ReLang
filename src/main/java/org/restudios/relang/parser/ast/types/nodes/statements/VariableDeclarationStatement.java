package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLRunnable;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;

public class VariableDeclarationStatement extends Statement {
    public final Type type;
    public final String variable;
    public final ArrayList<Visibility> visibilities;
    public final boolean varArgs;
    public final Expression value;

    public VariableDeclarationStatement(Token token, Type type, boolean varArgs, String variable, ArrayList<Visibility> visibilities, Expression value) {
        super(token);
        this.type = type;
        this.variable = variable;
        this.visibilities = visibilities;
        this.value = value;
        this.varArgs = varArgs;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        context.putVariable(variable, type);
        type.initClassOrType(context);
        if (value != null){
            Type t = value.predictType(context);
            if(!t.canBe(type)){
                throw new AnalyzerError("Invalid assigment value ("+type+" = "+t+")", value.token);
            }
        }
    }

    public Variable asVariable(Context context) {
        Value val = null;
        type.init(context);
        if(value != null){
            Context subContext = new Context(context);
            type.initClassOrType(context);
            Value v = value.eval(subContext).initContext(subContext).finalExpression();
            if(v instanceof RLRunnable) {
                if (type.like(Type.clazz(DynamicSLLClass.RUNNABLE, context))) {
                    if(type.subTypes.isEmpty()){
                        ((RLRunnable) v).setReturn(Primitives.VOID.type());
                    }else{
                        ((RLRunnable) v).setReturn(type.subTypes.get(0));
                    }
                }
            }
            val = CastExpression.cast(type, v, context);
        }
        if(value == null){
            val = new NullValue();
        }
        return new Variable(type, variable, val, visibilities);
    }

    @Override
    public void execute(Context context) {
        if(type.isCustomType()){
            type.init(context);
        }
        Variable v = asVariable(context);
        if(!v.absoluteValue().type().canBe(type)){
            throw new RLException("Cannot initialize variable with incompatible types", Type.internal(context), context);
        }
        context.putVariable(v);
    }
}
