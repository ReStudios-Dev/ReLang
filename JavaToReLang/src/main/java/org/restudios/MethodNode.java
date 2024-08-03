package org.restudios;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MethodNode extends Node{
    private Object declaration;
    private Type type;
    private String name;
    private List<Parameter> parameters;

    public MethodNode(MethodDeclaration declaration) {
        this.declaration = declaration;
        this.type = declaration.getType();
        this.name = declaration.getNameAsString();
        this.parameters = declaration.getParameters();
    }
    public MethodNode(ConstructorDeclaration declaration, ClassOrInterfaceDeclaration of) {
        this.declaration = declaration;
        this.type = null;
        this.name = of.getNameAsString();
        this.parameters = declaration.getParameters();
    }

    @Override
    public List<String> stringify() {
        List<String> result = new ArrayList<>();
        String info = "";

        if(declaration instanceof MethodDeclaration){
            MethodDeclaration md = (MethodDeclaration) declaration;
            info = getInfo(md.getTokenRange());
        }
        if(declaration instanceof ConstructorDeclaration){
            ConstructorDeclaration md = (ConstructorDeclaration) declaration;
            info = getInfo(md.getTokenRange());
        }

        result.add("/* "+info+" */");
        List<String> parts = new ArrayList<>();
        parts.add("native");
        Transformer.modifiers(declaration, parts);
        if(type != null) parts.add(Transformer.type(type));
        parts.add(name);
        List<String> arguments = new ArrayList<>();

        for (Parameter parameter : parameters) {
            arguments.add(Transformer.type(parameter.getType())+" "+parameter.getName().getIdentifier());
        }

        String args = "("+String.join(", ", arguments)+")";
        result.add(String.join(" ", parts)+args+";");
        return result;
    }
    public static String getInfo(Optional<TokenRange> tokenRange){
        if(tokenRange.isPresent()){
            Optional<Range> range = tokenRange.get().getBegin().getRange();
            if(range.isPresent()){
                Position pos = range.get().begin;
                return  "line: " +pos.line+", column: "+pos.column;
            }
        }
        return "unknown position";
    }
}
