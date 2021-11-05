package xyz.cofe.cbuffer.page;

import java.util.Map;

public interface MapReadonly<K,V> {
    ArrayReadonly<K> keys();
    V get(K key);
    int count();
    static <K,V> MapReadonly<K,V> of(Map<K,V> map){
        if( map==null )throw new IllegalArgumentException( "map==null" );
        return new MapReadonly<K, V>() {
            @Override
            public ArrayReadonly<K> keys() {
                return ArrayReadonly.of(map.keySet());
            }

            @Override
            public V get(K key) {
                return map.get(key);
            }

            @Override
            public int count() {
                return map.size();
            }
        };
    }
}
