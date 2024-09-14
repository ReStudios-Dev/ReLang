public native class Class {
    public readonly str name;
    public readonly ObjectVisibility visibility;
    public readonly ClassType type;

    native public array<ClassVariable> getVariables();
    native public array<ClassMethod> getMethods();

    public ClassVariable getVariable(str name){
        foreach(ClassVariable var : this.getVariables()){
            if(var.getName().equals(name)){
                return var;
            }
        }
        return null;
    }
}