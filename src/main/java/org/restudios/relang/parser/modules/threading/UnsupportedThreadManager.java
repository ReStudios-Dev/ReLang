package org.restudios.relang.parser.modules.threading;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLThread;
import org.restudios.relang.parser.exceptions.RLException;

public class UnsupportedThreadManager extends AThreadManager {



    @Override
    public void run(RLThread thread, Context context) {
        throw new RLException("Threads is unsupported", Type.internal(context), context);
    }
}
