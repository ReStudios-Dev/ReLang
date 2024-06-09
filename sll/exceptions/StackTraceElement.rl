public class StackTraceElement {
    public str source;
    public int line;
    public int col;
    public str method;

    public StackTraceElement(str source, int line, int col, str method){
        this.source = source;
        this.line = line;
        this.col = col;
        this.method = method;
    }

    explicit operator str(StackTraceElement elem){
        return elem.source+":"+elem.line+":"+elem.col+"("+method+")";
    }
}