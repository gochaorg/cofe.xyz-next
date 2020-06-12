package xyz.cofe.collection;

import xyz.cofe.ecolls.ImmediateEvent;
import xyz.cofe.fn.Pair;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Генерация событий перед началом модификации коллекции
 */
public interface PreEventList<E> extends EventList<E> {
    //region fireInserting() / fireUpdating() / fireDeleting()

    @Override
    default void fireCollectionEvent( CollectionEvent<EventList<E>, E> event ){
        if( !(
            event instanceof ImmediateEvent &&
            ((ImmediateEvent)event).isImmediateEvent()
            )
        ){
            // Для событий которые не являются немедленными (Adding|Removeing|...)
            // - т.е. обычные (Added|Removed|...)
            // увеличиваем счетчик т.к. он связан с модификацией коллекции
            nextscn(event);
        }

        listenerHelper().fireEvent(event);
    }

    /**
     * Рассылка уведомления подписчикам о предстоящем добавлении элемента
     * @param index индекс
     * @param e элемент
     */
    default void fireInserting(int index, E e) {
        InsertedEvent<EventList<E>,Integer,E> ev = InsertedEvent.create(this,index,e);
        fireCollectionEvent(ev);
    }

    /**
     * Рассылка уведомления подписчикам о предстоящем обновлении элемента
     * @param index индекс
     * @param old предыдущее значение
     * @param current текущее значение
     */
    default void fireUpdating(int index, E old, E current){
        UpdatedEvent<EventList<E>,Integer,E> ev = UpdatedEvent.create(this,index,current,old);
        fireCollectionEvent(ev);
    }

    /**
     * Рассылка уведомления подписчикам о предстоящем удалении элемента
     * @param index индекс
     * @param e элемент
     */
    default void fireDeleting(int index, E e){
        fireCollectionEvent(DeletedEvent.create(this,index,e));
    }
    //endregion

