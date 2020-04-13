/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

package xyz.cofe.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Подмножество карты, где ключи имеют определенный префикс.
 * Для пользователей этой карты все проходит прозначно.
 * Пользователь карты не должен использовать null ключи, они будут проигнорированы.
 */
public class PrefixStringMap<T> implements Map<String, T> {
    protected Map<String,T> wrappedMap = null;
    protected String prefix = null;

    /**
     * Конструктор
     * @param prefix префикс
     * @param wrappedMap целевая карта
     */
    public PrefixStringMap(String prefix, Map<String,T> wrappedMap){
        if( prefix==null )throw new IllegalArgumentException("prefix==null");
        if( wrappedMap==null )throw new IllegalArgumentException("wrappedMap==null");
        this.wrappedMap = wrappedMap;
        this.prefix = prefix;
    }

    /**
     * Карта которая хранит значения
     * @return карта
     */
    public Map<String,T> target(){return wrappedMap;}

    /**
     * Префик используемый в ключах
     * @return префикс ключей
     */
    public String getPrefix(){ return prefix; }

    /**
     * Возвращает значения карты
     * @return значения
     */
    @Override
    public Collection<T> values() {
        List<T> l = new ArrayList<T>();
        for( String k : keySet() ){
            if( k==null )continue;
            String key = prefix + k;
            T v = wrappedMap.get(key);
            l.add( v );
        }
        return l;
    }

    /**
     * Возвращает кол-во элементов
     * @return кол=во элементов
     */
    @Override
    public int size() {
        return keySet().size();
    }

    /**
     * Удаляет элемент по его ключу
     * @param key ключ
     * @return удаленный элемент
     */
    @Override
    public T remove(Object key) {
        if( key==null )return null;
        String k = key==null ? null : prefix+key.toString();
        return wrappedMap.remove(k);
    }

    /**
     * Добавляет значения из другой карты
     * @param m карта
     */
    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        for(String key : m.keySet()){
            if( key==null )continue;
            String k = prefix+key;
            wrappedMap.put(k, m.get(key));
        }
    }

    /**
     * Добавляет ключ/значение
     * @param key ключ
     * @param value значение
     * @return предыдущее значение
     */
    @Override
    public T put(String key, T value) {
        if( key==null )return null;
        String k = key==null ? null : prefix+key;
        return wrappedMap.put(k, value);
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> keySet() {
        Set<String> keys = new TreeSet<String>();
        for( String key : wrappedMap.keySet() ){
            if( key!=null && key.startsWith(prefix) ){
                String k = key.length()==prefix.length() ? "" : key.substring(prefix.length());
                keys.add( k );
            }
        }
        return keys;
    }

    /**
     * Проверяет что карта пуста
     * @return true - нет элементов в карте
     */
    @Override
    public boolean isEmpty() {
        return keySet().isEmpty();
    }

    /**
     * Возвращает значение по ключу
     * @param key ключ
     * @return значение или null
     */
    @Override
    public T get(Object key) {
        String k = key==null ? null : prefix+key;
        return wrappedMap.get(k);
    }

    protected Entry<String,T> createEntry(final String key,final T value){
        return new Entry<String, T>() {
            @Override
            public String getKey() {
                if( key!=null && key.startsWith(prefix) ){
                    if( key.length()==prefix.length() )return "";
                    return key.substring(prefix.length());
                }
                return key;
            }

            @Override
            public T getValue() {
                return wrappedMap.get(key);
            }

            @Override
            public T setValue(T value) {
                return wrappedMap.put(key, value);
            }
        };
    }

    /**
     * Возвращает перечень пар
     * @return пары
     */
    @Override
    public Set<Entry<String, T>> entrySet() {
        Set<Entry<String,T>> r = new HashSet<Entry<String, T>>();
        for( Entry<String,T> e : wrappedMap.entrySet() ){
            String key = e.getKey();
            T value = e.getValue();
            if( key==null )continue;
            if( !key.startsWith(prefix) )continue;
            r.add(createEntry(key, value));
        }
        return r;
    }

    /**
     * Прочеряет наличие значения в карте
     * @param value значение
     * @return true - значение есть в карте
     */
    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    /**
     * Проверяет наличие ключа в карте
     * @param key ключ
     * @return true - есть в карте
     */
    @Override
    public boolean containsKey(Object key) {
        String k = key==null ? null : prefix+key.toString();
        return wrappedMap.containsKey(k);
    }

    /**
     * Удаялет все значения
     */
    @Override
    public void clear() {
//        for( Object k : keySet().toArray() ){
//            remove(k);
//        }
        //TODO Вот тут херня какая-то
        wrappedMap.clear();
    }
}
