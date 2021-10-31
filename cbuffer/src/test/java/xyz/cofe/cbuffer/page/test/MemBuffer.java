package xyz.cofe.cbuffer.page.test;

import java.util.Arrays;
import xyz.cofe.cbuffer.ContentBuffer;

public class MemBuffer implements ContentBuffer {
    protected byte[] buffer;
    protected int dataSize;
    protected int extendSize=1024*64;

    public MemBuffer(){
        buffer = new byte[0];
        dataSize = 0;
    }

    public MemBuffer(int capacity, int extendSize){
        buffer = new byte[Math.max(capacity,0)];
        this.extendSize = Math.max(extendSize,512);
        dataSize = 0;
    }

    public byte[] getBuffer(){ return buffer; }

    @Override
    public long getSize() {
        return dataSize;
    }

    protected void extend( int targetSize ){
        int realSize = targetSize;

        int extra = targetSize % extendSize;
        if( extra>0 ){
            realSize = ((targetSize / extendSize) + 1) * (extendSize);
        }

        buffer = Arrays.copyOf(buffer,realSize);
    }

    @Override
    public void setSize(long size) {
        if( size<0 )throw new IllegalArgumentException( "size<0" );
        if( size>Integer.MAX_VALUE )throw new IllegalArgumentException( "size>Integer.MAX_VALUE" );

        if( size<=buffer.length ){
            dataSize = (int)size;
        }else {
            extend((int) size);
            dataSize = (int)size;
        }
    }

    @Override
    public void set(long offset, byte[] data, int dataOffset, int dataLen) {
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset<0" );
        if( dataLen<0 )throw new IllegalArgumentException( "dataLen<0" );
        if( dataLen==0 )return;
        if( offset<0 )throw new IllegalArgumentException( "offset<0" );
        if( offset>Integer.MAX_VALUE )throw new IllegalArgumentException( "offset>Integer.MAX_VALUE" );

        int oldSize = this.buffer.length;
        int targetSize = (int)offset + dataLen;
        if( targetSize>oldSize ){
            extend(targetSize);
        }

        System.arraycopy(data, dataOffset, this.buffer, (int)offset, dataLen);

        dataSize = targetSize;
    }

    @Override
    public byte[] get(long offset, int dataLen) {
        if( offset>Integer.MAX_VALUE )return new byte[]{};
        if( (int)offset >= dataSize )return new byte[]{};

        if( dataSize<1 )return new byte[]{};

        int minIdx = (int)offset;

        int targetEnd = (int)offset + dataLen;
        if( targetEnd > dataSize )targetEnd = dataSize;

        return Arrays.copyOfRange(this.buffer, minIdx, targetEnd);
    }

    @Override
    public void clear() {
        this.buffer = new byte[0];
        this.dataSize = 0;
    }

    @Override
    public void flush() { }

    @Override
    public void close() { }
}
