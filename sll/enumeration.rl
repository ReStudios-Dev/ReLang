/**
* Is parent of every enum class
*
* Initialization prohibited
*/
public class enumeration {

    native public str name();
    native public int ordinal();

    explicit operator str(enumeration e){
        return e.name();
    }
    explicit operator int(enumeration e){
        return e.ordinal();
    }

    native public override int hash();

    public bool equals(enumeration e){
        return e.hash() == this.hash();
    }
}