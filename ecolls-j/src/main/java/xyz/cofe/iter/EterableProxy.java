package xyz.cofe.iter;

import java.util.Iterator;

/**
 * Прокси для итератора
 * @param <A> тип элемента
 */
public class EterableProxy<A> implements Eterable<A> {
    protected final Iterable<A> target;

    /**
     * Конструктор
     * @param target исходный итератор
     */
    public EterableProxy(Iterable<A> target){
        if( target == null )throw new IllegalArgumentException( "target == null" );
        this.target = target;
    }

    @Override
    public Iterator<A> iterator() {
        return target.iterator();
    }
}
