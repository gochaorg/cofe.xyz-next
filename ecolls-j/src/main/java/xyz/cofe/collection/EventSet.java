package xyz.cofe.collection;

import xyz.cofe.ecolls.ReadWriteLockSupport;
import xyz.cofe.scn.LongScn;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Набор объектов с поддержкой уведомлений о изменение набора
 * @param <E> Тип объектов в наборе
 */
public interface EventSet<E>
    extends
    Set<E>,
    CollectionEventPublisher<EventSet<E>, E>,
    ReadWriteLockSupport,
    LongScn<EventSet<E>, CollectionEvent<EventSet<E>,E>>
{
    /**
     * Возвращает целевое множество (Set), над которым происходят преобразования
     * @return целевое множество (Set)
     */
    Set<E> target();

    //region fire, onAdded/onRemoved
    @Override
    default void fireCollectionEvent(CollectionEvent<EventSet<E>, E> event) {
        nextscn(event);
        CollectionEventPublisher.super.fireCollectionEvent(event);
    }

    /**
     * Рассылка уведомления подписчикам о добавлении элемента
     * @param e элемент
     */
    default void fireInserted(E e){
        fireCollectionEvent(AddedEvent.create(this,e));
    }

    /**
     * Рассылка уведомления подписчикам о добавлении элемента
     * @param e элемент
     */
    default void fireDeleted(E e){
        fireCollectionEvent(RemovedEvent.create(this,e));
    }

    /**
     * Добавляет подписчика на событие добавления элемента в набор
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    default AutoCloseable onAdded(Consumer<E> listener){
        if( listener == null )throw new IllegalArgumentException( "listener == null" );
        return addCollectionListener( ev -> {
            if( ev instanceof AddedEvent ){
                listener.accept(((AddedEvent<EventSet<E>, E>) ev).getNewItem());
            }
        });
    }

    /**
     * Добавляет подписчика на событие удаления элемента из набора
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    default AutoCloseable onRemoved(Consumer<E> listener){
        if( listener == null )throw new IllegalArgumentException( "listener == null" );
        return addCollectionListener( ev -> {
            if( ev instanceof RemovedEvent ){
                listener.accept(((RemovedEvent<EventSet<E>, E>) ev).getOldItem());
            }
        });
    }
    //endregion
    //region read methods
    @Override
    default int size(){
        return readLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.size();
        });
    }

    @Override
    default boolean isEmpty(){
        return readLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.isEmpty();
        });
    }

    @Override
    default boolean contains(Object o){
        return readLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.contains(o);
        });
    }

    static class EIterator<E> implements Iterator<E> {
        protected Iterator<E> itr;

        public EIterator(Iterator<E> itr){
            if( itr == null )throw new IllegalArgumentException( "itr == null" );
            this.itr = itr;
        }

        @Override
        public boolean hasNext() {
            return itr.hasNext();
        }

        @Override
        public E next() {
            return itr.next();
        }
    }

    static class DummyIterator implements Iterator {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    default Iterator<E> iterator(){
        return readLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            Iterator<E> itr = tgt.iterator();
            if( itr!=null ){
                return new EIterator<E>(itr);
            }else {
                return new DummyIterator();
            }
        });
    }

    @Override
    default Object[] toArray(){
        return readLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.toArray();
        });
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    default <T> T[] toArray(T[] a){
        return readLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.toArray(a);
        });
    }

//        default <T> T[] toArray(IntFunction<T[]> generator) {
//        return readLock(()->{
//            Set<E> tgt = target();
//            if( tgt == null ) throw new TargetNotAvailable();
//            return tgt.toArray(generator);
//        });
//    }
    //endregion
    //region modify methods
    @Override
    default boolean containsAll(Collection<?> c){
        return readLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.containsAll(c);
        });
    }

    @Override
    default boolean add(E e){
        return withCollectionEventQueue(()->writeLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            boolean added = tgt.add(e);
            if( added )fireInserted(e);
            return added;
        }));
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean remove(Object o){
        return withCollectionEventQueue(()->writeLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            boolean deleted = tgt.remove(o);
            if( deleted ){
                fireDeleted((E)o);
            }
            return deleted;
        }));
    }

    @Override
    default boolean addAll(Collection<? extends E> c){
        if( c == null )throw new IllegalArgumentException( "c == null" );
        return withCollectionEventQueue(()->writeLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            int cnt = 0;
            for( E e : c ){
                if( e==null )continue;
                if( tgt.add(e) ){
                    cnt++;
                    fireInserted(e);
                }
            }
            return cnt>0;
        }));
    }

    @Override
    default boolean retainAll(Collection<?> c){
        if( c == null )throw new IllegalArgumentException( "c == null" );
        return removeIf( x -> !c.contains(x) );
    }

    @Override
    default boolean removeAll(Collection<?> c){
        if( c == null )throw new IllegalArgumentException( "c == null" );
        return removeIf(c::contains);
    }

    @Override
    default void clear(){
        removeIf(x->true);
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean removeIf(Predicate<? super E> filter) {
        if( filter == null )throw new IllegalArgumentException( "filter == null" );
        return withCollectionEventQueue(()->writeLock(()->{
            Set<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            int cnt = 0;
            for( Object oe : tgt.toArray() ){
                E e = (E)oe;
                if( e==null )continue;
                if( filter.test(e) ){
                    if( tgt.remove(e) ){
                        cnt++;
                        fireDeleted(e);
                    }
                }
            }
            return cnt>0;
        }));
    }
    //endregion
}