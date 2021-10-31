package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Pair;

/**
 * Описывает изменение значения
 * @param <VALUE> Тип значения
 */
public interface Changes<VALUE> extends Pair<VALUE,VALUE> {
    public default VALUE old(){ return a(); }
    public default VALUE current(){ return b(); }
    public static <VALUE> Changes<VALUE> of( VALUE old, VALUE current ){
        return new Changes<VALUE>() {
            @Override
            public VALUE a(){
                return old;
            }

            @Override
            public VALUE b(){
                return current;
            }
        };
    }
}
