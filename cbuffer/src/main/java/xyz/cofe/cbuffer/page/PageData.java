package xyz.cofe.cbuffer.page;

public interface PageData extends PageMap, PageFastRead, PageFastWrite, PageSlowRead, PageLoad {
    /**
     * Читает данные страницы
     * @param page страница
     * @return данные
     */
    default byte[] data(int page){
        if( page<0 ) throw new IllegalArgumentException("page<0");

        int fastIdx = slowToFast(page);
        if( fastIdx>=0 ){
            return fastData(fastIdx);
        }

        int maxSlowPI = getMaxSlowPageIndex();
        if( page>maxSlowPI )return new byte[0];

        fastIdx = map(page);
        if( fastIdx<0 )throw new IllegalStateException("can't load page="+page);

        return fastData(fastIdx);
    }

    /**
     * Записывает данные в страницу
     * @param page
     * @param bytes
     */
    default void data(int page, byte[] bytes){
        if( page<0 ) throw new IllegalArgumentException("page<0");
        if( bytes==null ) throw new IllegalArgumentException("bytes==null");
        if( bytes.length==0 )return;

        int fastIdx = slowToFast(page);
        if( fastIdx>=0 ){
            fastData(fastIdx,bytes);
            return;
        }

        fastIdx = map(page);
        if( fastIdx<0 )throw new IllegalStateException("can't load page="+page);

        fastData(fastIdx,bytes);
    }
}
