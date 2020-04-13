package xyz.cofe.fn;

/**
 * Функция от 11ти аргументов
 * @param <A> Первый аргумент
 * @param <B> Второй аргумент
 * @param <C> Третий аргумент
 * @param <D> Четвертый аргумент
 * @param <E> Пятый аргумент
 * @param <F> Шестой аргумент
 * @param <G> Седьмой аргумент
 * @param <H> Восьмой аргумент
 * @param <I> Девятый аргумент
 * @param <J> Десятый аргумент
 * @param <K> Одинацетый аргумент
 * @param <Z> Результат
 */
public interface Fn11<A,B,C,D,E,F,G,H,I,J,K,Z> {
    /**
     * Вызов функции
     * @param a Первый аргумент
     * @param b Второй аргумент
     * @param c Третий аргумент
     * @param d Четвертый аргумент
     * @param e Пятый аргумент
     * @param f Шестой аргумент
     * @param g Седьмой аргумент
     * @param h Восьмой аргумент
     * @param i Девятый аргумент
     * @param j Десятый аргумент
     * @param k Одинацетый аргумент
     * @return Результат
     */
    Z apply( A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k );
}
