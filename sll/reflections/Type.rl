public native abstract class Type {

    native public array<Type> getSubTypes();
    native public bool isPrimitive();
    native public Class getTypeClass();
    native public PrimitiveType getPrimitiveType();

    public str getHumanReadablyType(){
        str result = "";
        if(this.isPrimitive()){
            result = this.getPrimitiveType().name().lower();
        }else{
            result = this.getTypeClass().name;
        }
        array<Type> subTypes = this.getSubTypes();
        if(subTypes.size() > 0){
            result += "<";
            result += subTypes.join(", ");
            result += ">";
        }
        return result;
    }
    explicit operator str(Type type){
        return type.getHumanReadablyType();
    }
}