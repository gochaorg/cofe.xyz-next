package xyz.cofe.fn;

/**
 * Потребитель данных от 3 значений
 * @param <A> Тип первого значения
 * @param <B> Тип 2го значения
 * @param <C> Тип 3го значения
 */
public interface Consumer3<A, B, C> {
    /**
     * Получает значение
     * @param a первое значение
     * @param b 2ое значение
     * @param c 3ое значение
     */
    void accept( A a, B b, C c );
}
