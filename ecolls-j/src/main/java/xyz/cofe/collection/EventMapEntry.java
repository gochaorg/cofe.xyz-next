package xyz.cofe.collection;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Пара ключ/значение для EventMap {@link EventMap}
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
public class EventMapEntry<K,V> implements Map.Entry<K,V> {
    protected final WeakReference<EventMap<K,V>> eventMap;
    protected K key;
    protected V value;

    /**
     * Конструктор
     * @param eventMap карта
     * @param key ключ
     * @param value значение
     */
    public EventMapEntry(EventMap<K,V> eventMap, K key, V value){
        if( eventMap == null )throw new IllegalArgumentException( "eventMap == null" );
        this.eventMap = new WeakReference<>(eventMap);
        this.key = key;
        this.value = value;
    }

    /**
     * Возвращает ключ
     * @return ключ
     */
    @Override
    public K getKey() {
        return key;
    }

    /**
     * Возвращает значение
     * @return значение
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * Устанавлиает значени для ключа
     * @param value значение
     * @return предыдущее значение
     */
    @Override
    public V setValue(V value) {
        EventMap<K,V> em = eventMap.get();
        if( em==null )throw new IllegalStateException("event map reference is null");

        return em.put(key,value);
    }
}
