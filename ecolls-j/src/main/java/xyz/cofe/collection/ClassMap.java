/*
 * The MIT License
 *
 * Copyright 2016 Kamnev Georgiy <nt.gocha@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package xyz.cofe.collection;

import java.io.Closeable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.collection.ClassSet;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.ecolls.TripleConsumer;

/**
 * Карта с возможностью поиска совместимых подтипов.
 * <pre>
 * class A {}
 * class B extends A {}
 * ...
 * cm = new ClassMap&lt;Integer&gt;
 * cm.put( A.class, 1 )
 *
 * cm.fetch( A.class ) // вернет 1
 * cm.fetch( B.class ) // вернет 1,
 *                     // т.к. B является подклассом A
 * </pre>
 * @author nt.gocha@gmail.com
 */
public class ClassMap<T> implements Map<Class, T>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ClassMap.class.getName());
    private static final Level logLevel = logger.getLevel();

    private static final boolean isLogSevere =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.SEVERE.intValue();

    private static final boolean isLogWarning =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.WARNING.intValue();

    private static final boolean isLogInfo =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.INFO.intValue();

    private static final boolean isLogFine =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.FINE.intValue();

    private static final boolean isLogFiner =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.FINER.intValue();

    private static final boolean isLogFinest =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.FINEST.intValue();

    private static void logFine(String message,Object ... args){
        logger.log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        logger.log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        logger.log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        logger.log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected final Lock lock;
    protected final Map<Class,T> map;
    protected final ClassSet types;

    public ClassMap(){
        lock = new java.util.concurrent.locks.ReentrantLock();

        EventMap<Class,T> emap = new BasicEventMap<Class, T>();
        map = emap;

        types = new ClassSet(true);
        syncTypes(types, emap);
    }

    private AutoCloseable syncTypes( final ClassSet types, EventMap<Class,T>  map ){
        TripleConsumer<Class,T,T> changels = (clazz,old,cur)->{
            try {
                lock.lock();
                if( old!=null ) {
                    if (types != null && clazz != null) {
                        types.remove(clazz);
                    }
                }
                if( cur!=null ){
                    if( types!=null && clazz!=null ){
                        types.add(clazz);
                    }
                }
            }
            finally {
                lock.unlock();
            }
        };

        Closeables cl = new Closeables();
        cl.append(
            map.onInserted(changels),
            map.onDeleted(changels),
            map.onUpdated(changels));

        return cl;
    }

    @Override
    public T put(Class key, T value) {
        return map.put(key, value);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    public T get(Class key) {
        return map.get(key);
    }

    public T remove(Class key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends Class, ? extends T> m) {
        map.putAll(m);
    }

    @Override
    public Set<Class> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<T> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<Class, T>> entrySet() {
        return map.entrySet();
    }

    public T fetch(Class cls){
        if( cls==null )return null;

        T t = map.get(cls);
        if( t!=null )return t;

        Collection<Class> matchParents = types.getAssignableFrom(cls, true, false);
        Set<Class> matchedCls = new LinkedHashSet<Class>();
        if( matchParents!=null ){
            for( Class c : matchParents ){
                if( c!=null ){
                    matchedCls.add(c);
//                    if( c.isInterface() ){
//                        continue;
//                    }
                    break;
                }
            }
        }

        for( Class c : matchedCls ){
            t = map.get(c);
            if( t!=null )return t;
        }

        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public T get(Object key) {
        return map.get(key);
    }

    @Override
    public T remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }
}
