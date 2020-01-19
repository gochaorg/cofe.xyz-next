package xyz.cofe.cbuffer.page;

import org.junit.Test;
import xyz.cofe.cbuffer.MemContentBuffer;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.fn.Pair;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class PageDataTest {
    public void describe(PageDataImpl pd){
        if( pd==null ) throw new IllegalArgumentException("pd==null");

        int psize = pd.getPageSize();
        int maxpage = pd.getMaxSlowPageIndex();
        System.out.println("pagesize="+psize);
        System.out.println("fast { pages="+(pd.fastPageCount())+" }");
        System.out.println("slow { pages="+(maxpage+1)+" }");
    }

    public class PDLogger implements Closeable {
        private final Closeables closeables = new Closeables();
        private Appendable out;
        private void log(String message){
            synchronized( closeables ){
                if( out!=null ){
                    try{
                        out.append("ev: ").append(message).append("\n");
                    } catch( IOException e ){
                        e.printStackTrace();
                    }
                }
            }
        }
        public PDLogger(PageDataImpl pd, Appendable out){
            if( pd==null ) throw new IllegalArgumentException("pd==null");
            if( out==null ) throw new IllegalArgumentException("out==null");

            this.out = out;

            closeables.append(
                pd.onMap.listen( e->{
                    Object[] arr = pd.getDirtyPages().stream().toArray();

                    log(
                        "map fast="+e.a()+"=slow=>"+e.b()+
                            " fast.cnt="+pd.fastPageCount()+"/"+pd.getMaxFastPageCount()+"max"+
                            " dirty.cnt="+pd.getDirtyPageCount()+
                            "["+pd.getDirtyPages().stream()
                            .map(Pair::a)
                            //.map(Objects::toString)
                            .reduce("", ( a, b )->a+(a.length()>0?",":"")+b, ( a, b )->a+b)
                            +"]");
                    }
                ),
                pd.onFastDataWrited.listen( e-> log("fast writed page="+e.a()+" "+e.b().length+" bytes")),
                pd.onDirty.listen( e -> log("dirty page="+e.a()+" dirty="+e.b()) ),
                pd.onAllocNewPage.listen( e->log("alloc new page "+e) ),
                pd.onAllocFreePage.listen( e->log("alloc free page "+e) ),
                pd.onAllocExistsPage.listen( e->log("alloc exists page "+e) ),
                pd.onFastDataSize.listen( e->log("page "+e.a()+" size "+e.b()) ),
                pd.onAlloc.listen( e -> log("alloc "+e)),
                pd.onSaveFastPage.listen( e -> log("save "+e) )
            );
        }

        @Override
        public void close(){
            synchronized( closeables ){
                closeables.close();
            }
        }
    }

    @Test
    public void test1(){
        PageDataImpl pd = new PageDataImpl();
        new PDLogger(pd,System.out);

        MemContentBuffer slowMem = new MemContentBuffer();
        pd.setSlowBuffer(slowMem);

        slowMem.setSize((2048*16)*10+1024/2);

        MemContentBuffer fastMem = new MemContentBuffer();
        pd.setFastBuffer(fastMem);

        pd.setPageSize(2048);
        pd.setMaxFastPageCount(16);

        describe(pd);

        int maxPage = pd.getMaxSlowPageIndex();
        int totPages = maxPage+1;
        int writeEach = 3;

        for( int i=0; i<totPages;i++ ){
            System.out.println("cycle "+(i+1)+"/"+totPages);
            byte[] data = pd.data(i%totPages);

            writeEach = ThreadLocalRandom.current().nextInt(4);
            if( writeEach>0 ){
                if( writeEach==1 ){
                    System.out.println("write data");
                    pd.data(i,data);
                }else if( (i%writeEach)==0 ){
                    System.out.println("write data");
                    pd.data(i,data);
                }
            }
        }

        System.out.println("fpages="+pd.fastPageCount()+" fbytes="+fastMem.getSize());
    }
}
