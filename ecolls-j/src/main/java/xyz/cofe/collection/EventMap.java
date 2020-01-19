package xyz.cofe.collection;

import xyz.cofe.ecolls.ReadWriteLockSupport;
import xyz.cofe.fn.TripleConsumer;
import xyz.cofe.scn.LongScn;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Карта (ключ/значение) с поддержкой уведомлений
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
public interface EventMap<K,V>
    extends
    Map<K,V>,
    CollectionEventPublisher<EventMap<K,V>, V>,
    ReadWriteLockSupport,
    LongScn<EventMap<K,V>,CollectionEvent<EventMap<K,V>,V>>
{
    /**
     * Возвращает целевую карту, над которой происходят преобразования
     * @return целевая карта
     */
    Map<K,V> target();

    //region notify
    @Override
    default void fireCollectionEvent(CollectionEvent<EventMap<K,V>, V> event) {
        nextscn(event);
        CollectionEventPublisher.super.fireCollectionEvent(event);
    }

    /**
     * Рассылка уведомления подписчикам о добавлении элемента
     * @param index индекс
     * @param e элемент
     */
    default void fireInserted(K index, V e) {
        InsertedEvent<EventMap<K,V>,K,V> ev = InsertedEvent.<EventMap<K,V>,K,V>create(this,index,e);
        fireCollectionEvent(ev);
    }

    /**
     * Рассылка уведомления подписчикам о обновлении элемента
     * @param index индекс
     * @param old предыдущее значение
     * @param current текущее значение
     */
    default void fireUpdated(K index, V old, V current){
        UpdatedEvent<EventMap<K,V>,K,V> ev = UpdatedEvent.<EventMap<K,V>,K,V>create(this,index,current,old);
        fireCollectionEvent(ev);
    }

    /**
     * Рассылка уведомления подписчикам о удалении элемента
     * @param index индекс
     * @param e элемент
     */
    default void fireDeleted(K index, V e){
        fireCollectionEvent(DeletedEvent.<EventMap<K,V>,K,V>create(this,index,e));
    }

    /**
     * Добавляет подписчика на событие добавления элемента
     * @param ls подписчик - fn( key:K, oldValue:V=null, newValue:V )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    default AutoCloseable onInserted(TripleConsumer<K,V,V> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener((e) -> {
            if( e instanceof InsertedEvent ){
                InsertedEvent<EventMap<K,V>,K,V> ev = (InsertedEvent)e;
                ls.accept(ev.getIndex(),null,ev.getNewItem());
            }
        });
    }

    /**
     * Добавляет подписчика на событие обновления элемента
     * @param ls подписчик - fn( key:K, oldValue:V, newValue:V )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    default AutoCloseable onUpdated(TripleConsumer<K,V,V> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener((e) -> {
            if( e instanceof UpdatedEvent ){
                UpdatedEvent<EventMap<K,V>,K,V> ev = (UpdatedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),ev.getNewItem());
            }
        });
    }

    /**
     * Добавляет подписчика на событие удаления элемента
     * @param ls подписчик - fn( key:K, oldValue:V, newValue:V=null )
     * @return отписка от уведомлений
     */
    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    default AutoCloseable onDeleted(TripleConsumer<K,V,V> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener((e) -> {
            if( e instanceof DeletedEvent ){
                DeletedEvent<EventMap<K,V>,K,V> ev = (DeletedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),null);
            }
        });
    }

    /**
     * Добавляет подписчика га событие изменения/добавления/удаления данных
     * @param ls подписчик - fn( key:K, oldValue:V, newValue:V )
     * @return отписка от уведомлений
     */
    default AutoCloseable onChanged(TripleConsumer<K,V,V> ls){
        if( ls == null )throw new IllegalArgumentException( "ls == null" );
        return addCollectionListener( e -> {
            if( e instanceof UpdatedEvent ){
                UpdatedEvent<EventMap<K,V>,K,V> ev = (UpdatedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),ev.getNewItem());
            } else if( e instanceof DeletedEvent ){
                DeletedEvent<EventMap<K,V>,K,V> ev = (DeletedEvent)e;
                ls.accept(ev.getIndex(),ev.getOldItem(),null);
            }else if( e instanceof InsertedEvent ){
                InsertedEvent<EventMap<K,V>,K,V> ev = (InsertedEvent)e;
                ls.accept(ev.getIndex(),null,ev.getNewItem());
            }
        } );
    }
    //endregion
    //region reads

    /**
     * Возвращает кол-во пар значений в карте
     * @return кол-во пар значений
     */
    @Override
    default int size(){
        return withCollectionEventQueue(()->readLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            return tgt.size();
        }));
    }

    /**
     * Проверяет что карта пустая
     * @return true - карта пустая
     */
    @Override
    default boolean isEmpty(){
        return withCollectionEventQueue(()->readLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            return tgt.isEmpty();
        }));
    }

    /**
     * Проверяет наличие узла в ключа
     * @param key ключ
     * @return true - ключ присуствует
     */
    @Override
    default boolean containsKey(Object key){
        return withCollectionEventQueue(()->readLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            return tgt.containsKey(key);
        }));
    }

    /**
     * Проверяет наличие значения в карте
     * @param value значение
     * @return true - значение присуствует
     */
    @Override
    default boolean containsValue(Object value){
        return withCollectionEventQueue(()->readLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            return tgt.containsValue(value);
        }));
    }

    /**
     * Возвращает по ключю значение
     * @param key ключ
     * @return значение
     */
    @Override
    default V get(Object key){
        return withCollectionEventQueue(()->readLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            return tgt.get(key);
        }));
    }

    /**
     * Возвращает множество ключей
     * @return множество ключей
     */
    @Override
    default Set<K> keySet(){
        return withCollectionEventQueue(()->readLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            return tgt.keySet();
        }));
    }

    /**
     * Возвращает значения
     * @return значения
     */
    @Override
    default Collection<V> values(){
        return withCollectionEventQueue(()->readLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            return tgt.values();
        }));
    }

    /**
     * Возвращает множество пар
     * @return множество пар
     */
    @Override
    default Set<Entry<K, V>> entrySet(){
        return withCollectionEventQueue(()->readLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            return tgt.entrySet().stream()
                .map(x -> new EventMapEntry<>(this,x.getKey(),x.getValue()))
                .collect(Collectors.toSet());
        }));
    }

    /**
     * Возвращает значение по ключю или значение по умолчанию, если нет соответ ключа
     * @param key ключ
     * @param defaultValue значение по умолчанию
     * @return значение
     */
    @Override
    default V getOrDefault(Object key, V defaultValue) {
        return withCollectionEventQueue(()->readLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            return tgt.getOrDefault(key,defaultValue);
        }));
    }
    //endregion
    //region modify functions

    /**
     * Указывает значение для ключа
     * @param key ключ
     * @param value значение
     * @return предыдущее значение
     */
    @Override
    default V put(K key, V value){
        return withCollectionEventQueue(()->writeLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            boolean has = tgt.containsKey(key);
            if( has ){
                V old = tgt.put(key,value);
                fireUpdated(key,old,value);
                return old;
            }else {
                V old = tgt.put(key, value);
                fireInserted(key,value);
                return old;
            }
        }));
    }

    /**
     * Удаляет пару для указанного ключа
     * @param key ключ
     * @return удаленное значение
     */
    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    @Override
    default V remove(Object key){
        return withCollectionEventQueue(()->writeLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            if( tgt.containsKey(key) ){
                V old = tgt.remove(key);
                fireDeleted((K)key, old);
                return old;
            }

            return tgt.remove(key);
        }));
    }

    /**
     * Добавляет карту в карту
     * @param m карта
     */
    @Override
    default void putAll(Map<? extends K, ? extends V> m){
        withCollectionEventQueue(()->writeLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            for( Map.Entry<? extends K,? extends V> en : m.entrySet() ){
                K k = en.getKey();
                V v = en.getValue();
                if( tgt.containsKey(k) ){
                    V old = tgt.put(k,v);
                    fireUpdated(k,old,v);
                }else{
                    tgt.put(k,v);
                    fireInserted(k,v);
                }
            }
        }));
    }

    /**
     * Очищает карту от всех пар ключ/значение
     */
    @Override
    default void clear(){
        withCollectionEventQueue(()->writeLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

            for( Map.Entry<K,V> en : tgt.entrySet() ){
                fireDeleted(en.getKey(), en.getValue());
            }
            tgt.clear();
        }));
    }

