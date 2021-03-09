package xyz.cofe.fn;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Функция без аргументов
 * @param <Z> результат
 */
public interface Fn0<Z> extends Supplier<Z>, Serializable {
    /**
     * Вызов функции
     * @return Результат
     */
    Z apply();
    default Z get(){ return apply(); }
}
