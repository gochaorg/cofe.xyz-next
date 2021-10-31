package xyz.cofe.cbuffer;

import xyz.cofe.cbuffer.page.PDLogger;
import xyz.cofe.cbuffer.page.PageDataImpl;
import xyz.cofe.fn.Consumer4;
import xyz.cofe.fn.Tuple4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Буфер с поддержкой кеширования в "оперативной" памяти.
 *
 * <p>
 * Память разделяется на два основных буфера - два слоя:
 * <ul>
 *     <li>
 *         Верхний - то, что может быть расположено в оперативной памяти или быстрой памяти,
 *         размер которой может быть ограничен.
 *
 *         <p>
 *         По сути кэш.
 *     </li>
 *     <li>
 *         Нижний слой - то, что может быть расположено в постоянной памяти (SSD/HDD)
 *         и размер которой может превышать размер первой.
 *     </li>
 * </ul>
 */
public class PageBuffer implements AutoCloseable, ContentBuffer {
    protected ContentBuffer hiBuffer;
    protected ContentBuffer loBuffer;
    private PageDataImpl pageDataImpl;
    protected volatile boolean closed;
    private PDLogger pdLogger;

    /**
     * Конструктор
     * @param hiBuffer Быстрая память (кеш)
     * @param loBuffer Медленная память
     * @param pageSize Размер страницы в байтах, минимально 256
     * @param maxHiPages Размер кешируемых страниц
     */
    public PageBuffer( ContentBuffer hiBuffer, ContentBuffer loBuffer, int pageSize, int maxHiPages ){
        if( hiBuffer==null )throw new IllegalArgumentException( "hiBuffer==null" );
        if( loBuffer==null )throw new IllegalArgumentException( "loBuffer==null" );
        if( pageSize<PageDataImpl.PAGE_SIZE_MIN )throw new IllegalArgumentException( "pageSize to small, min size="+PageDataImpl.PAGE_SIZE_MIN );
        if( maxHiPages<PageDataImpl.MAX_FAST_PAGE_COUNT_Min )throw new IllegalArgumentException( "maxHiPages to small, min ="+PageDataImpl.MAX_FAST_PAGE_COUNT_Min );
        this.hiBuffer = hiBuffer;
        this.loBuffer = loBuffer;
        pageDataImpl = new PageDataImpl();
        pageDataImpl.setPageSize(pageSize);
        pageDataImpl.setFastBuffer(hiBuffer);
        pageDataImpl.setSlowBuffer(loBuffer);
        pageDataImpl.setMaxFastPageCount(maxHiPages);
        closed = false;

        pdLogger = new PDLogger(pageDataImpl, System.out);
    }

