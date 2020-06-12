package xyz.cofe.collection;

import xyz.cofe.ecolls.ReadWriteLockSupport;
import xyz.cofe.fn.TripleConsumer;
import xyz.cofe.iter.Eterable;
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
            LongScn<EventList<E>,CollectionEvent<EventList<E>,E>>,
            Eterable<E>
{
    /**
     * Возвращает целевой список, над которым происходят преобразования
     * @return целевой список
     */
    List<E> target();

    //<editor-fold desc="notify methods">
    /**
     * Добавляет подписчика на событие добавления данных
     * @param ls подписчик - fn( key:Integer, oldValue:E=null, newValue:E )
     * @return отписка от уведомлений
     */
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

    /**
     * Добавляет подписчика на событие добавления данных
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E=null, newValue:E )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    default AutoCloseable onInserted(boolean weak, TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener(weak, (e) -> {
            if( e instanceof InsertedEvent ){
                InsertedEvent<EventList<E>,Integer,E> ev = (InsertedEvent)e;
                ls.accept(ev.getIndex(),null,ev.getNewItem());
            }
        });
    }

    /**
     * Добавляет подписчика на событие изменения данных
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
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

    /**
     * Добавляет подписчика на событие изменения данных
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    default AutoCloseable onUpdated(boolean weak, TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener(weak, (e) -> {
            if( e instanceof UpdatedEvent ){
                UpdatedEvent<EventList<E>,Integer,E> ev = (UpdatedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),ev.getNewItem());
            }
        });
    }

    /**
     * Добавляет подписчика на событие удаления данных
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E=null )
     * @return отписка от уведомлений
     */
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

    /**
     * Добавляет подписчика на событие удаления данных
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E=null )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    default AutoCloseable onDeleted(boolean weak, TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener(weak, (e) -> {
            if( e instanceof DeletedEvent ){
                DeletedEvent<EventList<E>,Integer,E> ev = (DeletedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),null);
            }
        });
    }

    /**
     * Добавляет подписчика на событие изменения/добавления/удаления данных
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
    default AutoCloseable onChanged(TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener( e -> {
            if( e instanceof UpdatedEvent ){
                UpdatedEvent<EventList<E>,Integer,E> ev = (UpdatedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),ev.getNewItem());
            } else if( e instanceof DeletedEvent ){
                DeletedEvent<EventList<E>,Integer,E> ev = (DeletedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),null);
            }else if( e instanceof InsertedEvent ){
                InsertedEvent<EventList<E>,Integer,E> ev = (InsertedEvent)e;
                ls.accept(ev.getIndex(),null,ev.getNewItem());
            }
        } );
    }

    /**
     * Добавляет подписчика на событие изменения/добавления/удаления данных
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
    default AutoCloseable onChanged(boolean weak, TripleConsumer<Integer,E,E> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener( weak, e -> {
            if( e instanceof UpdatedEvent ){
                UpdatedEvent<EventList<E>,Integer,E> ev = (UpdatedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),ev.getNewItem());
            } else if( e instanceof DeletedEvent ){
                DeletedEvent<EventList<E>,Integer,E> ev = (DeletedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),null);
            }else if( e instanceof InsertedEvent ){
                InsertedEvent<EventList<E>,Integer,E> ev = (InsertedEvent)e;
                ls.accept(ev.getIndex(),null,ev.getNewItem());
            }
        } );
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
        InsertedEvent<EventList<E>,Integer,E> ev = InsertedEvent.create(this,index,e);
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
        UpdatedEvent<EventList<E>,Integer,E> ev = UpdatedEvent.create(this,index,current,old);
        fireCollectionEvent(ev);
    }

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

    /**
     * Возвращает кол-во элементов в коллекции
     * @return кол-во элементов
     * @see #isEmpty()
     */
    @Override
    default int size() {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.size();
        });
    }

    /**
     * Проверяет что коллекция пуста
     * @return true - коллекция пуста
     */
    @Override
    default boolean isEmpty() {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.isEmpty();
        });
    }

    /**
     * Проверяет наличие элемента в коллеции
     * @param o элемент
     * @return true - элемент присуствует в коллекции
     */
    @Override
    default boolean contains(Object o) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.contains(o);
        });
    }

    /**
     * Создает массив объектов коллеции
     * @return массив объектов коллеции
     */
    @Override
    default Object[] toArray() {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.toArray();
        });
    }

    /**
     * Создает массив объектов коллеции
     * @param a пустой массив (изза особенности реализации Generic)
     * @param <T> ТИп элементов массива
     * @return массив
     */
    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    default <T> T[] toArray(T[] a) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.toArray(a);
        });
    }

    /**
     * Проверяет наличие всех элементов в коллеции
     * @param c искомые объекты
     * @return true - если все искомые объекты есть в коллеции
     */
    @Override
    default boolean containsAll(Collection<?> c) {
        if( c == null )throw new IllegalArgumentException( "c == null" );
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.containsAll(c);
        });
    }

    /**
     * Получение объекта по его индексу
     * @param index индекс элемента
     * @return Элемент
     */
    @Override
    default E get(int index) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.get(index);
        });
    }

    /**
     * Поиск первого индекса элемента в коллекции
     * @param o элемент
     * @return индекс или -1, если не найден
     */
    @Override
    default int indexOf(Object o) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.indexOf(o);
        });
    }

    /**
     * Поиск последнего индекса элемента в коллекции
     * @param o элемент
     * @return индекс или -1, если не найден
     */
    @Override
    default int lastIndexOf(Object o) {
        return readLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            return tgt.lastIndexOf(o);
        });
    }

    /**
     * Получение итератора по списку, от начала к концу
     * @return итератор
     */
    @Override
    default Iterator<E> iterator() {
        return new SubEventListIterator<>(this);
    }

    /**
     * Получение итератора по списку
     * @return итератор
     */
    @Override
    default ListIterator<E> listIterator() {
        return new SubEventListIterator<>(this);
    }

    /**
     * Получение итератора по списку,, начала итерации согласно указанному индексу
     * @param index индекс начала итерирования
     * @return итератор
     */
    @Override
    default ListIterator<E> listIterator(int index) {
        return new SubEventListIterator<>(this, index);
    }

    /**
     * Создание проекции списка
     * @param fromIndex начальный индекс
     * @param toIndex конечный индекс
     * @return Проекция
     */
    @Override
    default List<E> subList(int fromIndex, int toIndex) {
        return readLock(()->new SubEventList<>(this,fromIndex,toIndex));
    }
    //</editor-fold>
    //<editor-fold desc="modify methods">

    /**
     * Добавляет элемент в список
     * @param e элемент
     * @return true - список изменен
     */
    @Override
    default boolean add(E e) {
        //noinspection Contract
        return withCollectionEventQueue(()->writeLock(()->{
            List<E> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();
            //fireInserting(tgt.size(), e);

            boolean res = tgt.add(e);
            //noinspection ConstantConditions
            if( res ){
                fireInserted(tgt.size()-1, e);
            }
            return res;
        }));
    }

    /**
     * Удаляет элемент из списка
     * @param o элемент
     * @return true - список изменен
     */
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

    /**
     * Добавляет все элементы в список
     * @param c элементы
     * @return true - список изменен
     */
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

    /**
     * Добавляет все элементы в список, в указанную позицию
     * @param c элементы
     * @return true - список изменен
     */
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

    /**
     * Удаляет указанные элементы из списка
     * @param coll элементы
     * @return true - список изменен
     */
    @Override
    default boolean removeAll(Collection<?> coll) {
        if( coll == null ) throw new IllegalArgumentException("coll == null");
        return withCollectionEventQueue(()->writeLock(()->EventListImpl.removeByPredicate(this, coll::contains)));
    }

    /**
     * Удаляет элементы из списока, за исключением указанных
     * @param coll элементы
     * @return true - список изменен
     */
    @Override
    default boolean retainAll(Collection<?> coll) {
        if( coll == null ) throw new IllegalArgumentException("coll == null");
        return withCollectionEventQueue(()->writeLock(()->EventListImpl.removeByPredicate(this, x->!coll.contains(x))));
    }

    /**
     * Удаляет все элементы
     */
    @Override
    default void clear() {
        //scn(()->{
            withCollectionEventQueue(()->writeLock(()->EventListImpl.removeByPredicate(this, (e)->true)));
        //});
    }

    /**
     * Записиывает элемент в определенную позицию
     * @param index индекс
     * @param element элемент
     * @return предыдущее значение
     */
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

    /**
     * Вставляет элемент в определнное место списка
     * @param index индекс
     * @param element элемент
     */
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

    /**
     * Удялет элемент из списка
     * @param index индекс элемента
     * @return удаленный элемент
     */
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

    /**
     * Замена всех элементов
     * @param operator функция замены
     */
    @Override
    default void replaceAll(UnaryOperator<E> operator) {
        if( operator == null )throw new IllegalArgumentException( "operator == null" );
        withCollectionEventQueue(()->writeLock(()->{
            final ListIterator<E> li = this.listIterator();
            while( li.hasNext() ){
                li.set(operator.apply(li.next()));
            }
        }));
    }

    /**
     * Сортировка списка
     * @param c функция сравнения
     */
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

    /**
     * Удаление элементов согласно фильтру
     * @param filter фильтр
     * @return факт удаления
     */
    @Override
    default boolean removeIf(Predicate<? super E> filter) {
        if( filter == null )throw new IllegalArgumentException( "filter == null" );
        return withCollectionEventQueue(()->writeLock(()->EventListImpl.removeByPredicate(this, filter)));
    }
    //</editor-fold>
}
