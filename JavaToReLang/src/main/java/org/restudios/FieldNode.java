package org.restudios;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldNode extends Node{
    private FieldDeclaration field;

    public FieldNode(FieldDeclaration field) {
        this.field = field;
    }

    @Override
    public List<String> stringify() {
        List<String> result = new ArrayList<>();

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
