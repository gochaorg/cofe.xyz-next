package xyz.cofe.iter;

import java.util.Iterator;

/**
 * Итератор с ограниченной по кол-ву выборкой
 * @param <A> тип элементов
 */
public class LimitIterable<A> implements Eterable<A> {
    /**
     * Итератор с ограниченной по кол-ву выборкой
     * @param <A> тип элементов
     */
    public static class LimitIter<A> implements Iterator<A> {
        protected Iterator<A> iter;
        protected long limit;
        protected volatile long count;

        /**
         * Констркутор
         * @param iter итератор
         * @param limit максимальное кол-во элементов
         */
        public LimitIter(Iterator<A> iter, long limit) {
            this.iter = iter;
            this.limit = limit;
            this.count = 0;
        }

        @Override
        public boolean hasNext() {
            synchronized ( this ) {
                if( !iter.hasNext() ) return false;
                if( limit >= 0 && count >= limit ) return false;
                return true;
            }
        }

        @Override
        public A next() {
            synchronized ( this ) {
                if( limit >= 0 && count >= limit ) return null;
                A a = iter.next();
                count++;
                return a;
            }
        }
    }

    /**
     * Исходный итератор
     */
    protected Iterable<A> source;

    /**
     * максимальное кол-во элементов
     */
    protected long limit;

    @Override
    public Iterator<A> iterator() {
        return new LimitIter<>(source.iterator(),limit);
    }

    /**
     * Констркутор
     * @param source исходный итератор
     * @param limit максимальное кол-во элементов
     */
    public LimitIterable(Iterable<A> source, long limit) {
        this.source = source;
        this.limit = limit;
    }
}
