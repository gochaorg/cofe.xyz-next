package xyz.cofe.cbuffer;

import java.nio.ByteBuffer;

/**
 * Простая постраничная организация памяти
 */
public class MemPagedData implements PagedData {
    protected int pageSize;
    protected byte[] buffer;
    protected int dataSize;

    public MemPagedData(int pageSize, int capacity, byte[] buffer, int dataSize){
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

    public MemPagedData(int pageSize, int capacity){
        if( pageSize<1 )throw new IllegalArgumentException( "pageSize<1" );
        if( capacity<1 )throw new IllegalArgumentException( "capacity<1" );
        this.buffer = new byte[capacity];
        this.pageSize = pageSize;
        this.dataSize = 0;
    }

    protected class MemInfo implements PagedData.MemoryInfo {
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
    }

    protected MemInfo memInfo = new MemInfo();

    @Override
    public MemoryInfo memoryInfo() { return memInfo; }

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
}
