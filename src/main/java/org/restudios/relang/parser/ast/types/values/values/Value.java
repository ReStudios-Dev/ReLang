package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.TBoolean;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLStr;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

import java.util.List;

public interface Value {
    static Value value(char i){
        return new CharValue(i);
    }
    static Value value(int i){
        return new IntegerValue(i);
    }
    static Value value(float i){
        return new FloatValue(i);
    }
    static Value value(double i){
        return new FloatValue(i);
    }
    static Value value(boolean i){
        return new BooleanValue(i);
    }
    static Value value(TBoolean i){
        return new TBooleanValue(i);
    }
    static Value value(String i, Context c){
        return new RLStr(i, c);
    }
    static Value nullValue(){
        return new NullValue();
    }
    static Value voidValue(){
        return new VoidValue();
    }
    static Value value(Value[] list, Context c){
        Type t = list.length == 0 ? Type.obj(c) : list[0].type();
        RLArray arr = new RLArray(t, c);
        for (Value value : list) {
            arr.add(value);
        }
        return arr;
    }
    static Value value(Context c, Value... list){
        Type t = list.length == 0 ? Type.obj(c) : list[0].type();
        RLArray arr = new RLArray(t, c);
        for (Value value : list) {
            arr.add(value);
        }
        return arr;
    }
    static Value value(Context c, String... list){
        RLArray arr = new RLArray(c.getClass(DynamicSLLClass.STRING).type(), c);
        for (String value : list) {
            arr.add(new RLStr(value, c));
        }
        return arr;
    }

    static Value getRLClass(Context c, String name, List<Type> types, Value... constructorArguments){
        if(!c.containsClass(name)) return new NullValue();
        RLClass clazz = c.getClass(name);
        return clazz.instantiate(c, types, constructorArguments);
    }

    Type type();
    boolean isPrimitive();
    RLClass getRLClass();

    Object value();
    int intValue();
    double floatValue();
    boolean booleanValue();
    default Value finalExpression(){
        return this;
    }

    default Value initContext(Context context){return this;}

    default String stringValue(){return toString();}

}
