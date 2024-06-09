package org.restudios.relang;

import java.util.ArrayList;

public class ReLangClassLoader {
    private final ArrayList<ClassPath> classes = new ArrayList<>();

    @SuppressWarnings("UnusedReturnValue")
    public ReLangClassLoader addClassPath(ClassPath classPath){
        classes.add(classPath);
        return this;
    }


    public ArrayList<ClassPath> getClassPaths() {
        return this.classes;
    }
}
