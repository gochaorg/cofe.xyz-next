package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Consumer1;
import xyz.cofe.fn.Tuple2;

import java.util.Arrays;

public abstract class DirtyPagedDataBase<M extends UsedPagesInfo, S extends DirtyPagedState> implements ResizablePages<M> {
    protected ResizablePages<M> pagedData;

    protected abstract S state();


    public DirtyPagedDataBase(ResizablePages<M> pagedData){
        if( pagedData==null )throw new IllegalArgumentException( "pagedData==null" );
        this.pagedData = pagedData;

        UsedPagesInfo minf = pagedData.memoryInfo();
        int pageCnt = minf.pageCount();

        long[] flushTime = new long[pageCnt];
        long[] writeTime = new long[pageCnt];
        long[] readTime = new long[pageCnt];
        Arrays.fill(flushTime,0);
        Arrays.fill(writeTime,0);
        Arrays.fill(readTime,0);

        state().flushTime(flushTime);
        state().writeTime(writeTime);
        state().readTime(readTime);
    }

    protected long now(){ return System.nanoTime(); }

    /**
     * Отмечает страницу как чистую.
     *
     * <p>
     * Обновляет <code>flushTime[page] = now()</code>
     * @param page индекс страницы
     */
    public void flushPage( int page ){
        if( page<0 )throw new IllegalArgumentException( "page(="+page+" <0) out of range" );
        long[] flushTime = state().flushTime();
        if( page<flushTime.length ){
            flushTime[page] = now();
            state().flushTime(flushTime);
        }
    }

    /**
     * Проверяет что страница "грязная".
     * @param page страница
     * @return true - грязная - т.е. <code>flushTime[page] &lt;= writeTime[page]</code>
     */
    public boolean dirty( int page ){
        if( page<0 )throw new IllegalArgumentException( "page(="+page+" <0) out of range" );
        long[] flushTime = state().flushTime();
        long[] writeTime = state().writeTime();
        if( page<flushTime.length ){
            long ft = flushTime[page];
            long wt = writeTime[page];
            return ft<=wt;
        }
        throw new IllegalArgumentException( "page(="+page+") out of range" );
    }

    /**
     * Обход всех грязных страниц
     * @param dirtyPage грязная страница
     */
    public void dirtyPages(Consumer1<Integer> dirtyPage){
        if( dirtyPage==null )throw new IllegalArgumentException( "dirtyPage==null" );
        long[] flushTime = state().flushTime();
        long[] writeTime = state().writeTime();
        int pc = Math.min(flushTime.length, writeTime.length);
        for( int i=0; i<pc; i++ ){
            long ft = flushTime[i];
            long wt = writeTime[i];
            if( ft<=wt ){
                dirtyPage.accept(i);
            }
        }
    }

    @Override
    public M memoryInfo() {
        return pagedData.memoryInfo();
    }

    /**
     * Чтение страницы.
     *
     * <p>
     * Обновляет <code>readTime[page] = now()</code>
     * @param page индекс страницы, от 0 и более
     * @return массив байтов, по размеру равный {@link UsedPagesInfo#pageSize()} или меньше, если последняя страница
     */
    @Override
    public byte[] readPage(int page) {
        byte[] data = pagedData.readPage(page);
        long[] readTime = state().readTime();
        if( page>=0 && page<readTime.length ){
            readTime[page] = now();
        }
        state().readTime(readTime);
        return data;
    }

    /**
     * Запись страницы
     * <p>
     * Обновляет <code>writeTime[page] = now()</code>
     * @param page индекс страницы, от 0 и более
     * @param data массив байтов, размер не должен превышать {@link UsedPagesInfo#pageSize()}
     */
    @Override
    public void writePage(int page, byte[] data) {
        pagedData.writePage(page, data);
        long[] writeTime = state().writeTime();
        if( page>=0 && page<writeTime.length ){
            writeTime[page] = now();
        }
        state().writeTime(writeTime);
    }

    protected void onChangeResizePages(Tuple2<UsedPagesInfo, UsedPagesInfo> changes){
        if( changes==null )throw new IllegalArgumentException( "changes==null" );
        UsedPagesInfo before = changes.a();
        UsedPagesInfo now = changes.b();
        int pc = now.pageCount();
        long[] flushTime = state().flushTime();
        long[] writeTime = state().writeTime();
        long[] readTime = state().readTime();

        flushTime = Arrays.copyOf(flushTime, pc);
        writeTime = Arrays.copyOf(writeTime, pc);
        readTime = Arrays.copyOf(readTime, pc);

        int extCnt = now.pageCount() - before.pageCount();

        if( extCnt>0 ){
            for( int i=before.pageCount(); i<now.pageCount(); i++ ){
                flushTime[i] = 0;
                writeTime[i] = 0;
                readTime[i] = 0;
            }
        }

        state().writeTime(writeTime);
        state().flushTime(flushTime);
        state().readTime(readTime);
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> extendPages(int pages) {
        Tuple2<UsedPagesInfo, UsedPagesInfo> memChangeInfo = pagedData.extendPages(pages);
        onChangeResizePages(memChangeInfo);
        return memChangeInfo;
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> reducePages(int pages) {
        Tuple2<UsedPagesInfo, UsedPagesInfo> memChangeInfo = pagedData.reducePages(pages);
        onChangeResizePages(memChangeInfo);
        return memChangeInfo;
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> resizePages(int pages) {
        Tuple2<UsedPagesInfo, UsedPagesInfo> memChangeInfo = pagedData.resizePages(pages);
        onChangeResizePages(memChangeInfo);
        return memChangeInfo;
    }
}
