package xyz.cofe.collection;

import org.junit.Test;
import xyz.cofe.sort.SortInsert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class SortInsertTest {
    //region rndStr( len ):String
    private static final String dic = "abcdefghijklmnopqrstu";
    private static final Random rnd = new Random();
    private static String rndStr( int len ){
        StringBuilder sb = new StringBuilder();
        if( len>0 ){
            for(int i=0;i<len;i++ ){
                sb.append(dic.charAt(rnd.nextInt(dic.length())));
            }
        }
        return sb.toString();
    }
    //endregion

    private static  <E> int sortErrCount( Iterable<E> lst, Comparator<E> cmp ){
        if( lst==null ) throw new IllegalArgumentException("lst==null");
        if( cmp==null ) throw new IllegalArgumentException("cmp==null");
        int errCnt = 0;
        E last = null;
        int idx = -1;
        for( E e : lst ){
            idx++;
            if( idx==0 ){
                last = e;
                continue;
            }
            int c = cmp.compare(last,e);
            if( c>0 ){
                errCnt++;
            }
            last = e;
        }
        return errCnt;
    }

    private static <E> List<E> rndList( int count, Supplier<E> gen ){
        if( gen==null ) throw new IllegalArgumentException("gen==null");
        List<E> lst = new ArrayList<>();
        if( count>0 ){
            for( int i=0; i<count; i++ ){
                lst.add( gen.get() );
            }
        }
        return lst;
    }

    private static <E> Map<E,Integer> histo( Iterable<E> lst ){
        if( lst==null ) throw new IllegalArgumentException("lst==null");
        Map<E,Integer> h = new HashMap<>();
        for( E e : lst ){
            h.put( e, h.getOrDefault(e,0)+1 );
        }
        return h;
    }

    private static <E> boolean histoEquals( Map<E,Integer> h1, Map<E,Integer> h2 ){
        if( h1==null && h2==null )return true;
        if( h1==null && h2!=null )return false;
        if( h1!=null && h2==null )return false;
        if( h1.size() != h2.size() )return false;
        if( !h1.keySet().containsAll( h2.keySet() ) )return false;
        if( !h2.keySet().containsAll( h1.keySet() ) )return false;
        for( Map.Entry<E,Integer> me : h1.entrySet() ){
            if( !h2.containsKey(me.getKey()) )return false;
            if( !Objects.equals(h2.get(me.getKey()), me.getValue()) )return false;
        }
        return true;
    }

    @Test
    public void test01(){
        Comparator<String> cmpf = (a,b) -> a.compareTo(b);

        List<String> values = rndList( 50, ()->rndStr(5));
        System.out.println("init values, count="+values.size()+" errors="+sortErrCount(values,cmpf));

        SortInsert<List<String>,String> si = SortInsert.createForList();
        List<String> sortedValues = new ArrayList<>();
        values.forEach( (e) -> si.sortInsert(sortedValues,e,(a,b)->a.compareTo(b),0, sortedValues.size()) );

        System.out.println("sorted values, count="+sortedValues.size()+" errors="+sortErrCount(sortedValues,cmpf));

        Map<String,Integer> h1 = histo(values);
        Map<String,Integer> h2 = histo(sortedValues);
        boolean h1eq2 = histoEquals(h1,h2);

        System.out.println("histo equals="+h1eq2);
    }
}
