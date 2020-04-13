package xyz.cofe.fn;

/**
 * Потребитель данных от 7 значений
 * @param <A> Тип первого значения
 * @param <B> Тип 2го значения
 * @param <C> Тип 3го значения
 * @param <D> Тип 4го значения
 * @param <E> Тип 5го значения
 * @param <F> Тип 6го значения
 * @param <G> Тип 6го значения
 */
public interface Consumer7<A,B,C,D,E,F,G> {
    /**
     * Получает значение
     * @param a первое значение
     * @param b 2ое значение
     * @param c 3ое значение
     * @param d 4ое значение
     * @param e 5ое значение
     * @param f 6ое значение
     * @param g 7ое значение
     */
    void accept( A a, B b, C c, D d, E e, F f, G g );
}
