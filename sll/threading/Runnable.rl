public native abstract class Runnable<R>  {

    native public R run();

    explicit operator str(Runnable l){
        return "<runnable>";
    }
}