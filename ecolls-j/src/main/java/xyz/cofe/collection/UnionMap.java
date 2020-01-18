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

    /**
     * Возвращает карту из которой произодит чтение
     * @return карта для чтения
     */
    public Map<K, V> getReadMap() {
        return readMap;
    }

    /**
     * Указывает карту из которой произодит чтение
     * @param readMap карта для чтения
     */
    public void setReadMap(Map<K, V> readMap) {
        this.readMap = readMap;
    }

    /**
     * Возвращает карты в которые производит запись
     * @return карты для записи
     */
    public Iterable<Map> getWriteMaps() {
        return writeMaps;
    }

    /**
     * Указывет карты в которые производит запись
     * @param writeMaps карты для записи
     */
    public void setWriteMaps(Iterable<Map> writeMaps) {
        this.writeMaps = writeMaps;
    }

    /**
     * Указывет карты в которые производит запись
     * @param maps карты для записи
     */
    public void setWriteMaps(Map ... maps){
        this.writeMaps = new ArrayIterable<>(maps);
    }

    /**
     * Возвращает размер карты
     * @return размер карты
     */
    @Override
    public int size() {
        if( readMap==null )return 0;
        return readMap.size();
    }

    /**
     * Проверяет что карта пустая
     * @return true - нет элементов
     */
    @Override
    public boolean isEmpty() {
        if( readMap==null )return true;
        return readMap.isEmpty();
    }

    /**
     * Проверяет наличие ключа в карте
     * @param key ключ
     * @return true - есть в карте
     */
    @Override
    public boolean containsKey(Object key) {
        if( readMap==null )return false;
        return readMap.containsKey(key);
    }

    /**
     * ПРоверяет наличие значения в карте
     * @param value значение
     * @return true - есть значение
     */
    @Override
    public boolean containsValue(Object value) {
        if( readMap==null )return false;
        return readMap.containsValue(value);
    }

    /**
     * Возвращает значение по ключу
     * @param key ключ
     * @return значение
     */
    @Override
    public V get(Object key) {
        if( readMap==null )return null;
        return readMap.get(key);
    }

    /**
     * Устанавливает ключ/значение
     * @param key ключ
     * @param value значние
     * @return предыдущее (последнее) значение
     */
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

    /**
     * Удаляет ключ/значение
     * @param key ключ
     * @return предыдущее значение
     */
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

    /**
     * Добавляет значения
     * @param ms значения
     */
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

    /**
     * Удялет все значения
     */
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

    /**
     * Возвращает все ключи карты
     * @return ключи
     */
    @Override
    public Set<K> keySet() {
        if( readMap==null )return new HashSet<K>();
        return readMap.keySet();
    }

    /**
     * Возвращает значения карты
     * @return значения
     */
    @Override
    public Collection<V> values() {
        if( readMap==null )return new HashSet<V>();
        return readMap.values();
    }

    /**
     * Возвращает пары ключ/значние
     * @return пары ключ/значние
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        if( readMap==null )return new HashSet<Entry<K, V>>();
        return readMap.entrySet();
    }
}
