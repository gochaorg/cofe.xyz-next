package xyz.cofe.cbuffer.page;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CachePagedTest {
    public static byte[] generateRandom(int size){
        var buff = new byte[size];
        var rnd = ThreadLocalRandom.current();
        rnd.nextBytes(buff);
        return buff;
    }

    @Test
    public void test() {
        int pageSize = 1024;

        var fast = new MemFlatPaged(pageSize, pageSize*4);
        var slow = new MemFlatPaged(pageSize, pageSize*64);
        var cache = new CachePaged(fast,slow);
        cache.addListener(ev -> {
            if( ev instanceof CachePage.CachePageEvent ){
                System.out.println(ev+" "+((CachePage.CachePageEvent) ev).page());
            }else {
                System.out.println(ev);
            }
        });

        var data0 = generateRandom(pageSize);
        for( int i=0; i<32; i++ ){
            System.out.println("== "+i+" "+"=".repeat(30));

            cache.writePage(i,data0);

            if( i%5==0 ){
                cache.flush();
            }

            System.out.println("dirtyPages: "+
                cache.getCacheMap().dirtyPages().stream().map(
                        cp->""+cp.cachePageIndex+" -> "+cp.getTarget().map(t->""+t).orElse(""))
                    .collect(Collectors.joining(", ")));

            System.out.println("cache size "+cache.getCacheSize());
        }
    }
}
