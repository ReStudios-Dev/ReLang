package org.restudios.relang.parser.analyzer;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.MethodDeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.VariableDeclarationStatement;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzerContext {
    private Context root;
    private AnalyzerContext parent;
    public Map<String, Type> variables = new HashMap<>();
    public List<MethodDeclarationStatement> methods = new ArrayList<>();
    public List<RLClass> classes = new ArrayList<>();
    public RLClass handlingClass;
    private Type mustReturn;

    public AnalyzerContext(AnalyzerContext parent, Context root) {
        this.parent = parent;
        this.root = root;
        this.handlingClass = parent.handlingClass;
    }

    public AnalyzerContext(Context root) {
        this.root = root;
        classes.addAll(root.getClasses());
        for (RLClass aClass : classes) {
            if(aClass.getDeclaration() != null && !aClass.isLoaded()){
                aClass.getDeclaration().preload(aClass, root);
            }
        }
    }

    public Type getMustReturn() {
        if(mustReturn == null && parent != null) return parent.getMustReturn();
        return mustReturn;
    }

    public Type setMustReturn(Type inside) {
        Type before = this.mustReturn;
        this.mustReturn = inside;
        return before;
    }

    public RLClass getHandlingClass() {
        return handlingClass;
    }

    public AnalyzerContext setHandlingClass(RLClass handlingClass) {
        this.handlingClass = handlingClass;
        return this;
    }

    public Type getVariable(String key) {
        if(variables.containsKey(key)) return variables.get(key);
        if(parent == null) return null;
        return parent.getVariable(key);
    }

    public Type putVariable(String key, Type value) {
        return variables.put(key, value);
    }

    public RLClass getClass(String key){
        for (RLClass aClass : this.classes) {
            if(aClass.getName().equals(key))return aClass;
        }
        if(parent != null) return parent.getClass(key);
        return null;
    }
    public Type getMethod(String key, List<Type> params) {
        gen: for (MethodDeclarationStatement method : methods) {
            if(method.name.equals(key)){
                if(method.arguments.size() != params.size()) continue;
                for (int i = 0; i < method.arguments.size(); i++) {
                    Type needed = method.arguments.get(i).type;
                    Type provided = params.get(i);
                    if(!provided.canBe(needed)){
                        continue gen;
                    }
                }
                return method.returning;
            }
        }
        if(parent == null) return null;
        return parent.getMethod(key, params);
    }

    public void putMethod(MethodDeclarationStatement mds) {
        methods.add(mds);
    }

    public AnalyzerContext create() {
        return new AnalyzerContext(this, root);
    }

    public void putVariable(VariableDeclarationStatement argument) {
        putVariable(argument.variable, argument.type);
    }

    public boolean containsClass(String string) {
        for (RLClass aClass : classes) {
            if (aClass.getName().equals(string)) {
                return true;
            }
        }
        if(parent == null)return false;
        return parent.containsClass(string);
    }

    public Map<String, Type> getVariables() {
        return variables;
    }

    public List<MethodDeclarationStatement> getMethods() {
        return methods;
    }
}