    //region modify methods
    /**
     * Добавление элемента в список.
     * Будут сгенерированы два события:
     * <ol>
     *    <li>{@link InsertingEvent} перед началом добавления</li>
     *    <li>{@link InsertedEvent} после начала добавления</li>
     * </ol>
     * @param e элемент
     * @return факт добавления
     */
    @SuppressWarnings("Contract")
    @Override
    default boolean add( E e ){
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            fireInserting(tgt.size(), e);

            boolean res = tgt.add(e);
            //noinspection ConstantConditions
            if( res ){
                fireInserted(tgt.size()-1, e);
            }
            //noinspection ConstantConditions
            return res;
        }));
    }

    /**
     * Удаляет элемент из списка
     * Генерирует события {@link RemovingEvent}, {@link RemovedEvent}
     * @param o элемент
     * @return true - список изменен
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    default boolean remove( Object o ){
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            int idx = tgt.indexOf(o);
            if( idx >= 0 ){
                E deleted = tgt.get(idx);
                fireDeleting(idx, deleted);
                tgt.remove(idx);
                fireDeleted(idx, deleted);
                return true;
            }
            return false;
        }));
    }

    /**
     * Добавляет все элементы в список
     * Генерирует события {@link InsertingEvent}, {@link InsertedEvent}
     * @param c элементы
     * @return true - список изменен
     */
    @Override
    default boolean addAll( Collection<? extends E> c ){
        if( c == null ) throw new IllegalArgumentException("c == null");
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            LinkedList<E> commited = new LinkedList<>();
            int tidx = tgt.size();
            for( E e : c ){
                fireInserting(tidx, e);
                tidx++;
                commited.add(e);
            }

            int changeCount = 0;
            for( E e : commited ){
                //noinspection ConstantConditions
                if( add(e) ){
                    changeCount++;
                }
            }

            return changeCount>0;
        }));
    }

    /**
     * Добавляет все элементы в список, в указанную позицию
     * Генерирует события {@link InsertingEvent}, {@link InsertedEvent}
     * @param c элементы
     * @return true - список изменен
     */
    @Override
    default boolean addAll( int index, Collection<? extends E> c ){
        if( c == null ) throw new IllegalArgumentException("c == null");
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            LinkedList<E> commited = new LinkedList<>();
            int tidx = index;
            for( E e : c ){
                fireInserting(tidx, e);
                tidx++;
                commited.add(e);
            }

            int changeCount = 0;
            for( E e : commited ){
                add(index+changeCount, e);
                changeCount++;
            }

            return changeCount>0;
        }));
    }

    /**
     * Удаляет указанные элементы из списка
     * Генерирует события {@link RemovingEvent}, {@link RemovedEvent}
     * @param coll элементы
     * @return true - список изменен
     */
    @Override
    default boolean removeAll( Collection<?> coll ){
        if( coll == null ) throw new IllegalArgumentException("coll == null");
        return withCollectionEventQueue(
            ()->writeLock(
                ()->EventListImpl.removeByPredicate(this, coll::contains, this::fireDeleting)
            )
        );
    }

    /**
     * Удаляет элементы из списока, за исключением указанных
     * Генерирует события {@link RemovingEvent}, {@link RemovedEvent}
     * @param coll элементы
     * @return true - список изменен
     */
    @Override
    default boolean retainAll( Collection<?> coll ){
        if( coll == null ) throw new IllegalArgumentException("coll == null");
        return withCollectionEventQueue(
            ()->writeLock(
                ()->EventListImpl.removeByPredicate(this, x->!coll.contains(x), this::fireDeleting)
            )
        );
    }

    /**
     * Удаляет все элементы
     * Генерирует события {@link RemovingEvent}, {@link RemovedEvent}
     */
    @Override
    default void clear(){
        withCollectionEventQueue(
            ()->writeLock(
                ()->EventListImpl.removeByPredicate(this, (e)->true, this::fireDeleting)
            )
        );
    }

    /**
     * Записиывает элемент в определенную позицию
     * Генерирует события {@link UpdatingEvent}, {@link UpdatedEvent}
     * @param index индекс
     * @param element элемент
     * @return предыдущее значение
     */
    @Override
    default E set( int index, E element ){
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            E old = tgt.get(index);
            fireUpdating(index,old,element);
            old = tgt.set(index,element);
            fireUpdated(index,old,element);
            return old;
        }));
    }

    /**
     * Вставляет элемент в определнное место списка
     * Генерирует события {@link InsertingEvent}, {@link InsertedEvent}
     * @param index индекс
     * @param element элемент
     */
    @Override
    default void add( int index, E element ){
        withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            fireInserting(index, element);
            tgt.add(index, element);
            fireInserted(index, element);
        }));
    }

    /**
     * Удялет элемент из списка.
     * Генерирует события {@link RemovingEvent}, {@link RemovedEvent}
     * @param index индекс элемента
     * @return удаленный элемент
     */
    @Override
    default E remove( int index ){
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            E old = tgt.get(index);
            fireDeleting(index,old);
            old = tgt.remove(index);
            fireDeleted(index,old);
            return old;
        }));
    }

    /**
     * Проверка что два объекта идентичны
     * @param a первый
     * @param b второй
     * @return true - идентичны
     */
    default boolean sameElements(E a, E b){
        return Objects.equals(a,b);
    }

    /**
     * Замена всех элементов.
     * Генерирует события {@link UpdatingEvent}, {@link UpdatedEvent}
     * @param operator функция замены
     */
    @Override
    default void replaceAll( UnaryOperator<E> operator ){
        if( operator == null )throw new IllegalArgumentException( "operator == null" );
        withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            TreeMap<Integer, Pair<E,E>> modifyElements = new TreeMap<>();
            final ListIterator<E> li = this.listIterator();
            int idx = -1;
            while( li.hasNext() ){
                idx++;
                E curEl = li.next();
                E newEl = operator.apply(curEl);
                if( !sameElements(newEl, curEl) ){
                    fireUpdating(idx,curEl,newEl);
                    modifyElements.put(idx,Pair.of(curEl,newEl));
                }
            }

            for( Map.Entry<Integer,Pair<E,E>> e : modifyElements.entrySet() ){
                tgt.set(e.getKey(), e.getValue().b());
                fireUpdated(e.getKey(), e.getValue().a(), e.getValue().b());
            }
        }));
    }

    /**
     * Удаление элементов согласно фильтру.
     * Генерирует события {@link DeletingEvent}, {@link DeletedEvent}
     * @param filter фильтр
     * @return факт удаления
     */
    @Override
    default boolean removeIf( Predicate<? super E> filter ){
        if( filter == null )throw new IllegalArgumentException( "filter == null" );
        return withCollectionEventQueue(
            ()->writeLock(
                ()->EventListImpl.removeByPredicate(this, filter, this::fireDeleting)
            )
        );
    }
    //endregion
}
