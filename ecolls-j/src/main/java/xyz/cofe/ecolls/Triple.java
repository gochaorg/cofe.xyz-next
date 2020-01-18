package xyz.cofe.ecolls;

/**
 * Тройка значений
 * @param <A> Тип первого элемента
 * @param <B> Тип второго элемента
 * @param <C> Тип третьего элемента
 */
public interface Triple<A,B,C> {
    /**
     * Возвращает первый элемент тройки
     * @return первый элемент тройки
     */
    A a();

    /**
     * Возвращает второй элемент тройки
     * @return второй элемент тройки
     */
    B b();

    /**
     * Возвращает третий элемент тройки
     * @return третий элемент тройки
     */
    C c();

    /**
     * Cоздание 3ки параметров
     * @param a Первый параметр
     * @param b Второй параметр
     * @param c Третий параметр
     * @param <A> Тип первого параметр
     * @param <B> Тип 2го параметр
     * @param <C> Тип 3го параметр
     * @return 3ка параметров
     */
    static <A,B,C> Triple<A,B,C> of(A a, B b, C c){
        return new Triple<A, B, C>() {
            @Override
            public A a() {
                return a;
            }

            @Override
            public B b() {
                return b;
            }

            @Override
            public C c() {
                return c;
            }
        };
    }
}
