public class Map<K, V> {
    array<K> keys;
    array<V> values;

    public hash(){
        keys = [];
        values = [];
    }

    public void put(K key, V value){
        keys.add(key);
        values.add(value);
    }

    public V get(K key){
        for(int position = 0; position < keys.size(); position++){
            K ks = keys[position];
            if(key.equals(ks)){
                return values[position];
            }
        }
        return null;
    }

    public void remove(K key){
        int pos = -1;
        for(int position = 0; position < keys.size(); position++){
            K ks = keys[position];
            if(key.equals(ks)){
                pos = position;
            }
        }
        if(pos >= 0) {
            keys.removeAt(pos);
            values.removeAt(pos);
        }
    }

    explicit operator int(Map m){
        return m.keys.size();
    }
}