package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

public class SlowPagedData implements ResizablePages<UsedPagesInfo> {
    protected final ResizablePages<UsedPagesInfo> target;
    protected final long delay;

    public SlowPagedData(ResizablePages resizablePages, long delay){
        if( resizablePages==null )throw new IllegalArgumentException( "resizablePages==null" );
        this.target = resizablePages;
        this.delay = delay;
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> extendPages(int pages) {
        return target.extendPages(pages);
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        return target.memoryInfo();
    }

    @Override
    public byte[] readPage(int page) {
        if( delay>0 ){
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return target.readPage(page);
    }

    @Override
    public void writePage(int page, byte[] data) {
        if( delay>0 ){
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        target.writePage(page, data);
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> reducePages(int pages) {
        return target.reducePages(pages);
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> resizePages(int pages) {
        return target.resizePages(pages);
    }
}
