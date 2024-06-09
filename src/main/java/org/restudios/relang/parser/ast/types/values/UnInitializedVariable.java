package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLStr;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

import java.util.ArrayList;
import java.util.Collections;

public class UnInitializedVariable {
    private final Type type;
    private final String name;

    private final Expression value;
    private final ArrayList<Visibility> visibilities;

    public UnInitializedVariable(Type type, String name, Expression value, ArrayList<Visibility> visibilities) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.visibilities = visibilities;
    }

    public Variable initialize(Context context) {
        Value val = new NullValue();
        if(value != null) {
            val = value.eval(context);
        }
        Variable v = new Variable(type, name, val, visibilities);
        context.putVariable(v);
        return v;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    public ArrayList<Visibility> getVisibilities() {
        return visibilities;
    }

    public Value getClassReflectionVariable(Context context){
        type.init(context);
        type.initClassOrType(context);
        ClassInstance classInstance = context.getClass(DynamicSLLClass.REFL_CLASSVARIABLE).instantiate(context, Collections.emptyList());
        classInstance.getContext().getVariable("visibility").setValueForce(Visibility.getReflectionVisibility(this.visibilities, context));
        classInstance.getContext().getVariable("type").setValueForce(type.getReflectionClass(context));

        classInstance.getContext().getVariable("name").setValueForce(new RLStr(this.name, context));
        return classInstance;
    }
}
