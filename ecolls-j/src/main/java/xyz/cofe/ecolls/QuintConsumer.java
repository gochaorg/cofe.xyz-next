package xyz.cofe.ecolls;

/**
 * Потребитель данных от 5 значений
 * @param <A> Тип первого значения
 * @param <B> Тип 2го значения
 * @param <C> Тип 3го значения
 * @param <D> Тип 4го значения
 * @param <E> Тип 5го значения
 */
public interface QuintConsumer<A,B,C,D,E> {
    /**
     * Получает значение
     * @param a первое значение
     * @param b 2ое значение
     * @param c 3ое значение
     * @param d 4ое значение
     * @param e 5ое значение
     */
    void accept(A a,B b,C c,D d,E e);
}
