public annotation Hello {
    public Hello( int i ){
        out "1 arg: "+i;
    }
    public Hello( int i1, int i2 ){
        out "2 args: "+i1+", "+i2;
    }
    public Hello() {
        out "no args";
    }
}

@Hello(33)
@Hello(11, 22)
@Hello
public class test {

}
