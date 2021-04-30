package xyz.cofe.fn;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Пара значение
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 */
public interface Tuple2<A,B> {
    /**
     * Возвращает первый элемент пары
     * @return первый элемент пары
     */
    A a();

    /**
     * Возвращает второй элемент пары
     * @return второй элемент пары
     */
    B b();

    /**
     * Реализация кортежа
     * @param <A> тип значения
     */
    public static class Tuple2Impl<A,B> implements Tuple2<A,B>, Serializable {
        private final A a;
        private final B b;

        public Tuple2Impl(
            A a, B b
        ){
            this.a = a;this.b = b;
        }

        @Override public A a(){ return a; }
        @Override public B b(){ return b; }

        public String toString(){
            return "(a="+a+",b="+b+
                ")";
        }
    }

    /**
     * Врзвращает пару
     * @param a первый элемент
     * @param b второй элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @return пара значений
     */
    static <A,B> Tuple2<A,B> of(A a, B b){
        return new Tuple2Impl<>(a,b);
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple2<A,B> apply( BiConsumer<A,B> consumer){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(a(),b());
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple2<A,B> apply( Consumer2<A,B> consumer){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(a(),b());
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     * @param <Z> тип результата
     */
    default <Z> Z apply( BiFunction<A,B,Z> fn){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(a(),b());
    }

    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param c значение
     * @param <C> тип значения
     * @return Кортэж
     */
    default <C> Tuple3<A,B, C> add(C c){
        return Tuple3.of(a(),b(),c);
    }
}
