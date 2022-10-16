package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;
import xyz.cofe.cbuffer.Flushable;

/**
 * Страничный буфер поверх обычного
 */
public class CBuffPaged implements Flushable, Paged, ResizablePages {
    protected final ContentBuffer cbuff;
    protected final int pageSize;
    protected volatile long maxSize = -1;
    protected boolean resizeable = false;

    /**
     * Конструктор
     * @param cbuff исходный буфер
     * @param pageSize размер страницы
     * @param resizeable возможно изменение размера страничного буфера
     * @param maxSize максимальный размер или -1 без ограничения
     */
    public CBuffPaged(ContentBuffer cbuff, int pageSize, boolean resizeable, long maxSize){
        if( cbuff==null )throw new IllegalArgumentException( "cbuff==null" );
        if( pageSize<1 )throw new IllegalArgumentException( "pageSize<1" );
        if( maxSize>1 ){
            if( (maxSize % pageSize)>0 )throw new IllegalArgumentException("maxSize not aligned by pageSize");
        }
        this.pageSize = pageSize;
        this.cbuff = cbuff;
        this.maxSize = maxSize;
        this.resizeable = resizeable;
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        long total = maxSize>=0 ? Math.min(cbuff.getSize(),maxSize) : cbuff.getSize();
        if( total<=0 )return UsedPagesInfo.of(0,0,0);
        long pc = total / pageSize;
        if( pc>Integer.MAX_VALUE )throw new IllegalStateException("pageCount more than Integer.MAX_VALUE");

        long pc_1 = total % pageSize;
        long c = (pc_1 > 0 ? pc+1 : pc);
        if( c>Integer.MAX_VALUE )throw new IllegalStateException("pageCount more than Integer.MAX_VALUE");

        return UsedPagesInfo.of(pageSize, (int)c, (int)(pc_1==0 ? pageSize : pc_1));
    }

    @Override
    public byte[] readPage(int page) {
        if( page<0 )throw new IllegalArgumentException( "page<0" );

        long total = maxSize>=0 ? Math.min(cbuff.getSize(),maxSize) : cbuff.getSize();
        if( total<=0 )return new byte[0];

        int pages = memoryInfo().pageCount();
        if( page>=pages )return new byte[0];

        long off = (long)page * pageSize;
        long avail = total - off;
        if( avail<=0 )return new byte[0];
        int avail_i = avail > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)avail;

        int read = Math.min(avail_i,pageSize);

        return cbuff.get(off,read);
    }

    @Override
    public void writePage(int page, byte[] data) {
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( data.length<1 )return;
        if( data.length>pageSize )throw new IllegalArgumentException( "data.length(="+data.length+") > pageSize(="+pageSize+")" );

        if( page<0 )throw new IllegalArgumentException( "page<0" );

        long total = maxSize>=0 ? Math.min(cbuff.getSize(),maxSize) : cbuff.getSize();
        if( total<=0 )throw new PageError( "pages not exists" );

        int pages = memoryInfo().pageCount();
        if( page>=pages ){
            if( !resizeable )throw new PageError("can't resize to "+(page+1)+" pages; not resizeable");

            long targetSize = ((long)pages * pageSize) + data.length;
            if( targetSize>maxSize && maxSize>=0 )throw new PageError("can't resize to "+(page+1)+" pages; limit by maxSize(="+maxSize+")");
            long currSize = cbuff.getSize();

            if( currSize<targetSize )cbuff.setSize(targetSize);
        }

        long off = ((long)page * pageSize);
        if( data.length < pageSize ){
            byte[] buff = cbuff.get(off, pageSize);
            System.arraycopy(data,0,buff,0,data.length);
            cbuff.set(off,buff,0,buff.length);
        }else {
            cbuff.set(off,data,0,data.length);
        }
    }

    @Override
    public void flush() {
        cbuff.flush();
    }

    @Override
    public ResizedPages resizePages(int pages) {
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        if( !resizeable )throw new IllegalStateException("not resizeable");

        if( pages==0 ){
            var before = memoryInfo().clone();
            cbuff.setSize(0);
            return new ResizedPages(before,memoryInfo().clone());
        }else {
            var before = memoryInfo().clone();

            cbuff.setSize(pageSize*((long)pages));

            var after = memoryInfo().clone();
            return new ResizedPages(before,after);
        }
    }
}
