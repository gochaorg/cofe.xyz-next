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

package xyz.cofe.collection;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.collection.SortInsert;

/**
 * Вставка значения в контейнер с профеллирование времени работы
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public abstract class SortInsertProfiling<Container,Element> extends SortInsertDefault<Container,Element>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(SortInsertProfiling.class.getName());
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
        logger.entering(SortInsertProfiling.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(SortInsertProfiling.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(SortInsertProfiling.class.getName(), method, result);
    }
    //</editor-fold>

    @Override
    protected boolean profiling() {
        return true;
    }

    @Override
    protected void profBeginInsert() {
        insertStartNS = System.nanoTime();
    }

    @Override
    protected void profEndInsert() {
        insertEndNS = System.nanoTime();
        insertTotalNS += (insertEndNS - insertStartNS);
    }

    public int maxDepth = 0;
    public int currentDepth = 0;

    public long sortInsertTimeNS = -1;
    public long sortInsertStartNS = -1;
    public long sortInsertEndNS = -1;

    public long insertStartNS = -1;
    public long insertEndNS = -1;
    public long insertTotalNS = -1;

    protected void initCounters(){
        maxDepth = 1;
        sortInsertTimeNS = -1;
        sortInsertStartNS = System.nanoTime();
        sortInsertEndNS = -1;

        insertStartNS = -1;
        insertEndNS = -1;
        insertTotalNS = 0;
    }

    @Override
    public synchronized int sortInsert(
            Container container,
            Element item,
            Comparator<Element> comp,
            int begin,
            int endex
    ){
        try{
            currentDepth++;
            if( currentDepth==1 ){
                initCounters();
            }else{
                if( maxDepth<currentDepth ){
                    maxDepth = currentDepth;
                }
            }

            int r = super.sortInsert(container, item, comp, begin, endex);
            return r;
        }finally{
            currentDepth--;
            if( currentDepth==0 ){
                sortInsertEndNS = System.nanoTime();
                sortInsertTimeNS = sortInsertEndNS - sortInsertStartNS;
            }
        }
    }

    /**
     * Создание экземпляра для работы со списком
     * @return экземпляр
     */
    public static SortInsert<List,Object> createForList(){
        return new SortInsertProfiling<List, Object>() {
            @Override
            public void insert(List container, int position, Object item) {
                container.add(position, item);
            }

            @Override
            public Object get(List container, int position) {
                return container.get(position);
            }
        };
    }
}
