package xyz.cofe.cbuffer;

import org.junit.Test;
import xyz.cofe.cbuffer.page.CycleOperationData;
import xyz.cofe.cbuffer.page.NanoDuration;
import xyz.cofe.cbuffer.page.NanoTime;

import java.util.Random;

public class CycleOpDataTest {
    @Test
    public void test01(){
        CycleOperationData<String, NanoDuration, NanoTime> cdata = new CycleOperationData<>(30);

        int cnt = 100;
        String[] items = new String[]{
            "hello", "world", "Liza", "Peter", "gun",
            "word", "letter", "mix", "eUra", "OOps"
        };

        Random rnd = new Random();
        for( int i=0; i<cnt; i++ ) {
            NanoTime t1 = new NanoTime();
            NanoTime t2 = new NanoTime(t1.time+ rnd.nextInt(100));
            cdata.collect(t1, t2, items[rnd.nextInt(items.length)]);
        }

        System.out.println("counts:");
//        cdata.counts().forEach( (data,dcnt) -> {
//            System.out.println("  "+data+", cnt="+dcnt );
//        });
        cdata.countsSorted().forEach( (dcnt, dataLst)-> dataLst.forEach( data -> {
            System.out.println(""+dcnt+" "+data);
        }) );

        System.out.println("duration:");
//        cdata.duration().forEach( (data,dur) -> {
//            System.out.println("  "+data+", "+dur );
//        });
        cdata.durationSorted().forEach( (dur, dataLst) -> dataLst.forEach( data -> {
            System.out.println(""+dur+" "+data);
        }) );

        System.out.println("entries:");
        cdata.read((begin,end,data) -> {
            System.out.println("begin="+begin.time+" end="+end.time+" data="+data);
        });
    }
}
