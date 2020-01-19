package xyz.cofe.fn;

/**
 * Потребитель данных от 2 значений
 * @param <A> Тип первого значения
 * @param <B> Тип 2го значения
 */
public interface Consumer2<A, B> {
    /**
     * Получает значение
     * @param a первое значение
     * @param b 2ое значение
     */
    void accept( A a, B b );
}
