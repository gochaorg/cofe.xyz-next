package xyz.cofe.cbuffer.page;

import java.util.Map;

public interface MapMutable<K,V> extends MapReadonly<K,V> {
    V put(K key,V value);
    V remove(K key);

    static <K,V> MapMutable<K,V> of(Map<K,V> map){
        if( map==null )throw new IllegalArgumentException( "map==null" );
        return new MapMutable<K, V>() {
            @Override
            public V put(K key, V value) {
                return map.put(key,value);
            }

            @Override
            public V remove(K key) {
                return map.remove(key);
            }

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
