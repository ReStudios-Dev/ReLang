package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.parser.ast.types.ClassType;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.nodes.extra.LoadedAnnotation;
import org.restudios.relang.parser.ast.types.nodes.statements.ClassDeclarationStatement;
import org.restudios.relang.parser.ast.types.values.values.*;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLStr;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;

import java.util.*;
import java.util.stream.Collectors;

public class RLClass implements Instantiable<ClassInstance>, Value {
    private final String name;
    private ArrayList<CustomTypeValue> subTypes;
    private RLClass extending;
    private ArrayList<RLClass> implementing;
    private final ArrayList<Visibility> visibility;
    private ArrayList<FunctionMethod> methods;
    private ArrayList<ConstructorMethod> constructors;
    private ArrayList<FunctionMethod> operatorsOverloading;
    private ArrayList<RLClass> subClasses;
    private List<LoadedAnnotation> annotations = new ArrayList<>();
    private ArrayList<UnInitializedVariable> variables;
    private Context statics;
    private Context createdContext;
    private boolean initialized = false;
    private boolean loaded = false;
    private final ClassType classType;
    public RLClass(String name, ClassType classType, ArrayList<Visibility> visibility){
        this.name = name;
        this.classType = classType;
        this.visibility = visibility;
    }

    public RLClass(Context context, String name, ClassType classType, ArrayList<CustomTypeValue> subTypes, RLClass extending, ArrayList<RLClass> implementing, ArrayList<Visibility> visibility, ArrayList<FunctionMethod> methods, ArrayList<ConstructorMethod> constructors, ArrayList<RLClass> subClasses, ArrayList<UnInitializedVariable> variables, ArrayList<FunctionMethod> operatorsOverloading) {
        this(name, classType, visibility);
        loadClassData(context, subTypes, extending, implementing, methods, constructors, subClasses, variables, operatorsOverloading);
    }
    public void loadClassData(Context context, ArrayList<CustomTypeValue> subTypes, RLClass extending, ArrayList<RLClass> implementing, ArrayList<FunctionMethod> methods, ArrayList<ConstructorMethod> constructors, ArrayList<RLClass> subClasses, ArrayList<UnInitializedVariable> variables, ArrayList<FunctionMethod> operatorsOverloading) {
        if(loaded) return;
        this.loaded = true;
        this.subTypes = subTypes;
        this.extending = extending;
        this.implementing = implementing;
        this.methods = methods;
        this.constructors = constructors;
        this.subClasses = subClasses;
        this.variables = variables;
        this.operatorsOverloading = operatorsOverloading;
        createdContext = context;
        statics = new Context(context);
        for (CustomTypeValue subType : subTypes) {
            createdContext.putVariable(new Variable(Type.primitive(Primitives.TYPE), subType.name, new NullValue(), new ArrayList<>(Arrays.asList(Visibility.PRIVATE, Visibility.READONLY))));
        }
    }
    public void loadClassData(Context context, RLClass original) {
        loadClassData(context, original.subTypes, original.extending, original.implementing, original.methods, original.constructors, original.subClasses, original.variables, original.operatorsOverloading);
    }
    public void loadClassData(Context context){
        if(statement != null && !loaded){
            statement.doLoad(this, context);
        }
    }


    public RLClass getSuperClass(){
        return extending;
    }
    @SuppressWarnings("unused")
    public ArrayList<RLClass> getSuperInterfaces(){
        return implementing;
    }

    @Override
    public String toString() {
        return "class "+name;
    }

