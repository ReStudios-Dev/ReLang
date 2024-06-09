public class array<T> {
    native public T get(int index);
    native public void add(T value);
    native public void removeAt(int index);
    native public bool empty();
    native public bool contains(T value);
    native public int size();
    native public str join(str delimiter);

    explicit operator str(array a){
        str result = "[" + a.join(", ") + "]";
        return result;
    }
}