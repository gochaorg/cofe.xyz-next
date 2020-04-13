package xyz.cofe.fn;

/**
 * 5ка значений
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 * @param <C> тип третьего элемента
 * @param <D> тип 4го элемента
 * @param <E> тип 4го элемента
 */
public interface Tuple5<A,B,C,D,E> {
    /**
     * Возвращает первый элемент 5ки значений
     * @return первый элемент 5ки значений
     */
    A a();

    /**
     * Возвращает второй элемент 5ки значений
     * @return второй элемент 5ки значений
     */
    B b();

    /**
     * Возвращает третий элемент 5ки значений
     * @return третий элемент 5ки значений
     */
    C c();

    /**
     * Возвращает 4ий элемент 5ки значений
     * @return 4ий элемент 5ки значений
     */
    D d();

    /**
     * Возвращает 5ий элемент 5ки значений
     * @return 5ий элемент 5ки значений
     */
    E e();

    /**
     * Возвращает 5ку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param c 3й элемент
     * @param d 4й элемент
     * @param e 5й элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @param <D> тип 4го элемента
     * @param <E> тип 5го элемента
     * @return 4ка значений
     */
    static <A,B,C,D,E> Tuple5<A,B, C, D, E> of( A a, B b, C c, D d, E e ){
        return new Tuple5<A, B, C, D, E>() {
            @Override public A a() {
                return a;
            }
            @Override public B b() {
                return b;
            }
            @Override public C c() { return c; }
            @Override public D d() { return d; }
            @Override public E e() { return e; }
        };
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple5<A,B,C,D,E> apply( Consumer5<A, B, C, D, E> consumer ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(a(),b(),c(),d(),e());
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     * @param <Z> тип результата
     */
    default <Z> Z apply( Fn5<A, B, C, D, E, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(a(),b(),c(),d(), e());
    }
}
