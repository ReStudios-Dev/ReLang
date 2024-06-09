public abstract class Type {
    /** If the type is primitive - type will be null, otherwise primitive will be null */
    public readonly Class clazz;
    public readonly PrimitiveType primitive;

    public readonly array<Type> subtypes;

    public str getHumanReadablyType(){
        str result;
        if(this.primitive != null){
            result = this.primitive.name().lower();
        }else{
            result = this.clazz.name;
        }
        if(this.subtypes.size() > 0){
            result += "<";
            result += this.subtypes.join(", ");
            result += ">";
        }
        return result;
    }
    explicit operator str(Type type){
        return type.getHumanReadablyType();
    }
}