package xyz.cofe.sort;

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

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Вставка в контейнер значения по умолчанию
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public abstract class SortInsertDefault<Container,Element> extends SortInsert<Container,Element>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(SortInsertDefault.class.getName());
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
        logger.entering(SortInsertDefault.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(SortInsertDefault.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(SortInsertDefault.class.getName(), method, result);
    }
    //</editor-fold>

    protected void profBeginInsert(){};
    protected void profEndInsert(){};
    protected boolean profiling(){ return false; }

//    protected int sortInsert( Container container, Element item, Comparator<Element> comp, int begin, int endex ){
//    }

    /**
     * Вставка сортировкой
     * @param container контейнер
     * @param item элемент
     * @param comp компаратор
     * @param begin начало диапазона вставки
     * @param endex конец диапазона вставки
     * @return позиция в которую произведена вставка
     */
    @Override
    public int sortInsert( Container container, Element item, Comparator<Element> comp, int begin, int endex ){
        if( container==null )throw new IllegalArgumentException( "container==null" );

        //int cycle = 0; // счетчик циклов
        while( true ){

            // диапазон в который производится вставка
            int rangeSize = endex-begin;

            // диапазон равен 0 - вставка в начало диапазона
            if( rangeSize<=0 ){
                if( profiling() ) profBeginInsert();
                insert(container, begin, item);
                if( profiling() ) profEndInsert();
                return begin;
            }

            int result = Integer.MIN_VALUE;

            // диапазон содержит 1 элемент
            // два варианта вставки, перед и после элемента
            if( rangeSize==1 ){
                // Object it = list.get(begin);
                Element it = get(container, begin);
                int cmp = comp.compare(item, it);

                // вставка перед элементом
                if( cmp<0 ){
                    // list.add( begin, item );
                    if( profiling() ) profBeginInsert();
                    insert(container, begin, item);
                    if( profiling() ) profEndInsert();
                    return begin;
                } else{
                    // вставка после элемента
                    //list.add( begin+1, item );
                    if( profiling() ) profBeginInsert();
                    insert(container, begin+1, item);
                    if( profiling() ) profEndInsert();
                    return begin+1;
                    //return;
                }
            } else{
                // диапазон содержит 2 или более элементов
                // разделить диапазон на две части
                // взять первый элемент из второй части и сравнить
                // если меньше, то повторить для процедуру для первой части
                // если равен, то вставить в начало второй части
                // если больше, то повторить для процедуру для второй части
                int leftPartSize = rangeSize/2;
                int rightPartSize = rangeSize-leftPartSize;

                int leftBegin = begin;
                int leftEndEx = begin+leftPartSize;

                int rightBegin = begin+leftPartSize;
                int rightEndEx = rightBegin+rightPartSize;

                if( leftPartSize<1 && rightPartSize<1 ){
                    throw new Error("error!");
                } else if( leftPartSize<1 && rightPartSize >= 1 ){
                    throw new Error("error!");
                } else if( leftPartSize >= 1 && rightPartSize >= 1 ){
                    // Получаем значение центрального элемента (начало второй половины)
                    Element it0 = get(container, rightBegin);
                    int cmp = comp.compare(item, it0);
                    if( cmp<0 ){
                        // result = sortInsert(container, item, comp, leftBegin, leftEndEx);
                        // return result;
                        begin = leftBegin;
                        endex = leftEndEx;
                        continue;
                    } else if( cmp==0 ){
                        if( profiling() ) profBeginInsert();
                        insert(container, rightBegin, item);
                        if( profiling() ) profEndInsert();
                        return rightBegin;
                    } else if( cmp>0 ){
//                        result = sortInsert(container, item, comp, rightBegin, rightEndEx);
//                        return result;
                        begin = rightBegin;
                        endex = rightEndEx;
                        continue;
                    }
                } else if( leftPartSize >= 1 && rightPartSize<1 ){
                    throw new Error("error!");
                }
            }
        }
//        return result;
    }
}
