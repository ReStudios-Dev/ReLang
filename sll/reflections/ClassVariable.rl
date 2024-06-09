public abstract class ClassVariable {
    public readonly ObjectVisibility visibility;

    public Type type;

    public readonly str name;

    /** ignore readonly modifier */
    native public void setValueForce(obj object, obj value);
    native public void setValue(obj object, obj value);
    native public obj getValue(obj object);

    explicit operator str(ClassVariable var){
        return var.visibility.name().lower()+" "+var.type.getHumanReadablyType()+" "+var.name;
    }
}