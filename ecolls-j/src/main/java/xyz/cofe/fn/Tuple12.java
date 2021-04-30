package xyz.cofe.fn;

import java.io.Serializable;

/**
 * 12ка значений
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
 */
public interface Tuple12<A,B,C,D,E,F,G,H,I,J,K,L> {
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
     * Реализация кортежа
     * @param <A> тип значения
     */
    public static class Tuple12Impl<A,B,C,D,E,F,G,H,I,J,K,L> implements Tuple12<A,B,C,D,E,F,G,H,I,J,K,L>, Serializable {
        private final A a;
        private final B b;
        private final C c;
        private final D d;
        private final E e;
        private final F f;
        private final G g;
        private final H h;
        private final I i;
        private final J j;
        private final K k;
        private final L l;

        public Tuple12Impl(
            A a, B b, C c, D d, E e,
            F f, G g, H h, I i, J j,
            K k, L l
        ){
            this.a = a;this.b = b;this.c = c;this.d = d;this.e = e;
            this.f = f;this.g = g;this.h = h;this.i = i;this.j = j;
            this.k = k;this.l = l;
        }

        @Override public A a(){ return a; }
        @Override public B b(){ return b; }
        @Override public C c(){ return c; }
        @Override public D d(){ return d; }
        @Override public E e(){ return e; }
        @Override public F f(){ return f; }
        @Override public G g(){ return g; }
        @Override public H h(){ return h; }
        @Override public I i(){ return i; }
        @Override public J j(){ return j; }
        @Override public K k(){ return k; }
        @Override public L l(){ return l; }

        public String toString(){
            return "(a="+a+",b="+b+",c="+c+",d="+d+",e="+e+
                ",f="+f+",g="+g+",h="+h+",i="+i+",j="+j+
                ",k="+k+",l="+l+
                ")";
        }
    }

    /**
     * Возвращает 12ку значений
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
     * @param l 12й элемент
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
     * @param <L> тип 12го элемента
     * @return 12ка значений
     */
    static <A,B,C,D,E,F,G,H,I,J,K,L> Tuple12<A,B,C,D,E,F,G,H,I,J,K,L>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l
    ){
        return new Tuple12Impl<>(
            a,b,c,d,e,
            f,g,h,i,j,
            k,l
        );
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple12<A,B,C,D,E,F,G,H,I,J,K,L> apply(
        Consumer12<A, B, C, D, E, F, G, H, I, J, K, L>
            consumer
    ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k(),l()
        );
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param <Z> тип результата
     * @param fn функция приемник
     * @return результат вызова функции
     */
    default <Z> Z apply( Fn12<A, B, C, D, E, F, G, H, I, J, K, L, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k(),l()
        );
    }

    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param m значение
     * @param <M> тип значения
     * @return Кортэж
     */
    default <M> Tuple13<A,B,C,D,E,F,G,H,I,J,K,L,M> add(M m){
        return Tuple.of(a(),b(),c(),d(),e(),f(),g(),h(),i(),j(),k(),l(),m);
    }
}
