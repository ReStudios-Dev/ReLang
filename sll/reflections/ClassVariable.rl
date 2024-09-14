public native abstract class ClassVariable {

    /** ignore readonly modifier */
    native public void setValueForce(obj object, obj value);
    native public void setValue(obj object, obj value);
    native public obj getValue(obj object);

    native public ObjectVisibility getVisibility();
    native public Type getType();
    native public str getName();

    explicit operator str(ClassVariable var){
        return var.getVisibility().name().lower()+" "+var.getType().getHumanReadablyType()+" "+var.getName();
    }
}