package xyz.cofe.fn;

import java.io.Serializable;

/**
 * 6ка значений
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 * @param <C> тип третьего элемента
 * @param <D> тип 4го элемента
 * @param <E> тип 5го элемента
 * @param <F> тип 6го элемента
 */
public interface Tuple6<A,B,C,D,E,F> {
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
     * Реализация кортежа
     * @param <A> тип значения
     */
    public static class Tuple6Impl<A,B,C,D,E,F> implements Tuple6<A,B,C,D,E,F>, Serializable {
        private final A a;
        private final B b;
        private final C c;
        private final D d;
        private final E e;
        private final F f;

        public Tuple6Impl(
            A a, B b, C c, D d, E e,
            F f
        ){
            this.a = a;this.b = b;this.c = c;this.d = d;this.e = e;
            this.f = f;
        }

        @Override public A a(){ return a; }
        @Override public B b(){ return b; }
        @Override public C c(){ return c; }
        @Override public D d(){ return d; }
        @Override public E e(){ return e; }
        @Override public F f(){ return f; }

        public String toString(){
            return "(a="+a+",b="+b+",c="+c+",d="+d+",e="+e+
                ",f="+f+
                ")";
        }
    }

    /**
     * Возвращает 6ку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param c 3й элемент
     * @param d 4й элемент
     * @param e 5й элемент
     * @param f 6й элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @param <D> тип 4го элемента
     * @param <E> тип 5го элемента
     * @param <F> тип 6го элемента
     * @return 6ка значений
     */
    static <A,B,C,D,E,F> Tuple6<A,B,C,D,E,F>
    of(
        A a, B b, C c, D d, E e,
        F f
    ){
        return new Tuple6Impl<>(
            a,b,c,d,e,
            f
        );
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple6<A,B,C,D,E,F> apply(
        Consumer6<A, B, C, D, E, F>
            consumer
    ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(
            a(),b(),c(),d(),e(),
            f()
        );
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     * @param <Z> тип результата
     */
    default <Z> Z apply( Fn6<A, B, C, D, E, F, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(
            a(),b(),c(),d(),e(),
            f()
        );
    }

    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param g значение
     * @param <G> тип значения
     * @return Кортэж
     */
    default <G> Tuple7<A,B,C,D,E,F,G> add(G g){
        return Tuple.of(a(),b(),c(),d(),e(),f(),g);
    }
}
