package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.values.*;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLRunnable;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.utils.NativeMethod;
import org.restudios.relang.parser.utils.NativeMethodBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class ClassInstance implements Instance {
    private RLClass clazz;
    private final Context context;

    private final ArrayList<FunctionMethod> methods = new ArrayList<>();
    private final ArrayList<CustomTypeValue> subTypes = new ArrayList<>();
    private final Map<String, Object> cache = new HashMap<>();

    public ClassInstance(String sll, List<Type> types, Context parent){
        this.clazz = parent.getClass(sll);
        if(clazz == null){
            throw new RuntimeException("Standard Language Library not found.");
        }
        this.context = new Context(parent);
        putThisInto(context);
        this.subTypes.addAll(this.clazz.getSubTypes().stream().map(CustomTypeValue::clone).collect(Collectors.toList()));

        applyTypes(types);
        clazz.initializeStaticContext();
        clazz.createdChild(this);
    }
    public ClassInstance(RLClass clazz, List<Type> types, Context parent) {
        this.clazz = clazz;
        this.context = new Context(parent);
        putThisInto(context);
        this.subTypes.addAll(this.clazz.getSubTypes().stream().map(CustomTypeValue::clone).collect(Collectors.toList()));

        applyTypes(types);
        clazz.initializeStaticContext();
        methods.addAll(clazz.getAllMethods(false, true, false));
        for (FunctionMethod method : clazz.getAllDeclaredMethods()) {
            if(method.visibility.contains(Visibility.STATIC))continue;
            methods.add(method);
        }
        for (FunctionMethod method : methods) {
            context.putMethod(method);
        }
        for (UnInitializedVariable variable : clazz.getAllVariables(true)) {
            if(variable.getVisibilities().contains(Visibility.STATIC))continue;
            context.putVariable(variable.initialize(context));
        }
        for (RLClass subClass : clazz.getSubClasses()) {
            if(subClass.getVisibility().contains(Visibility.STATIC))continue;
            context.putClass(subClass);
        }
        methods.forEach(functionMethod -> functionMethod.getCustomTypes().addAll(subTypes));

    }

    public Context getContext(){
        return context;
    }
    public void applyTypes(List<Type> types){
        if(this.subTypes.size() != types.size() && !types.isEmpty()){
            throw new RLException("Invalid sub types size", Type.internal(context), context);
        }
        for (int i = 0; i < this.subTypes.size(); i++) {
            CustomTypeValue cst = this.subTypes.get(i);
            Type t = types.isEmpty() ? context.getClass(DynamicSLLClass.OBJECT).type() : types.get(i);
            cst.setValue(t);
        }
    }

    public void putThisInto(Context context){
        context.putVariable(new Variable(
                Type.clazz(clazz),
                "this",
                this,
                new ArrayList<>(Arrays.asList(Visibility.PRIVATE, Visibility.READONLY))
        ));
    }


    @Override
    public Type type() {
        Type t = clazz.type().clone();
        t.subTypes.clear();
        t.subTypes.addAll(subTypes.stream().map(customTypeValue -> customTypeValue.value).collect(Collectors.toList()));
        return t;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public RLClass getRLClass() {
        return clazz;
    }

    @Override
    public Object value() {
        return clazz;
    }

    @Override
    public int intValue() {
        return 1;
    }

    @Override
    public double floatValue() {
        return 1;
    }

    @Override
    public boolean booleanValue() {
        return false;
    }

    public ClassInstance cast(Type castToType, Context context) {
        if(castToType.isCustomType()){
            castToType.init(context);
            clazz = castToType.clazz;
        }
        return this;
    }

    @Override
    public String toString() {
        return "ClassInstance{" +
                "clazz=" + clazz +
                ", context=" + context +
                '}';
    }

    public ArrayList<FunctionMethod> getAllMethods(boolean includeThis, boolean implementedOnly, boolean allowStatic){
        ArrayList<FunctionMethod> fms = getRLClass().getAllMethods(includeThis, implementedOnly, allowStatic);
        ArrayList<FunctionMethod> toRemove = new ArrayList<>();
        for (FunctionMethod method : methods) {
            for (FunctionMethod fm : fms) {
                if(method.checkForArguments(fm)){
                    toRemove.add(fm);
                }
            }
        }
        for (FunctionMethod functionMethod : toRemove) {
            fms.remove(functionMethod);
        }
        fms.addAll(methods);
        return fms;
    }

    public FunctionMethod findMethodFromNameAndArguments(Context context, String name, Value... values) {
        return findMethodFromNameAndArguments(name, values, getAllMethods(true, true, false), context, this);
    }
    public static FunctionMethod findMethodFromNameAndArguments(String name, Value[] values, ArrayList<FunctionMethod> methods, Context context, ClassInstance instance) {
        ArrayList<Type> types = Arrays.stream(values).map(value -> value.finalExpression().type()).collect(Collectors.toCollection(ArrayList::new));
        lst:for (FunctionMethod method : methods) {
            if(method.name.equals(name)) {
                if (method.getArgumentTypes().size()==types.size() || method.getArguments().get(method.getArguments().size()-1).isVarArg()) {
                    List<Type> argumentTypes = method.getArgumentTypes();
                    boolean varArg = !method.getArguments().isEmpty() && method.getArguments().get(method.getArguments().size() - 1).varArg;
                    Type varArgType = varArg ? argumentTypes.get(argumentTypes.size()-1) : null;
                    int from = argumentTypes.size() - 1;
                    for (int i = 0; i < types.size(); i++) {
                        boolean mustBeVarArg = varArg && i >= from;
                        Type in = types.get(i);
                        Type out = mustBeVarArg ? varArgType : method.getArgumentTypes().get(i);
                        if(instance != null)out.init(instance.context);
                        else out.init(context);
                        if(out.primitive != Primitives.NULL && !in.canBe(out)) {
                            continue  lst;
                        }
                    }
                    return method;
                }
            }
        }
        return null;
    }
    public static Type findMethodFromNameAndArguments(String name, List<Type> types, ArrayList<FunctionMethod> methods, Type inherit, AnalyzerContext context) {
        lst:for (FunctionMethod method : methods) {
            if(method.name.equals(name)) {
                if (method.getArgumentTypes().size()==types.size() || method.getArguments().get(method.getArguments().size()-1).isVarArg()) {
                    List<Type> argumentTypes = method.getArgumentTypes();
                    boolean varArg = !method.getArguments().isEmpty() && method.getArguments().get(method.getArguments().size() - 1).varArg;
                    Type varArgType = varArg ? argumentTypes.get(argumentTypes.size()-1) : null;
                    int from = argumentTypes.size() - 1;
                    for (int i = 0; i < types.size(); i++) {
                        boolean mustBeVarArg = varArg && i >= from;
                        Type in = types.get(i);
                        Type out = mustBeVarArg ? varArgType : method.getArgumentTypes().get(i);
                        if(inherit != null){
                            out.initClassOrType(context, inherit);
                        }else{
                            out.initClassOrType(context);
                        }
                        in.initClassOrType(context);
                        if(out.primitive != Primitives.NULL && !in.canBe(out)) {
                            continue  lst;
                        }
                    }
                    return method.getReturnType();
                }
            }
        }
        return null;
    }

    @Override
    public String stringValue() {
        CastOperatorOverloadFunctionMethod d = clazz.findExplicitOperator(Type.clazz(context.getClass(DynamicSLLClass.STRING)));
        if(clazz.getName().equals(DynamicSLLClass.STRING)){
            return toString();
        }
        return d.runMethod(context, getContext(),this).finalExpression().stringValue();
    }

    public boolean isExceptionClass(){
        return type().canBe(Type.clazz(DynamicSLLClass.EXCEPTION, context));
    }

    public void tryToPrintException() {
        if(isExceptionClass()){
            findMethodFromNameAndArguments(context,"print").runMethod(context, context);
        }
    }

    public ArrayList<CustomTypeValue> getSubTypes() {
        return subTypes;
    }

    public ArrayList<FunctionMethod> getAvailableMethods() {
        return methods;
    }

    public Variable getVariable(String name) {
        return getContext().getVariable(name);
    }

    public Map<String, Object> getCache(){
        return cache;
    }

    @Override
    public Object convertNative() {
        return this;
    }

    public boolean isRunnable() {
        return this instanceof RLRunnable;
    }

    public void implementMethod(FunctionMethod original, FunctionMethod m) {
        while (methods.contains(original)) methods.remove(original);
        methods.add(m);
    }
}
