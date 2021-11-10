package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

/**
 * Изменение кол-ва выделенных страниц
 */
public interface ResizablePages<MEM extends UsedPagesInfo> extends PagedData<MEM>, ReduciblePages<MEM>, ExtendablePages<MEM> {
    /**
     * Изменение кол-ва выделенных страниц
     * @param pages целевое кол-во страниц
     * @return Сколько было и сколько стало памяти
     */
    public default Tuple2<UsedPagesInfo,UsedPagesInfo> resizePages(int pages) {
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );

        if( pages==0 )return Tuple2.of(memoryInfo(), memoryInfo());

        long curPageCnt = memoryInfo().pageCount();
        long nxtPageCnt = curPageCnt + pages;
        long diffPgeCnt = nxtPageCnt - curPageCnt;
        if( diffPgeCnt>0 ){
            if( diffPgeCnt>Integer.MAX_VALUE ){
                throw new IllegalArgumentException("can't extend over "+diffPgeCnt+", Integer.MAX_VALUE");
            }
            return extendPages((int)diffPgeCnt);
        }else{
            long abs_diff = -diffPgeCnt;
            if( abs_diff>Integer.MAX_VALUE ){
                throw new IllegalArgumentException("can't reduce over "+abs_diff+", Integer.MAX_VALUE");
            }
            return reducePages((int)abs_diff);
        }
    }
}
