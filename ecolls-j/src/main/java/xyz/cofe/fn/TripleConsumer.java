package xyz.cofe.fn;

/**
 * Потребитель данных от 3 значений
 * @param <A> Тип первого значения
 * @param <B> Тип 2го значения
 * @param <C> Тип 3го значения
 */
public interface TripleConsumer<A, B, C> extends Consumer3<A,B,C> {
}
