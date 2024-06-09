package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.parser.ast.types.ClassType;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.values.values.CustomTypeValue;
import org.restudios.relang.parser.ast.types.values.values.ConstructorMethod;
import org.restudios.relang.parser.ast.types.values.values.EnumItemValue;
import org.restudios.relang.parser.ast.types.values.values.Value;

import java.util.ArrayList;

public class RLEnumClass extends RLClass {
    public final ArrayList<EnumItemValue> values;
    private boolean initialized = false;

    public RLEnumClass(String name, ClassType classType, ArrayList<Visibility> visibility, ArrayList<EnumItemValue> values) {
        super(name, classType, visibility);
        this.values = values;
    }

    public RLEnumClass(Context context, String name, ClassType classType, ArrayList<CustomTypeValue> subTypes, RLClass extending, ArrayList<RLClass> implementing, ArrayList<Visibility> visibility, ArrayList<FunctionMethod> methods, ArrayList<ConstructorMethod> constructors, ArrayList<RLClass> subClasses, ArrayList<UnInitializedVariable> variables, ArrayList<FunctionMethod> operatorsOverloading, ArrayList<EnumItemValue> values) {
        super(context, name, classType, subTypes, extending, implementing, visibility, methods, constructors, subClasses, variables, operatorsOverloading);
        this.values = values;
    }

    @Override
    public void initStatic() {
        super.initStatic();
        if(!initialized){
            initialized = true;
            for (EnumItemValue value : values) {
                value.init(this);
                getStaticContext().putEnum(value, this);
            }
        }
    }
    @SuppressWarnings("unused")
    public int ordinal(EnumItemValue item){
        return this.values.indexOf(item);
    }

    public EnumClassInstance instantiateEnumeration(Context context, EnumItemValue value, Value... values) {
        initStatic();
        EnumClassInstance instance = new EnumClassInstance(this, context, value);
        createdChild(instance);
        callConstructor(context, instance.getContext(), values);
        return instance;
    }
}
