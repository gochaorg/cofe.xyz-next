package xyz.cofe.fn;

/**
 * Функция от трех аргументов
 * @param <A> Первый аргумент
 * @param <B> Второй аргумент
 * @param <C> Третий аргмент
 * @param <Z> Результат
 */
public interface Fn3<A,B,C,Z> {
    /**
     * Вызов функции
     * @param a Первый аргумент
     * @param b Второй аргумент
     * @param c Третий аргмент
     * @return Результат
     */
    Z apply( A a, B b, C c );
}
