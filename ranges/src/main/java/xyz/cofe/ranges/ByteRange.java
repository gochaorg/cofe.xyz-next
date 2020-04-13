package xyz.cofe.ranges;

/**
 * Диапазоны байт. Тип интервала - полу закрытый begin <= x < end.
 * @param <SELF> Собственный тип
 */
public interface ByteRange<SELF extends ByteRange<SELF>> extends LHalfRange<Long>, LongAlgebra {
    public static class DefByteRange implements ByteRange<DefByteRange> {
        private long begin;
        private long end;
        public DefByteRange(long begin,long end){
            if( begin>end )throw new IllegalArgumentException( "begin>end" );
            this.begin = begin;
            this.end = end;
        }

        @Override
        public Long begin() {
            return begin;
        }

        @Override
        public Long end() {
            return end;
        }
    }

    /**
     * Создает полу-интервал
     * @param begin начало включительно
     * @param end конец исключительно
     * @return Интервал
     */
    static DefByteRange of( long begin, long end ){
        if( begin>end )throw new IllegalArgumentException( "begin>end" );
        return new DefByteRange(begin,end);
    }

    /**
     * Размер
     * @return размер интервала
     */
    default Long size(){ return end()-begin(); }
}
