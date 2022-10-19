package xyz.cofe.cbuffer.page;

import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MemChunkPaged implements Paged, ResizablePages {
    public static class Chunk {
        public volatile byte[] data;
        public volatile int firstInt;
        public Chunk(int pageSize){
            data = new byte[pageSize];
        }

        public static int readInt(byte[] data, int offset){
            var b0 = data[offset+3] & 0xFF;
            var b1 = data[offset+2] & 0xFF;
            var b2 = data[offset+1] & 0xFF;
            var b3 = data[offset+0] & 0xFF;
            return (b3 << 8*3) | (b2 << 8*2) | (b1 << 8) | b0;
        }

        public static void writeInt(byte[] data, int offset, int value ){
            var b0 = (value >> 8*3) & 0xFF;
            var b1 = (value >> 8*2) & 0xFF;
            var b2 = (value >> 8*1) & 0xFF;
            var b3 = (value >> 8*0) & 0xFF;
            data[offset+0] = (byte)b0;
            data[offset+1] = (byte)b1;
            data[offset+2] = (byte)b2;
            data[offset+3] = (byte)b3;
        }

        public synchronized byte[] read(){
            data = data;
            var res = new byte[data.length];
            for(var i=0;i<res.length;i++ )res[i] = data[i];
            return res;
        }
        public synchronized void write(byte[] newData){
            data = data;
            if( data.length!=newData.length )throw new IllegalStateException("!!!");
            for( var i=0;i< data.length; i++ ){
                data[i] = newData[i];
            }
            firstInt = readInt(data,0);
            data = data;
        }
        public synchronized int size(){ return data.length; }
    }

    protected final int pageSize;
    public MemChunkPaged(int pageSize){
        this.pageSize = pageSize;
    }

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    public volatile Chunk[] chunks = new Chunk[0];

    @Override
    public UsedPagesInfo memoryInfo() {
        try {
            rwLock.readLock().lock();
            return UsedPagesInfo.of(
                pageSize,
                chunks.length,
                chunks.length > 0 ?
                    chunks[chunks.length - 1].size() : 0
            );
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public ResizedPages resizePages(int pages) {
        if(pages<0)throw new IllegalArgumentException("pages<0");
        try {
            rwLock.writeLock().lock();
            var adds = pages - chunks.length;
            var newChunks = Arrays.copyOf(chunks,pages);
            if( adds>0 ){
                var from=chunks.length;
                for( var i=0;i<adds;i++ ){
                    newChunks[from+i] = new Chunk(pageSize);
                }
            }
            chunks = newChunks;
        }finally {
            rwLock.writeLock().unlock();;
        }
        return null;
    }

    @Override
    public synchronized byte[] readPage(int page) {
        if(page<0)throw new IllegalArgumentException("page<0");
        try {
            rwLock.readLock().lock();
            if(page>=chunks.length)throw new IllegalArgumentException("page>=chunks.length");
            var ch = chunks[page];
            synchronized (ch) {
                return chunks[page].read();
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public synchronized void writePage(int page, byte[] data) {
        if(page<0)throw new IllegalArgumentException("page<0");
        if(data==null)throw new IllegalArgumentException("data==null");
        try {
            rwLock.readLock().lock();
            if(page>=chunks.length)throw new IllegalArgumentException("page>=chunks.length");
            var writeData = data.length!=page ? Arrays.copyOf(data,pageSize) : data;
            var ch = chunks[page];
            synchronized (ch) {
                chunks[page].write(writeData);
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
