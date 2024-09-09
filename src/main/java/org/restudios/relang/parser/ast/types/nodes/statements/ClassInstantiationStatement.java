package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.IdentifierExpression;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;
import org.restudios.relang.parser.utils.RLStackTraceElement;

import java.util.ArrayList;
import java.util.List;

public class ClassInstantiationStatement extends Statement {
    public final Expression clazz;
    public final List<Type> types;
    public final List<Expression> args;

    public ClassInstantiationStatement(Token token, Expression clazz, List<Type> types, List<Expression> args) {
        super(token);
        this.clazz = clazz;
        this.types = types;
        this.args = args;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        Type t = clazz.predictType(c);
        t.setInstance(true);
        return t;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        if (!clazz.predictType(context).isCustomType()) {
            throw new AnalyzerError("Cannot instantiate non class", clazz.token);
        }
    }

    @Override
    public Value eval(Context context) {
        RLStackTraceElement elem = context.putTrace(this);
        context.setCurrentMethod("<init>");
        Value v;
        if(clazz instanceof IdentifierExpression){
            v = ((IdentifierExpression) clazz).find(context, false);
        }else{
            v = clazz.eval(context).initContext(context).finalExpression();
        }
        if(v instanceof NullValue){
            throw new RLException("Null reference", Type.nullPointer(context), context);
        }
        if(!(v instanceof RLClass)){
            throw new RLException("Cannot initialize non class", Type.internal(context), context);
        }
        RLClass c = (RLClass) v;
        if(c.getName().equals(DynamicSLLClass.ENUM)){
            throw new RLException("Cannot initialize native class", Type.internal(context), context);
        }
        if(!c.isBase()){
            throw new RLException("Cannot initialize "+c.getClassType().name().toLowerCase()+" class "+ c.getName(), Type.internal(context), context);
        }
        Value[] args = new Value[this.args.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = this.args.get(i).eval(context);
        }
        List<Type> nt = new ArrayList<>(types);
        for (Type type : nt) {
            type.init(context);
            type.initClassOrType(context);
        }
        Value vs = c.instantiate(context, nt, args);
        context.removeTrace(elem);
        return vs;
    }

    @Override
    public void execute(Context context) {
        eval(context);

    }
}
