package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

/**
 * Чтение из slow буферв
 */
public interface PageSlowRead extends GetPageSize, PageBuffers {
    /**
     * Чтение страницы из slow
     * @param pageIndex индекс slow страницы
     * @return данные или null если страница отсуствует
     */
    default byte[] slowData(int pageIndex){
        if( pageIndex<0 ) throw new IllegalArgumentException("pageIndex<0");

        int pageSize = getPageSize();
        if( pageSize<1 )return null;

        ContentBuffer buff = getSlowBuffer();
        if( buff==null )throw new IllegalStateException("slow buffer not available");

        return buff.get(
            ((long)pageSize) * ((long)pageIndex),
            pageSize
        );
    }

    /**
     * Возвращает макс индекс slow страницы
     * @return макс индекс
     */
    default int getMaxSlowPageIndex(){
        ContentBuffer buff = getSlowBuffer();
        if( buff==null )throw new IllegalStateException("slow buffer not available");

        int pageSize = getPageSize();
        if( pageSize<1 )throw new IllegalStateException("pageSize to small");

        long pi = buff.getSize() / pageSize;
        if( pi>Integer.MAX_VALUE )throw new IllegalStateException(
            "max page (="+pi+") out of Integer range (..."+Integer.MAX_VALUE+")");

        return (int)pi;
    }
}
