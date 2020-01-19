package xyz.cofe.fn;

/**
 * Четверка параметров
 * @param <A> Тип первого значения
 * @param <B> Тип второго значения
 * @param <C> Тип третьего значения
 * @param <D> Тип четвертого значения
 */
public interface Quadruple<A,B,C,D> {
    /**
     * Возвращает первый элемент четверки
     * @return первый элемент четверки
     */
    A a();

    /**
     * Возвращает второй элемент четверки
     * @return второй элемент четверки
     */
    B b();

    /**
     * Возвращает третий элемент четверки
     * @return третий элемент четверки
     */
    C c();

    /**
     * Возвращает четвертый элемент четверки
     * @return четвертый элемент четверки
     */
    D d();

    /**
     * Cоздание 4ки параметров
     * @param a Первый параметр
     * @param b Второй параметр
     * @param c Третий параметр
     * @param d Четвертый параметр
     * @param <A> Тип первого параметр
     * @param <B> Тип 2го параметр
     * @param <C> Тип 3го параметр
     * @param <D> Тип 4го параметр
     * @return 4ка параметров
     */
    static <A,B,C,D> Quadruple<A,B,C,D> of(A a, B b, C c, D d){
        return new Quadruple<A, B, C, D>() {
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

            @Override
            public D d() {
                return d;
            }
        };
    }
}
