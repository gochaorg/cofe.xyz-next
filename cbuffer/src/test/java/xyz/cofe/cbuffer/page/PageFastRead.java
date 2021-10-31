package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

import java.util.Arrays;

/**
 * Чтение из fast буфера - т.е. кэша
 */
public interface PageFastRead extends GetPageSize, PageBuffers, FastDataSize {
    /**
     * Чтение страницы из fast
     * @param pageIndex индекс fast страницы
     * @return данные или null если страница отсуствует
     */
    default byte[] fastData(int pageIndex){
        if( pageIndex<0 ) throw new IllegalArgumentException("pageIndex<0");

        int pageCnt = fastPageCount();
        if( pageIndex>=pageCnt )throw new IllegalArgumentException("pageIndex >= fastPageCount(="+pageCnt+")");

        ContentBuffer buff = getFastBuffer();
        if( buff==null )throw new IllegalStateException("fast buffer not available");

        int pageSize = getPageSize();
        if( pageSize<1 )return null;

        int dataSize = fastDataSize(pageIndex);
        if( dataSize<0 )return null;
        if( dataSize==0 )return new byte[0];

        byte[] data = buff.get(pageSize*pageIndex, dataSize);
        if( data==null )return null;
        if( data.length<=dataSize )return data;
        return Arrays.copyOf(data,dataSize);
    }

    /**
     * Кол-во всех fast страниц
     * @return кол-во fast страниц
     */
    int fastPageCount();
}
