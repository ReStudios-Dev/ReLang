package org.restudios;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class ClassNode extends Node {
    private String packageName;
    private ClassOrInterfaceDeclaration declaration;
    private Class<?> clazz;
    private List<Node> fields;
    private List<Node> methods;
    private List<Node> classes;

    public ClassNode(ClassOrInterfaceDeclaration declaration, String packageName) {
        this.declaration = declaration;
        this.packageName = packageName;
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.classes = new ArrayList<>();
        for (BodyDeclaration<?> member : declaration.getMembers()) {
            if(member.isFieldDeclaration()){
                fields.add(new FieldNode(member.asFieldDeclaration()));
            }
            if(member.isConstructorDeclaration()){
                methods.add(new MethodNode(member.asConstructorDeclaration(), declaration));
            }
            if(member.isMethodDeclaration()){
                methods.add(new MethodNode(member.asMethodDeclaration()));
            }
            if(member.isClassOrInterfaceDeclaration()){
                String pack = !packageName.isEmpty() ? packageName+"."+declaration.getName() : declaration.getNameAsString();
                classes.add(new ClassNode(member.asClassOrInterfaceDeclaration(), pack));
            }
        }
    }
    public ClassNode(Class<?> declaration){
        this.clazz = declaration;
        this.packageName = clazz.getPackageName();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.classes = new ArrayList<>();
        for (Field declaredField : declaration.getDeclaredFields()) {
            fields.add(new FieldNode(declaredField));
        }
        for (Method declaredMethod : declaration.getDeclaredMethods()) {
            if(declaredMethod.getName().startsWith("lambda$")) continue;
            methods.add(new MethodNode(declaredMethod));
        }
        for (Class<?> declaredClass : declaration.getDeclaredClasses()) {
            classes.add(new ClassNode(declaredClass));
        }
    }


    @Override
    public List<String> stringify() {
        List<String> result = new ArrayList<>();

        result.add("/*");
        result.add("* GENERATED");
        result.add("*/");

        if (this.clazz != null){
            result.add("@ReflectionClass(\""+clazz.getName()+"\")");
            result.add(String.join(" ", reflectClassHead())+" {");
        }else{
            result.add("@ReflectionClass(\""+packageName+"."+declaration.getName()+"\")");
            result.add(String.join(" ", jpClassHead())+" {");
        }

        for (Node subNode : fields) {
            result.addAll(subNode.stringify().stream().map(s -> "    "+s).toList());
        }
        if(!fields.isEmpty() && !methods.isEmpty()) result.add("");
        if(!fields.isEmpty() && !methods.isEmpty()) result.add("");
        for (Node subNode : methods) {
            result.addAll(subNode.stringify().stream().map(s -> "    "+s).toList());
            result.add("");
        }
        if(!methods.isEmpty() && !fields.isEmpty()) result.add("");
        for (Node subNode : classes) {
            result.addAll(subNode.stringify().stream().map(s -> "    "+s).toList());
            result.add("");
        }
        result.add("}");
        return result;
    }
    public List<String> reflectClassHead() {
        String type = clazz.isInterface() ? "interface" : clazz.isEnum() ? "enum" : "class";
        List<String> parts = new ArrayList<>();
        Transformer.modifiers(clazz, parts);
        parts.add("native");
        parts.add(type);
        parts.add(clazz.getSimpleName());

        List<String> types = new ArrayList<>();
        for (TypeVariable<? extends Class<?>> typeParameter : clazz.getTypeParameters()) {
            types.add(typeParameter.getName());
        }
        if(!types.isEmpty()) parts.add("<"+String.join(", " ,types)+">");

        String ext = clazz.getSuperclass() == null ? null : clazz.getSuperclass().getName().equals("java.lang.Object") ? null : clazz.getSuperclass().getSimpleName();

        List<String> implementations = new ArrayList<>();
        for (Type implementedType : clazz.getGenericInterfaces()) {
            implementations.add(Transformer.type(implementedType));
        }
        if(ext != null){
            parts.add("extends");
            parts.add(ext);
        }
        if(!implementations.isEmpty()){
            parts.add("implements");
            parts.add(String.join(", ", implementations));
        }
        return parts;
    }
    public List<String> jpClassHead(){
        String type = declaration.isInterface() ? "interface" : (declaration.isEnumDeclaration() ? "enum" : "class");

        List<String> parts = new ArrayList<>();
        Transformer.modifiers(declaration, parts);
        parts.add("native");
        parts.add(type);
        parts.add(declaration.getNameAsString());
        List<String> types = new ArrayList<>();
        for (TypeParameter typeParameter : declaration.getTypeParameters()) {
            types.add(typeParameter.getName().getIdentifier());
        }
        if(!types.isEmpty()) parts.add("<"+String.join(", " ,types)+">");

        String ext = null;
        for (ClassOrInterfaceType extendedType : declaration.getExtendedTypes()) {
            ext = Transformer.type(extendedType);
        }
        List<String> implementations = new ArrayList<>();
        for (ClassOrInterfaceType implementedType : declaration.getImplementedTypes()) {
            implementations.add(Transformer.type(implementedType));
        }
        if(ext != null){
            parts.add("extends");
            parts.add(ext);
        }
        if(!implementations.isEmpty()){
            parts.add("implements");
            parts.add(String.join(", ", implementations));
        }

        return parts;
    }
}
