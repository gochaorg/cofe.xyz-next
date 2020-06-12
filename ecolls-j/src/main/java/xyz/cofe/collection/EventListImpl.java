package xyz.cofe.collection;

import xyz.cofe.fn.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация приватных методов EventList для обратной совместимости с java 8
 */
@SuppressWarnings("WeakerAccess")
public class EventListImpl {
    /**
     * Удаление элементов согласно указанному предикату
     * @param lst список
     * @param filter предикат
     * @param <E> тип элементов
     * @return факт удаления
     */
    public static <E> boolean removeByPredicate( EventList<E> lst, Predicate<? super E> filter) {
        return removeByPredicate(lst,filter,null);
    }

    /**
     * Удаление элементов согласно указанному предикату
     * @param lst список
     * @param filter предикат
     * @param <E> тип элементов
     * @param fireDeleting (возможно null) уведомление о удалении
     * @return факт удаления
     */
    @SuppressWarnings({"ConstantConditions", "UnnecessaryUnboxing"})
    public static <E> boolean removeByPredicate(
        EventList<E> lst,
        Predicate<? super E> filter,
        BiConsumer<Integer,? super E> fireDeleting
    ) {
        if( filter==null ) throw new IllegalArgumentException("filter==null");
        if( lst==null ) throw new IllegalArgumentException("lst==null");

        List<E> tgt = lst.target();
        if( tgt == null ) throw new TargetNotAvailable();

        int changeCount = 0;
        TreeSet<Integer> removeSet = new TreeSet<>();
        for( int i = lst.size()-1; i >= 0; i-- ){
            E e = tgt.get(i);
            if( filter.test(e) ){
                if( fireDeleting!=null )fireDeleting.accept(i, e);
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
