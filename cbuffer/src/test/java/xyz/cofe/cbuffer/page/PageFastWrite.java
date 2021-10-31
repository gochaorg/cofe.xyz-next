package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

import java.util.Arrays;

/**
 * Запись данных в fast страницы (кэш)
 */
public interface PageFastWrite extends PageFastRead, PageFastDirty, FastDataSize {
    /**
     * Запись данных в fast страницы (кэш)
     *
     * <ul>
     * <li>Запись страницы в fast.
     * <li>Страница помечается как измененная {@link #dirty(int)}
     * </ul>
     * @param pageIndex индекс кэш страницы
     * @param bytes данные
     */
    default void fastData(int pageIndex, byte[] bytes){
        if( pageIndex<0 ) throw new IllegalArgumentException("pageIndex<0");
        if( bytes==null )throw new IllegalArgumentException("bytes == null");

        int pageCnt = fastPageCount();
        if( pageIndex>=pageCnt )throw new IllegalArgumentException("pageIndex >= fastPageCount(="+pageCnt+")");

        ContentBuffer buff = getFastBuffer();
        if( buff==null )throw new IllegalStateException("fast buffer not available");

        int pageSize = getPageSize();
        if( pageSize<1 )throw new IllegalStateException("pageSize to small");

        if( bytes.length>pageSize )throw new IllegalArgumentException("bytes.length > pageSize(="+pageSize+")");

        buff.set( pageIndex*pageSize, bytes, 0, bytes.length);
        dirty(pageIndex,true);
        fastDataSize(pageIndex, bytes.length);
    }
}
