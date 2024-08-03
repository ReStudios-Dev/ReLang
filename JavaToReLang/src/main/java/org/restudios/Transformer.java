package org.restudios;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAccessModifiers;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithFinalModifier;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithStaticModifier;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transformer {
    public static Map<PrimitiveType.Primitive, String> primitiveMap = new HashMap<>();
    public static Map<String, String> classesMap = new HashMap<>();
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
    }

    public static String type(Type type){

        if(type.isPrimitiveType()){
            PrimitiveType t = type.asPrimitiveType();
            return primitiveMap.get(t.getType());
        }else if(type.isClassOrInterfaceType()){
            String name = type.asClassOrInterfaceType().getName().getIdentifier();
            if(classesMap.containsKey(name)) name =classesMap.get(name);
            List<String> subs = new ArrayList<>();
            if(type.asClassOrInterfaceType().getTypeArguments().isPresent()) for (Type type1 : type.asClassOrInterfaceType().getTypeArguments().get()) {
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
        if(o instanceof NodeWithAccessModifiers<?> field){
            boolean isPublic = field.isPublic();
            boolean isPrivate = field.isPrivate();
            boolean isProtected = field.isProtected();
            if(isPublic) parts.add("public");
            if(isPrivate) parts.add("private");
            if(isProtected) parts.add("protected");
        }
        if(o instanceof NodeWithStaticModifier<?> field){
            boolean isStatic = field.isStatic();
            if(isStatic) parts.add("static");
        }
        if(o instanceof NodeWithFinalModifier<?> field){
            boolean isFinal = field.isFinal();
            if(isFinal) parts.add("readonly");
        }
    }
}
