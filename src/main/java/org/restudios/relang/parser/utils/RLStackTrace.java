package org.restudios.relang.parser.utils;

import java.util.ArrayList;
import java.util.List;

public class RLStackTrace {
    private final List<RLStackTraceElement> trace;

    public RLStackTrace() {
        trace = new ArrayList<>();
    }

    public RLStackTrace(List<RLStackTraceElement> trace) {
        this.trace = trace;
    }

    public RLStackTrace duplicate(){
        return new RLStackTrace(new ArrayList<>(trace));
    }

    public List<RLStackTraceElement> getElements() {
        return trace;
    }
}
