package org.restudios.relang.parser.ast.types.values.values.sll.classes;

import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.ReFunction;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.SLLClassInstance;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

import java.util.ArrayList;

public class RLRunnable extends SLLClassInstance {
    public final ReFunction value;
    public RLRunnable(ReFunction value, Context parent) {
        super(DynamicSLLClass.RUNNABLE, new ArrayList<>(), parent);
        this.value = value;


    }

    @Override
    public String toString() {
        return "<runnable>";
    }

    @Override
    public int intValue() {
        return -1;
    }

    @Override
    public double floatValue() {
        return -11;
    }

    public ReFunction getFunction(){
        return this.value;
    }


    public Value run(Context context, Context callContext) {
        return value.runMethod(context, callContext);
    }
}