    public boolean isEnum(){
        return classType == ClassType.ENUM;
    }
    public boolean isAbstract(){
        return classType == ClassType.ABSTRACT;
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isBase(){
        return classType == ClassType.CLASS;
    }
    public boolean isInterface(){
        return classType == ClassType.INTERFACE;
    }
    @SuppressWarnings("unused")
    public ArrayList<FunctionMethod> getOverrideMethods() {
        ArrayList<FunctionMethod> result = methods;
        result.removeIf(functionMethod -> functionMethod.visibility.contains(Visibility.OVERRIDE));
        return result;
    }

    public void createdChild(ClassInstance child) {
        if(extending != null){
            extending.createdChild(child);
        }
        for (RLClass rlClass : implementing) {
            rlClass.createdChild(child);
        }
    }
    public FunctionMethod findMethod(String name, List<Visibility> visibility, Type returning, ArrayList<Type> arguments, boolean haveToBeOverride){
        gen:for (FunctionMethod method : methods) {
            if(method.name.equals(name)){
                if(method.getReturnType().like(returning)){
                    if(method.getArguments().size() != arguments.size()) continue;
                    for (int i = 0; i < method.getArguments().size(); i++) {
                        FunctionArgument fa = method.getArguments().get(i);
                        Type ar = arguments.get(i);
                        if(!fa.type.like(ar)){
                            continue gen;
                        }
                    }
                    if(!method.visibility.equals(visibility)) {

                        List<Visibility> nw = new ArrayList<>(method.visibility);
                        nw.remove(Visibility.OVERRIDE);
                        if(!nw.equals(visibility)) continue;
                    }
                    if(haveToBeOverride && !method.visibility.contains(Visibility.OVERRIDE))continue;
                    return method;

                }
            }
        }
        return null;
    }
    public static ArrayList<FunctionMethod> getOnlyAbstractMethods(ArrayList<FunctionMethod> me){
        ArrayList<FunctionMethod> methods = new ArrayList<>();
        for (FunctionMethod method : me) {
            if(method.isAbstract && !method.visibility.contains(Visibility.STATIC)){
                methods.add(method);
            }
        }
        return methods;
    }
    public ArrayList<FunctionMethod> getMethodsNeedOverride(){
        ArrayList<FunctionMethod> result = new ArrayList<>();
        if(extending != null) {
            if(extending.isAbstract()){
                result.addAll(extending.getNotImplementedAbstractMethods());
            }
            result.addAll(getOnlyAbstractMethods(extending.methods));
        }
        for (RLClass rlClass : implementing) {
            result.addAll(getOnlyAbstractMethods(rlClass.methods));
        }

        return result;
    }
    public ArrayList<FunctionMethod> getOverridableMethods(boolean includeThis){
        ArrayList<FunctionMethod> result = new ArrayList<>();
        if(extending != null) {
            result.addAll(extending.getOverridableMethods(true));
        }
        for (RLClass rlClass : implementing) {
            result.addAll(getOnlyAbstractMethods(rlClass.methods));
        }
        if(includeThis){
            for (FunctionMethod method : methods) {
                if(!method.visibility.contains(Visibility.STATIC)){
                    result.add(method);
                }
            }
        }
        return result;
    }
    public ArrayList<FunctionMethod> getNotImplementedAbstractMethods(){
        ArrayList<FunctionMethod> result = new ArrayList<>();
        for (FunctionMethod method : getMethodsNeedOverride()) {
            if(findMethod(method.name, method.visibility, method.getReturnType(), method.getArgumentTypes(), true) == null){
                result.add(method);
            }
        }
        return result;
    }
    @SuppressWarnings("unused")
    public ArrayList<FunctionMethod> getAbstractMethods(){
        ArrayList<FunctionMethod> result = new ArrayList<>();
        for (FunctionMethod method : methods) {
            if(method.isAbstract){
                result.add(method);
            }
        }
        return result;
    }

    public ArrayList<FunctionMethod> getAllMethods(boolean includeThis, boolean implementedOnly, boolean allowStatic){
        ArrayList<FunctionMethod> result = new ArrayList<>();

        if(extending != null){
            for (FunctionMethod parentMethod : extending.getAllMethods(true, implementedOnly, allowStatic)) {

                if(!allowStatic && parentMethod.visibility.contains(Visibility.STATIC))continue;
                if(result.contains(parentMethod))continue;
                if(implementedOnly){
                    if(!parentMethod.isAbstract || parentMethod.isNative) {
                        result.add(parentMethod);
                    }
                }else{
                    result.add(parentMethod);
                }
            }
        }
        if(!implementedOnly){
            for (RLClass rlClass : implementing) {
                for (FunctionMethod parentMethod : rlClass.getAllMethods(true, false, allowStatic)) {
                    if(!allowStatic && parentMethod.visibility.contains(Visibility.STATIC))continue;
                    if(result.contains(parentMethod))continue;
                    result.add(parentMethod);
                }
            }
        }
        for (FunctionMethod method : methods) {
            tryToAdd(method, allowStatic, result, createdContext);

        }
        return result;
    }
    public static ArrayList<FunctionMethod> tryToAdd(FunctionMethod method, boolean allowStatic, ArrayList<FunctionMethod> result, Context context){
        if(!allowStatic && method.visibility.contains(Visibility.STATIC)) return result;
        if(method.visibility.contains(Visibility.OVERRIDE) || method.isNative){
            List<FunctionMethod> remove = new ArrayList<>();
            lst:for (FunctionMethod functionMethod : result) {
                if (method.name.equals(functionMethod.name)) {
                    ArrayList<Type> fm = functionMethod.getArgumentTypes();
                    ArrayList<Type> fs = method.getArgumentTypes();
                    if (fm.size() != fs.size()) continue;
                    for (int i = 0; i < fm.size(); i++) {
                        fm.get(i).init(context);
                        fm.get(i).initClassOrType(context);
                        fs.get(i).init(context);
                        fs.get(i).initClassOrType(context);
                        if (!fm.get(i).like(fs.get(i))) continue lst;
                    }
                    if (functionMethod.visibility.contains(Visibility.FINAL)) {
                        throw new RLException("Could not to override final method", Type.internal(context), context);
                    }
                    //if (!replaceOverridden) return result;
                    remove.add(functionMethod);
                    break;
                }
            }
            result.removeAll(remove);
        }
        result.remove(method);
        result.add(method);
        return result;
    }


    public boolean isAssignableFrom(RLClass clazz){
        if(clazz == null)return false;
        if(clazz.equals(this)) return true;
        if(extending != null){
            if(extending.equals(clazz))return true;
            if(extending.isAssignableFrom(clazz))return true;
        }
        for (RLClass rlClass : implementing) {
            if(rlClass.isAssignableFrom(clazz))return true;
        }
        return false;
    }
    public void validate(Context context){
        if(extending != null){
            if(!extending.isInterface() && isInterface()){
                throw new RLException("Interface can extend interface only", Type.internal(context), context);
            }
            if(!(isInterface() && extending.isInterface()) && !extending.isAbstract() && !extending.isBase()){
                throw new RLException("Cannot extend "+extending.classType.name().toLowerCase()+" class", Type.internal(context), context);
            }
            if(extending.visibility.contains(Visibility.FINAL)){
                throw new RLException("Cannot extend final class", Type.internal(context), context);
            }
        }
        if(isInterface() && !implementing.isEmpty()){
            throw new RLException("Interface cannot implement other classes", Type.internal(context), context);
        }
        for (RLClass rlClass : implementing) {
            if(!rlClass.isInterface()){
                throw new RLException("Classes can implement interfaces only", Type.internal(context), context);
            }
        }
        if(!isAbstract()){
            ArrayList<String> names = new ArrayList<>();
            for (FunctionMethod method : getNotImplementedAbstractMethods()) {
                names.add(method.name);
            }
            if(!names.isEmpty()){
                try {

                    throw new RLException("Unimplemented methods: "+names, Type.internal(context), context);
                }catch (Exception e){
                    throw new RuntimeException("Unimplemented methods: "+names+" ["+this.name+"]");
                }
            }
        }
        ArrayList<FunctionMethod> fa = getOverridableMethods(false);
        for (FunctionMethod method : methods) {

            for (FunctionMethod functionMethod : methods) {
                if(method == functionMethod) continue;
                if(method.same(functionMethod)){
                    throw new RLException("Method "+functionMethod.name+" already exists", Type.internal(context), context);
                }
            }

            if(method.visibility.contains(Visibility.OVERRIDE)){
                boolean f = false;
                for (FunctionMethod functionMethod : fa) {
                    if(functionMethod.checkOverridden(method)){
                        if(functionMethod.visibility.contains(Visibility.FINAL)){
                            throw new RLException("Method " + functionMethod.name +" is final, and cannot be overridden", Type.internal(context), context);
                        }
                        f = true;
                        break;
                    }
                }
                if (!f){
                    throw new RLException("Could not find method " + method.name+" to override", Type.internal(context), context);
                }

            }

            if(method.visibility.contains(Visibility.OVERRIDE) && method.visibility.contains(Visibility.FINAL)){
                throw new RLException("Methods cannot be overridden and final", Type.internal(context), context);
            }
        }
        for (UnInitializedVariable variable : variables) {
            if(variable.getVisibilities().contains(Visibility.OVERRIDE) ){
                throw new RLException("Variables cannot be overridden", Type.internal(context), context);
            }
            if(variable.getVisibilities().contains(Visibility.FINAL)) {
                throw new RLException("Variables cannot be final. Maybe you mean \"readonly\"?", Type.internal(context), context);
            }
        }
        for (FunctionMethod functionMethod : operatorsOverloading) {
            if(functionMethod instanceof CastOperatorOverloadFunctionMethod){
                CastOperatorOverloadFunctionMethod overload = (CastOperatorOverloadFunctionMethod)functionMethod;
                if(overload.getArguments().size() != 1){
                    throw new RLException("Operator overload error: cast operator receives only one argument", Type.internal(context), context);
                }
                if(overload.implicit){
                    Type ret = overload.getReturnType();
                    ret.init(statics);
                    if(ret.isPrimitive){
                        throw new RLException("Operator overload error: cannot return primitive type in implicit operator", Type.internal(context), context);
                    }
                    if(!ret.like(Type.clazz(this))){
                        throw new RLException("Operator overload error: cannot return other class in implicit operator", Type.internal(context), context);
                    }
                }
            }else{
                if(functionMethod.getArguments().size() != 2){
                    throw new RLException("Operator overload error: Binary override operator takes only two type arguments", Type.internal(context), context);
                }
                 
            }
        }
    }
    public void initializeStaticContext(){
        if(!initialized){
            initialized = true;
            for (FunctionMethod method : methods) {
                if(method.visibility.contains(Visibility.STATIC)){
                    statics.putMethod(method);
                }
            }
            for (UnInitializedVariable variable : variables) {
                if(variable.getVisibilities().contains(Visibility.STATIC)){
                    statics.putVariable(variable.initialize(statics));
                }
            }
            for (RLClass clazz : subClasses) {
                if(clazz.visibility.contains(Visibility.STATIC)){
                    statics.putClass(clazz);
                }
            }
        }
    }


    public CastOperatorOverloadFunctionMethod findExplicitOperator(Type to){
        for (FunctionMethod functionMethod : operatorsOverloading) {
            if(functionMethod instanceof CastOperatorOverloadFunctionMethod){
                CastOperatorOverloadFunctionMethod overload = (CastOperatorOverloadFunctionMethod)functionMethod;
                if (!overload.implicit){
                    overload.to.init(statics);
                    overload.from.type.init(statics);
                    if(overload.to.like(to))return overload;
                }
            }
        }
        if(extending != null){
            return extending.findExplicitOperator(to);
        }
        return null;
    }
    public CastOperatorOverloadFunctionMethod findImplicitOperator(Type to){
        for (FunctionMethod functionMethod : operatorsOverloading) {
            if(functionMethod instanceof CastOperatorOverloadFunctionMethod){
                CastOperatorOverloadFunctionMethod overload = (CastOperatorOverloadFunctionMethod)functionMethod;
                if (overload.implicit){
                    overload.to.init(statics);
                    overload.from.type.init(statics);
                    if(overload.from.type.like(to))return overload;
                }
            }
        }
        return null;
    }
    public FunctionMethod findBinaryOperator(String operator, Type left, Type right) {
        for (FunctionMethod functionMethod : operatorsOverloading) {
            if(!(functionMethod instanceof CastOperatorOverloadFunctionMethod)){
                if(functionMethod.name.equals(operator)){
                    functionMethod.getReturnType().init(statics);
                    for (FunctionArgument argument : functionMethod.getArguments()) {
                        argument.type.init(statics);
                    }
                    if (functionMethod.getArguments().get(0).type.canBe(left)) {
                        if (functionMethod.getArguments().get(1).type.canBe(right)) {
                            return functionMethod;
                        }
                    }
                }
            }
        }
        return null;
    }
    public FunctionMethod findBinaryOperator(String operator, RLClass left, RLClass right) {
        return findBinaryOperator(operator, Type.clazz(left), Type.clazz(right));
    }
    private FunctionMethod findMethod(String name, LinkedHashMap<String, Type> arguments, ArrayList<FunctionMethod> methods, Context ci){
        mfs:for (FunctionMethod method : methods) {
            if(name.equals(method.name)){
                ArrayList<Type> types = new ArrayList<>(arguments.values());
                if(arguments.size() != method.getArguments().size()) {
                    continue;
                }
                for (int i = 0; i < method.getArguments().size(); i++) {
                    FunctionArgument fa = method.getArguments().get(i);
                    fa.type.init(ci);
                    fa.type.initClassOrType(ci);
                    types.get(i).init(ci);
                    types.get(i).initClassOrType(ci);
                    if(!types.get(i).canBe(fa.type)){
                        continue mfs;
                    }
                }
                return method;

            }
        }
        return null;
    }
    public FunctionMethod findRawMethod(String name, LinkedHashMap<String, Type> arguments, Context context) {
        FunctionMethod fm = findMethod(name, arguments, methods, context);
        if(fm != null) return fm;
        fm = findMethod(name, arguments, getAllMethods(true, false, false), context);
        if(fm != null) return fm;
        throw new RLException("Method not found: " + name, Type.internal(context), context);
    }

    @Override
    public ClassInstance instantiate(Context context, List<Type> types, Value... constructorArguments) {
        initializeStaticContext();
        ClassInstance instance = new ClassInstance(this, types, context);
        createdChild(instance);
        String cmethod = context.getCurrentMethod();
        context.setCurrentMethod("<init>");
        callConstructor(context, instance.getContext(), constructorArguments);
        context.setCurrentMethod(cmethod);
        return instance;
    }

    public FunctionMethod callConstructor(Context context, Context constructContext, Value... values) {
        if(constructors.isEmpty() && values.length == 0) {
            return null;
        }
        for (FunctionMethod constructor : constructors) {
            List<FunctionArgument> fa = constructor.getArguments();
            if (fa.size() != values.length) continue;
            Value[] casted = new Value[fa.size()];
            for (int i = 0; i < fa.size(); i++) {
                FunctionArgument f = fa.get(i);
                casted[i] = CastExpression.cast(f.type, values[i], context);
            }
            constructor.runMethod(constructContext, context, casted);
            return constructor;
        }
        throw new RLException("Could not find constructor with received arguments", Type.internal(context), context);
    }

    public ArrayList<Type> subTypes(){
        ArrayList<Type> result = new ArrayList<>();
        for (CustomTypeValue subType : subTypes) {
            result.add(subType.value);
        }
        return result;
    }

    @Override
    public Type type() {
        return new Type(null, subTypes(), this);
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public RLClass getRLClass() {
        return this;
    }

    @Override
    public Object value() {
        return this;
    }

    @Override
    public int intValue() {
        return -1;
    }

    @Override
    public double floatValue() {
        return -1;
    }

    @Override
    public boolean booleanValue() {
        return false;
    }
    public ArrayList<FunctionMethod> getStaticMethods(){
        ArrayList<FunctionMethod> staticMethods = new ArrayList<>();
        for (FunctionMethod method : methods) {
            if(method.visibility.contains(Visibility.STATIC)){
                staticMethods.add(method);
            }
        }
        return staticMethods;
    }

    public FunctionMethod findStaticMethodFromNameAndArguments(String name, Value[] values, Context from) {
        initializeStaticContext();
        ArrayList<FunctionMethod> staticMethods = getStaticMethods();
        return ClassInstance.findMethodFromNameAndArguments(name, values, staticMethods, from, null);
    }

    public ArrayList<UnInitializedVariable> getAllVariables(boolean includeThis) {
        ArrayList<UnInitializedVariable> result = new ArrayList<>();

        if(extending != null){
            result.addAll(extending.getAllVariables(true));
        }
        if(includeThis){
            result.addAll(variables);
        }

        return result;
    }
    @SuppressWarnings("unused")
    public String dump() {
        String res;
        res = name+"["+String.join(", ", implementing.stream().map(RLClass::dump).collect(Collectors.toCollection(ArrayList::new)))+"]";
        if(extending != null){
            res += " -> "+extending.dump();
        }
        return res;
    }

    public boolean check(RLClass clazz) {
        return Objects.equals(this, clazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RLClass rlClass = (RLClass) o;

        if (!name.equals(rlClass.name)) return false;
        return classType == rlClass.classType;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + classType.hashCode();
        return result;
    }


    public ClassInstance getReflectionClass(Context context) {
        ClassInstance classInstance = context.getClass(DynamicSLLClass.REFL_CLASS).instantiate(context, new ArrayList<>());
        classInstance.getContext().getVariable("name").setValueForce(new RLStr(this.name, context));
        classInstance.getContext().getVariable("visibility").setValueForce(Visibility.getReflectionVisibility(this.visibility, context));
        classInstance.getContext().getVariable("type").setValueForce(getReflectionType(context));
        classInstance.getContext().getVariable("variables").setValueForce(getReflectionVariables(context));
        classInstance.getContext().getVariable("methods").setValueForce(getReflectionMethods(context));
        return classInstance;
    }
    public Value getReflectionMethods(Context context){
        RLArray array = new RLArray(Type.clazz(DynamicSLLClass.REFL_CLASSMETHOD, context), context);
        for (FunctionMethod parentMethod : this.getAllMethods(true, true, true)) {
            array.add(parentMethod.getReflectionClass(context));
        }
        return array;
    }
    public Value getReflectionVariables(Context context){
        RLArray array = new RLArray(Type.clazz(DynamicSLLClass.REFL_CLASSVARIABLE, context), context);
        for (UnInitializedVariable variable : this.variables) {
            array.add(variable.getClassReflectionVariable(context));
        }
        return array;
    }

    public Value getReflectionType(Context context){
        RLEnumClass e = ((RLEnumClass) context.getClass(DynamicSLLClass.REFL_CLASSTYPE));
        e.initializeStaticContext();
        for (EnumItemValue value : e.values) {
            if(value.name().equalsIgnoreCase(classType.name())){
                return e.instantiateEnumeration(context, value);
            }
        }
        return new NullValue();
    }

    public ArrayList<CustomTypeValue> getSubTypes() {
        return subTypes;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Visibility> getVisibility() {
        return visibility;
    }

    public ArrayList<RLClass> getSubClasses() {
        return subClasses;
    }

    public ArrayList<UnInitializedVariable> getVariables() {
        return variables;
    }

    public Context getStaticContext() {
        return statics;
    }

    public Context getCreatedContext() {
        return createdContext;
    }

    public ClassType getClassType() {
        return classType;
    }

    public ArrayList<FunctionMethod> getAllDeclaredMethods() {
        return methods;
    }
    @SuppressWarnings("unused")
    public ArrayList<ConstructorMethod> getConstructors() {
        return constructors;
    }
    @SuppressWarnings("unused")
    public ArrayList<FunctionMethod> getOperatorsOverloading() {
        return operatorsOverloading;
    }

    ClassDeclarationStatement statement;
    public void setDeclarationStatement(ClassDeclarationStatement classDeclarationStatement) {
        statement = classDeclarationStatement;
    }

    public List<LoadedAnnotation> getAnnotations() {
        return annotations;
    }
}
