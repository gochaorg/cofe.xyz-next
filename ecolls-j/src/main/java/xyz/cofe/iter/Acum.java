package xyz.cofe.iter;

/**
 * Контейнер для значения
 * @param <A> тип значения
 */
public class Acum<A> {
    private A value;

    /**
     * Конструктор
     * @param initial начальное значение
     */
    Acum( A initial ){
        this.value = initial;
    }

    /**
     * Получение значения
     * @return значение
     */
    public A get(){ return value; }

    /**
     * Установка значения
     * @param value значение
     * @return пбредыдущее значение
     */
    public A set(A value){
        A prev = this.value;
        this.value = value;
        return prev;
    }
}
