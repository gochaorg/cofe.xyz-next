package xyz.cofe.iter;

import org.junit.Test;
import xyz.cofe.fn.Consumer2;
import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.fn.Tuple3;
import xyz.cofe.txt.Str;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamTest01 {
    public static class Tracker {
        private final long[] times;
        private volatile int laps = 0;

        public Tracker(int maxTracks){
            if( maxTracks<0 )throw new IllegalArgumentException( "maxTracks<0" );
            times = new long[maxTracks];
        }

        public synchronized void collect( long timeLap ){
            if( times.length<=0 )return;
            times[ laps % times.length ] = timeLap;
            laps++;
            sortedTimes = null;
        }

        public synchronized long[] times(){
            if( times.length<=0 )return new long[0];
            if( laps<times.length )return Arrays.copyOf(times,laps);
            return times;
        }

        public synchronized int laps(){ return laps; }

        public synchronized long summaryTimes(){
            long s = 0;
            for( long t : times() ){
                s += t;
            }
            return s;
        }

        private volatile long[] sortedTimes;
        public synchronized long[] sortedTimes(){
            if( sortedTimes!=null )return sortedTimes;
            long[] times = times();
            times = Arrays.copyOf(times,times.length);
            Arrays.sort(times);
            sortedTimes = times;
            return sortedTimes;
        }

        public long precentTime( int prc ){
            if( prc<0 )throw new IllegalArgumentException( "prc<0" );
            if( prc>100 )throw new IllegalArgumentException( "prc>100" );

            long[] times = sortedTimes();
            if( times.length<=0 )return -1L;

            if( prc==0 )return times[0];
            if( prc==100 )return times[times.length-1];
            if( times.length==1 )return times[0];

            return times[ (times.length*prc)/100 ];
        }

        public double avgTime(){
            long[] times = times();
            if( times.length<=0 )return -1;
            return (double) summaryTimes() / (double) times.length;
        }
    }

    public static <R> R track(Supplier<R> code,Tracker tracker){
        if( code==null )throw new IllegalArgumentException( "code==null" );
        if( tracker==null )throw new IllegalArgumentException( "tracker==null" );

        long t0 = System.nanoTime();
        R r = code.get();
        long t1 = System.nanoTime();
        tracker.collect(t1-t0);
        return r;
    }

    public long seqAtomicLong(int cnt, Tracker tracker){
        AtomicLong seqGen = new AtomicLong(0);
        long sum = track( ()->
            Stream.generate(seqGen::incrementAndGet).limit(cnt).reduce(0L, (r,a)->r+a )
            , tracker );
        return sum;
    }

    public long seqAcum(int cnt, Tracker tracker){
        Acum<Long> acc = new Acum<>(0L);
        long sum = track( ()->
            Stream.generate(()->{
                synchronized (acc){
                    long v = acc.get();
                    acc.set(v+1);
                    return acc.get();
                }
            }).limit(cnt).reduce(0L, (r,a)->r+a )
            , tracker );
        return sum;
    }

    public long presetList( int cnt, Tracker tracker ){
        List<Long> nums = new ArrayList<>(cnt);
        for( int i=0; i<cnt; i++ ){
            nums.add((long)(i+1));
        }
        return
            track(
                ()->nums.stream().reduce( (Long)0L, (r,a)->r+a ), tracker
            );
    }

    public long ppresetList( int cnt, Tracker tracker ){
        List<Long> nums = new ArrayList<>(cnt);
        for( int i=0; i<cnt; i++ ){
            nums.add((long)(i+1));
        }
        return
            track(
                ()->nums.parallelStream().reduce( (Long)0L, (r,a)->r+a ), tracker
            );
    }

    @Test
    public void sequence01(){
        List<Tuple3<String,Tracker, Consumer2<Integer,Tracker>>> tests =
            Arrays.asList(
                Tuple3.of("atomicLong",
                    new Tracker(1000), this::seqAtomicLong ),
                Tuple3.of("acum",
                    new Tracker(1000), this::seqAcum ),
                Tuple3.of("presetList",
                    new Tracker(1000), this::presetList )
                ,Tuple3.of("ppresetList",
                    new Tracker(1000), this::ppresetList )
            );

        for( int i=1; i<1000; i++ ) {
            final int ii = i;
            tests.forEach( tst ->
                tst.c().accept(ii, tst.b())
            );
        }

        DecimalFormat numfmt = new DecimalFormat("0.000000");
        int maxNameLen = tests.stream().map( t->t.a().length() ).max(Integer::compareTo).get();

        tests.forEach( tst -> {
            String name = tst.a();
            if( name.length()<maxNameLen ){
                StringBuilder sb = new StringBuilder();
                sb.append(name);
                for( int i=0;i<(maxNameLen-name.length()); i++ ){
                    sb.append(" ");
                }
                name = sb.toString();
            }
            System.out.println(
                "test of " + name + ": " +
                    " avg=" + (numfmt.format(tst.b().avgTime() / Math.pow(10, 6)) + " ms") +
                    " p90=" + (numfmt.format(tst.b().precentTime(90) / Math.pow(10, 6)) + " ms") +
                    " p95=" + (numfmt.format(tst.b().precentTime(95) / Math.pow(10, 6)) + " ms") +
                ""
            );
        });

        Thread[] threads = new Thread[1000];
        int threadsCount = Thread.enumerate(threads);
        for( int t=0; t<threadsCount; t++ ){
            Thread th = threads[t];
            System.out.println("thread "+th.getId()+" "+th.getName());
        }
    }
}
