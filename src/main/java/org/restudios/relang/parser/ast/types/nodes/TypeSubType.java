package org.restudios.relang.parser.ast.types.nodes;

import org.restudios.relang.parser.ast.types.values.RLClass;

public class TypeSubType {

    public String name;
    public RLClass clazz;

    public TypeSubType(String name, RLClass clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public TypeSubType setName(String name) {
        this.name = name;
        return this;
    }

    public RLClass getClazz() {
        return clazz;
    }

    public TypeSubType setClazz(RLClass clazz) {
        this.clazz = clazz;
        return this;
    }

    public boolean compare(TypeSubType tps){
        if(tps == this) return true;
        if(tps.name.equals(name)){

            return tps.clazz.equals(clazz);

        }
        return false;
    }
}
