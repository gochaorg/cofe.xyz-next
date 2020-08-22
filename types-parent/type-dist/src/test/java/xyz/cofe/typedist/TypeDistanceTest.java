package xyz.cofe.typedist;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class TypeDistanceTest {
    @Test
    public void test01(){
        TreeSet<Integer> dist = new TreeSet<>();
        Class<?> a = Number.class;
        Class<?> b = Integer.class;

        TypeDistance.cdistance(a, b, dist::add);
        System.out.println("distance from "+a.getSimpleName()+" to "+b.getSimpleName()+distance(dist));

        dist.clear();
        a = Object.class;
        b = Integer.class;
        TypeDistance.cdistance(a, b, dist::add);
        System.out.println("distance from "+a.getSimpleName()+" to "+b.getSimpleName()+distance(dist));

        dist.clear();
        a = Integer.class;
        b = Object.class;
        TypeDistance.cdistance(a, b, dist::add);
        System.out.println("distance from "+a.getSimpleName()+" to "+b.getSimpleName()+distance(dist));
    }

    public interface A {}
    public interface AA extends A {}
    public interface AAA extends AA {}

    public interface B {}
    public interface BB extends B {}
    public interface BBB extends BB {}

    public interface C extends AA, BB {};
    public interface CC extends C, BBB {};

    private String distance( TreeSet<Integer> dist ){
        if( dist.isEmpty() ){
            return " no distance";
        }else{
            if( dist.size()==1 ){
                return(" = "+dist.first());
            }else{
                return(" = "+
                    dist.stream().map(Object::toString).reduce("",
                        (v1, v2)-> v1.length()<1 ? v2 : v1+","+v2)
                );
            }
        }
    }

    @Test
    public void test02(){
        List<Class<?>> types  =Arrays.asList(
            A.class, AA.class, AAA.class,
            B.class, BB.class, BBB.class,
            C.class, CC.class
        );
        for( Class<?> a : types ){
            for( Class<?> b : types ){
                TreeSet<Integer> dist = new TreeSet<>();
                TypeDistance.idistance(a,b, dist::add);

                System.out.print("distance from "+a.getSimpleName()+" to "+b.getSimpleName()+distance(dist));
                System.out.println();
            }
        }
    }

    @Test
    public void test03(){
        Class<?> a = Number.class;
        Class<?> b = Integer.class;
        System.out.println("distance from "+a.getSimpleName()+" to "+b.getSimpleName()+" = "+TypeDistance.distance(a,b));

        a = Object.class;
        b = Integer.class;
        System.out.println("distance from "+a.getSimpleName()+" to "+b.getSimpleName()+" = "+TypeDistance.distance(a,b));

        a = Integer.class;
        b = Object.class;
        System.out.println("distance from "+a.getSimpleName()+" to "+b.getSimpleName()+" = "+TypeDistance.distance(a,b));

        List<Class<?>> types  =Arrays.asList(
            A.class, AA.class, AAA.class,
            B.class, BB.class, BBB.class,
            C.class, CC.class
        );
        for( Class<?> aa : types ){
            for( Class<?> bb : types ){
                System.out.println("distance from "+aa.getSimpleName()+" to "+bb.getSimpleName()+" = "+TypeDistance.distance(aa,bb));
            }
        }
    }
}