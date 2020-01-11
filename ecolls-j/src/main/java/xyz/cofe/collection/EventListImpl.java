package xyz.cofe.collection;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;

/**
 * Реализация приватных методов EventList для обратной совместимости с java 8
 */
public class EventListImpl {
    /**
     * Удаление элементов согласно указанному предикату
     * @param lst список
     * @param filter предикат
     * @param <E> тип элементов
     * @return факт удаления
     */
    @SuppressWarnings({"ConstantConditions", "UnnecessaryUnboxing"})
    public static <E> boolean removeByPredicate( EventList<E> lst, Predicate<? super E> filter) {
        if( filter==null ) throw new IllegalArgumentException("filter==null");
        if( lst==null ) throw new IllegalArgumentException("lst==null");

        List<E> tgt = lst.target();
        if( tgt == null ) throw new TargetNotAvailable();

        int changeCount = 0;
        TreeSet<Integer> removeSet = new TreeSet<>();
        for( int i = lst.size()-1; i >= 0; i-- ){
            E e = tgt.get(i);
            if( filter.test(e) ){
                //fireDeleting(i, e);
                removeSet.add(i);
            }
        }

        Iterator<Integer> iter = removeSet.descendingIterator();
        if( iter != null ){
            while( iter.hasNext() ) {
                int idx = iter.next().intValue();
                E e = tgt.remove(idx);
                lst.fireDeleted(idx, e);
                changeCount++;
            }
        }

        return changeCount>0;
    }
}