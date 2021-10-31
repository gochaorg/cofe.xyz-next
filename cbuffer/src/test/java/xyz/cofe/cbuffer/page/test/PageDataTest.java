package xyz.cofe.cbuffer.page.test;

import org.junit.Test;
import xyz.cofe.cbuffer.MemContentBuffer;
import xyz.cofe.cbuffer.page.PDLogger;
import xyz.cofe.cbuffer.page.PageDataImpl;

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

//    private void tst01(){
//        EventList el = new BasicEventList();
//    }
}
