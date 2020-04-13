package xyz.cofe.cbuffer.page;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Реализация свойства pageSize
 * см {@link GetPageSize}
 */
public class PageSizePropertyHolder {
    private static final Map<GetPageSize,Integer> value
        = PageConf.weakPageSize() ? new WeakHashMap<>() : new HashMap<>();

    /**
     * Чтение значения свойства указанного объекта
     * @param inst объект
     * @return значение, по умолчанию 8k
     */
    public static Integer get(GetPageSize inst){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        return value.computeIfAbsent(inst,(i)->1024*8);
    }

    /**
     * Установка значения свойства pageSize указанного объекта
     * @param inst объект владелец
     * @param val значение
     */
    public static void set(GetPageSize inst,int val){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        value.put(inst,val);
    }
}
