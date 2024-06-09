package org.restudios.relang.parser.ast.types.values.values.sll.classes;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.ReFunction;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.SLLClassInstance;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

import java.util.ArrayList;
import java.util.List;

public class RLThread extends SLLClassInstance {
    public RLThread(String name, Context context) {
        super(context.getClass(DynamicSLLClass.THREAD), new ArrayList<>(), context);
        setRunnable(new RLRunnable(ReFunction.emptyVoidFunction(), context));
        setName(name);
    }
    @SuppressWarnings("unused")
    public RLThread(RLRunnable runnable, Context context) {
        super(context.getClass(DynamicSLLClass.THREAD), new ArrayList<>(), context);
        setRunnable(runnable);
    }

    public RLThread(RLClass clazz, List<Type> types, Context parent) {
        super(clazz, types, parent);
    }

    @SuppressWarnings("unused")
    public RLRunnable getRunnable(){
        Value thread = getVariable("thread").getValue().finalExpression();
        return (RLRunnable) thread;
    }
    public void setRunnable(RLRunnable runnable){
        getVariable("thread").setValueForce(runnable);
    }
    public String getName(){
        Value name = getVariable("name").getValue().finalExpression();
        return name.stringValue();
    }
    public void setName(String name){
        getVariable("name").setValueForce(Value.value(name, getContext()));
    }
}
