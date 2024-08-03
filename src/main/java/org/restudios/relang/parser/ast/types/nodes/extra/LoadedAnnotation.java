package org.restudios.relang.parser.ast.types.nodes.extra;

import org.restudios.relang.parser.ast.types.values.ClassInstance;

public class LoadedAnnotation {
    public ClassInstance ci;

    public LoadedAnnotation(ClassInstance ci) {
        this.ci = ci;
    }

    public ClassInstance getClassInstance() {
        return ci;
    }
}
