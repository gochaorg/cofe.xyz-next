package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

import java.util.WeakHashMap;

public class PageBuffersPropertyHolder {
    private static final WeakHashMap<PageBuffers,ContentBuffer> fastBuff = new WeakHashMap<>();

    public static ContentBuffer getFastBuffer(PageBuffers inst){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        return fastBuff.get(inst);
    }

    public static void setFastBuffer(PageBuffers inst, ContentBuffer buff){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        fastBuff.put(inst, buff);
    }

    private static final WeakHashMap<PageBuffers,ContentBuffer> slowBuff = new WeakHashMap<>();

    public static ContentBuffer getSlowBuffer(PageBuffers inst){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        return slowBuff.get(inst);
    }

    public static void getSlowBuffer(PageBuffers inst, ContentBuffer buff){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        slowBuff.put(inst,buff);
    }
}
