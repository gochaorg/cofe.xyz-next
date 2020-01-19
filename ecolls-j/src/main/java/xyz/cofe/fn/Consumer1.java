package xyz.cofe.fn;

/**
 * Потребитель данных
 * @param <A> Тип значения
 */
public interface Consumer1<A> {
    /**
     * Получает значение
     * @param a первое значение
     */
    void accept( A a );
}
