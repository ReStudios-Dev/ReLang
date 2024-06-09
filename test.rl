

public void second(){

    third();
}
public void third(){
    throw new NullPointerException("e");
}

public class Test {
    public Test(){
    }
    public void first(){
        second();
    }
}
Test t = new Test();
t.first();