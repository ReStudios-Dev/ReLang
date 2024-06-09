package org.restudios.relang.parser.modules.threading;

import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLThread;

public abstract class AThreadManager {
    public RLThread getCurrentThread(Context context) {
        return new RLThread("main", context);
    }
    public abstract void run(RLThread threadClassInstance, Context context);
}
