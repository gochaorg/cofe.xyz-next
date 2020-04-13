package xyz.cofe.ranges;

/**
 * Левый полу интервал left <= x < right
 * @param <N> Тип чисел
 */
public interface LHalfRange<N> extends NComare<N> {
    /**
     * Начало диапазона
     * @return начало диапазона
     */
    N begin();

    /**
     * Конец диапзона
     * @return конец диапазона
     */
    N end();

    /**
     * Проверка, что указанная точка входит в интервал
     * @param n точка
     * @return true - принадлежит полу-интервалу
     */
    default boolean include( N n ){
        if( n==null )throw new IllegalArgumentException( "n==null" );

        N left = begin();
        N right = end();

        if( left!=null ){
            int c0 = compare(n, left);
            if( c0<0 )return false;
        }

        if( right!=null ){
            int c1 = compare(right, n);
            if( c1<=0 )return false;
        }

        return true;
    }
}
