package xyz.cofe.fn;

/**
 * 16ка значений
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
 */
public interface Tuple16<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P> {
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
     * Возвращает 16ку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @param <D> тип 4го элемента
     * @param <E> тип 5го элемента
     * @return 16ка значений
     */
    static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P> Tuple16<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p
    ){
        return new Tuple16<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P>() {
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
        };
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple16<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P> apply(
        Consumer16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>
            consumer
    ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k(),l(),m(),n(),o(),
            p()
        );
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     */
    default <Z> Z apply( Fn16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k(),l(),m(),n(),o(),
            p()
        );
    }
}
