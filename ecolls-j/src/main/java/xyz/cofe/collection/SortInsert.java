/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy <nt.gocha@gmail.com>.
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
import xyz.cofe.collection.SortInsertDefault;

/**
 * Сортировка вставкой. <p>
 *
 * Реализован метод sortInsert, сложнасть должна варьироваться от O( n * log n ) до O( n^2 ).
 * <p>
 * Пример 1:
 * <pre><code style="font-size:100%">
 * // Список куда будут добавлены в порядке возрастания элементы
 * List&lt;Integer&gt; sortedList = new LinkedList&lt;&gt;();
 *
 * // Функция сравнения
 * Comparator&lt;Integer&gt; intComparator
 * &nbsp; = (Integer a, Integer b) -&gt; a &lt; b ? -1 : (a==b ? 0 : 1);
 *
 * // Функция сортировки
 * SortInsert sinserter = SortInsert.createForList();
 *
 * // Генерация случайных элементов
 * Random rnd = new Random();
 * for( int i=0; i&lt;50; i++ ){
 * &nbsp; sinserter.sortInsert( // вставка в список
 * &nbsp; &nbsp; sortedList, // список
 * &nbsp; &nbsp; rnd.nextInt(100), // случайное число
 * &nbsp; &nbsp; intComparator, // функция сравнения
 * &nbsp; &nbsp; 0, // вставлять от начала списка
 * &nbsp; &nbsp; sortedList.size() // и до конца
 * &nbsp; );
 * }
 * </code></pre>
 * @author nt.gocha@gmail.com
 * @param <Container> Тип конейнера
 * @param <Element> Тип элемента
 */
//TODO Переместить в какойнибудь пакет отдельно
public abstract class SortInsert<Container,Element> {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(SortInsert.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
                ? true
                : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
                ? true
                : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
                ? true
                : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
                ? true
                : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
                ? false
                : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
                ? false
                : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(SortInsert.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(SortInsert.class.getName(), method, result);
    }

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

    /**
     * Вставка элемента в контейнер
     * @param container контейнер
     * @param position позиция в которую производится вставка
     * @param item Элемент
     */
    public abstract void insert( Container container, int position, Element item );

    /**
     * Получение элемента в контейнере
     * @param container контейнер
     * @param position позиция
     * @return Элемент
     */
    public abstract Element get( Container container, int position );

    /**
     * Вставка сортировкой
     * @param container контейнер
     * @param item элемент
     * @param comp компаратор
     * @param begin начало диапазона вставки
     * @param endex конец диапазона вставки
     * @return позиция в которую произведена вставка
     */
    public abstract int sortInsert( Container container, Element item, Comparator comp, int begin, int endex );

    /**
     * Создание экземпляра для работы со списком
     * @return экземпляр
     */
    public static SortInsert<List,Object> createForList(){
        return new SortInsertDefault<List, Object>() {
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
