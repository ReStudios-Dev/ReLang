package org.restudios.relang.parser.exceptions;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLStr;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.utils.RLStackTrace;
import org.restudios.relang.parser.utils.RLStackTraceElement;

import java.util.ArrayList;

public class RLException extends RuntimeException {
    private final Type type;
    private final ClassInstance instance;
    private RLStackTrace trace;

    public RLException(String message, Type type, Context context) {
        super(message);
        this.type = type;
        this.trace = context.getTrace();
        instance = null;
    }

    public RLException(Throwable cause, Type type, Context context) {
        super(cause);
        this.type = type;
        this.trace = context.getTrace();
        instance = null;
    }

    public RLException(String message, ClassInstance instance) {
        super(message);
        this.instance = instance;
        type = null;
    }

    public RLException(Throwable cause, ClassInstance instance) {
        super(cause);
        this.instance = instance;
        type = null;
    }

    public RLException(String message, Context context) {
        this(message, Type.clazz(DynamicSLLClass.EXCEPTION, context), context);
    }
    public RLException(String message, String clazz, Context context) {
        this(message, Type.clazz(clazz, context), context);
    }

    public RLStackTrace getTrace() {
        return trace;
    }

    @SuppressWarnings("UnusedReturnValue")
    public RLException setTrace(RLStackTrace trace) {
        this.trace = trace;
        return this;
    }

    public ClassInstance instantiate(Context context){
        if(instance != null) {
            loadTrace(context, instance);
            return instance;
        }
        if(type == null) throw new RuntimeException("Or type or instance must be non null");
        type.init(context);
        type.initClassOrType(context);
        ClassInstance ci = type.clazz.instantiate(context, new ArrayList<>(), new RLStr(getMessage(), context));
        loadTrace(context, ci);
        return ci;
    }
    private void loadTrace(Context context, ClassInstance ci){
        RLArray array = new RLArray(Type.clazz(DynamicSLLClass.STACK_TRACE_ELEMENT, context), context);
        if(trace != null){
            for (RLStackTraceElement element : trace.getElements()) {
                array.add(element.instance(context));
            }
        }
        ci.getVariable("trace").setValueForce(array);
    }
    public Type getType(){
        if(type != null) return type;
        if(instance == null) throw new RuntimeException("Or type or instance must be non null");
        return instance.type();
    }
}
