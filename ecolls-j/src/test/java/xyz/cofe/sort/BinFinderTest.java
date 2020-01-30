package xyz.cofe.sort;

import org.junit.Assert;
import org.junit.Test;
import xyz.cofe.fn.Fn2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class BinFinderTest {
    @Test
    public void test01(){
        ArrayList<Integer> lst = new ArrayList<>();
        lst.addAll(Arrays.asList(1,1,1,2,2,3,3,3,5,6,6,8,8,9,10,10,10) );

        BinFinder<? super List<Integer>,Integer> finder = new BinFinder<List<Integer>, Integer>() {
            @Override
            public Integer get( List<Integer> lst, int index ){
                return lst.get(index);
            }
        };

        Comparator<Integer> numCmp = (a,b) -> a-b;
        int head3 = finder.headIndex(lst,numCmp,3,0,lst.size());
        System.out.println("head of 3 = "+head3); // wait 4

        int tail3 = finder.tailIndex(lst,numCmp,3,0,lst.size());
        System.out.println("tail of 3 = "+tail3); // wait 8

        int head5 = finder.headIndex(lst,numCmp,5,0,lst.size());
        System.out.println("head of 5 = "+head5); // wait 7
    }

    @Test
    public void test02(){
        ArrayList<Integer> lst = new ArrayList<>();
        lst.addAll(Arrays.asList(
            1,1,       // 0
            4,4,4,     // 2
            7,7,7,     // 5
            10,10,10,  // 8
            15,15,     // 11
            17,        // 13
            19         // 14
            ,22,25     // 15
        ));

        BinFinder<? super List<Integer>,Integer> finder = new BinFinder<List<Integer>, Integer>() {
            @Override
            public Integer get( List<Integer> lst, int index ){
                return lst.get(index);
            }
        };

        Comparator<Integer> numCmp = (a,b) -> a-b;
        int head = finder.headIndex(lst,numCmp,10,0,lst.size());
        System.out.println("head of 10 = "+head); // wait 7
        Assert.assertTrue(head == 7);

        head = finder.headIndex(lst,numCmp,11,0,lst.size());
        System.out.println("head of 11 = "+head); // wait 10
        Assert.assertTrue(head == 10);

        head = finder.headIndex(lst,numCmp,9,0,lst.size());
        System.out.println("head of 9 = "+head); // wait 7
        Assert.assertTrue(head == 7);

        head = finder.headIndex(lst,numCmp,7,0,lst.size());
        System.out.println("head of 7 = "+head); // wait 4
        Assert.assertTrue(head == 4);
    }

    @Test public void test03(){
        Random rnd = new Random();
        Fn2<Integer,Integer,List<Integer>> gen = (size,rlim) -> {
            ArrayList<Integer> lst = new ArrayList<>();
            for( int i=0;i<size;i++ ){
                lst.add( rnd.nextInt(rlim));
            }
            return lst;
        };

        Comparator<Integer> numCmp = (a,b) -> a-b;
        for( int l=10; l<500; l+=10 ){
            for( int i = 10; i<50; i++ ){
                List<Integer> lst = gen.apply(i,l);
                ArrayList<Integer> sorted = new ArrayList<>();

                // sort
                SortInsert<List<Integer>,Integer> si = SortInsert.createForList();
                lst.forEach( n -> si.sortInsert(sorted,n,numCmp,0,sorted.size()) );

                BinFinder<? super List<Integer>,Integer> finder = new BinFinder<List<Integer>, Integer>() {
                    @Override
                    public Integer get( List<Integer> lst, int index ){
                        return lst.get(index);
                    }
                };

                int tgt = l >> 1;
                int idx = finder.headIndex(sorted,numCmp,tgt,0,sorted.size());
                if( idx<0 ){
                    System.err.println("not found");
                    System.err.println("target = "+tgt);
                    System.err.println("values:");
                    sorted.forEach(System.err::println);
                    return;
                }
            }
        }
    }
}
