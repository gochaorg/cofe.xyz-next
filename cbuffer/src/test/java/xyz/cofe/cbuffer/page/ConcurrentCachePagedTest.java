package xyz.cofe.cbuffer.page;

import org.junit.Test;
import xyz.cofe.fn.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

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
        public final Consumer4<Long,Integer,Integer,Integer> log;

        public Worker(CachePaged cache, Consumer4<Long,Integer,Integer,Integer> log){
            this.cache = cache;
            this.log = log;
        }
        public Worker cycles(int c){
            cyclesTotal = c;
            return this;
        }
        public Worker minSleep(long t){
            minSleep = t;
            return this;
        }
        public Worker maxSleep(long t){
            maxSleep = t;
            return this;
        }

        public volatile boolean dead = false;

        @Override
        public void run() {
            try {
                for (var i = 0; i < cyclesTotal; i++) {
                    var page = ThreadLocalRandom.current().nextInt(cache.memoryInfo().pageCount());
                    cache.updatePage(
                        page,
                        bytes -> {
                            //synchronized (this) {
                                var num_a = readInt(bytes, 0);
                                var num_b = num_a + 1;
                                //writeInt(bytes, 0, num_b);
                                var newBytes = Arrays.copyOf(bytes,bytes.length);
                                writeInt(newBytes, 0, num_b);
                                try {
                                    Thread.sleep(ThreadLocalRandom.current().nextLong(Math.abs(maxSleep - minSleep) + Math.min(maxSleep, minSleep)));
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                cyclesExec += 1;
                                log.accept(getId(), page, num_a, num_b);
                                // return bytes;
                                return newBytes;
                            //}
                        }
                    );
                }
            } catch (Throwable err){
                dead = true;
                throw err;
            }
        }
    }

    @Test
    public void test(){
        int pageSize = 1024;

        var fast = new MemPaged(pageSize, pageSize*4);
        var slow = new MemPaged(pageSize, pageSize*8);

        var initData = new byte[pageSize];
        Arrays.fill(initData,(byte)0);
        for( var i=0;i<slow.memoryInfo().pageCount();i++ ){
            slow.writePage(i,initData);
        }

        var cache = new CachePaged(fast,slow);
        var log = new CopyOnWriteArrayList<Tuple4<Long,Integer,Integer,Integer>>();

        var workers = new ArrayList<Worker>();
        var cyclesPerWorker = 500;
        for( var i=0;i<20;i++ ){
            workers.add(
                new Worker(
                    cache,
                    (wId,page,from,to)->{
                        synchronized (log) {
                            log.add(Tuple4.of(wId, page, from, to));
                        }
                    }
                ).cycles(cyclesPerWorker).minSleep(1).maxSleep(2)
            );
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
        System.out.println("dead count = "+workers.stream().filter(w -> w.dead).count());
        System.out.println("succ count = "+workers.stream().filter(w -> !w.dead).count());
        System.out.println("worker cyclesExec min "+workers.stream().map(w->w.cyclesExec).min(Integer::compare));
        System.out.println("worker cyclesExec max "+workers.stream().map(w->w.cyclesExec).max(Integer::compare));

        var pageHistory = new TreeMap<Integer, List<Tuple3<Long,Integer,Integer>>>();
        log.forEach(t4 -> {
            var worker = t4.a();
            var page = t4.b();
            var from = t4.c();
            var to = t4.d();
            pageHistory.computeIfAbsent(page, x -> new ArrayList<>()).add(Tuple3.of(worker,from,to));
        });

        pageHistory.forEach((page,hist) -> {
            Tuple3<Long,Integer,Integer> prev = null;
            for( var t3 : hist ){
                var worker = t3.a();
                var from = t3.b();
                var to = t3.c();

                //System.out.println("page="+page+" w="+worker+" from="+from+" to="+to);
                if( prev==null ){
                    prev = t3;
                }else{
                    if(Objects.equals(prev.b(), from)){
                        var p_worker = prev.a();
                        var p_from = prev.b();
                        var p_to = prev.c();
                        System.out.println("!!!! worker:"+worker+" "+p_worker+" from:"+p_from+" "+from+" to:"+p_to+" "+to);
                    }
                    prev = t3;
                }
            };
        });
    }
}
