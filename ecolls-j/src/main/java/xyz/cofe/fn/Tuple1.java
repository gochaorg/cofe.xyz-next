package xyz.cofe.fn;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Значение - кортэаж из одного элемента
 * @param <A> тип значения
 */
public interface Tuple1<A> {
    /**
     * Возвращает значение
     * @return значение
     */
    A a();

    /**
     * Врзвращает пару
     * @param a первый элемент
     * @param <A> тип первого элемента
     * @return пара значений
     */
    static <A> Tuple1<A> of( A a ){
        return new Tuple1<A>() {
            @Override
            public A a() {
                return a;
            }
        };
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple1<A> apply( Consumer<A> consumer ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(a());
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple1<A> apply( Consumer1<A> consumer ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(a());
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     * @param <Z> тип результата
     */
    default <Z> Z apply( Function<A, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(a());
    }

    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param b значение
     * @param <B> тип значения
     * @return Кортэж
     */
    default <B> Tuple2<A,B> add(B b){
        return Tuple2.of(a(),b);
    }
}
