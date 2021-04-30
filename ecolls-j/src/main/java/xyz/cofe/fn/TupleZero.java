package xyz.cofe.fn;

import java.io.Serializable;

/**
 * Кортэж из 0 элементов
 */
public class TupleZero implements Tuple, Serializable {
    /**
     * Экземпляр
     */
    public static final TupleZero instance = new TupleZero();
}
