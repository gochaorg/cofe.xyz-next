/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy (nt.gocha@gmail.com).
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

package xyz.cofe.sql.cpool.proxy;

import xyz.cofe.collection.BasicEventMap;
import xyz.cofe.fn.Fn4;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Статистика вызовов методов
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class InvokeActivityStat {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(InvokeActivityStat.class.getName());
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

    private static void logEntering(String method,Object ... params){
        logger.entering(InvokeActivityStat.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(InvokeActivityStat.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(InvokeActivityStat.class.getName(), method, result);
    }
    //</editor-fold>
    
    protected final Map<String,Long> lastCall;
    protected final Map<String,Long> firstCall;
    protected final Map<String,Long> callCount;
    protected volatile Long lastActivity;
    protected ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    /**
     * Конструктор по умолчанию
     */
    public InvokeActivityStat(){
        lastCall  = new BasicEventMap<>(new LinkedHashMap(), readWriteLock);
        firstCall = new BasicEventMap<>(new LinkedHashMap(), readWriteLock);
        callCount = new BasicEventMap<>(new LinkedHashMap(), readWriteLock);
    }
    
    /**
     * Возвращает время последнего вызова
     * @return метод / время
     */
    public Map<String,Long> getLastCall(){ return lastCall; }
    
    /**
     * Возвращает время первого вызова
     * @return метод / время
     */
    public Map<String,Long> getFirstCall(){ return firstCall; }
    
    /**
     * ВОзвращает кол-во вызовов
     * @return метод / кол-во
     */
    public Map<String,Long> getCallCount(){ return callCount; }
    
    /**
     * Сбор статистики вызова метода
     * @param methodName имя метода
     */
    public void collect( String methodName ){
        if( methodName==null )return;
        synchronized(this){
            long t = System.currentTimeMillis();
            Long c = callCount.get(methodName);
            if( c==null ){
                firstCall.put(methodName, t);
                lastCall.put(methodName, t);
                callCount.put(methodName, 1L);
            }else{
                lastCall.put(methodName, t);
                callCount.put(methodName, (Long)(long)(c + 1) );
            }
            lastActivity = t;
        }
    }
    
    /**
     * Возвращает время последнего вызова методов
     * @return время последнего вызова или null
     */
    public Date getLastActivityDate(){        
        synchronized(this){
            return lastActivity!=null ? new Date(lastActivity) : null;
        }
    }
    
    /**
     * Возвращает время последнего вызова методов
     * @return время или null
     */
    public Long getLastActivity(){
        synchronized(this){
            return lastActivity;
        }
    }
    
    /**
     * Описывает статистику вызова метода
     */
    public static class Value {
        private volatile String name;
        private volatile Long last;
        private volatile Long first;
        private volatile Long count;
        
        /**
         * Конструктор
         * @param name имя метода 
         */
        public Value(String name) {
            this.name = name;
        }

        /**
         * Конструктор
         * @param name имя метода
         * @param first время первого вызова
         * @param last время последнего вызова
         * @param count кол-во вызовов
         */
        public Value(String name, Long first, Long last, Long count) {
            this.name = name;
            this.first = first;
            this.last = last;
            this.count = count;
        }

        /**
         * Возвращает кол-во вызовов
         * @return кол-во вызовов
         */
        public Long getCount() { return count; }
        
        /**
         * Указывает кол-во вызовов
         * @param count кол-во вызовов
         */
        public void setCount(Long count) { this.count = count; }

        /**
         * Возвращает время последнего вызова
         * @return время последнего вызова
         */
        public Long getLast() {return last; }
        
        /**
         * Указывает время последнего вызова
         * @param last время последнего вызова
         */
        public void setLast(Long last) { this.last = last; }

        /**
         * Возвращает время первого вызова
         * @return время первого вызова
         */
        public Long getFirst() { return first; }
        
        /**
         * Указывает время первого вызова
         * @param first время первого вызова
         */
        public void setFirst(Long first) { this.first = first; }

        /**
         * Возвращает имя метода
         * @return имя метода
         */
        public String getName() { return name; }
        
        /**
         * Указывает имя метода
         * @param name имя метода
         */
        public void setName(String name) { this.name = name; }
    }
    
    /**
     * Возвращает выборку текущий метрик
     * @return выборка метрик
     */
    public List<Value> getValues(){
        ArrayList<Value> values = new ArrayList<>();
        synchronized(this){
            Set<String> names = new LinkedHashSet<>();
            names.addAll(firstCall.keySet());
            names.addAll(lastCall.keySet());
            names.addAll(callCount.keySet());
            
            for( String name : names ){
                Value val = new Value(name);
                val.setCount(callCount.get(name));
                val.setFirst(firstCall.get(name));
                val.setLast(lastCall.get(name));
                values.add(val);
            }
        }
        return values;
    }
    
    /**
     * Создает подписчика на вызовы методов прокси объектов
     * @param stat куда будут записываться метрики
     * @return подписчик
     */
    public static MethodCallListener createActivityTracker( final InvokeActivityStat stat ){
        if( stat==null )throw new IllegalArgumentException("stat == null");
        return new MethodCallAdapter(){
            @Override
            public void beginCall(Object proxy, Object source, Method meth, Object[] args) {
                if( meth==null )return;                
                stat.collect(meth.getName());
                return;
            }
        };
    }

    /**
     * Создает подписчика на вызовы методов прокси объектов
     * @param wstat куда будут записываться метрики
     * @return подписчик
     */
    public static MethodCallListener createActivityTracker( 
        final WeakReference<InvokeActivityStat> wstat
    ){
        if( wstat==null )throw new IllegalArgumentException("wstat == null");
        return new MethodCallAdapter(){
            @Override
            public void beginCall(Object proxy, Object source, Method meth, Object[] args) {
                if( meth==null )return;
                
                InvokeActivityStat stat = wstat.get();
                if( stat==null )return;
                
                stat.collect(meth.getName());
            }
        };
    }
    
    /**
     * Возвращает указанную константу в качестве метрики
     * @param name имя метрики
     * @return функц получения имени метрики
     */
    public static Fn4<Object,Object, Method, Object[],String> constName( final String name ){
        if( name==null )throw new IllegalArgumentException("name == null");
        return new Fn4<Object, Object, Method, Object[],String>() {
            @Override
            public String apply(Object arg1, Object arg2, Method arg3, Object[] arg4) {
                return name;
            }
        };
    }
    
    /**
     * Возвращает имя метода в качетсве имени метрики
     * @return функц получения имени метрики
     */
    public static Fn4<Object,Object, Method, Object[],String> simpleMethodName(){
        return new Fn4<Object, Object, Method, Object[],String>() {
            @Override
            public String apply(Object arg1, Object arg2, Method arg3, Object[] arg4) {
                if( arg3==null )return null;
                return arg3.getName();
            }
        };
    }
    
    /**
     * Создает функц именования метрики для указаных метрик, за исключением указанных методов
     * @param include функция именования метрик
     * @param excludeMethodNames методы исключемые из сбора статистики
     * @return функция сопоставления вызова метода и метрики
     */
    public static Fn4<Object,Object, Method, Object[],String> exclude(
        final Fn4<Object,Object, Method, Object[],String> include,
        final String ... excludeMethodNames
    ){
        if( include==null )throw new IllegalArgumentException("include == null");
        return new Fn4<Object, Object, Method, Object[],String>() {
            @Override
            public String apply(Object arg1, Object arg2, Method arg3, Object[] arg4) {
                if( arg3!=null ){
                    for( String exclName : excludeMethodNames ){
                        if( arg3.getName().equals(exclName) )return null;
                    }
                }
                return include.apply(arg1, arg2, arg3, arg4);
            }
        };
    }

    /**
     * Создает подписчика для сбора статистики
     * @param stat куда будет записиываться статистика
     * @param asWeak статистика будет храниться в памяти как weak ссылка
     * @param nameGeneraor метод генерации имени метрики
     * @return подписчик
     */
    public static MethodCallListener createActivityTracker( 
        final InvokeActivityStat stat,
        final boolean asWeak,
        final Fn4<Object,Object, Method, Object[],String> nameGeneraor
    ){
        if( stat==null )throw new IllegalArgumentException("stat == null");
        
        if( asWeak ){
            final WeakReference<InvokeActivityStat> wstat = new WeakReference<>(stat);
            return new MethodCallAdapter(){
                @Override
                public void beginCall(Object proxy, Object source, Method meth, Object[] args) {
                    if( meth==null )return;

                    String name = nameGeneraor!=null ?
                        nameGeneraor.apply(proxy, source, meth, args)
                        : meth.getName();

                    if( name==null )return;

                    InvokeActivityStat stat = wstat.get();
                    if( stat==null )return;

                    stat.collect(name);
                }
            };
        }else{
            return new MethodCallAdapter(){
                @Override
                public void beginCall(Object proxy, Object source, Method meth, Object[] args) {
                    if( meth==null )return;

                    String name = nameGeneraor!=null ?
                        nameGeneraor.apply(proxy, source, meth, args)
                        : meth.getName();

                    if( name==null )return;

                    //InvokeActivityStat stat = wstat.get();
                    //if( stat==null )return;

                    stat.collect(name);
                    return;
                }
            };
        }
    }
}
