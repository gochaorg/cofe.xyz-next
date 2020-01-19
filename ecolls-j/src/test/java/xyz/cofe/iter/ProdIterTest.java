package xyz.cofe.iter;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import xyz.cofe.fn.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("SimplifiableJUnitAssertion")
public class ProdIterTest {
    @Test
    public void test01(){
        List<String> lst1 = Arrays.asList("A", "B", "C", "D");
        List<Integer> lst2 = Arrays.asList(1,2,3,4);

        BiProductIterator<String,Integer> itr = new BiProductIterator<String, Integer>(lst1,lst2);
        int idx = -1;
        while (itr.hasNext()){
            Pair<String,Integer> p = itr.next();
            idx++;
            System.out.println(idx+" "+"a="+p.a()+" b="+p.b());
        }
        assertTrue(idx==15);

        System.out.println("--------------------");

        AtomicInteger aidx=new AtomicInteger(-1);
        Eterable.of(lst1).product(lst2,(a,b)->{
            aidx.incrementAndGet();
            System.out.println(aidx.get()+" "+"a="+a+" b="+b);
        });
        assertTrue(aidx.get()==15);
    }

    @Test
    public void test02(){
        List<String> lst1 = Arrays.asList("A","B","C","D");
        List<Integer> lst2 = Arrays.asList(1,2,3,4);
        List<Boolean> lst3 = Arrays.asList(true,false);

        Eterable.of(lst1).product(lst2,lst3, (a,b,c)->{
            System.out.println("a="+a+" b="+b+" c="+c);
        });
    }
}
