package xyz.cofe.fn;

import java.io.Serializable;

/**
 * 7ка значений
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 * @param <C> тип третьего элемента
 * @param <D> тип 4го элемента
 * @param <E> тип 5го элемента
 * @param <F> тип 6го элемента
 * @param <G> тип 7го элемента
 */
public interface Tuple7<A,B,C,D,E,F,G> {
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
     * Реализация кортежа
     * @param <A> тип значения
     */
    public static class Tuple7Impl<A,B,C,D,E,F,G> implements Tuple7<A,B,C,D,E,F,G>, Serializable {
        private final A a;
        private final B b;
        private final C c;
        private final D d;
        private final E e;
        private final F f;
        private final G g;

        public Tuple7Impl(
            A a, B b, C c, D d, E e,
            F f, G g
        ){
            this.a = a;this.b = b;this.c = c;this.d = d;this.e = e;
            this.f = f;this.g = g;
        }

        @Override public A a(){ return a; }
        @Override public B b(){ return b; }
        @Override public C c(){ return c; }
        @Override public D d(){ return d; }
        @Override public E e(){ return e; }
        @Override public F f(){ return f; }
        @Override public G g(){ return g; }

        public String toString(){
            return "(a="+a+",b="+b+",c="+c+",d="+d+",e="+e+
                ",f="+f+",g="+g+
                ")";
        }
    }

    /**
     * Возвращает 7ку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param c 3й элемент
     * @param d 4й элемент
     * @param e 5й элемент
     * @param f 6й элемент
     * @param g 7й элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @param <D> тип 4го элемента
     * @param <E> тип 5го элемента
     * @param <F> тип 6го элемента
     * @param <G> тип 7го элемента
     * @return 7ка значений
     */
    static <A,B,C,D,E,F,G> Tuple7<A,B,C,D,E,F,G>
    of(
        A a, B b, C c, D d, E e,
        F f, G g
    ){
        return new Tuple7Impl<>(
            a,b,c,d,e,
            f,g
        );
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple7<A,B,C,D,E,F,G> apply(
        Consumer7<A, B, C, D, E, F, G>
            consumer
    ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(
            a(),b(),c(),d(),e(),
            f(),g()
        );
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     * @param <Z> тип результата
     */
    default <Z> Z apply( Fn7<A, B, C, D, E, F, G, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(
            a(),b(),c(),d(),e(),
            f(),g()
        );
    }

    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param h значение
     * @param <H> тип значения
     * @return Кортэж
     */
    default <H> Tuple8<A,B,C,D,E,F,G,H> add(H h){
        return Tuple.of(a(),b(),c(),d(),e(),f(),g(),h);
    }
}
