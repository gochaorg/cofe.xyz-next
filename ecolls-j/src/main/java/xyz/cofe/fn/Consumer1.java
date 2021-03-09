package xyz.cofe.fn;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Потребитель данных
 * @param <A> Тип значения
 */
public interface Consumer1<A> extends Consumer<A>, Serializable {
    /**
     * Получает значение
     * @param a первое значение
     */
    void accept( A a );
}
