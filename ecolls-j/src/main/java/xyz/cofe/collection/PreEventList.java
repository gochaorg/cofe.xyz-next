package xyz.cofe.collection;

import xyz.cofe.ecolls.ImmediateEvent;
import xyz.cofe.fn.Pair;
import xyz.cofe.fn.TripleConsumer;

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
        InsertingEvent<EventList<E>,Integer,E> ev = InsertingEvent.create(this,index,e);
        fireCollectionEvent(ev);
    }

    /**
     * Добавляет подписчика на событие начала добавления данных {@link InsertedEvent}
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E=null, newValue:E )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({ "UnusedReturnValue", "unchecked", "rawtypes" })
    default AutoCloseable onInserting(boolean weak, TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener(weak, (e) -> {
            if( e instanceof InsertingEvent ){
                InsertingEvent<EventList<E>,Integer,E> ev = (InsertingEvent)e;
                ls.accept(ev.getIndex(),null,ev.getNewItem());
            }
        });
    }

    /**
     * Добавляет подписчика на событие начала добавления данных {@link InsertedEvent}
     * @param ls подписчик - fn( key:Integer, oldValue:E=null, newValue:E )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({ "UnusedReturnValue" })
    default AutoCloseable onInserting(TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return onInserting(false, ls);
    }

    /**
     * Рассылка уведомления подписчикам о предстоящем обновлении элемента
     * @param index индекс
     * @param old предыдущее значение
     * @param current текущее значение
     */
    default void fireUpdating(int index, E old, E current){
        UpdatingEvent<EventList<E>,Integer,E> ev = UpdatingEvent.create(this,index,current,old);
        fireCollectionEvent(ev);
    }

    /**
     * Добавляет подписчика на событие изменения данных {@link UpdatingEvent}
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({ "UnusedReturnValue", "unchecked", "rawtypes" })
    default AutoCloseable onUpdating(boolean weak, TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener(weak, (e) -> {
            if( e instanceof UpdatingEvent ){
                UpdatingEvent<EventList<E>,Integer,E> ev = (UpdatingEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),ev.getNewItem());
            }
        });
    }

    /**
     * Добавляет подписчика на событие изменения данных {@link UpdatingEvent}
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({ "UnusedReturnValue" })
    default AutoCloseable onUpdating(TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return onUpdating(false, ls);
    }

    /**
     * Рассылка уведомления подписчикам о предстоящем удалении элемента
     * @param index индекс
     * @param e элемент
     */
    default void fireDeleting(int index, E e){
        fireCollectionEvent(DeletingEvent.create(this,index,e));
    }

    /**
     * Добавляет подписчика на событие удаления данных {@link DeletingEvent}
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E=null )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({ "UnusedReturnValue", "unchecked", "rawtypes" })
    default AutoCloseable onDeleting(boolean weak, TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener(weak, (e) -> {
            if( e instanceof DeletingEvent ){
                DeletingEvent<EventList<E>,Integer,E> ev = (DeletingEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),null);
            }
        });
    }

    /**
     * Добавляет подписчика на событие удаления данных {@link DeletingEvent}
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E=null )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({ "UnusedReturnValue" })
    default AutoCloseable onDeleting(TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return onDeleting(false, ls );
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
                if( tgt.add(e) ){
                    changeCount++;
                    //noinspection ConstantConditions
                    fireInserted(tgt.size()-1, e);
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
                tgt.add(index+changeCount, e);
                fireInserted(index+changeCount, e);
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
