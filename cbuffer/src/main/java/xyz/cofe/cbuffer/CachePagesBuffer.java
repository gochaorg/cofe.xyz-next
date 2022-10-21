package xyz.cofe.cbuffer;

import xyz.cofe.cbuffer.page.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Страничная организация памяти с поддержкой кеширования
 * @param <CACHEPAGES> Кеш память
 * @param <PERSISTPAGES> Постоянная память
 */
public class CachePagesBuffer<CACHEPAGES extends Paged & ResizablePages, PERSISTPAGES extends Paged & ResizablePages> implements ContentBuffer {
    public CachePagesBuffer(CachePaged<CACHEPAGES,PERSISTPAGES> pages){
        if(pages ==null) throw new IllegalArgumentException("cachePaged==null");
        this.pages = pages;
    }

    private final CachePaged<CACHEPAGES,PERSISTPAGES> pages;
    public CachePaged<CACHEPAGES,PERSISTPAGES> getPages(){ return pages; }

    @Override
    public long getSize() {
        var memInfo = getPages().memoryInfo();
        return memInfo.pageCount()==0 ? 0L : ((long)(memInfo.pageCount()-1))*memInfo.pageSize() + memInfo.lastPageSize();
    }

    @Override
    public void setSize(long size) {
        if( size<0 )throw new IllegalArgumentException("size<0");
        if( size==0 ){
            // todo may be bug
            pages.resizeCachePages(0);
            pages.resizePages(0);
            return;
        }

        var memInfo = getPages().memoryInfo();
        long pc_rest = size % memInfo.pageSize();
        long pc = size / memInfo.pageSize() + (pc_rest>0 ? 1 : 0);
        if( pc>Integer.MAX_VALUE ){
            throw new IllegalArgumentException("overflow pages count, over Integer.MAX_VALUE");
        }

        getPages().resizePages((int)pc);
    }

    @Override
    public void set(long offset, byte[] data, int dataOffset, int dataLen) {
        if( offset<0 ) throw new IllegalArgumentException("offset<0");
        if( dataLen<0 )throw new IllegalArgumentException( "dataLen<0" );
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset<0" );
        if( data.length < (dataOffset+dataLen) )throw new IllegalArgumentException( "data.length(="+data.length+") < (dataOffset(="+dataOffset+")+dataLen(="+dataLen+"))" );
        if( dataLen==0 )return;

        int page_size = pages.memoryInfo().pageSize();
        if( page_size<1 )throw new IllegalStateException("pageSize(="+page_size+") to small, must be 1 or greater");

        long page_from = offset / page_size;
        if( page_from>Integer.MAX_VALUE )throw new IllegalStateException("can't write page(="+page_from+") > Integer.MAX_VALUE");

        long page_to = ((offset+dataLen) / page_size) + 1;
        if( page_to>Integer.MAX_VALUE )throw new IllegalStateException("can't write page(="+page_to+") > Integer.MAX_VALUE");

        Runnable write = ()->{
            int page = (int)page_from;
            int page_off = (int)(offset % page_size);
            int writed = 0;
            byte[] page_buff = new byte[page_size];
            while (writed<dataLen){
                int page_av = page_size - page_off;
                int avail = dataLen - writed;
                int write_size = Math.min(avail,page_av);
                if( page_off>0 || write_size!=page_size ){
                    byte[] page_data = pages.readPage(page);
                    if( page_data.length<page_size )page_data = Arrays.copyOf(page_data,page_size);

                    System.arraycopy(data,writed+dataOffset, page_data, page_off, write_size);
                    pages.writePage(page, page_data);
                }else{
                    System.arraycopy(data, writed+dataOffset, page_buff, 0, page_size);
                    pages.writePage(page, page_buff);
                }
                page_off = 0;
                page++;
                writed += write_size;
            }
        };

        pages.writePersistentLock((int)page_from, (int)page_to, write);
    }

    @Override
    public byte[] get(long offset, int dataLen) {
        if( offset<0 )throw new IllegalArgumentException( "offset<0" );
        if( dataLen<0 )throw new IllegalArgumentException( "dataLen<0" );
        if( dataLen==0 )return new byte[0];

        long total_bytes_count = getSize();
        if( offset>=total_bytes_count )return new byte[0];

        int pageSize = pages.memoryInfo().pageSize();
        if( pageSize<1 )throw new IllegalStateException("pageSize(="+pageSize+") to small, must be 1 or greater");

        long pageFrom = offset / pageSize;
        if( pageFrom>Integer.MAX_VALUE )throw new IllegalStateException("can't write page(="+pageFrom+") > Integer.MAX_VALUE");

        long pageTo = ((offset+dataLen) / pageSize) + 1;
        if( pageTo>Integer.MAX_VALUE )throw new IllegalStateException("can't write page(="+pageTo+") > Integer.MAX_VALUE");

        int initialPageOffset = (int)(offset % pageSize);

        return pages.readPersistentLock((int)pageFrom,(int)pageTo,()->{
            var ba = new ByteArrayOutputStream();
            var page = (int)pageFrom;
            var off = initialPageOffset;
            while (ba.size()<dataLen){
                var bytes = pages.readPage(page);
                if( off>=bytes.length ){
                    break;
                }else{
                    var avail = bytes.length - off;
                    var rest = dataLen - ba.size();
                    var readSize = Math.min(rest,avail);
                    ba.write(bytes,off,readSize);
                    page++;
                    off=0;
                }
            }
            return ba.toByteArray();
        });
    }

    @Override
    public void clear() {
        setSize(0);
    }

    @Override
    public void flush() {
        pages.flush();
    }

    @Override
    public void close() {
    }
}
