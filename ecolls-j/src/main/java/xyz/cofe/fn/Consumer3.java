package xyz.cofe.fn;

import java.io.Serializable;

/**
 * Потребитель данных от 3 значений
 * @param <A> Тип первого значения
 * @param <B> Тип 2го значения
 * @param <C> Тип 3го значения
 */
public interface Consumer3<A, B, C>  extends Serializable {
    /**
     * Получает значение
     * @param a первое значение
     * @param b 2ое значение
     * @param c 3ое значение
     */
    void accept( A a, B b, C c );
}
