package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

import java.util.Arrays;

/**
 * Обвертка над {@link PagedData} с учетом грязных страниц
 */
public class DirtyPagedData implements ResizablePages {
    protected ResizablePages pagedData;

    protected long[] flushTime;
    protected long[] writeTime;
    protected long[] readTime;

    public DirtyPagedData(ResizablePages pagedData){
        if( pagedData==null )throw new IllegalArgumentException( "pagedData==null" );
        this.pagedData = pagedData;

        UsedPagesInfo minf = pagedData.memoryInfo();
        int pageCnt = minf.pageCount();
        flushTime = new long[pageCnt];
        writeTime = new long[pageCnt];
        readTime = new long[pageCnt];
        Arrays.fill(flushTime,0);
        Arrays.fill(writeTime,0);
        Arrays.fill(readTime,0);
    }

    protected long now(){ return System.nanoTime(); }

    public void flushPage( int page ){
        if( page<0 )throw new IllegalArgumentException( "page(="+page+" <0) out of range" );
        if( page<flushTime.length ){
            flushTime[page] = now();
        }
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        return pagedData.memoryInfo();
    }

    @Override
    public byte[] readPage(int page) {
        byte[] data = pagedData.readPage(page);
        if( page>=0 && page<readTime.length ){
            readTime[page] = now();
        }
        return data;
    }

    @Override
    public void writePage(int page, byte[] data) {
        pagedData.writePage(page, data);
        if( page>=0 && page<writeTime.length ){
            writeTime[page] = now();
        }
    }

    protected void onChangeResizePages(Tuple2<UsedPagesInfo, UsedPagesInfo> changes){
        if( changes==null )throw new IllegalArgumentException( "changes==null" );
        UsedPagesInfo before = changes.a();
        UsedPagesInfo now = changes.b();
        int pc = now.pageCount();
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
