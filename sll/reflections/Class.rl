public abstract class Class {
    public readonly str name;
    public readonly ObjectVisibility visibility;
    public readonly ClassType type;
    public readonly array<ClassVariable> variables;
    public readonly array<ClassMethod> methods;

    public ClassVariable getVariable(str name){
        foreach(ClassVariable var : this.variables){
            if(var.name.equals(name)){
                return var;
            }
        }
        return null;
    }
}