package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

public interface PageSlowWrite extends GetPageSize, PageBuffers {
    /**
     * Запись страницы в slow
     * @param pageIndex индекс slow страницы
     * @param bytes данные
     */
    default void slowData(int pageIndex, byte[] bytes){
        if( pageIndex<0 ) throw new IllegalArgumentException("pageIndex<0");

        int pageSize = getPageSize();
        if( pageSize<1 )throw new IllegalStateException("pageSize to small");

        if( bytes==null )throw new IllegalArgumentException("bytes == null");
        if( bytes.length>pageSize )throw new IllegalArgumentException("bytes.length > pageSize(="+pageSize+")");

        ContentBuffer buff = getSlowBuffer();
        if( buff==null )throw new IllegalStateException("slow buffer not available");

        buff.set(
            ((long)pageSize) * ((long)pageIndex),
            bytes,
            0,
            bytes.length
        );
    }
}
