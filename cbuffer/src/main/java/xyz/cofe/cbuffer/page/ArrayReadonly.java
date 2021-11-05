package xyz.cofe.cbuffer.page;

import java.util.ArrayList;
import java.util.List;

public interface ArrayReadonly<V> {
    V get(int idx);
    int count();
    static <V> ArrayReadonly<V> of(Iterable<V> iter){
        if( iter==null )throw new IllegalArgumentException( "iter==null" );
        List<V> lst = new ArrayList<>();
        for( V e : iter ){
            lst.add(e);
        }
        return of(lst);
    }
    static <V> ArrayReadonly<V> of(List<V> lst){
        if( lst==null )throw new IllegalArgumentException( "lst==null" );
        return new ArrayReadonly<V>() {
            @Override
            public V get(int idx) {
                return lst.get(idx);
            }

            @Override
            public int count() {
                return lst.size();
            }
        };
    }
}
