public class Exception {
    private str message;
    public readonly array<StackTraceElement> trace;

    public Exception(str message){
        this.message = message;
    }

    public void print(){
        Thread thread = Thread.getCurrentThread();

        err "Exception "+getClass().name+" in thread "+thread.getName()+": "+this.message;
        foreach(StackTraceElement ste : this.trace){
            err "  at "+ste;
        }
    }
}