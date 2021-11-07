package xyz.cofe.cbuffer;

import xyz.cofe.cbuffer.ContentBuffer;
import xyz.cofe.cbuffer.Flushable;
import xyz.cofe.cbuffer.page.PageLock;
import xyz.cofe.cbuffer.page.ResizablePages;
import xyz.cofe.cbuffer.page.UsedPagesInfo;

import java.util.Arrays;

/**
 * Буфер страниц
 */
public class PagesBuffer implements ContentBuffer {
    protected final ResizablePages pages;

    public PagesBuffer(ResizablePages pages){
        if( pages==null )throw new IllegalArgumentException( "pages==null" );
        this.pages = pages;
    }

    @Override
    public long getSize() {
        UsedPagesInfo upi = pages.memoryInfo();
        int ps = upi.pageSize();
        if( ps<=0 )return 0;

        int pc = upi.pageCount();
        if( pc<=0 )return 0;

        int lps = upi.lastPageSize();
        if( lps==ps ){
            return (long)ps * pc;
        } else if( lps<ps ){
            long s = (long)pc * ps;
            s -= ps - lps;
            return s;
        } else {
            return (long)pc * ps;
        }
    }

    @Override
    public void setSize(long size) {
        if( size<0 )throw new IllegalArgumentException( "size<0" );
        if( size==0 ){
            pages.resizePages(0);
        }else {
            UsedPagesInfo upi = pages.memoryInfo();
            int ps = upi.pageSize();
            if( ps<=0 )throw new IllegalStateException("page size <= 0");

            int pc = upi.pageCount();
            int lps = upi.lastPageSize();

            long n_pc_long = size / ps;
            if( n_pc_long>Integer.MAX_VALUE )throw new IllegalArgumentException("can't allocate pages over Integer.MAX_VALUE");

            long n_ext = size % pc;
            if( n_ext>0 )n_pc_long++;

            if( n_pc_long>Integer.MAX_VALUE )throw new IllegalArgumentException("can't allocate pages over Integer.MAX_VALUE");

            int n_pc = (int)n_pc_long;
            if( n_pc!=pc ){
                pages.resizePages(n_pc);
            }
        }
    }

    @Override
    public void set(long offset, byte[] data, int dataOffset, int dataLen) {
        if( offset<0 )throw new IllegalArgumentException( "offset<0" );
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

        if( pages instanceof PageLock){
            ((PageLock) pages).writePageLock((int)page_from, (int)page_to, write);
        }else{
            write.run();
        }
    }

    @Override
    public byte[] get(long offset, int dataLen) {
        if( offset<0 )throw new IllegalArgumentException( "offset<0" );
        if( dataLen<0 )throw new IllegalArgumentException( "dataLen<0" );
        if( dataLen==0 )return new byte[0];

        long total_bytes_count = getSize();
        if( offset>=total_bytes_count )return new byte[0];

        int page_size = pages.memoryInfo().pageSize();
        if( page_size<1 )throw new IllegalStateException("pageSize(="+page_size+") to small, must be 1 or greater");

        long page_from = offset / page_size;
        if( page_from>Integer.MAX_VALUE )throw new IllegalStateException("can't write page(="+page_from+") > Integer.MAX_VALUE");

        long page_to = ((offset+dataLen) / page_size) + 1;
        if( page_to>Integer.MAX_VALUE )throw new IllegalStateException("can't write page(="+page_to+") > Integer.MAX_VALUE");

        int[] bytesReaded = new int[]{ 0 };
        byte[] buff = new byte[dataLen];
        Runnable read = ()->{
            int readed = 0;
            int page = (int)page_from;
            int page_off = (int)(offset % page_size);
            while (readed < dataLen){
                int avail = page_size - page_off;
                int need = dataLen - readed;
                int read_size = Math.min(avail,need);
                byte[] bytes = pages.readPage(page);
                int valid_data_size = bytes.length-page_off;
                if( valid_data_size<read_size ){
                    // прочитано меньше требуемого объема
                    if( bytes.length>0 && page_off<bytes.length ) {
                        System.arraycopy(bytes, page_off, buff, readed, bytes.length-page_off);
                        readed += bytes.length-page_off;
                    }
                    break;
                }else{
                    // прочитано достаточно данных
                    System.arraycopy(bytes, page_off, buff, readed, read_size);
                    readed += read_size;
                    page++;
                    page_off = 0;
                }
            }
            bytesReaded[0] = readed;
        };

        if( pages instanceof PageLock ){
            ((PageLock) pages).readPageLock((int)page_from, (int)page_to, read);
        }else{
            read.run();
        }

        byte[] res_buff = buff;
        if( bytesReaded[0]<res_buff.length ){
            res_buff = Arrays.copyOf(res_buff,bytesReaded[0]);
        }

        return res_buff;
    }

    @Override
    public void clear() {
        setSize(0);
    }

    @Override
    public void flush() {
        if( pages instanceof Flushable){
            ((Flushable) pages).flush();
        }
    }

    @Override
    public void close() {
        if( pages instanceof AutoCloseable ){
            try {
                ((AutoCloseable) pages).close();
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }
}
