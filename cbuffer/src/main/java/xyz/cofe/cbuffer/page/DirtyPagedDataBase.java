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

        state().resizePages(pagedData.memoryInfo().pageCount());
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
        state().flushed(page,now());
    }

    /**
     * Проверяет что страница "грязная".
     * @param page страница
     * @return true - грязная - т.е. <code>flushTime[page] &lt;= writeTime[page]</code>
     */
    public boolean dirty( int page ){
        if( page<0 )throw new IllegalArgumentException( "page(="+page+" <0) out of range" );
        DirtyPagedState.PageState ps = state().page(page).orElseThrow(()->new IllegalArgumentException("page="+page+" not found"));
        return ps.dirty();
    }

    /**
     * Обход всех грязных страниц
     * @param dirtyPage грязная страница
     */
    public void dirtyPages(Consumer1<Integer> dirtyPage){
        if( dirtyPage==null )throw new IllegalArgumentException( "dirtyPage==null" );

        state().each( ps -> {
            if( ps.dirty() ){
                dirtyPage.accept(ps.page());
            }
        });
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
        state().read(page,now());
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
        state().written(page,now());
    }

    protected void onChangeResizePages(Tuple2<UsedPagesInfo, UsedPagesInfo> changes){
        if( changes==null )throw new IllegalArgumentException( "changes==null" );
        state().resizePages(changes.b().pageCount());
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
