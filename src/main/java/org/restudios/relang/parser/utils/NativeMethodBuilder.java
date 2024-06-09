package org.restudios.relang.parser.utils;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.VoidValue;
import org.restudios.relang.parser.ast.types.values.values.sll.SLLMethod;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

import java.util.LinkedHashMap;

@SuppressWarnings("unused")
public class NativeMethodBuilder {
    private final Context context;
    private String name;
    private boolean isStatic = false;
    private LinkedHashMap<String, Type> arguments = new LinkedHashMap<>();
    private NativeMethod.NativeMethodExecution handler = (arguments1, context, callContext, clazz) -> null;

    public NativeMethodBuilder(Context context, String name) {
        if(!context.containsClass(DynamicSLLClass.OBJECT)){
            throw new RuntimeException("Initialization native methods before SLL initialization");
        }
        this.context = context;
        this.name = name;
    }

    /**
     * SLL only
     */
    public NativeMethodBuilder(RLClass clazz, String name) {
        this.context = clazz.getCreatedContext();
        this.name = name;
    }

    public NativeMethodBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public NativeMethodBuilder setStatic(boolean aStatic) {
        isStatic = aStatic;
        return this;
    }

    public NativeMethodBuilder arg(String name){
        return arg(name, Primitives.NULL.type());
    }
    public NativeMethodBuilder arg(String name, Type type){
        arguments.put(name, type);
        return this;
    }
    public NativeMethodBuilder stringArgument(String name){
        return arg(name, Type.clazz(DynamicSLLClass.STRING, this.context));
    }
    public NativeMethodBuilder intArgument(String name){
        return arg(name, Type.primitive(Primitives.INTEGER));
    }
    public NativeMethodBuilder floatArgument(String name){
        return arg(name, Type.primitive(Primitives.FLOAT));
    }
    public NativeMethodBuilder boolArgument(String name){
        return arg(name, Type.primitive(Primitives.BOOL));
    }
    public NativeMethodBuilder tboolArgument(String name){
        return arg(name, Type.primitive(Primitives.TBOOL));
    }
    public NativeMethodBuilder charArgument(String name){
        return arg(name, Type.primitive(Primitives.CHAR));
    }
    public NativeMethodBuilder objectArgument(String name){
        return arg(name, Type.clazz(DynamicSLLClass.OBJECT, context));
    }
    public NativeMethodBuilder classArgument(String name, String className){
        return arg(name, Type.clazz(className, context));
    }

    public NativeMethodBuilder setArguments(LinkedHashMap<String, Type> arguments) {
        this.arguments = arguments;
        return this;
    }

    public NativeMethodBuilder setHandler(NativeMethod.NativeMethodExecution handler) {
        this.handler = handler;
        return this;
    }

    public NativeMethodBuilder setHandler(NativeMethod.VoidNativeMethodExecution handler) {
        this.handler = (arguments1, context, callContext,clazz) -> {
            handler.apply(arguments1, context, callContext,clazz);
            return new VoidValue();
        };
        return this;
    }
    public NativeMethodBuilder setIntHandler(NativeMethod.IntNativeMethodExecution handler) {
        this.handler = (arguments1, context, callContext,clazz) -> Value.value(handler.apply(arguments1, context, callContext,clazz));
        return this;
    }
    public NativeMethodBuilder setFloatHandler(NativeMethod.FloatNativeMethodExecution handler) {
        this.handler = (arguments1, context, callContext,clazz) -> Value.value(handler.apply(arguments1, context, callContext,clazz));
        return this;
    }
    public NativeMethodBuilder setCharHandler(NativeMethod.CharNativeMethodExecution handler) {
        this.handler = (arguments1, context, callContext,clazz) -> Value.value(handler.apply(arguments1, context, callContext,clazz));
        return this;
    }
    public NativeMethodBuilder setBoolHandler(NativeMethod.BoolNativeMethodExecution handler) {
        this.handler = (arguments1, context, callContext,clazz) -> Value.value(handler.apply(arguments1, context, callContext,clazz));
        return this;
    }
    public NativeMethodBuilder setStrHandler(NativeMethod.StrNativeMethodExecution handler) {
        this.handler = (arguments1, context, callContext,clazz) -> Value.value(handler.apply(arguments1, context, callContext,clazz), context);
        return this;
    }


    public NativeMethod build(){
        return new NativeMethod(this.name, this.isStatic, this.arguments, this.handler);
    }
    public SLLMethod buildSLL(String clazz, Context context) {
        return new SLLMethod(this.name, this.isStatic, this.arguments, (arguments, context1, callContext, clazz1) -> handler.apply(arguments, context1, callContext, clazz1), context.getClass(clazz), context);
    }
    public SLLMethod buildSLL(DynamicSLLClass sll) {
        return new SLLMethod(this.name, this.isStatic, this.arguments, (arguments, context1, callContext, clazz1) -> handler.apply(arguments, context1, callContext, clazz1), sll, sll.getCreatedContext());
    }

}
