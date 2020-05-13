package xyz.cofe.text.tparse;

import java.util.WeakHashMap;

/**
 * Хранение ссылки на "имя" правила
 */
public class GRNameImpl {
    public static final WeakHashMap<Object,String> objName = new WeakHashMap<>();
    public static String name( Object obj ){ return objName.get(obj); }
    public static void name( Object obj, String name ){
        objName.put(obj,name);
    }
}
