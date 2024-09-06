package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.modules.ModuleRegistry;
import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.values.EnumItemValue;
import org.restudios.relang.parser.ast.types.values.values.nativing.DynamicNativeClass;
import org.restudios.relang.parser.modules.threading.AThreadManager;
import org.restudios.relang.parser.modules.threading.UnsupportedThreadManager;
import org.restudios.relang.parser.tokens.Token;
import org.restudios.relang.parser.utils.NativeClass;
import org.restudios.relang.parser.utils.RLOutput;
import org.restudios.relang.parser.utils.RLStackTrace;
import org.restudios.relang.parser.utils.RLStackTraceElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Context {
    private Context parent;
    private final ArrayList<Variable> variables;
    private final ArrayList<RLClass> classes;
    private final ArrayList<FunctionMethod> methods;
    private final HashMap<EnumItemValue, RLEnumClass> enumerations;
    private final ArrayList<NativeClass> nativeClasses;
    private RLOutput output;
    private AThreadManager threadManager;
    private RLStackTrace trace = new RLStackTrace();
    private boolean inConstructor;
    private String currentMethod;
    private ModuleRegistry moduleRegistry;
    private RLClass staticCall;

    public Context(Context parent){
        this.parent = parent;
        variables = new ArrayList<>();
        classes = new ArrayList<>();
        methods = new ArrayList<>();
        nativeClasses = new ArrayList<>();
        enumerations = new HashMap<>();
        inConstructor = false;
        if(parent != null) {
            setOutput(parent.getOutput());
            trace = parent.trace.duplicate();
            threadManager = parent.threadManager;
            currentMethod = parent.currentMethod;
            this.moduleRegistry = parent.moduleRegistry;
            staticCall = parent.staticCall;
        }else{
            threadManager = new UnsupportedThreadManager();
            this.moduleRegistry = new ModuleRegistry();
            currentMethod = "<root>";
            staticCall = null;
        }
    }


    public ModuleRegistry getModuleRegistry() {
        return moduleRegistry;
    }

    public boolean isInConstructor() {
        return inConstructor;
    }

    public Context setInConstructor(boolean inConstructor) {
        this.inConstructor = inConstructor;
        return this;
    }

    public AThreadManager getThreadManager() {
        return threadManager;
    }

    public Context setThreadManager(AThreadManager threadManager) {
        this.threadManager = threadManager;
        return this;
    }

    public RLStackTrace getTrace() {
        return trace;
    }

    public ArrayList<NativeClass> getNativeClasses() {
        return nativeClasses;
    }

    public Context getParent() {
        return parent;
    }

    public void setParent(Context parent) {
        this.parent = parent;
    }
    public Variable putVariable(Variable variable) {
        variables.add(variable);
        return variable;
    }
    public boolean containsVariableInThisContext(String name) {
        for (Variable variable : variables) {
            if(variable == null)continue;
            if(variable.getName().equals(name))return true;
        }
        return false;
    }
    public boolean containsVariable(String name) {
        if(containsVariableInThisContext(name))return true;
        if(parent == null)return false;
        return parent.containsVariable(name);
    }
    public void removeVariableInThisContext(String name) {
        variables.removeIf(variable -> variable.getName().equals(name));
    }
    public Variable getVariable(String name) {
        for (Variable variable : variables) {
            if(variable == null)continue;
            if(variable.getName().equals(name))return variable;
        }
        if(parent == null)return null;
        return parent.getVariable(name);
    }


    public RLClass putClass(RLClass clazz) {
        classes.add(clazz);
        return clazz;
    }
    public boolean containsClassInThisContext(String clazz) {
        for (RLClass potentialClass : classes) {
            if(potentialClass.getName().equals(clazz))return true;
        }
        return false;
    }
    public boolean containsClass(String name) {
        if(containsClassInThisContext(name))return true;
        if(parent == null)return false;
        return parent.containsClass(name);
    }
    public void removeClassInThisContext(String name) {
        classes.removeIf(rlClass -> rlClass.getName().equals(name));
    }
    public RLClass getClass(String name) {
        int where = 0;
        DynamicNativeClass what = null;
        RLClass returning = null;
        b:for (int i = 0; i < classes.size(); i++) {
            RLClass potentialClass = classes.get(i);
            if (!(potentialClass instanceof DynamicNativeClass)) {
                for (NativeClass nativeClass : nativeClasses) {
                    if (potentialClass.getName().equals(name) && nativeClass.getName().equals(name)){
                        where = i;
                        what = new DynamicNativeClass(nativeClass, potentialClass);
                        what.loadClassData(this, potentialClass);
                        returning = what;
                        break b;
                    }
                }
            }
            if (potentialClass.getName().equals(name)) {
                returning = potentialClass;
                break;
            }
        }
        if(what != null){
            classes.set(where, what);
        }
        if(returning != null) return returning;
        if(parent == null) return null;
        return parent.getClass(name);
    }

    public void putEnum(EnumItemValue value, RLEnumClass clazz) {
        this.enumerations.put(value, clazz);
    }
    public FunctionMethod putMethod(FunctionMethod clazz) {
        methods.add(clazz);
        return clazz;
    }
    public boolean containsMethodInThisContext(String method) {
        for (FunctionMethod m : methods) {
            if(m.name.equals(method))return true;
        }
        return false;
    }
    public boolean containsMethod(String name) {
        if(containsMethodInThisContext(name))return true;
        if(parent == null)return false;
        return parent.containsMethod(name);
    }
    public void removeMethodInThisContext(String name) {
        methods.removeIf(m -> m.name.equals(name));
    }

    public ArrayList<FunctionMethod> getMethods(String name) {
        ArrayList<FunctionMethod> result = new ArrayList<>();
        for (FunctionMethod method : methods) {
            if(method.name.equals(name)){
                result.add(method);
            }
        }
        if(parent != null){
            try {
                result.addAll(parent.getMethods(name));
            } catch (StackOverflowError ignored) { }
        }
        return result;
    }
    public FunctionMethod getMethod(String name, Type... arguments) {
        gen:for (FunctionMethod methods : methods) {
            if(methods.name.equals(name)){
                if(methods.getArguments().size() != arguments.length) continue;
                for (int i = 0; i < methods.getArgumentTypes().size(); i++) {
                    Type methodArgument = methods.getArgumentTypes().get(i);
                    Type findArgument = arguments[i];
                    if(!findArgument.canBe(methodArgument)){
                        continue gen;
                    }
                }
                return methods;
            }
        }
        if(parent != null){
            try {
                return parent.getMethod(name, arguments);
            } catch (StackOverflowError ignored) { }
        }
        return null;
    }

    public ClassInstance thisClass(){
        if(!containsVariable("this")) return null;
        return (ClassInstance) getVariable("this").absoluteValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Context that = (Context) obj;
        return Objects.equals(hashCode(), that.hashCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables, methods, enumerations, classes);
    }

    @Override
    public String toString() {
        return "Context {variables: "+variables.size() + ", methods: "+methods.size() + ", classes: "+classes.size();
    }

    public String dump() {
        String parent = "";
        if (this.parent != null) {
            parent = this.parent.dump()+" <- ";
        }
        return parent+this;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public ArrayList<RLClass> getClasses() {
        return classes;
    }

    public ArrayList<FunctionMethod> getMethods() {
        return methods;
    }

    public HashMap<EnumItemValue, RLEnumClass> getEnumerations() {
        return enumerations;
    }


    public RLOutput getOutput() {
        if(output == null){
            output = new RLOutput();
        }
        return output;
    }

    public Context setOutput(RLOutput output) {
        this.output = output;
        return this;
    }

    public RLStackTraceElement putTrace(Statement statement) {
        return putTrace(statement, statement.getToken());
    }
    public RLStackTraceElement putTrace(Statement statement, Token token) {
        RLStackTraceElement elem = new RLStackTraceElement(token.source, token.getFrom().getLine(), token.getFrom().getColumn(), token.getPosition(), getCurrentMethod());
        getTrace().getElements().add(elem);
        return elem;
    }
    public String getCurrentMethod(){
        return this.currentMethod;
    }

    public String setCurrentMethod(String currentMethod) {
        String bef = this.currentMethod;
        this.currentMethod = currentMethod;
        return bef;
    }

    public void removeTrace(RLStackTraceElement elem) {
        getTrace().getElements().remove(elem);
    }

    public void setTrace(RLStackTrace trace) {
        this.trace = trace;
    }

    public RLClass setStaticCall(RLClass clazz) {
        RLClass bef = this.staticCall;
        this.staticCall = clazz;
        return bef;
    }

    public RLClass getStaticCall() {
        return staticCall;
    }
}
