public native abstract class ClassMethod {
    public readonly str name;
    public readonly ObjectVisibility visibility;
    public readonly bool isStatic;
    public readonly bool isFinal;

    public Type getReturnType() {} // todo
    public array<Type> getArguments() {} // todo

    public str getHumanReadably(){
        str result;
        result += this.visibility.name().lower();
        if(this.isFinal){
            result += " final";
        }
        if(this.isStatic){
            result += " static";
        }
        //result += this.returnType;
        result += this.name;
        result += "(";
        //result += this.arguments.join(", ");
        result += ")";
        return result;
    }
}