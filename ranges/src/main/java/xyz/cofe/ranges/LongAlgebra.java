package xyz.cofe.ranges;

/**
 * Цело численный диапазон
 */
public interface LongAlgebra extends NComare<Long>, NInvert<Long>, NSum<Long> {
    @Override
    default int compare(Long a, Long b){
        if( a==null )throw new IllegalArgumentException( "a==null" );
        if( b==null )throw new IllegalArgumentException( "b==null" );
        return a.compareTo(b);
    }

    @Override
    default Long invert(Long a){
        if( a==null )throw new IllegalArgumentException( "a==null" );
        return -a;
    }

    @Override
    default Long sum(Long a, Long b) {
        if( a==null )throw new IllegalArgumentException( "a==null" );
        if( b==null )throw new IllegalArgumentException( "b==null" );
        return a+b;
    }
}
