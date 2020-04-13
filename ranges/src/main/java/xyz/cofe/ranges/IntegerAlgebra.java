package xyz.cofe.ranges;

/**
 * Сравнение целых чисел
 */
public interface IntegerAlgebra extends NComare<Integer>, NInvert<Integer>, NSum<Integer> {
    @Override
    default int compare(Integer a, Integer b){
        if( a==null )throw new IllegalArgumentException( "a==null" );
        if( b==null )throw new IllegalArgumentException( "b==null" );
        return a.compareTo(b);
    }

    @Override
    default Integer invert(Integer a){
        if( a==null )throw new IllegalArgumentException( "a==null" );
        return -a;
    }

    @Override
    default Integer sum(Integer a, Integer b) {
        if( a==null )throw new IllegalArgumentException( "a==null" );
        if( b==null )throw new IllegalArgumentException( "b==null" );
        return a+b;
    }
}
