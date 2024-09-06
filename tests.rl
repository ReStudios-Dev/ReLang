public interface TestAction {
    public bool test();
}
public void test(str name, TestAction action) {
  if(action.test()){
    out "[ok] "+name;
  }else{
    err "[err] "+name;
  }
}

test("test lambda", () -> true);

// IFS
if (true) {
    out "[ok] if true";
}

if(false){
    err "[err] if false";
}else{
    out "[ok] if false else";
}

if true out "[ok] if without braces";
// /IFS

// FORI
int foreachTest = 0;
for(int i = 0; i < 5; i++){
    foreachTest += i;
}
if(foreachTest == 10){
    out "[ok] fori";
}else {
    err "[err] fori (10 != "+foreachTest+")";
}
// /FORI


// CLASSES

class Test {

    public Test() {
        out "[ok] constructor";
    }

    public void method(){
        out "[ok] class method";
    }

}
Test t = new Test();
t.method();

bool throwed = false;
try {
    new Test(4);
}catch(InternalException e){
    throwed = true;
    if(e.message == "Could not find constructor with received arguments"){
        out "[ok] non-existent constructor call";
    }else {
        err "[err] non-existent constructor call";
    }
}
if(!throwed){
        err "[err] non-existent constructor call (NOT THROWED)";
}

interface SubTestInterface {
    public void test();
}

class SubTest extends Test implements SubTestInterface  {
    public SubTest() : super() /* call parent constructor */ {

    }
    public override void test() {
        out "[ok] overrided";
    }
    public void checkHash() {

        if(this.hash() > 0){
            out "[ok] class hash (method)";
        }else {
            err "[err] class hash (method)";
        }
    }
}
SubTest st = new SubTest();
st.method();
st.test();

if(st.hash() > 0){
    out "[ok] class hash";
}else {
    err "[err] class hash";
}
st.checkHash();

class vec {
    private int x;
    private int y;
    private int z;

    public vec(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    explicit operator str(vec vector){
        return vector.x+":"+vector.y+":"+vector.z;
    }
    vec operator +(vec vec1, vec vec2){
        return new vec(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z);
    }
}
vec vector1 = new vec(1, 2, 3);
if(((str)vector1) == "1:2:3") {
    out "[ok] explicit operator";
}else {
    err "[err] explicit operator";
}
vec vector2 = vector1 + vector1;
if(((str)vector2) == "2:4:6") {
    out "[ok] binary operator";
}else {
    err "[err] binary operator";
}

public annotation Hello {
    public Hello( int i ){
        if(i == 33) out "[ok] annotation 1/3"
        else err "[err] annotation 1/3";
    }
    public Hello( int i1, int i2 ){
        if(i1 == 11 && i2 == 22) out "[ok] annotation 2/3"
        else err "[err] annotation 2/3";
    }
    public Hello() {
        out "[ok] annotation 3/3";
    }
}

@Hello(33)
@Hello(11, 22)
@Hello
public class test {

}


public class aa {

    public void testVarArgs(int i, str... args){
        if(i == 3 && args[0] == "2" && args[1] == "3"){
            out "[ok] varargs 2/2";
        }else {
            err "[err] varargs 2/2";
        }
    }

    public void testVarArgs(int i, int... args){
        if(i == 1 && args[0] == 2 && args[1] == 3){
            out "[ok] varargs 1/2";
        }else {
            err "[err] varargs 1/2";
        }
    }

}
aa x = new aa();
x.testVarArgs(1, 2, 3);
x.testVarArgs(3, "2", "3");


class A {}

class B extends A {}

A a = new B();

if(a instanceof A) {out "[ok] instanceof 1/3";} else err "[err] instanceof 1/3";
if(a instanceof B) {out "[ok] instanceof 2/3";} else err "[err] instanceof 2/3";
if(!(a instanceof aa)) {out "[ok] instanceof 3/3";} else err "[err] instanceof 3/3";

// /CLASSES

// LAMBDA

Runnable<int> sf = () -> {
    return 'a' + 'b' + 'c';
};

if(sf() == 117){
    out "[ok] lambda";
}

() -> { out "[ok] inline lambda"; }();


public interface Sum {
    public int sum(int first, int second);
}

public int multiply(Sum sumManager, int first, int second, int power){
    return sumManager.sum(first, second) * power;
}

int i = multiply(  (int first, int second) -> first + second  ,   2, 3,   2);
if(i == 10){
    out "[ok] lambda to interface";
}else {
    err "[err] lambda to interface";
}

// /LAMBDA