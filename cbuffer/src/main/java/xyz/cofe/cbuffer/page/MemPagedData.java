package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Простая постраничная организация памяти
 */
public class MemPagedData implements PagedData, ExtendablePages, ReduciblePages, ResizablePages {
    protected int pageSize;
    protected byte[] buffer;
    protected int dataSize;
    protected int maxPages = -1;

    public MemPagedData(int pageSize, int capacity, byte[] buffer, int dataSize){
        if( pageSize<1 )throw new IllegalArgumentException( "pageSize<1" );
        if( capacity<1 )throw new IllegalArgumentException( "capacity<1" );

        int ext = capacity % pageSize;
        if( ext>0 )throw new IllegalArgumentException(
            "capacity(="+capacity+") not align by pageSize(="+pageSize+"); " +
                "capacity % pageSize = "+ext+" > 0"
        );

        if( dataSize<0 )throw new IllegalArgumentException( "dataSize<0" );
        if( dataSize>capacity )throw new IllegalArgumentException( "dataSize>capacity" );

        if( buffer!=null ){
            if( buffer.length!=capacity ){
                throw new IllegalArgumentException( "buffer.length!=capacity" );
            }
            this.buffer = buffer;
            this.pageSize = pageSize;
            this.dataSize = dataSize;
        }else{
            this.buffer = new byte[capacity];
            this.pageSize = pageSize;
            this.dataSize = 0;
        }
    }
    public MemPagedData(int pageSize, int capacity){
        if( pageSize<1 )throw new IllegalArgumentException( "pageSize<1" );
        if( capacity<1 )throw new IllegalArgumentException( "capacity<1" );

        int ext = capacity % pageSize;
        if( ext>0 )throw new IllegalArgumentException(
            "capacity(="+capacity+") not align by pageSize(="+pageSize+"); " +
                "capacity % pageSize = "+ext+" > 0"
        );

        this.buffer = new byte[capacity];
        this.pageSize = pageSize;
        this.dataSize = 0;
    }

    //region memInfo, memoryInfo(),
    protected class MemInfoUsed implements UsedPagesInfo, Capacity {
        @Override
        public int pageCount() {
            int pc = dataSize / pageSize;
            int pc_d = dataSize % pageSize;
            return pc_d>0 ? pc+1 : pc;
        }

        @Override
        public int lastPageSize() {
            return dataSize % pageSize;
        }

        @Override
        public int pageSize() {
            return pageSize;
        }

        @Override
        public BigInteger capacity() {
            return BigInteger.valueOf(buffer.length);
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public UsedPagesInfo clone() {
            return new MemInfoUsedClone(this);
        }
    }
    protected static class MemInfoUsedClone implements UsedPagesInfo, Capacity {
        protected BigInteger capacity;
        protected int pageCount;
        protected int pageSize;
        protected int lastPageSize;

        public MemInfoUsedClone(BigInteger capacity, int pageCount, int pageSize, int lastPageSize) {
            this.capacity = capacity;
            this.pageCount = pageCount;
            this.pageSize = pageSize;
            this.lastPageSize = lastPageSize;
        }

        public MemInfoUsedClone(MemInfoUsedClone sample){
            if( sample==null )throw new IllegalArgumentException( "sample==null" );
            this.capacity = sample.capacity;
            this.pageCount = sample.pageCount;
            this.pageSize = sample.pageSize;
            this.lastPageSize = sample.lastPageSize;
        }

        public MemInfoUsedClone(MemInfoUsed sample){
            if( sample==null )throw new IllegalArgumentException( "sample==null" );
            this.capacity = sample.capacity();
            this.pageCount = sample.pageCount();
            this.pageSize = sample.pageSize();
            this.lastPageSize = sample.lastPageSize();
        }

        @Override
        public BigInteger capacity() {
            return capacity;
        }

        @Override
        public int pageCount() {
            return pageCount;
        }

        @Override
        public int lastPageSize() {
            return lastPageSize;
        }

        @Override
        public int pageSize() {
            return pageSize;
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public UsedPagesInfo clone() {
            return new MemInfoUsedClone(this);
        }
    }
    protected MemInfoUsed memInfo = new MemInfoUsed();

    @Override
    public UsedPagesInfo memoryInfo() { return memInfo; }
    //endregion

    private static final byte[] empty_bytes = new byte[0];

    @Override
    public byte[] readPage(int page) {
        if( page<0 )throw new IllegalArgumentException( "page<0" );
        int off = page * pageSize;
        if( off>=dataSize )return empty_bytes;
        int tailSize = dataSize - off;
        int readSize = Math.min(tailSize, pageSize);
        byte[] buf = new byte[readSize];
        System.arraycopy(buffer,off, buf,0, readSize);
        return buf;
    }

    @Override
    public void writePage(int page, byte[] data) {
        if( page<0 )throw new IllegalArgumentException( "page<0" );
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( data.length>pageSize )throw new IllegalArgumentException( "data.length>pageSize" );
        if( data.length<1 )return;

        int off = page*pageSize;
        int avail = buffer.length - off;
        if( avail<=0 )throw new IllegalArgumentException("out of range");
        if( avail<data.length )throw new IllegalArgumentException("out of range");

        System.arraycopy(data,0, buffer, off, data.length);
        int end = off+data.length;
        if( end>dataSize ){
            dataSize = end;
        }
    }

    @Override
    public Tuple2<UsedPagesInfo,UsedPagesInfo> extendPages(int pages) {
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        if( pages==0 )return Tuple2.of(memInfo, memInfo);

        UsedPagesInfo beforeChange = memInfo.clone();

        long currentPages = memoryInfo().pageCount();

        long nextPages = currentPages+pages;
        long nextSize = nextPages * pageSize;
        if( nextSize>Integer.MAX_VALUE ) throw new OutOfMemoryError("can't extend, limit by Integer.MAX_VALUE");
        if( (maxPages>0 && nextPages>maxPages) )throw new OutOfMemoryError("can't extend, limit by maxPages");

        buffer = Arrays.copyOf(buffer, (int)nextSize);
        dataSize = (int) nextSize;
        return Tuple2.of(beforeChange,memInfo);
    }

    @Override
    public Tuple2<UsedPagesInfo,UsedPagesInfo> reducePages(int pages) {
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        if( pages==0 )return Tuple2.of(memInfo, memInfo);
        UsedPagesInfo beforeChange = memInfo.clone();

        long currentPages = memoryInfo().pageCount();

        long nextPages = currentPages-pages;
        if( nextPages<0 )throw new IllegalArgumentException("can't reduce to negative size");

        long nextSize = nextPages * pageSize;
        buffer = Arrays.copyOf(buffer, (int)nextSize);
        dataSize = (int) nextSize;

        return Tuple2.of(beforeChange,memInfo);
    }
}
