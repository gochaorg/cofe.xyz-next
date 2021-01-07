package xyz.cofe.fn;

/**
 * Создание кортэжа
 */
public interface Tuple {
    /**
     * Создание кортежа из 0 элементов
     * @return Кортеж
     */
    public static Tuple of(){ return TupleZero.instance; }

    /**
     * Создание кортежа из 1 элемента
     * @param a  1ый элемент
     * @param <A> Тип 1го элемента
     * @return Кортеж
     */
    public static <A> Tuple1<A> of( A a ){ return Tuple1.of(a); }

    /**
     * Создание кортежа из 2 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @return Кортеж
     */
    public static <A,B> Tuple2<A,B> of( A a, B b ){ return Tuple2.of(a,b); }

    /**
     * Создание кортежа из 3 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @return Кортеж
     */
    public static <A,B,C,D> Tuple3<A,B,C> of( A a, B b, C c ){
        return Tuple3.of(a,b,c);
    }

    /**
     * Создание кортежа из 4 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @return Кортеж
     */
    public static <A,B,C,D> Tuple4<A,B,C,D> of( A a, B b, C c, D d ){
        return Tuple4.of(a,b,c,d);
    }

    /**
     * Создание кортежа из 5 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E> Tuple5<A,B,C,D,E> of( A a, B b, C c, D d, E e ){
        return Tuple5.of(a,b,c,d,e);
    }

    /**
     * Создание кортежа из 6 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F> Tuple6<A,B,C,D,E,F> of( A a, B b, C c, D d, E e, F f ){
        return Tuple6.of(a,b,c,d,e,f);
    }

    /**
     * Создание кортежа из 7 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G> Tuple7<A,B,C,D,E,F,G>
    of(
        A a, B b, C c, D d, E e, F f, G g
    ){
        return Tuple7.of(a,b,c,d,e,f,g);
    }

    /**
     * Создание кортежа из 8 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H> Tuple8<A,B,C,D,E,F,G,H>
    of(
        A a, B b, C c, D d, E e, F f, G g, H h
    ){
        return Tuple8.of(a,b,c,d,e,f,g,h);
    }

    /**
     * Создание кортежа из 9 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I> Tuple9<A,B,C,D,E,F,G,H,I>
    of(
        A a, B b, C c, D d, E e, F f, G g, H h, I i
    ){
        return Tuple9.of(a,b,c,d,e,f,g,h,i);
    }

    /**
     * Создание кортежа из 10 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J> Tuple10<A,B,C,D,E,F,G,H,I,J>
    of(
        A a, B b, C c, D d, E e, F f, G g, H h, I i, J j
    ){
        return Tuple10.of(a,b,c,d,e,f,g,h,i,j);
    }

    /**
     * Создание кортежа из 11 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K> Tuple11<A,B,C,D,E,F,G,H,I,J,K>
    of(
        A a, B b, C c, D d, E e, F f, G g, H h, I i, J j,
        K k
    ){
        return Tuple11.of(a,b,c,d,e,f,g,h,i,j,k);
    }

    /**
     * Создание кортежа из 12 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L> Tuple12<A,B,C,D,E,F,G,H,I,J,K,L>
    of(
        A a, B b, C c, D d, E e, F f, G g, H h, I i, J j,
        K k, L l
    ){
        return Tuple12.of(a,b,c,d,e,f,g,h,i,j,k,l);
    }

    /**
     * Создание кортежа из 13 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M>
    Tuple13<A,B,C,D,E,F,G,H,I,J,K,L,M>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m
    ){
        return Tuple13.of(a,b,c,d,e,f,g,h,i,j,k,l,m);
    }

    /**
     * Создание кортежа из 14 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N>
    Tuple14<A,B,C,D,E,F,G,H,I,J,K,L,M,N>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n
    ){
        return Tuple14.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n);
    }

    /**
     * Создание кортежа из 15 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O>
    Tuple15<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o
    ){
        return Tuple15.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o);
    }

    /**
     * Создание кортежа из 16 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P>
    Tuple16<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p
    ){
        return Tuple16.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p);
    }

    /**
     * Создание кортежа из 17 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param q 17ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @param <Q> Тип 17го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q>
    Tuple17<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q
    ){
        return Tuple17.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q);
    }

    /**
     * Создание кортежа из 18 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param q 17ый элемент
     * @param r 18ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @param <Q> Тип 17го элемента
     * @param <R> Тип 18го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R>
    Tuple18<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r
    ){
        return Tuple18.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r);
    }

    /**
     * Создание кортежа из 19 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param q 17ый элемент
     * @param r 18ый элемент
     * @param s 19ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @param <Q> Тип 17го элемента
     * @param <R> Тип 18го элемента
     * @param <S> Тип 19го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S>
    Tuple19<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r, S s
    ){
        return Tuple19.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s);
    }

    /**
     * Создание кортежа из 20 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param q 17ый элемент
     * @param r 18ый элемент
     * @param s 19ый элемент
     * @param t 20ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @param <Q> Тип 17го элемента
     * @param <R> Тип 18го элемента
     * @param <S> Тип 19го элемента
     * @param <T> Тип 20го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T>
    Tuple20<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r, S s, T t
    ){
        return Tuple20.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t);
    }

    /**
     * Создание кортежа из 21 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param q 17ый элемент
     * @param r 18ый элемент
     * @param s 19ый элемент
     * @param t 20ый элемент
     * @param u 21ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @param <Q> Тип 17го элемента
     * @param <R> Тип 18го элемента
     * @param <S> Тип 19го элемента
     * @param <T> Тип 20го элемента
     * @param <U> Тип 21го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U>
    Tuple21<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r, S s, T t,
        U u
    ){
        return Tuple21.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u);
    }

    /**
     * Создание кортежа из 22 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param q 17ый элемент
     * @param r 18ый элемент
     * @param s 19ый элемент
     * @param t 20ый элемент
     * @param u 21ый элемент
     * @param v 22ой элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @param <Q> Тип 17го элемента
     * @param <R> Тип 18го элемента
     * @param <S> Тип 19го элемента
     * @param <T> Тип 20го элемента
     * @param <U> Тип 21го элемента
     * @param <V> Тип 22го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V>
    Tuple22<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r, S s, T t,
        U u, V v
    ){
        return Tuple22.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v);
    }

    /**
     * Создание кортежа из 23 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param q 17ый элемент
     * @param r 18ый элемент
     * @param s 19ый элемент
     * @param t 20ый элемент
     * @param u 21ый элемент
     * @param v 22ой элемент
     * @param w 23ий элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @param <Q> Тип 17го элемента
     * @param <R> Тип 18го элемента
     * @param <S> Тип 19го элемента
     * @param <T> Тип 20го элемента
     * @param <U> Тип 21го элемента
     * @param <V> Тип 22го элемента
     * @param <W> Тип 23го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W>
    Tuple23<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r, S s, T t,
        U u, V v, W w
    ){
        return Tuple23.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w);
    }

    /**
     * Создание кортежа из 24 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param q 17ый элемент
     * @param r 18ый элемент
     * @param s 19ый элемент
     * @param t 20ый элемент
     * @param u 21ый элемент
     * @param v 22ой элемент
     * @param w 23ий элемент
     * @param x 24ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @param <Q> Тип 17го элемента
     * @param <R> Тип 18го элемента
     * @param <S> Тип 19го элемента
     * @param <T> Тип 20го элемента
     * @param <U> Тип 21го элемента
     * @param <V> Тип 22го элемента
     * @param <W> Тип 23го элемента
     * @param <X> Тип 24го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X>
    Tuple24<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r, S s, T t,
        U u, V v, W w, X x
    ){
        return Tuple24.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x);
    }

    /**
     * Создание кортежа из 25 элементов
     * @param a  1ый элемент
     * @param b  2ой элемент
     * @param c  3ый элемент
     * @param d  4ый элемент
     * @param e  5ый элемент
     * @param f  6ой элемент
     * @param g  7ой элемент
     * @param h  8ой элемент
     * @param i  9ый элемент
     * @param j 10ый элемент
     * @param k 11ый элемент
     * @param l 12ый элемент
     * @param m 13ый элемент
     * @param n 14ый элемент
     * @param o 15ый элемент
     * @param p 16ый элемент
     * @param q 17ый элемент
     * @param r 18ый элемент
     * @param s 19ый элемент
     * @param t 20ый элемент
     * @param u 21ый элемент
     * @param v 22ой элемент
     * @param w 23ий элемент
     * @param x 24ый элемент
     * @param y 25ый элемент
     * @param <A> Тип 1го элемента
     * @param <B> Тип 2го элемента
     * @param <C> Тип 3го элемента
     * @param <D> Тип 4го элемента
     * @param <E> Тип 5го элемента
     * @param <F> Тип 6го элемента
     * @param <G> Тип 7го элемента
     * @param <H> Тип 8го элемента
     * @param <I> Тип 9го элемента
     * @param <J> Тип 10го элемента
     * @param <K> Тип 11го элемента
     * @param <L> Тип 12го элемента
     * @param <M> Тип 13го элемента
     * @param <N> Тип 14го элемента
     * @param <O> Тип 15го элемента
     * @param <P> Тип 16го элемента
     * @param <Q> Тип 17го элемента
     * @param <R> Тип 18го элемента
     * @param <S> Тип 19го элемента
     * @param <T> Тип 20го элемента
     * @param <U> Тип 21го элемента
     * @param <V> Тип 22го элемента
     * @param <W> Тип 23го элемента
     * @param <X> Тип 24го элемента
     * @param <Y> Тип 25го элемента
     * @return Кортеж
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y>
    Tuple25<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r, S s, T t,
        U u, V v, W w, X x, Y y
    ){
        return Tuple25.of(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y);
    }
}
