package xyz.cofe.fn;

/**
 * 11ка значений
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
 */
public interface Tuple11<A,B,C,D,E,F,G,H,I,J,K> {
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
     * Возвращает 11ку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param c 3й элемент
     * @param d 4й элемент
     * @param e 5й элемент
     * @param f 6й элемент
     * @param g 7й элемент
     * @param h 8й элемент
     * @param i 9й элемент
     * @param j 10й элемент
     * @param k 11й элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @param <D> тип 4го элемента
     * @param <E> тип 5го элемента
     * @param <F> тип 6го элемента
     * @param <G> тип 7го элемента
     * @param <H> тип 8го элемента
     * @param <I> тип 9го элемента
     * @param <J> тип 10го элемента
     * @param <K> тип 11го элемента
     * @return 11ка значений
     */
    static <A,B,C,D,E,F,G,H,I,J,K> Tuple11<A,B,C,D,E,F,G,H,I,J,K>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k
    ){
        return new Tuple11<A,B,C,D,E,F,G,H,I,J,K>() {
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
        };
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple11<A,B,C,D,E,F,G,H,I,J,K> apply(
        Consumer11<A, B, C, D, E, F, G, H, I, J, K>
            consumer
    ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k()
        );
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     * @param <Z> тип результата
     */
    default <Z> Z apply( Fn11<A, B, C, D, E, F, G, H, I, J, K, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k()
        );
    }

    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param l значение
     * @param <L> тип значения
     * @return Кортэж
     */
    default <L> Tuple12<A,B,C,D,E,F,G,H,I,J,K,L> add(L l){
        return Tuple.of(a(),b(),c(),d(),e(),f(),g(),h(),i(),j(),k(),l);
    }
}
