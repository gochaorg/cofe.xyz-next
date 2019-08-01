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
