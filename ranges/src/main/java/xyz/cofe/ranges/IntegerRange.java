package xyz.cofe.ranges;

/**
 * Цело численный диапазон
 */
public interface IntegerRange extends Range<Integer>, IntegerAlgebra {
    static IntegerRange of( int begin, boolean beginInc, int end, boolean endInc ){
        if( begin>end )throw new IllegalArgumentException("begin > end");
        return new IntegerRange() {
            @Override
            public Integer begin() {
                return begin;
            }

            @Override
            public boolean beginIncluded() {
                return beginInc;
            }

            @Override
            public Integer end() {
                return end;
            }

            @Override
            public boolean endIncluded() {
                return endInc;
            }
        };
    }
    static IntegerRange of( int begin, int end ){
        if( begin>end )throw new IllegalArgumentException("begin > end");
        return of( begin, true, end, false );
    }
}
