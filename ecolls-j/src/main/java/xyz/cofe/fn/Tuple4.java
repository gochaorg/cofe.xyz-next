package xyz.cofe.fn;

import java.io.Serializable;

/**
 * 4ка значений
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 * @param <C> тип третьего элемента
 * @param <D> тип 4го элемента
 */
public interface Tuple4<A,B,C,D> {
    /**
     * Возвращает первый элемент 4ки значений
     * @return первый элемент 4ки значений
     */
    A a();

    /**
     * Возвращает второй элемент 4ки значений
     * @return второй элемент 4ки значений
     */
    B b();

    /**
     * Возвращает третий элемент 4ки значений
     * @return третий элемент 4ки значений
     */
    C c();

    /**
     * Возвращает 4ий элемент 4ки значений
     * @return 4ий элемент 4ки значений
     */
    D d();

    /**
     * Реализация кортежа
     * @param <A> тип значения
     */
    public static class Tuple4Impl<A,B,C,D> implements Tuple4<A,B,C,D>, Serializable {
        private final A a;
        private final B b;
        private final C c;
        private final D d;

        public Tuple4Impl(
            A a, B b, C c, D d
        ){
            this.a = a;this.b = b;this.c = c;this.d = d;
        }

        @Override public A a(){ return a; }
        @Override public B b(){ return b; }
        @Override public C c(){ return c; }
        @Override public D d(){ return d; }

        public String toString(){
            return "(a="+a+",b="+b+",c="+c+",d="+d+
                ")";
        }
    }

    /**
     * Возвращает 4ку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param c третий элемент
     * @param d 4й элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @param <D> тип 4го элемента
     * @return 4ка значений
     */
    static <A,B,C,D> Tuple4<A,B, C, D> of( A a, B b, C c, D d ){
        return new Tuple4Impl<>(
            a,b,c,d
        );
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple4<A,B,C,D> apply( Consumer4<A, B, C, D> consumer ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(a(),b(),c(),d());
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     * @param <Z> тип результата
     */
    default <Z> Z apply( Fn4<A, B, C, D, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(a(),b(),c(),d());
    }

    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param e значение
     * @param <E> тип значения
     * @return Кортэж
     */
    default <E> Tuple5<A,B,C,D,E> add(E e){
        return Tuple.of(a(),b(),c(),d(),e);
    }
}
