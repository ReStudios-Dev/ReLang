public abstract class Runnable {

    native public void run();

    explicit operator str(Runnable l){
        return "<runnable>";
    }
}