    @Override
    public void close() {
        if( hiBuffer instanceof AutoCloseable ){
            hiBuffer.close();
            hiBuffer = null;
        }

        if( loBuffer instanceof AutoCloseable ){
            loBuffer.close();
            loBuffer = null;
        }

        if( pageDataImpl instanceof AutoCloseable ){
            try {
                ((AutoCloseable) pageDataImpl).close();
                pageDataImpl = null;
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        if( pdLogger != null ){
            pdLogger.close();
            pdLogger = null;
        }

        closed = true;
    }

    /**
     * Возвращает размер страницы
     * @return размер страницы в байтах
     */
    public int getPageSize(){
        if(closed)throw new IllegalStateException("PageBuffer is closed");
        return pageDataImpl.getPageSize();
    }

    @Override
    public long getSize() {
        if(closed)throw new IllegalStateException("PageBuffer is closed");
        return loBuffer.getSize();
    }

    @Override
    public void setSize(long size) {
        if(closed)throw new IllegalStateException("PageBuffer is closed");
        unmapAll();
        loBuffer.setSize(size);
    }

    /**
     * Освобождение всех кешированных страниц
     */
    private void unmapAll(){
        int cnt = pageDataImpl.fastPageCount();
        for( int pi=cnt-1; pi>=0; pi-- ){
            pageDataImpl.unmap(pi);
        }
    }

    /**
     * Расчет расположения данных на странице
     * @param pageSize размер страницы
     * @param dataOffset смещение данных в линейном пространстве
     * @param dataSize размер данных
     * @param pageDataLoc расположение данных на страницах: <code>fn( target, page, off, len )</code>,
     *                    где
     *                      target - смещение в целевом массиве
     *                      page - индекс страницы
     *                      off - смещение на странице
     *                      len - размер данных
     */
    private static void pageDataLocations( int pageSize, long dataOffset, int dataSize, Consumer4<Integer, Integer,Integer,Integer> pageDataLoc ){
        if( dataSize<1 )throw new IllegalArgumentException( "dataSize<1" );
        if( pageSize<1 )throw new IllegalArgumentException( "pageSize<1" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset<0" );

        long page_begin_long = dataOffset / pageSize;
        if( page_begin_long>=Integer.MAX_VALUE ){
            throw new IllegalArgumentException(
                "can't compute initial page, out of range (Integer.MAX_VALUE="+Integer.MAX_VALUE+") for dataOffset="+dataOffset+" pageSize="+pageSize);
        }

        int page_begin_off = (int)(dataOffset % pageSize);

        int page = (int)page_begin_long;
        int off = page_begin_off;

        int total = 0;
        while (total < dataSize) {
            int avail = pageSize - off;
            int need = dataSize - total;
            int read = Math.min(need, avail);
            pageDataLoc.accept(total, page, off, read);
            total += read;
            page += 1;
            off = 0;
        }
    }

    @Override
    public void set(long offset, byte[] data, int dataOffset, int dataLen) {
        if(closed)throw new IllegalStateException("PageBuffer is closed");

        int pageSize = getPageSize();
        if( pageSize<1 ) throw new IllegalStateException("BUG!!! pageSize<1");

        // target,page,off,len
        // List<Tuple4<Integer,Integer,Integer,Integer>> pageDataLocs = new ArrayList<>();
        pageDataLocations(pageSize, offset, dataLen, (target,page,off,len)->{
            // pageDataLocs.add(Tuple4.of(target,page,off,len));
            int unchanged_after = pageSize + (off+len);
            int unchanged_before = off;

            if( unchanged_before<1 && unchanged_after<1 && len==pageSize ){
                byte[] buff = new byte[len];
                System.arraycopy(data,target, buff,0, len);
                pageDataImpl.data(page,buff);
            }else{
                byte[] buff = pageDataImpl.data(page);
                if( buff.length < (off+len) ){
                    buff = Arrays.copyOf(buff, off+len);
                }
                System.arraycopy(data,target, buff,off, len);
                pageDataImpl.data(page,buff);
            }
        });
    }

    private final static byte[] emptyBytes = new byte[0];

    @Override
    public byte[] get(long offset, int dataLen) {
        if(closed)throw new IllegalStateException("PageBuffer is closed");
        if( offset<0 )throw new IllegalArgumentException( "offset<0" );
        if( dataLen<0 )throw new IllegalArgumentException( "dataLen<0" );
        if( dataLen==0 )return emptyBytes;

        int pageSize = getPageSize();
        if( pageSize<1 ) throw new IllegalStateException("BUG!!! pageSize<1");

        byte[] result = new byte[dataLen];
        int[] err_cnt = new int[]{ 0 };
        pageDataLocations(pageSize, offset, dataLen, (target,page,off,len) -> {
            byte[] pageData = pageDataImpl.data(page);
            if( pageData.length >= off+len ){
                System.arraycopy(pageData,off, result,target, len);
            }else{
                err_cnt[0]++;
            }
        });

        if( err_cnt[0]>0 )return emptyBytes;

        return result;
    }

    @Override
    public void clear() {
        if(closed)throw new IllegalStateException("PageBuffer is closed");
        unmapAll();
        loBuffer.clear();
    }

    @Override
    public void flush() {
        if(closed)throw new IllegalStateException("PageBuffer is closed");
        hiBuffer.flush();

        for( var e : pageDataImpl.dirtyFastPages.entrySet() ){
            int fastPage = e.getKey();
            if( e.getValue() ){
                pageDataImpl.saveFastPage(fastPage);
            }
        }

        loBuffer.flush();
    }
}
