// fori(variable declaration; init value; max value; step)
fori(int i; 0; 50; 10){} // equivalent: for(int i = 0; i <= 50; i += 10){}

// default for
for(int i = 0; i < 5; i++){}

// array of string declaration
array<str> arr = ["hello", "world"];

// java foreach structure
foreach(str s : arr){}

// default do while
do { } while(true);

// default while
while (true) {}

// output any object to program output
out(""); // you can also use without brackets: out "str";

// new line
out;

// output any object to program error stream
err(""); // you can also use without brackets: out "str";
err; // new line

// default variable operations/default binary operations
int i = 4 ** 2; // pow
i /= 2; // equivalent: i = i / 2
i ??= 4; // equivalent: if(i == null){ i = 4; }

public class test<T> extends Something implements SomeInterface, SomeInterface2 { // custom types
  // get static class entries by <class>.<entry>: test.testVar;
  // readonly variables can't be changed
  private readonly static int testVar = 0;
  public T val;

  public test(str arg) : super(arg, "some") { // call constructor of parent class

  }
  // test(str) != test()
  public test(){
    val = new T();
  }

  /* EXPlicit - export to other type, IMPlicit = import from other type */
  explicit operator str(test t){ // when casting class to str
    return "";
  }
  implicit operator test(str s){ // when casting string to class
    return new test();
  }

  /** cannot be other return type */
  test operator +(test left, test right){ // test + test = test
    return new test();
  }
}


// trying
try {
    // code
} catch(Case1 c1, Case2 c2/*, ...*/) {
    // c1 will be null, when thrown Case2
} catch(Case3 c3) { // Some catch cases
} catch(Exception e) { // Exception - for ANY exceptions. "Exception" is supertype for all exceptions
}

// interfaces
public interface Num /* interface can extends other interface */{
    public int getInt();
}
public class Int implements Number {
    // override keyword is important
    public override int getInt(){
        return 3;
    }
}

// abstract classes
public abstract class Number { // like interface
    public abstract str getStr();
    public int getInt(); /* but "abstract" keyword is not required */
    public float getFloat(){ return 3.14; }
}

// final classes: can't be extended (and can't be abstract)
public final class Number {}

public class Person {
    private str name = "Ashley";
    private int age = 16;

    public void hello(str world){}
}

Person t = new Person(); // instantiation

/* REFLECTIONS */

Class c = Person.class; // or t.getClass()

out "Name: "      +   c.name;                       // class name
out "Visibility: "+   c.visibility.name();          // public/private
out "Type: "      +   c.type.name();                // class/interface/enum/abstract
out "Variables: " +   c.variables;                  // variables

ClassVariable ageVariable = c.getVariable("age");
out "Age: "       +   ageVariable.getValue(t);      // specify variable
out "Age type: "  +   ageVariable.type;             // specify variable

ageVariable.setValue(t, 16); // update value
ageVariable.setValueForce(t, 16); // update value forcibly (ignore readonly modifier)



// lambda functions
Runnable lamb = () -> {
    throw new Exception("");
};
lamb.run();
@fsdf