package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;
import xyz.cofe.cbuffer.Flushable;
import xyz.cofe.fn.Tuple2;

public class CBuffPagedData
implements PagedData, Flushable, ResizablePages
{
    protected final ContentBuffer cbuff;
    protected final int pageSize;
    protected long maxSize = -1;
    protected boolean resizeable = false;

    public CBuffPagedData(ContentBuffer cbuff, int pageSize, boolean resizeable, long maxSize){
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
        long s = cbuff.getSize();
        if( s<=0 )return UsedPagesInfo.of(0,0,0);
        long pc = s / pageSize;
        if( pc>Integer.MAX_VALUE )throw new IllegalStateException("pageCount more than Integer.MAX_VALUE");

        long pc_1 = s % pageSize;
        long c = (pc_1 > 0 ? pc+1 : pc);
        if( c>Integer.MAX_VALUE )throw new IllegalStateException("pageCount more than Integer.MAX_VALUE");

        return UsedPagesInfo.of(pageSize, (int)c, (int)pc_1);
    }

    @Override
    public byte[] readPage(int page) {
        if( page<0 )throw new IllegalArgumentException( "page<0" );

        long total = cbuff.getSize();
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

        long total = cbuff.getSize();
        if( total<=0 )throw new IllegalStateException( "pages not exists" );

        int pages = memoryInfo().pageCount();
        if( page>=pages ){
            if( !resizeable )throw new IllegalStateException("can't resize to "+(page+1)+" pages; not resizeable");

            long targetSize = ((long)pages * pageSize) + data.length;
            if( targetSize>maxSize && maxSize>1 )throw new IllegalStateException("can't resize to "+(page+1)+" pages; limit by maxSize(="+maxSize+")");
            long currSize = cbuff.getSize();

            if( currSize<targetSize )cbuff.setSize(targetSize);
        }

        long off = ((long)pages * pageSize);
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
    public Tuple2<UsedPagesInfo, UsedPagesInfo> extendPages(int pages) {
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        if( pages==0 )return Tuple2.of(memoryInfo().clone(), memoryInfo());
        if( !resizeable )throw new IllegalStateException("can't resize, not resizeable");

        UsedPagesInfo pi = memoryInfo();

        long targetPages = pi.pageCount() + (long)pageSize;
        if( targetPages>Integer.MAX_VALUE )throw new IllegalArgumentException("can't extend to "+targetPages+" pages; is more than Integer.MAX_VALUE");

        long targetSize = targetPages * pi.pageSize();
        if( targetSize>maxSize && maxSize>0 )throw new IllegalArgumentException("can't extend to "+targetSize+" bytes; is more than maxSize(="+maxSize+")");

        pi = pi.clone();
        cbuff.setSize(targetSize);

        return Tuple2.of(pi, memoryInfo());
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> reducePages(int pages) {
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        if( pages==0 )return Tuple2.of(memoryInfo().clone(), memoryInfo());
        if( !resizeable )throw new IllegalStateException("can't resize, not resizeable");

        UsedPagesInfo pi = memoryInfo();

        long targetPages = pi.pageCount() + (long)pageSize;
        if( targetPages<0 )throw new IllegalArgumentException("can't reduce to "+targetPages+" pages; is less than 0");

        long targetSize = targetPages * pi.pageSize();
        pi = pi.clone();
        cbuff.setSize(targetSize);

        return Tuple2.of(pi, memoryInfo());
    }
}