//    @Override
//    default void forEach(BiConsumer<? super K, ? super V> action) {
//
//    }

//    @Override
//    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
//        withCollectionEventQueue(()->writeLock(()->{
//            Map<K,V> tgt = target();
//            if( tgt == null ) throw new TargetNotAvailable();
//
//            return tgt.replaceAll(function);
//        }));
//    }

    /**
     * Добавляет значение для ключа, если ключ отсуствует карте
     * @param key ключ
     * @param value значение
     * @return предыдущее значение или указанное значение
     */
    @Override
    default V putIfAbsent(K key, V value) {
        return withCollectionEventQueue(()->writeLock(()->{
            V v = get(key);
            if (v == null) {
                v = put(key, value);
            }
            return v;
        }));
    }

    /**
     * Удаляет пару если совпадает ключ и значение
     * @param key ключ
     * @param value значение
     * @return true - пара удалена
     */
    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    @Override
    default boolean remove(Object key, Object value) {
        return withCollectionEventQueue(()->writeLock(()->{
            Map<K,V> tgt = target();
            if( tgt == null ) throw new TargetNotAvailable();

                Object curValue = tgt.get(key);
                if (!Objects.equals(curValue, value) ||
                    (curValue == null && !tgt.containsKey(key))) {
                    return false;
                }

                V old = tgt.remove(key);
                fireDeleted((K)key,old);
                return true;
        }));
    }

    /**
     * Заменяет значение ключа, если старое значение совпадает с указанным
     * @param key ключ
     * @param oldValue старое значение
     * @param newValue новое значение
     * @return true - значение заменено
     */
    @Override
    default boolean replace(K key, V oldValue, V newValue) {
        return withCollectionEventQueue(()->writeLock(()->{
            Object curValue = get(key);
            if( !Objects.equals(curValue, oldValue) ||
                (curValue == null && !containsKey(key)) ){
                return false;
            }
            V old = put(key, newValue);
            fireUpdated(key,old,newValue);
            return true;
        }));
    }

    /**
     * заменяет значение для существующего ключа
     * @param key ключ
     * @param value новое значение
     * @return новое значение
     */
    @Override
    default V replace(K key, V value) {
        return withCollectionEventQueue(()->writeLock(()->{
            V curValue;
            if( ((curValue = get(key)) != null) || containsKey(key) ){
                curValue = put(key, value);
                fireUpdated(key,curValue,value);
            }
            return curValue;
        }));
    }

    /**
     * Вычисляет значение для ключа, если ключ отсуствует,
     * то создает значение и добавляет его в карту и возвращает его.
     * @param key ключ
     * @param mappingFunction функция генерирования значения для ключа
     * @return значение
     */
    @Override
    default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return withCollectionEventQueue(()->writeLock(()->{
            Objects.requireNonNull(mappingFunction);
            V v;
            if ((v = get(key)) == null) {
                V newValue;
                if ((newValue = mappingFunction.apply(key)) != null) {
                    put(key, newValue);
                    return newValue;
                }
            }
            return v;
        }));
    }

    /**
     * Вычисляет значение для ключа, если ключ присуствует,
     * то создает значение и добавляет его в карту и возвращает его.
     * @param key ключ
     * @param remappingFunction функция генерирования значения для ключа
     * @return значение
     */
    @Override
    default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return withCollectionEventQueue(()->writeLock(()->{
            Objects.requireNonNull(remappingFunction);
            V oldValue;
            if( (oldValue = get(key)) != null ){
                V newValue = remappingFunction.apply(key, oldValue);
                if( newValue != null ){
                    put(key, newValue);
                    return newValue;
                } else {
                    remove(key);
                    return null;
                }
            } else {
                return null;
            }
        }));
    }

    /**
     * Вычисляет значение ключа и изменяет карту согласно функции remappingFunction
     * @param key ключ
     * @param remappingFunction функция принимающая ключ и старое значение,
     *                          возвращающая новое значение, если вернет null, то соответ пара будет удалена
     * @return значение
     */
    @Override
    default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return withCollectionEventQueue(()->writeLock(()->{
            Objects.requireNonNull(remappingFunction);
            V oldValue = get(key);

            V newValue = remappingFunction.apply(key, oldValue);
            if( newValue == null ){
                // delete mapping
                if( oldValue != null || containsKey(key) ){
                    // something to remove
                    remove(key);
                    return null;
                } else {
                    // nothing to do. Leave things as they were.
                    return null;
                }
            } else {
                // add or replace old mapping
                put(key, newValue);
                return newValue;
            }
        }));
    }

    /**
     * Слиение данных, для указанного ключа берет значение (oldValue)
     * и если oldValue == null, то возвращает указанне значение,
     * иначе вернет результат вызова функции remmaping( oldValue, value )
     * @param key ключ
     * @param value значение
     * @param remappingFunction функция отображения
     * @return вычисленное значение
     */
    @Override
    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return withCollectionEventQueue(()->writeLock(()->{
            Objects.requireNonNull(remappingFunction);
            Objects.requireNonNull(value);
            V oldValue = get(key);
            V newValue = (oldValue == null) ? value :
                         remappingFunction.apply(oldValue, value);
            if( newValue == null ){
                remove(key);
            } else {
                put(key, newValue);
            }
            return newValue;
        }));
    }
    //endregion
}
