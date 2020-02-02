package xyz.cofe.cbuffer.page;

/**
 * Читает данные указанной страницы, нумерация страниц как для кэша, так и для данных сквозная
 *
 * <br>
 *     см функции:
 *     <ul>
 *         <li> {@link #slowToFast(int)} - получение отображения страницы на кэш</li>
 *         <li> {@link #fastData(int)} - получение данных из кэша</li>
 *         <li> {@link #map(int)} - загрузка данных из диска в кэш</li>
 *         <li> {@link #getMaxSlowPageIndex()} - получение максимального номера страницы на диске</li>
 *     </ul>
 */
public interface PageData extends PageMap, PageFastRead, PageFastWrite, PageSlowRead, PageLoad {
    /**
     * Читает данные страницы
     * @param page страница - <b>сквозная нумерация</b>
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
     * @param page страница - <b>сквозная нумерация</b>
     * @param bytes данные
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
