package org.restudios;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAbstractModifier;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAccessModifiers;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithFinalModifier;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithStaticModifier;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transformer {
    public static Map<PrimitiveType.Primitive, String> primitiveMap = new HashMap<>();
    public static Map<String, String> classesMap = new HashMap<>();
    public static List<String> noParametersList = new ArrayList<>();
    static  {
        primitiveMap.put(PrimitiveType.Primitive.BOOLEAN, "bool");
        primitiveMap.put(PrimitiveType.Primitive.BYTE, "int");
        primitiveMap.put(PrimitiveType.Primitive.INT, "int");
        primitiveMap.put(PrimitiveType.Primitive.CHAR, "char");
        primitiveMap.put(PrimitiveType.Primitive.DOUBLE, "float");
        primitiveMap.put(PrimitiveType.Primitive.FLOAT, "float");
        primitiveMap.put(PrimitiveType.Primitive.LONG, "int");
        primitiveMap.put(PrimitiveType.Primitive.SHORT, "int");

        classesMap.put("String", "str");
        classesMap.put("Map", "Map");
        classesMap.put("HashMap", "Map");
        classesMap.put("ArrayList", "array");
        classesMap.put("List", "array");
        classesMap.put("boolean", "bool");
        classesMap.put("byte", "int");
        classesMap.put("int", "int");
        classesMap.put("char", "char");
        classesMap.put("double", "float");
        classesMap.put("float", "float");
        classesMap.put("long", "int");
        classesMap.put("short", "int");

        noParametersList.add("Class");
    }

    public static String type(java.lang.reflect.Type type){
        if(type instanceof ParameterizedType pt){
            String name = ((Class<?>) pt.getRawType()).getSimpleName();
            if(classesMap.containsKey(name)) name = classesMap.get(name);
            List<String> subs = new ArrayList<>();
            if(!noParametersList.contains(name)) for (java.lang.reflect.Type type1 : pt.getActualTypeArguments()) {
                subs.add(type(type1));
            }
            return name + (!subs.isEmpty() ? "<"+String.join(", ", subs)+">" : "");
        }
        if(type instanceof Class<?> c){
            String name = c.getSimpleName();
            if(classesMap.containsKey(name)) name = classesMap.get(name);
            List<String> subs = new ArrayList<>();
            if(!noParametersList.contains(name)) for (java.lang.reflect.Type type1 : c.getTypeParameters()) {
                subs.add("obj");
            }
            return name + (!subs.isEmpty() ? "<"+String.join(", ", subs)+">" : "");
        }
        if(type instanceof TypeVariable<?> t){
            return t.getName();
        }
        if(type instanceof WildcardType t){
            return "/* Wildcard "+ t +" */ obj";
        }
        throw new RuntimeException("Unsupported type: "+type);
    }
    public static String type(Type type){

        if(type.isPrimitiveType()){
            PrimitiveType t = type.asPrimitiveType();
            return primitiveMap.get(t.getType());
        }else if(type.isClassOrInterfaceType()){
            String name = type.asClassOrInterfaceType().getName().getIdentifier();
            if(classesMap.containsKey(name)) name =classesMap.get(name);
            List<String> subs = new ArrayList<>();
            if(!noParametersList.contains(name)) if(type.asClassOrInterfaceType().getTypeArguments().isPresent()) for (Type type1 : type.asClassOrInterfaceType().getTypeArguments().get()) {
                subs.add(type(type1));
            }
            return name + (!subs.isEmpty() ? "<"+String.join(", ", subs)+">" : "");
        }else if(type.isArrayType()){
            Type el = type.asArrayType().getElementType();
            String t = type(el);
            return "array<"+t+">";
        }else if(type.isTypeParameter()){
            return type.asTypeParameter().getName().getIdentifier();
        }else if(type.isVoidType()) {
            return "void";
        }else {
            throw new RuntimeException("Unsupported type: "+type.asString());
        }
    }
    public static void modifiers(Object o, List<String> parts){
        boolean isPublic = false, isPrivate = false, isProtected = false, isAbstract = false, isStatic = false, isFinal = false;
        if(o instanceof NodeWithAccessModifiers<?> field){
            isPublic = field.isPublic();
            isPrivate = field.isPrivate();
            isProtected = field.isProtected();
        }
        if(o instanceof NodeWithAbstractModifier<?> field){
            isAbstract = field.isAbstract();
        }
        if(o instanceof NodeWithStaticModifier<?> field){
            isStatic = field.isStatic();
        }
        if(o instanceof NodeWithFinalModifier<?> field){
            isFinal = field.isFinal();
        }
        int modifiers = (o instanceof Method) ? ((Method) o).getModifiers() : (o instanceof Field) ? ((Field) o).getModifiers() : (o instanceof Class<?>) ? ((Class<?>) o).getModifiers() : -1;
        if(modifiers != -1) {
            isPublic = Modifier.isPublic(modifiers);
            isPrivate = Modifier.isPrivate(modifiers);
            isProtected = Modifier.isProtected(modifiers);
            isAbstract = Modifier.isAbstract(modifiers);
            isStatic = Modifier.isStatic(modifiers);
            isFinal = Modifier.isAbstract(modifiers);
        }
        if(isPublic) parts.add("public");
        if(isPrivate) parts.add("private");
        if(isProtected) parts.add("protected");
        if(isFinal) parts.add("readonly");
        if(isStatic) parts.add("static");
        if(isAbstract)parts.add("abstract");
    }
}
