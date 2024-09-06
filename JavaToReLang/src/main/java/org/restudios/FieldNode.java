package org.restudios;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldNode extends Node{
    private FieldDeclaration field;
    private Field reflectField;

    public FieldNode(FieldDeclaration field) {
        this.field = field;
    }
    public FieldNode(Field field){
        this.reflectField = field;
    }

    @Override
    public List<String> stringify() {

        List<String> result = new ArrayList<>();

        if(reflectField != null){
            List<String> parts = new ArrayList<>();
            Transformer.modifiers(reflectField, parts);
            parts.add(Transformer.type(reflectField.getGenericType()));
            parts.add(reflectField.getName());
            result.add(String.join(" ", parts)+";");
            return Collections.singletonList(String.join(" ", result));
        }

        String info = MethodNode.getInfo(field.getTokenRange());

        result.add("/* "+info+" */");

        List<String> parts = new ArrayList<>();

        Transformer.modifiers(field, parts);

        for (VariableDeclarator variable : field.getVariables()) {
            List<String> cpy = new ArrayList<>(parts);
            Type type = variable.getType();
            cpy.add(Transformer.type(type));
            cpy.add(variable.getNameAsString());
            result.add(String.join(" ", cpy)+";");
        }



        return Collections.singletonList(String.join(" ", result));
    }
}
