package xyz.cofe.ecolls;

/**
 * Функция от пяти аргументов
 * @param <A> Первый аргумент
 * @param <B> Второй аргумент
 * @param <C> Третий аргумент
 * @param <D> Четвертый аргумент
 * @param <E> Пятый аргумент
 * @param <Z> Результат
 */
public interface Fn5<A,B,C,D,E,Z> {
    /**
     * Вызов функции
     * @param a Первый аргумент
     * @param b Второй аргумент
     * @param c Третий аргумент
     * @param d Четвертый аргумент
     * @param e Пятый аргумент
     * @return Результат
     */
    Z apply( A a, B b, C c, D d, E e );
}
