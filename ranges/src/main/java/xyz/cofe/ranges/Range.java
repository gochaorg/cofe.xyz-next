package xyz.cofe.ranges;

public interface Range<N> extends NComare<N>, NSum<N>, NInvert<N> {
    /**
     * Начало диапазона
     * @return начало диапазона
     */
    N begin();

    /**
     * Начало диапазона включается в отрезок
     * @return true - begin включается в отрезок
     */
    boolean beginIncluded();

    /**
     * Конец диапзона
     * @return конец диапазона
     */
    N end();

    /**
     * Конец диапазона включается в отрезок
     * @return true - уонец включен в отрезок
     */
    boolean endIncluded();

    /**
     * Проверка, что указанная точка включена в отрезок
     * @param n точка
     * @return true - входит в отрезок
     */
    default boolean include( N n ){
        if( n==null )throw new IllegalArgumentException( "n==null" );

        N begin = begin();
        if( begin!=null ){
            int c1 = compare(n,begin);
            if( c1<0 )return false;
            if( c1==0 && !beginIncluded() )return false;
        }

        N end = end();
        if( end!=null ){
            int c2 = compare(end,n);
            if( c2<0 )return false;
            if( c2==0 && !endIncluded() )return false;
        }

        return true;
    }

    /**
     * Проверка что данный диапазон включает в себя указанный
     * @param a диапазон
     * @return true a включется в данный
     */
    default <R extends Range<N>> boolean include(R a){
        if( a==null )throw new IllegalArgumentException( "a==null" );

        N a0 = a.begin();
        N a1 = a.end();
        if( a0!=null && a1!=null && compare(a0,a1)>0 )
            throw new IllegalArgumentException("a.begin > a.end");

        N t0 = begin();
        N t1 = end();
        if( t0!=null && t1!=null && compare(t0,t1)>0 )
            throw new IllegalStateException("this.begin > this.end");

        // Отрезок полностью включает все возможные значения
        if( t0==null && t1==null )return true;

        // Начало текущего отрезка опеределено
        if( t0!=null ){
            // край не опреределн
            if( a0==null )return false;
            int c0 = compare(a0, t0);
            if( c0<0 )return false;
            if( c0==0 ){
                boolean binc = beginIncluded();
                boolean ainc = a.beginIncluded();
                if( !binc && ainc )return false;
            }
        }

        // Конец текущего отрезка опеределено
        if( t1!=null ){
            // край не опреределн
            if( a1==null )return false;
            int c1 = compare(t1,a1);
            if( c1<0 )return false;
            if( c1==0  ){
                boolean eInc = endIncluded();
                boolean aInc = a.endIncluded();
                if( !eInc && aInc )return false;
            }
        }

        return true;
    }

    // [10,20] - [12,18] = [10,12], [18,20]
    // [a,b] - [c,d] => [e,f], [g,h]
    // [a,b) - [c,d) => [e,f), [g,h)

    // [10,20) - [12,18] => [10,12), (18,20)
    //                          12) - ибо был вычит [12
    //                               (18 - ибо был вычит 18], т.е. число 18 > x > 20

    // [10,20) - [12,18) => [10,12), [18,20)
    // [10,20) - (12,18] => [10,12], (18,20)
    // [10,20) - [12,18] => [10,12), (18,20)

    // [10,20) - [10,20) => none
    // (10,20) - (10,20) => none

    // [10,20] - [10,20) =>
    // [10 <= x <= 20] - [10 <= x < 20]  => [20 <= x <= 20]

    // [10,20] - [10,19] => (19,20]
    // [10 <= x <= 20] - [10 <= x <= 19] => [19 < x <= 20]

    // [10,20] - (9,19] => (19,20]
    // [10 <= x <= 20] - [9 < x <= 19] => [19 < x <= 20]

    // [10,20] - (10,19) => [10,10], [19,20]
    // [10<=x<=20] - [10<x<19] => [10<=x<=10], [19<=x<=20]
}
