package xyz.cofe.fn;

/**
 * 23ка значений
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 * @param <C> тип третьего элемента
 * @param <D> тип 4го элемента
 * @param <E> тип 5го элемента
 * @param <F> тип 6го элемента
 * @param <G> тип 7го элемента
 * @param <H> тип 8го элемента
 * @param <I> тип 9го элемента
 * @param <J> тип 10го элемента
 * @param <K> тип 11го элемента
 * @param <L> тип 12го элемента
 * @param <M> тип 13го элемента
 * @param <N> тип 14го элемента
 * @param <O> тип 15го элемента
 * @param <P> тип 16го элемента
 * @param <Q> тип 17го элемента
 * @param <R> тип 18го элемента
 * @param <S> тип 19го элемента
 * @param <T> тип 20го элемента
 * @param <U> тип 21го элемента
 * @param <V> тип 22го элемента
 * @param <W> тип 23го элемента
 */
public interface Tuple23<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W> {
    /**
     * Возвращает первый элемент
     * @return первый элемент
     */
    A a();

    /**
     * Возвращает второй элемент
     * @return второй элемент
     */
    B b();

    /**
     * Возвращает третий элемент
     * @return третий элемент
     */
    C c();

    /**
     * Возвращает 4ий элемент
     * @return 4ий элемент
     */
    D d();

    /**
     * Возвращает 5ий элемент
     * @return 5ий элемент
     */
    E e();

    /**
     * Возвращает 6ий элемент
     * @return 6ий элемент
     */
    F f();

    /**
     * Возвращает 7ий элемент
     * @return 7ий элемент
     */
    G g();

    /**
     * Возвращает 8ий элемент
     * @return 8ий элемент
     */
    H h();

    /**
     * Возвращает 9ий элемент
     * @return 9ий элемент
     */
    I i();

    /**
     * Возвращает 10ий элемент
     * @return 10ий элемент
     */
    J j();

    /**
     * Возвращает 11ий элемент
     * @return 11ий элемент
     */
    K k();

    /**
     * Возвращает 12ий элемент
     * @return 12ий элемент
     */
    L l();

    /**
     * Возвращает 13ий элемент
     * @return 13ий элемент
     */
    M m();

    /**
     * Возвращает 14ий элемент
     * @return 14ий элемент
     */
    N n();

    /**
     * Возвращает 15ий элемент
     * @return 15ий элемент
     */
    O o();

    /**
     * Возвращает 16ий элемент
     * @return 16ий элемент
     */
    P p();

    /**
     * Возвращает 17ий элемент
     * @return 17ий элемент
     */
    Q q();

    /**
     * Возвращает 18ий элемент
     * @return 18ий элемент
     */
    R r();

    /**
     * Возвращает 19ий элемент
     * @return 19ий элемент
     */
    S s();

    /**
     * Возвращает 20ий элемент
     * @return 20ий элемент
     */
    T t();

    /**
     * Возвращает 21ий элемент
     * @return 21ий элемент
     */
    U u();

    /**
     * Возвращает 22ий элемент
     * @return 22ий элемент
     */
    V v();

    /**
     * Возвращает 23ий элемент
     * @return 23ий элемент
     */
    W w();

    /**
     * Возвращает 23ку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @param <D> тип 4го элемента
     * @param <E> тип 5го элемента
     * @return 23ка значений
     */
    static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W> Tuple23<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r, S s, T t,
        U u, V v, W w
    ){
        return new Tuple23<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W>() {
            @Override public A a() {
                return a;
            }
            @Override public B b() {
                return b;
            }
            @Override public C c() { return c; }
            @Override public D d() { return d; }
            @Override public E e() { return e; }
            @Override public F f() { return f; }
            @Override public G g() { return g; }
            @Override public H h() { return h; }
            @Override public I i() { return i; }
            @Override public J j() { return j; }
            @Override public K k() { return k; }
            @Override public L l() { return l; }
            @Override public M m() { return m; }
            @Override public N n() { return n; }
            @Override public O o() { return o; }
            @Override public P p() { return p; }
            @Override public Q q() { return q; }
            @Override public R r() { return r; }
            @Override public S s() { return s; }
            @Override public T t() { return t; }
            @Override public U u() { return u; }
            @Override public V v() { return v; }
            @Override public W w() { return w; }
        };
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple23<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W> apply(
        Consumer23<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W>
            consumer
    ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k(),l(),m(),n(),o(),
            p(),q(),r(),s(),t(),
            u(),v(),w()
        );
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     */
    default <Z> Z apply( Fn23<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k(),l(),m(),n(),o(),
            p(),q(),r(),s(),t(),
            u(),v(),w()
        );
    }
}
