package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Хранит ссылки на данные и кэш
 * см {@link PageConf}
 */
public class PageBuffersPropertyHolder {
    private static final Map<PageBuffers,ContentBuffer> fastBuff
        = PageConf.fastWeakBufferReference() ? new WeakHashMap<>() : new HashMap<>();

    /**
     * Возвращает данные кэша страниц
     * @param inst Экземпляр {@link ContentBuffer}
     * @return данные кэша страниц
     */
    public static ContentBuffer getFastBuffer(PageBuffers inst){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        return fastBuff.get(inst);
    }

    /**
     * Устанавливает данные кэша страниц
     * @param inst Экземпляр {@link ContentBuffer}
     * @param buff данные кэша страниц
     */
    public static void setFastBuffer(PageBuffers inst, ContentBuffer buff){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        fastBuff.put(inst, buff);
    }

    private static final Map<PageBuffers,ContentBuffer> slowBuff
        = PageConf.slowWeakBufferReference() ? new WeakHashMap<>() : new HashMap<>();

    /**
     * Возвращает данные страниц
     * @param inst Экземпляр {@link ContentBuffer}
     * @return данные страниц
     */
    public static ContentBuffer getSlowBuffer(PageBuffers inst){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        return slowBuff.get(inst);
    }

    /**
     * Устанавливает данные страниц
     * @param inst Экземпляр {@link ContentBuffer}
     * @param buff данные страниц
     */
    public static void getSlowBuffer(PageBuffers inst, ContentBuffer buff){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        slowBuff.put(inst,buff);
    }
}
