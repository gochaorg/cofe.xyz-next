package xyz.cofe.fn;

import java.io.Serializable;

/**
 * 8ка значений
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 * @param <C> тип третьего элемента
 * @param <D> тип 4го элемента
 * @param <E> тип 5го элемента
 * @param <F> тип 6го элемента
 * @param <G> тип 7го элемента
 * @param <H> тип 8го элемента
 */
public interface Tuple8<A,B,C,D,E,F,G,H> {
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
     * Реализация кортежа
     * @param <A> тип значения
     */
    public static class Tuple8Impl<A,B,C,D,E,F,G,H> implements Tuple8<A,B,C,D,E,F,G,H>, Serializable {
        private final A a;
        private final B b;
        private final C c;
        private final D d;
        private final E e;
        private final F f;
        private final G g;
        private final H h;

        public Tuple8Impl(
            A a, B b, C c, D d, E e,
            F f, G g, H h
        ){
            this.a = a;this.b = b;this.c = c;this.d = d;this.e = e;
            this.f = f;this.g = g;this.h = h;
        }

        @Override public A a(){ return a; }
        @Override public B b(){ return b; }
        @Override public C c(){ return c; }
        @Override public D d(){ return d; }
        @Override public E e(){ return e; }
        @Override public F f(){ return f; }
        @Override public G g(){ return g; }
        @Override public H h(){ return h; }

        public String toString(){
            return "(a="+a+",b="+b+",c="+c+",d="+d+",e="+e+
                ",f="+f+",g="+g+",h="+h+
                ")";
        }
    }

    /**
     * Возвращает 8ку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param c 3й элемент
     * @param d 4й элемент
     * @param e 5й элемент
     * @param f 6й элемент
     * @param g 7й элемент
     * @param h 8й элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @param <D> тип 4го элемента
     * @param <E> тип 5го элемента
     * @param <F> тип 6го элемента
     * @param <G> тип 7го элемента
     * @param <H> тип 8го элемента
     * @return 8ка значений
     */
    static <A,B,C,D,E,F,G,H> Tuple8<A,B,C,D,E,F,G,H>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h
    ){
        return new Tuple8Impl<>(
            a,b,c,d,e,
            f,g,h
        );
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple8<A,B,C,D,E,F,G,H> apply(
        Consumer8<A, B, C, D, E, F, G, H>
            consumer
    ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(
            a(),b(),c(),d(),e(),
            f(),g(),h()
        );
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     * @param <Z> тип результата
     */
    default <Z> Z apply( Fn8<A, B, C, D, E, F, G, H, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(
            a(),b(),c(),d(),e(),
            f(),g(),h()
        );
    }

    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param i значение
     * @param <I> тип значения
     * @return Кортэж
     */
    default <I> Tuple9<A,B,C,D,E,F,G,H,I> add(I i){
        return Tuple.of(a(),b(),c(),d(),e(),f(),g(),h(),i);
    }
}
