package org.restudios.relang.parser.ast.types.nodes.statements.classes;

import org.restudios.relang.parser.ast.types.nodes.statements.*;

import java.util.ArrayList;

public class ClassBlock {
    public final ArrayList<VariableDeclarationStatement> variables;
    public final ArrayList<MethodDeclarationStatement> methods;
    public final ArrayList<ConstructorDeclarationStatement> constructors;
    public final ArrayList<ClassDeclarationStatement> classes;
    public final ArrayList<OperatorOverloadStatement> operators;

    public ClassBlock(ArrayList<VariableDeclarationStatement> variables, ArrayList<MethodDeclarationStatement> methods, ArrayList<ConstructorDeclarationStatement> constructors, ArrayList<ClassDeclarationStatement> classes, ArrayList<OperatorOverloadStatement> operators) {
        this.variables = variables;
        this.methods = methods;
        this.constructors = constructors;
        this.classes = classes;
        this.operators = operators;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\tvariables=" + variables +
                ",\n\t methods=" + methods +
                ",\n\t constructors=" + constructors +
                ",\n\t classes=" + classes +
                ",\n\t operators=" + operators +
                "\n}";
    }
}
