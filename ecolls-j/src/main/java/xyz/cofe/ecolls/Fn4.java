package xyz.cofe.ecolls;

/**
 * Функция от четырех аргументов
 * @param <A> Первый аргумент
 * @param <B> Второй аргумент
 * @param <C> Третий аргумент
 * @param <D> Четвертый аргумент
 * @param <Z> Результат
 */
public interface Fn4<A,B,C,D,Z> {
    /**
     * Вызов функции
     * @param a Первый аргумент
     * @param b Второй аргумент
     * @param c Третий аргумент
     * @param d Четвертый аргумент
     * @return Результат
     */
    Z apply( A a, B b, C c, D d );
}
