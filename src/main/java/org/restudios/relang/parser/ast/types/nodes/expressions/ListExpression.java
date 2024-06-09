package org.restudios.relang.parser.ast.types.nodes.expressions;

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
    public Value eval(Context context) {
        ArrayList<Value> values = new ArrayList<>();
        for (Expression item : items) {
            values.add(item.eval(context));
        }
        Type t = new Type(null, new ArrayList<>(), context.getClass(DynamicSLLClass.OBJECT));
        if(!values.isEmpty()) t = values.get(0).type();
        RLArray le = new RLArray(t, context);
        for (Value value : values) {
            le.add(value);
        }
        return le;
    }
}
