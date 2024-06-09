public class Thread {
    private readonly Runnable thread;
    private str name;

    public Thread(Runnable runnable){
        this.thread = runnable;
        this.name = Thread.createName();
    }

    public void setName(str name){
        this.name = name;
    }
    public str getName(){
        return this.name;
    }

    native public void run();

    native public static Thread getCurrentThread();

    native private static void createName();

    explicit operator str(Thread t){
        return t.getName();
    }
}