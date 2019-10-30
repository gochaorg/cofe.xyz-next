package xyz.cofe.cbuffer.page;

import java.util.WeakHashMap;

public class PageSizePropertyHolder {
    private static final WeakHashMap<GetPageSize,Integer> value = new WeakHashMap<>();

    public static Integer get(GetPageSize inst){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        return value.computeIfAbsent(inst,(i)->1024*8);
    }

    public static void set(GetPageSize inst,int val){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        value.put(inst,val);
    }
}
