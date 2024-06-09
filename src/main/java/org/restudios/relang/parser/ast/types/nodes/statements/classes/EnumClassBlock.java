package org.restudios.relang.parser.ast.types.nodes.statements.classes;

import org.restudios.relang.parser.ast.types.values.values.EnumItemValue;

import java.util.ArrayList;

public class EnumClassBlock extends ClassBlock{
    public final ArrayList<String> values;

    public EnumClassBlock(ArrayList<String> values, ClassBlock statements) {
        super(statements.variables, statements.methods, statements.constructors, statements.classes, statements.operators);
        this.values = values;
    }

    public ArrayList<EnumItemValue> createValues() {
        ArrayList<EnumItemValue> result = new ArrayList<>();
        for (String value : values) {
            result.add(new EnumItemValue(null, value, null));
        }
        return result;
    }
}
