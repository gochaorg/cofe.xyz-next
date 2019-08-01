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

import xyz.cofe.iter.ArrayIterable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Карта для записи в несколько других карт и чтение из указанной
 * @author gocha
 */
public class UnionMap<K,V> implements Map<K, V>{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(UnionMap.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(UnionMap.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(UnionMap.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(UnionMap.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(UnionMap.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(UnionMap.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected Map<K,V> readMap = null;
    protected Iterable<Map> writeMaps = null;

    public Map<K, V> getReadMap() {
        return readMap;
    }

    public void setReadMap(Map<K, V> readMap) {
        this.readMap = readMap;
    }

    public Iterable<Map> getWriteMaps() {
        return writeMaps;
    }

    public void setWriteMaps(Iterable<Map> writeMaps) {
        this.writeMaps = writeMaps;
    }

    public void setWriteMaps(Map ... maps){
        this.writeMaps = new ArrayIterable<>(maps);
    }

    @Override
    public int size() {
        if( readMap==null )return 0;
        return readMap.size();
    }

    @Override
    public boolean isEmpty() {
        if( readMap==null )return true;
        return readMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if( readMap==null )return false;
        return readMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if( readMap==null )return false;
        return readMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if( readMap==null )return null;
        return readMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        V r = null;
        if( writeMaps!=null ){
            for( Map<K,V> m : writeMaps ){
                if( m!=null ){
                    r = m.put(key, value);
                }
            }
        }
        return r;
    }

    @Override
    public V remove(Object key) {
        V r = null;
        if( writeMaps!=null ){
            for( Map<K,V> m : writeMaps ){
                if( m!=null ){
                    r = m.remove(key);
                }
            }
        }
        return r;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> ms) {
        if( writeMaps!=null ){
            for( Map<K,V> m : writeMaps ){
                if( m!=null ){
                    m.putAll(ms);
                }
            }
        }
    }

    @Override
    public void clear() {
        if( writeMaps!=null ){
            for( Map<K,V> m : writeMaps ){
                if( m!=null ){
                    m.clear();
                }
            }
        }
    }

    @Override
    public Set<K> keySet() {
        if( readMap==null )return new HashSet<K>();
        return readMap.keySet();
    }

    @Override
    public Collection<V> values() {
        if( readMap==null )return new HashSet<V>();
        return readMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        if( readMap==null )return new HashSet<Entry<K, V>>();
        return readMap.entrySet();
    }
}
