package xyz.cofe.collection;

import xyz.cofe.ecolls.ReadWriteLockSupport;
import xyz.cofe.ecolls.TripleConsumer;
import xyz.cofe.scn.LongScn;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Список с поддержкой уведомлений
 * @param <E> тип элемента списка
 */
public interface EventList<E>
    extends List<E>,
            CollectionEventPublisher<EventList<E>, E>,
            ReadWriteLockSupport,
            LongScn<EventList<E>,CollectionEvent<EventList<E>,E>>
{
    /**
     * Возвращает целевой список, над которым происходят преобразования
     * @return целевой список
     */
    List<E> target();

    //<editor-fold desc="notify methods">
    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    default AutoCloseable onInserted(TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener((e) -> {
            if( e instanceof InsertedEvent ){
                InsertedEvent<EventList<E>,Integer,E> ev = (InsertedEvent)e;
                ls.accept(ev.getIndex(),null,ev.getNewItem());
            }
        });
    }

    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    default AutoCloseable onUpdated(TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener((e) -> {
            if( e instanceof UpdatedEvent ){
                UpdatedEvent<EventList<E>,Integer,E> ev = (UpdatedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),ev.getNewItem());
            }
        });
    }

    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    default AutoCloseable onDeleted(TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener((e) -> {
            if( e instanceof DeletedEvent ){
                DeletedEvent<EventList<E>,Integer,E> ev = (DeletedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),null);
            }
        });
    }

    @Override
    default void fireCollectionEvent(CollectionEvent<EventList<E>, E> event) {
        nextscn(event);
        CollectionEventPublisher.super.fireCollectionEvent(event);
    }

    //default void fireInserting(int index, E e){};

    /**
     * Рассылка уведомления подписчикам о добавлении элемента
     * @param index индекс
     * @param e элемент
     */
    default void fireInserted(int index, E e) {
        InsertedEvent<EventList<E>,Integer,E> ev = InsertedEvent.<EventList<E>,Integer,E>create(this,index,e);
        fireCollectionEvent(ev);
    }

    //default void fireUpdating(int index, E old, E current){};

    /**
     * Рассылка уведомления подписчикам о обновлении элемента
     * @param index индекс
     * @param old предыдущее значение
     * @param current текущее значение
     */
    default void fireUpdated(int index, E old, E current){
        UpdatedEvent<EventList<E>,Integer,E> ev = UpdatedEvent.<EventList<E>,Integer,E>create(this,index,current,old);
        fireCollectionEvent(ev);
    }

    //default void fireDeleting(int index, E e){};

    /**
     * Рассылка уведомления подписчикам о удалении элемента
     * @param index индекс
     * @param e элемент
     */
    default void fireDeleted(int index, E e){
        fireCollectionEvent(DeletedEvent.<EventList<E>,Integer,E>create(this,index,e));
    }
    //</editor-fold>
    //<editor-fold desc="read methods">
    @Override
    default int size() {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.size();
        });
    }

    @Override
    default boolean isEmpty() {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.isEmpty();
        });
    }

    @Override
    default boolean contains(Object o) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.contains(o);
        });
    }

    @Override
    default Object[] toArray() {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.toArray();
        });
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    default <T> T[] toArray(T[] a) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.toArray(a);
        });
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        if( c == null )throw new IllegalArgumentException( "c == null" );
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.containsAll(c);
        });
    }

    @Override
    default E get(int index) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.get(index);
        });
    }

    @Override
    default int indexOf(Object o) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.indexOf(o);
        });
    }

    @Override
    default int lastIndexOf(Object o) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.lastIndexOf(o);
        });
    }

    @Override
    default Iterator<E> iterator() {
        return new SubEventListIterator<>(this);
    }

    @Override
    default ListIterator<E> listIterator() {
        return new SubEventListIterator<>(this);
    }

    @Override
    default ListIterator<E> listIterator(int index) {
        return new SubEventListIterator<>(this, index);
    }

    @Override
    default List<E> subList(int fromIndex, int toIndex) {
        return readLock(()->new SubEventList<>(this,fromIndex,toIndex));
    }
    //</editor-fold>
    //<editor-fold desc="modify methods">
    @Override
    default boolean add(E e) {
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            //fireInserting(tgt.size(), e);

            boolean res = tgt.add(e);
            if( res ){
                fireInserted(tgt.size()-1, e);
            }
            return res;
        }));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    default boolean remove(Object o) {
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            int idx = tgt.indexOf(o);
            if( idx >= 0 ){
                E deleted = tgt.get(idx);
                //fireDeleting(idx, deleted);
                tgt.remove(idx);
                fireDeleted(idx, deleted);
                return true;
            }
            return false;
        }));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    default boolean addAll(Collection<? extends E> c) {
        if( c == null ) throw new IllegalArgumentException("c == null");
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            int changeCount = 0;
            for( E e : c ){
                if( add(e) ){
                    changeCount++;
                }
            }

            return changeCount>0;
        }));
    }

    @Override
    default boolean addAll(int index, Collection<? extends E> c) {
        if( c == null ) throw new IllegalArgumentException("c == null");
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            int changeCount = 0;
            for( E e : c ){
                add(index+changeCount, e);
                changeCount++;
            }

            return changeCount>0;
        }));
    }

    @SuppressWarnings({"ConstantConditions", "UnnecessaryUnboxing"})
    private boolean removeByPredicate(Predicate<? super E> filter) {
        List<E> tgt = target();
        if( tgt == null ) throw new TargetNotAvailable();

        int changeCount = 0;
        TreeSet<Integer> removeSet = new TreeSet<>();
        for( int i = size()-1; i >= 0; i-- ){
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
                fireDeleted(idx, e);
                changeCount++;
            }
        }

        return changeCount>0;
    }

    @Override
    default boolean removeAll(Collection<?> coll) {
        if( coll == null ) throw new IllegalArgumentException("coll == null");
        return withCollectionEventQueue(()->writeLock(()->removeByPredicate(coll::contains)));
    }

    @Override
    default boolean retainAll(Collection<?> coll) {
        if( coll == null ) throw new IllegalArgumentException("coll == null");
        return withCollectionEventQueue(()->writeLock(()->removeByPredicate(x->!coll.contains(x))));
    }

    @Override
    default void clear() {
        //scn(()->{
            withCollectionEventQueue(()->writeLock(()->removeByPredicate((e)->true)));
        //});
    }

    @Override
    default E set(int index, E element) {
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            E old = tgt.get(index);
            //fireUpdating(index,old,element);
            old = tgt.set(index,element);
            fireUpdated(index,old,element);
            return old;
        }));
    }

    @Override
    default void add(int index, E element) {
        withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            //fireInserting(index, element);
            tgt.add(index, element);
            fireInserted(index, element);
        }));
    }

    @Override
    default E remove(int index) {
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            E old = tgt.get(index);
            //fireDeleting(index,old);
            old = tgt.remove(index);
            fireDeleted(index,old);
            return old;
        }));
    }

    @Override
    default void replaceAll(UnaryOperator<E> operator) {
        if( operator == null )throw new IllegalArgumentException( "operator == null" );
    }

    @SuppressWarnings("unchecked")
    @Override
    default void sort(Comparator<? super E> c) {
        withCollectionEventQueue(()->writeLock(()->{
            Object[] a = this.toArray();
            Arrays.sort(a, (Comparator) c);
            ListIterator<E> i = this.listIterator();
            for (Object e : a) {
                i.next();
                i.set((E) e);
            }
        }));
    }

    @Override
    default boolean removeIf(Predicate<? super E> filter) {
        if( filter == null )throw new IllegalArgumentException( "filter == null" );
        return withCollectionEventQueue(()->writeLock(()->removeByPredicate(filter)));
    }
    //</editor-fold>
}
