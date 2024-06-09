package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.values.IntegerValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;

import java.util.ArrayList;

public class Variable implements Value {

    public static Variable integer(String name, int value){
        return variable(name, new IntegerValue(value));
    }
    public static Variable variable(String name, Value value){
        return new Variable(value.type(), name, value, new ArrayList<>());
    }

    private final Type type;
    private final String name;
    private Value value;
    private final ArrayList<Visibility> visibilities;

    public Variable(Type type, String name, Value value, ArrayList<Visibility> visibilities) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.visibilities = visibilities;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Value getValue() {
        return value;
    }
    public Value absoluteValue(){
        Value v = value;
        while (v instanceof Variable){
            v = ((Variable) v).absoluteValue();
        }
        return v;
    }

    @Override
    public Value finalExpression() {
        return absoluteValue();
    }
    public void setValueForce(Value value){
        this.value = value;
    }
    public void setValue(Value value, Context context) {
        if(visibilities.contains(Visibility.READONLY)){
            throw new RLException("Cannot modify read only variable", Type.internal(context), context);
        }
        setValueForce(value);
    }

    @Override
    public String toString() {
        return type.toString()+" "+name+" = "+value;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public RLClass getRLClass() {
        return null;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public double floatValue() {
        return value.floatValue();
    }

    @Override
    public boolean booleanValue() {
        return value.booleanValue();
    }


}
