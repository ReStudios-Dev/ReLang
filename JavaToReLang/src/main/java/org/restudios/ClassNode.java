package org.restudios;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassNode extends Node {
    private ClassOrInterfaceDeclaration declaration;
    private List<Node> fields;
    private List<Node> methods;
    private List<Node> classes;

    public ClassNode(ClassOrInterfaceDeclaration declaration) {
        this.declaration = declaration;
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
                classes.add(new ClassNode(member.asClassOrInterfaceDeclaration()));
            }
        }
    }


    @Override
    public List<String> stringify() {
        List<String> result = new ArrayList<>();

        result.add("/*");
        result.add("* GENERATED");
        result.add("*/");

        result.add(String.join(" ", classHead(declaration))+" {");

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
    public static List<String> classHead(ClassOrInterfaceDeclaration declaration){
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
