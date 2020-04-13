package xyz.cofe.fn;

/**
 * Пятерка параметров
 * @param <A> Тип первого значения
 * @param <B> Тип второго значения
 * @param <C> Тип третьего значения
 * @param <D> Тип четвертого значения
 * @param <E> Тип пятого значения
 */
public interface Quintuple<A,B,C,D,E> {
    /**
     * Возвращает первый элемент
     * @return первый элемент
     */
    A a();

    /**
     * Возвращает второй элемент
     * @return второй элемент
     */
    B b();

    /**
     * Возвращает третий элемент
     * @return третий элемент
     */
    C c();

    /**
     * Возвращает четвертый элемент
     * @return четвертый элемент
     */
    D d();

    /**
     * Возвращает пятый элемент
     * @return пятый элемент
     */
    E e();

    /**
     * Cоздание 5ки параметров
     * @param a Первый параметр
     * @param b Второй параметр
     * @param c Третий параметр
     * @param d Четвертый параметр
     * @param e Пятый параметр
     * @param <A> Тип первого параметр
     * @param <B> Тип 2го параметр
     * @param <C> Тип 3го параметр
     * @param <D> Тип 4го параметр
     * @param <E> Тип 5го параметр
     * @return 5 параметров
     */
    static <A,B,C,D,E> Quintuple<A,B,C,D,E> of(A a, B b, C c, D d, E e){
        return new Quintuple<A, B, C, D, E>() {
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

            @Override
            public E e() {
                return e;
            }
        };
    }
}
