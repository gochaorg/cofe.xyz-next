package xyz.cofe.fn;

/**
 * Потребитель данных от 25 значений
 * @param <A> Тип первого значения
 * @param <B> Тип 2го значения
 * @param <C> Тип 3го значения
 * @param <D> Тип 4го значения
 * @param <E> Тип 5го значения
 * @param <F> Тип 6го значения
 * @param <G> Тип 7го значения
 * @param <H> Тип 8го значения
 * @param <I> Тип 9го значения
 * @param <J> Тип 10го значения
 * @param <K> Тип 11го значения
 * @param <L> Тип 12го значения
 * @param <M> Тип 13го значения
 * @param <N> Тип 14го значения
 * @param <O> Тип 15го значения
 * @param <P> Тип 16го значения
 * @param <Q> Тип 17го значения
 * @param <R> Тип 18го значения
 * @param <S> Тип 19го значения
 * @param <T> Тип 20го значения
 * @param <U> Тип 21го значения
 * @param <V> Тип 22го значения
 * @param <W> Тип 23го значения
 * @param <X> Тип 24го значения
 * @param <Y> Тип 25го значения
 */
public interface Consumer25<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y> {
    /**
     * Получает значение
     * @param a первое значение
     * @param b 2ое значение
     * @param c 3ое значение
     * @param d 4ое значение
     * @param e 5ое значение
     * @param f 6ое значение
     * @param g 7ое значение
     * @param h 8ое значение
     * @param i 9ое значение
     * @param j 10ое значение
     * @param k 11ое значение
     * @param l 12ое значение
     * @param m 13ое значение
     * @param n 14ое значение
     * @param o 15ое значение
     * @param p 16ое значение
     * @param q 17ое значение
     * @param r 18ое значение
     * @param s 19ое значение
     * @param t 20ое значение
     * @param u 21ое значение
     * @param v 22ое значение
     * @param w 23ое значение
     * @param x 24ое значение
     * @param y 25ое значение
     */
    void accept(
        A a, B b, C c, D d, E e, F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o, P p, Q q, R r, S s, T t,
        U u, V v, W w, X x, Y y
    );
}
