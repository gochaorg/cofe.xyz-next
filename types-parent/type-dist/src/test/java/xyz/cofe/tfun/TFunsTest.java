package xyz.cofe.tfun;

import org.junit.jupiter.api.Test;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.typedist.TypeDistance;

import java.util.*;

public class TFunsTest {
    public int sum1(Object a, Object b){ return 0; }
    public int sum2(Number a, Object b){ return 0; }
    public int sum3(Object a, Number b){ return 0; }
    public int sum4(Number a, Number b){ return 0; }
    public int sum5(Integer a, Number b){ return 0; }
    public int sum6(Number a, Integer b){ return 0; }
    public int sum7(Integer a, Integer b){ return 0; }
    public int sum8(Object a, Integer b){ return 0; }
    public int sum9(Integer a, Object b){ return 0; }

    @Test
    public void test01(){
        TFuns funs = new TFuns();
        funs = funs.add(
            TFunction.of(Object.class, Object.class, Integer.class, this::sum1)
            ,TFunction.of(Number.class, Object.class, Integer.class, this::sum2)
            ,TFunction.of(Object.class, Number.class, Integer.class, this::sum3)
            ,TFunction.of(Number.class, Number.class, Integer.class, this::sum4)
            ,TFunction.of(Integer.class, Number.class, Integer.class, this::sum5)
            ,TFunction.of(Number.class, Integer.class, Integer.class, this::sum6)
            ,TFunction.of(Integer.class, Integer.class, Integer.class, this::sum7)
            ,TFunction.of(Object.class, Integer.class, Integer.class, this::sum8)
            ,TFunction.of(Integer.class, Object.class, Integer.class, this::sum9)
        );

        System.out.println("count="+funs.count());

        Class<?>[] args = new Class[]{Number.class,Number.class};
        funs.functions().map( fn -> {
            List<Integer> dists = new ArrayList<>();
            Class[] targs = fn.input();
            if( targs.length==args.length ){
                Integer v = null;
                for( int ai=0; ai<args.length; ai++ ){
                    Optional<TypeDistance> td = TypeDistance.distance(targs[ai], args[ai]);
                    if( td.isPresent() ){
                        v = td.get().dist().orElse(null);
                    }else {
                        v = null;
                    }
                    dists.add(v);
                }
                return Tuple2.of(dists, fn);
            }else {
                return Tuple2.of(Collections.emptyList(),fn);
            }
        }).forEach( p ->{
            System.out.println("fn("+ Arrays.asList(p.b().types())+") = "+p.a());
        });
    }

    @Test
    public void test02(){
        TFuns funs = new TFuns();
        funs = funs.add(
            TFunction.of(Object.class, Object.class, Integer.class, this::sum1)
            ,TFunction.of(Number.class, Object.class, Integer.class, this::sum2)
            ,TFunction.of(Object.class, Number.class, Integer.class, this::sum3)
            ,TFunction.of(Number.class, Number.class, Integer.class, this::sum4)
            ,TFunction.of(Integer.class, Number.class, Integer.class, this::sum5)
            ,TFunction.of(Number.class, Integer.class, Integer.class, this::sum6)
            ,TFunction.of(Integer.class, Integer.class, Integer.class, this::sum7)
            ,TFunction.of(Object.class, Integer.class, Integer.class, this::sum8)
            ,TFunction.of(Integer.class, Object.class, Integer.class, this::sum9)
        );

        System.out.println("find Number, Number");
        funs.findByArgs( Number.class, Number.class ).preffered().forEach( f ->
            System.out.println("found "+Arrays.asList(f.types()) )
        );

        System.out.println("find Integer, Integer");
        funs.findByArgs( Integer.class, Integer.class ).preffered().forEach( f ->
            System.out.println("found "+Arrays.asList(f.types()) )
        );
    }
}