package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * Простая постраничная организация памяти
 */
public class MemFlatPaged implements Paged, ResizablePages {
    protected volatile int pageSize;
    protected volatile byte[] buffer;
    protected volatile int dataSize;
    protected volatile int maxPages = -1;

    /**
     * Конструктор
     * @param pageSize размер страницы, мин 1
     * @param capacity объем памяти в байтах, мин 1
     * @param buffer буфер памяти или null
     * @param dataSize размер используемой памяти, не должен превышать capacity
     */
    public MemFlatPaged(int pageSize, int capacity, byte[] buffer, int dataSize){
        if( pageSize<1 )throw new IllegalArgumentException( "pageSize<1" );
        if( capacity<1 )throw new IllegalArgumentException( "capacity<1" );

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

    /**
     * Конструктор
     * @param pageSize размер страницы, мин 1
     * @param capacity объем памяти в байтах, мин 1
     */
    public MemFlatPaged(int pageSize, int capacity){
        if( pageSize<1 )throw new IllegalArgumentException( "pageSize<1" );
        if( capacity<1 )throw new IllegalArgumentException( "capacity<1" );

        this.buffer = new byte[capacity];
        this.pageSize = pageSize;
        this.dataSize = capacity;
    }

    public byte[] buffer(){ return buffer; }

    //region memInfo, memoryInfo(),
    private static String toString(UsedPagesInfo m){
        return
            "UsedPagesInfo {"+
                " pageSize=" + m.pageSize() +
                " pageCount=" + m.pageCount() +
                " lastPageSize=" + m.lastPageSize()+
                " }";
    }
    public class MemInfoUsed implements UsedPagesInfo {
        @Override
        public int pageCount()
        {
            return readLock(()->{
                int pc = dataSize / pageSize;
                int pc_d = dataSize % pageSize;
                return pc_d > 0 ? pc + 1 : pc;
            });
        }

        @Override
        public int lastPageSize() {
            return readLock(()->{
                var size = dataSize % pageSize;
                return size == 0 ? pageSize : size;
            });
        }

        @Override
        public int pageSize() {
            return readLock(()->{
                return pageSize;
            });
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public UsedPagesInfo clone() {
            return readLock(()->{
                return new MemInfoUsedClone(this);
            });
        }

        @Override
        public String toString() {
            return readLock(()->{
                return MemFlatPaged.toString(this);
            });
        }
    }
    protected static class MemInfoUsedClone implements UsedPagesInfo {
        protected volatile int pageCount;
        protected volatile int pageSize;
        protected volatile int lastPageSize;

        public MemInfoUsedClone(int pageCount, int pageSize, int lastPageSize) {
            this.pageCount = pageCount;
            this.pageSize = pageSize;
            this.lastPageSize = lastPageSize;
        }

        public MemInfoUsedClone(MemInfoUsedClone sample){
            if( sample==null )throw new IllegalArgumentException( "sample==null" );
            this.pageCount = sample.pageCount;
            this.pageSize = sample.pageSize;
            this.lastPageSize = sample.lastPageSize;
        }

        public MemInfoUsedClone(MemInfoUsed sample){
            if( sample==null )throw new IllegalArgumentException( "sample==null" );
            this.pageCount = sample.pageCount();
            this.pageSize = sample.pageSize();
            this.lastPageSize = sample.lastPageSize();
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

        @Override
        public String toString() {
            return MemFlatPaged.toString(this);
        }
    }
    protected volatile MemInfoUsed memInfo = new MemInfoUsed();

    @Override
    public UsedPagesInfo memoryInfo() {
        return memInfo;
    }
    //endregion

    private static final byte[] empty_bytes = new byte[0];

    @Override
    public byte[] readPage(int page) {
        return readLock(()->{
            buffer = buffer;
            if (page < 0) throw new IllegalArgumentException("page<0");
            int off = page * pageSize;
            if (off >= dataSize) return empty_bytes;
            int tailSize = dataSize - off;
            int readSize = Math.min(tailSize, pageSize);
            byte[] buf = new byte[readSize];
            /////////////////////////////////
            //   here bug in arraycopy
            //     System.arraycopy(buffer,off, buf,0, readSize);
            //   buffer[i] - not sync (visible) in other thread
            for (int i = 0; i < readSize; i++) {
                buf[i] = buffer[off + i];
            }
            return buf;
        });
    }

    @Override
    public void writePage(int page, byte[] data) {
        readLock(()->{
            buffer = buffer;
            if (page < 0) throw new IllegalArgumentException("page<0");
            if (data == null) throw new IllegalArgumentException("data==null");
            if (data.length > pageSize) throw new IllegalArgumentException("data.length>pageSize");
            if (data.length < 1) return;

            int off = page * pageSize;
            int avail = buffer.length - off;
            if (avail <= 0) throw new PageError("out of range");
            if (avail < data.length) throw new PageError("out of range");

            /////////////////////////////////
            //   here bug in arraycopy
            //     System.arraycopy(data,0, buffer, off, data.length);
            //   buffer[i] - not sync (visible) in other thread

            var newBuff = Arrays.copyOf(buffer,buffer.length);

            for (int i = 0; i < data.length; i++) {
                newBuff[off + i] = data[i];
            }

            int end = off + data.length;
            if (end > dataSize) {
                dataSize = end;
            }

            buffer = newBuff;
            buffer = buffer;
        });
    }

    private Tuple2<UsedPagesInfo,UsedPagesInfo> extendPages(int pages) {
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
    private Tuple2<UsedPagesInfo,UsedPagesInfo> reducePages(int pages) {
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        //if( pages==0 )return Tuple2.of(memInfo, memInfo);
        UsedPagesInfo beforeChange = memInfo.clone();

        long currentPages = memoryInfo().pageCount();

        long nextPages = currentPages-pages;
        if( nextPages<0 )throw new PageError("can't reduce to negative size");

        long nextSize = nextPages * pageSize;
        buffer = Arrays.copyOf(buffer, (int)nextSize);
        dataSize = (int) nextSize;

        return Tuple2.of(beforeChange,memInfo);
    }

    @Override
    public ResizedPages resizePages(int pages) {
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        return writeLock(()-> {
            if (pages == 0) {
                var before = memoryInfo().clone();
                buffer = empty_bytes;
                dataSize = 0;
                return new ResizedPages(before, memoryInfo().clone());
            } else {
                long curPageCnt = memoryInfo().pageCount();
                long nxtPageCnt = pages;
                long diffPgeCnt = nxtPageCnt - curPageCnt;
                if (diffPgeCnt > 0) {
                    if (diffPgeCnt > Integer.MAX_VALUE) {
                        throw new PageError("can't extend over " + diffPgeCnt + ", Integer.MAX_VALUE");
                    }
                    var ext = extendPages((int) diffPgeCnt);
                    return new ResizedPages(ext.a(), ext.b());
                } else {
                    long abs_diff = -diffPgeCnt;
                    if (abs_diff > Integer.MAX_VALUE) {
                        throw new PageError("can't reduce over " + abs_diff + ", Integer.MAX_VALUE");
                    }
                    var red = reducePages((int) abs_diff);
                    return new ResizedPages(red.a(), red.b());
                }
            }
        });
    }

    //#region read/write lock
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public <R> R readLock(Supplier<R> code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.readLock().lock();
            return code.get();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
    public void readLock(Runnable code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.readLock().lock();
            code.run();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
    public <R> R writeLock(Supplier<R> code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.writeLock().lock();
            return code.get();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    public void writeLock(Runnable code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.writeLock().lock();
            code.run();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    //#endregion
}
