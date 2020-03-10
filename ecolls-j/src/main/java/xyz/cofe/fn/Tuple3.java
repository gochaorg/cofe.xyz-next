package xyz.cofe.fn;

/**
 * 3ка значений
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 * @param <C> Тип третьего элемента
 */
public interface Tuple3<A,B,C> {
    /**
     * Возвращает первый элемент 3ки значений
     * @return первый элемент пары
     */
    A a();

    /**
     * Возвращает второй элемент 3ки значений
     * @return второй элемент пары
     */
    B b();

    /**
     * Возвращает третий элемент 3ки значений
     * @return третий элемент тройки
     */
    C c();

    /**
     * Возвращает тройку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param c третий элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @return пара значений
     */
    static <A,B,C> Tuple3<A,B, C> of( A a, B b, C c ){
        return new Tuple3<A, B, C>() {
            @Override public A a() {
                return a;
            }
            @Override public B b() {
                return b;
            }
            @Override public C c() { return c; }
        };
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple3<A,B,C> apply( Consumer3<A, B, C> consumer ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(a(),b(),c());
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     * @param <Z> тип результата
     */
    default <Z> Z apply( Fn3<A, B, C, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(a(),b(),c());
    }


    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param d значение
     * @param <D> тип значения
     * @return Кортэж
     */
    default <D> Tuple4<A,B, C, D> add(D d){
        return Tuple4.of(a(),b(),c(),d);
    }
}
