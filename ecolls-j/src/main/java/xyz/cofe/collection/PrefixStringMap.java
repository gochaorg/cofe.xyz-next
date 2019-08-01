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

    public PrefixStringMap(String prefix, Map<String,T> wrappedMap){
        if( prefix==null )throw new IllegalArgumentException("prefix==null");
        if( wrappedMap==null )throw new IllegalArgumentException("wrappedMap==null");
        this.wrappedMap = wrappedMap;
        this.prefix = prefix;
    }

    public Map<String,T> getWrappedMap(){return wrappedMap;}
    public String getPrefix(){ return prefix; }

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

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public T remove(Object key) {
        if( key==null )return null;
        String k = key==null ? null : prefix+key.toString();
        return wrappedMap.remove(k);
    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        for(String key : m.keySet()){
            if( key==null )continue;
            String k = prefix+key;
            wrappedMap.put(k, m.get(key));
        }
    }

    @Override
    public T put(String key, T value) {
        if( key==null )return null;
        String k = key==null ? null : prefix+key;
        return wrappedMap.put(k, value);
    }

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

    @Override
    public boolean isEmpty() {
        return keySet().isEmpty();
    }

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

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    @Override
    public boolean containsKey(Object key) {
        String k = key==null ? null : prefix+key.toString();
        return wrappedMap.containsKey(k);
    }

    @Override
    public void clear() {
        wrappedMap.clear();
    }
}
