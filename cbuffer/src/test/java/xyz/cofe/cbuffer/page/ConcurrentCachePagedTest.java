package xyz.cofe.cbuffer.page;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertTrue;

public class ConcurrentCachePagedTest {
    @SuppressWarnings("PointlessArithmeticExpression")
    public static int readInt(byte[] data, int offset){
        var b0 = data[offset+3] & 0xFF;
        var b1 = data[offset+2] & 0xFF;
        var b2 = data[offset+1] & 0xFF;
        var b3 = data[offset+0] & 0xFF;
        return (b3 << 8*3) | (b2 << 8*2) | (b1 << 8) | b0;
    }

    @SuppressWarnings("PointlessArithmeticExpression")
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

    @Test
    public void testReadWriteInt(){
        byte[] buff = new byte[12];
        writeInt(buff,0, 12345);
        var r = readInt(buff,0);
        assertTrue(r==12345);
    }

    public static class Worker extends Thread {
        public final CachePaged cache;
        public int cyclesTotal = 100;
        public long minSleep = 5;
        public long maxSleep = 10;
        public volatile int cyclesExec = 0;

        public Worker(CachePaged cache){
            this.cache = cache;
        }

        @Override
        public void run() {
            System.out.println("run worker#"+getId());
            for( var i=0;i<cyclesTotal; i++ ){
                var page = ThreadLocalRandom.current().nextInt(cache.memoryInfo().pageCount());
                System.out.println("worker#"+getId()+" updatePage "+page);
                cache.updatePage(
                    page,
                    bytes -> {
                        var num = readInt(bytes,0);
                        writeInt(bytes,0,num+1);
                        try {
                            Thread.sleep(ThreadLocalRandom.current().nextLong(Math.abs(maxSleep-minSleep)+ Math.min(maxSleep,minSleep)));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        cyclesExec += 1;
                        return bytes;
                    }
                );
            }
        }
    }

    @Test
    public void test(){
        int pageSize = 1024;

        var fast = new MemPaged(pageSize, pageSize*4);
        var slow = new MemPaged(pageSize, pageSize*64);

        var initData = new byte[pageSize];
        Arrays.fill(initData,(byte)0);
        for( var i=0;i<slow.memoryInfo().pageCount();i++ ){
            slow.writePage(i,initData);
        }

        var cache = new CachePaged(fast,slow);

        var workers = new ArrayList<Worker>();
        for( var i=0;i<20;i++ ){
            workers.add(new Worker(cache));
        }

        workers.forEach(w -> w.start());
        workers.forEach(w -> {
            try {
                w.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        var numbers = new ArrayList<Integer>();
        var sum = 0;
        for( var p=0;p<cache.memoryInfo().pageCount();p++ ){
            var bytes = cache.readPage(p);
            var num = readInt(bytes,0);
            numbers.add(num);
            sum += num;
        }

        System.out.println("sum = "+sum);
        System.out.println("num = "+numbers);
        for( var w : workers ){
            System.out.println("worker cyclesExec "+w.cyclesExec);
        }
    }
}
