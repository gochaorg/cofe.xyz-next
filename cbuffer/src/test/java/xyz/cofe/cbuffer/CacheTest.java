package xyz.cofe.cbuffer;

import org.junit.Test;
import xyz.cofe.cbuffer.page.*;
import xyz.cofe.fn.Tuple2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class CacheTest {
    private Tuple2<Long,Long> testRandom(PagedData pagedData, int repeats){
        UsedPagesInfo pi = pagedData.memoryInfo();

        Random rnd = new Random();
        System.out.println("try random access");

        long sum = 0;
        long cnt = 0;

        int[] try_no = new int[]{ 0 };
        int[] try_tot = new int[]{ 0 };
        long[] last_echo = new long[]{ System.currentTimeMillis() };
        long[] sum_echo = new long[]{ 0 };
        long[] cnt_echo = new long[]{ 0 };
        Runnable echo = ()->{
            long last = last_echo[0];
            if( (System.currentTimeMillis()-last)>500 ){
                long s = sum_echo[0];
                long c = cnt_echo[0];
                System.out.print("try #"+try_no[0]+"/"+try_tot[0]);
                if( c>0 ){
                    double avg = (double)s / c;
                    avg *= 0.000001;
                    System.out.print(" avg "+avg+" ms");
                }

                if( pagedData instanceof CachePagedDataBase){
                    Tuple2<Long,Long> hitMiss = ((CachePagedDataBase<?,?,?>) pagedData).cacheHitMiss();
                    long total = hitMiss.a()+hitMiss.b();
                    if( total>0 ){
                        double ratio = hitMiss.a() / (double)total;
                        System.out.print(" cache hit ratio "+ratio);
                    }
                }

                System.out.println();
                last_echo[0] = System.currentTimeMillis();
            }
        };

        int cnt_try = pi.pageCount()*repeats;
        int pages = pi.pageCount();

        for( int i=0; i<cnt_try; i++ ){
            int page = rnd.nextInt(pages);
            long t0 = System.nanoTime();
            pagedData.readPage(page);
            long t1 = System.nanoTime();
            cnt++;
            sum += t1 - t0;

            try_no[0] = i;
            try_tot[0] = cnt_try;
            sum_echo[0] = sum;
            cnt_echo[0] = cnt;
            echo.run();
        }

        return Tuple2.of(sum,cnt);
    }

    @Test
    public void test01(){
        System.out.println("test01");

        File targetFile = new File("/home/uzer/Загрузки/01-broshennye-mashiny.mp3");
        if( !targetFile.isFile() )return;

        System.out.println("file "+targetFile);
        System.out.println("file size "+targetFile.length()+" "+(targetFile.length()/1024/1024)+"mb");

        try {
            System.out.println("open file");
            RandomAccessFile raf = new RandomAccessFile(targetFile,"r");

            System.out.println("create raf");
            RAFBuffer rafBuffer = new RAFBuffer();
            rafBuffer.setRaf(raf);

            int pageSize = 1024*64;
            System.out.println("page size "+pageSize);

            System.out.println("create slowPages");
            ResizablePages slowPages = new CBuffPagedData(rafBuffer,pageSize,false,-1);

            System.out.println("slow pages count "+slowPages.memoryInfo().pageCount());
            int cache_pages_max = (int)(slowPages.memoryInfo().pageCount() * 0.75);
            System.out.println("fast pages count / cache size "+cache_pages_max);

            System.out.println("create fastPages, pages="+cache_pages_max);
            MemPagedData fastPages = new MemPagedData(pageSize, pageSize*cache_pages_max);

            System.out.println("create dirty pages, pages="+cache_pages_max);
            DirtyPagedData dirtyPagedData = new DirtyPagedData(fastPages);
            dirtyPagedData.resizePages(cache_pages_max);

            System.out.println("create cachePages");
            slowPages = new SlowPagedData(slowPages,2);
            CachePagedData cachePages = new CachePagedData(dirtyPagedData, slowPages);

            System.out.println("mem info");
            UsedPagesInfo pi = cachePages.memoryInfo();
            System.out.println("pages "+pi.pageCount());

            int repeats = 20;

            System.out.println("test for file");
            Tuple2<Long,Long> fileStat = testRandom(slowPages,repeats);
            System.out.println(". . . . . . .");
            System.out.println("sum = "+fileStat.a()+" cnt="+fileStat.b()+" avg "+((fileStat.a() / fileStat.b().doubleValue())/1000_000.0));

            System.out.println("test cachePages");
            Tuple2<Long,Long> cacheStat = testRandom(cachePages,repeats);
            System.out.println(". . . . . . .");
            System.out.println("sum = "+cacheStat.a()+" cnt="+cacheStat.b()+" avg "+(cacheStat.a() / cacheStat.b().doubleValue())/1000_000.0);

            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
