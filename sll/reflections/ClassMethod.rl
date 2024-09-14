public native abstract class ClassMethod {

    native public Type getReturnType();
    native public array<Type> getArguments();
    native public ObjectVisibility getVisibility();
    native public bool isFinal();
    native public bool isStatic();
    native public str getName();

    public str getHumanReadably(){
        array<str> parts = [this.getVisibility().name().lower()];
        if(this.isFinal()){
            parts.add("final");
        }
        if(this.isStatic()){
            parts.add("static");
        }
        parts.add((str) this.getReturnType());
        parts.add(this.getName() + "(" + this.getArguments().join(", ") + ")");
        return parts.join(" ");
    }
    explicit operator str(ClassMethod var){
        return var.getHumanReadably();
    }
}