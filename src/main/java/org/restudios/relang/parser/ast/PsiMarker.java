package org.restudios.relang.parser.ast;

import org.restudios.relang.parser.ast.types.nodes.Expression;

public interface PsiMarker {
    default <T extends Expression> T touch(T exp){
        done(exp);
        return exp;
    }
    void done(Expression exp);
}
