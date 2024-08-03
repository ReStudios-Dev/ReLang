/**
* Is parent of every class
*/
public native class obj {

    public bool equals(obj some){
        return this.hash() == some.hash();
    }

    explicit operator str(obj f){
        return f.getClass().name+"@"+f.hash();
    }

    /**
    * Returns hash of class
    */
    native public int hash();
    native public Class getClass();
}