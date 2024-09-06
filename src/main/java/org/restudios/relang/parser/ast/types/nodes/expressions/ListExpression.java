package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;

public class ListExpression extends Expression {
    public final ArrayList<Expression> items;

    public ListExpression(Token token, ArrayList<Expression> items) {
        super(token);
        this.items = items;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        return c.getClass(DynamicSLLClass.ARRAY).type();
    }
 
    @Override
    public Value eval(Context context) {
        ArrayList<Value> values = new ArrayList<>();
        Type t = null;
        if(items.isEmpty()){
             t= new Type(null, new ArrayList<>(), context.getClass(DynamicSLLClass.OBJECT));
        }
        for (Expression item : items) {
            Value val = item.eval(context).finalExpression();
            Type p = val.type();
            if(t == null) t = p;
            if(!p.canBe(t)){
                throw new AnalyzerError("Mixed types", item.token);
            }
            values.add(val);
        }
        if(!values.isEmpty()) t = values.get(0).type();
        RLArray le = new RLArray(t, context);
        for (Value value : values) {
            le.add(value);
        }
        return le;
    }
}
