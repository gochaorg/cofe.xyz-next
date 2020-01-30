package xyz.cofe.fn;

import java.util.function.Consumer;

/**
 * Потребитель данных
 * @param <A> Тип значения
 */
public interface Consumer1<A> extends Consumer<A> {
    /**
     * Получает значение
     * @param a первое значение
     */
    void accept( A a );
}
