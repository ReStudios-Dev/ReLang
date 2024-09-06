public native class JavaClass {
    native public JavaClass(str className);
    native public void method(str name, obj... args);
}

public annotation ReflectionClass {
    public readonly str classname;

    public ReflectionClass(str classPath){
        this.classname = classPath;
    }
}
