package xyz.cofe.fn;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * Функция 2го аргумента
 * @param <A> аргумент
 * @param <B> аргумент
 * @param <Z> результат
 */
public interface Fn2<A,B,Z> extends BiFunction<A,B,Z>, Serializable {
    /**
     * Вызов функции
     * @param a аргумент
     * @param b аргумент
     * @return результат
     */
    Z apply(A a, B b);
}
