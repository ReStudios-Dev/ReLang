package org.restudios.relang.parser.ast.types.values.values.sll.classes;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.SLLClassInstance;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class RLArray extends SLLClassInstance {
    public final Type type;
    private final ArrayList<Variable> values = new ArrayList<>();
    public RLArray(Type type, Context parent) {
        super(DynamicSLLClass.ARRAY, new ArrayList<>(Collections.singletonList(type)), parent);
        this.type = type;
    }

    public RLArray add(Value v){
        v = v.finalExpression();
        if(!v.type().canBe(type)){
            throw new RLException("Invalid insertion value", Type.internal(getContext()), getContext());
        }
        Variable variable = new Variable(type, UUID.randomUUID().toString(), v, new ArrayList<>(Collections.singletonList(Visibility.PUBLIC)));
        values.add(variable);
        return this;
    }
    public ArrayList<Value> getValues() {
        ArrayList<Value> vals = new ArrayList<>();
        for (Variable value : this.values) {
            vals.add(value.finalExpression());
        }
        return vals;
    }

    @Override
    public String toString() {
        return getValues().toString();
    }

    @Override
    public int intValue() {
        return values.size();
    }

    @Override
    public double floatValue() {
        return intValue();
    }

    public Type getType() {
        return type;
    }

    public ArrayList<Variable> getValuesPointers() {
        return values;
    }

    @Override
    public Object value() {
        return values;
    }


}
