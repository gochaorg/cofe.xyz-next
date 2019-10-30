/*
 * The MIT License
 *
 * Copyright 2017 user.
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

import xyz.cofe.ecolls.Pair;
import xyz.cofe.ecolls.QuadConsumer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Список содежащий уникальные элементы, отсортированные в порядке возрастания
 * @param <A> Тип элементов в множестве
 */
interface IndexSet<A extends Comparable<A>>
{
    /**
     * Кол-во элементов
     * @return Кол-во элементов
     */
    int size();

    /**
     * Получение элемента по его индексу
     * @param idx индекс
     * @return Элемент
     */
    A get(int idx);

    /**
     * Обновление элемента
     * @param a текущее значение
     * @param b новое значение
     * @param allowMerge
     * true - если <b>b</b> существует, то удалить <b>a</b> <br>
     * false - если поведение зависит от generateError
     * @param generateError true - если b существует, то сгенерировать ошибку <br>
     * false - если b существует, то не обновлять и вернуть null
     * @return Смена индекса или null если элемент a не существует
     */
    //Pair<Integer,Integer> update(A a, A b, boolean allowMerge, boolean generateError);

    /**
     * Проверка наличия элемента в списке
     * @param a элемент
     * @return true - существует
     */
    boolean exists(A a);

    /**
     * Получение идекса элемента
     * @param a элемент
     * @return индекс (0 и более) или -1 отсуствие
     */
    int indexOf( A a );

    // Поиск хвоста, где значения >= a
    /**
     * Поиск хвоста, где <b>искомое_значения</b> &gt;= a
     * @param a значение с которым производится сравнение
     * @param strong строгое сравнение: <br>
     * true  - искомое_значения <b>&gt;</b> a <br>
     * false - искомое_значения <b>&gt;=</b> a
     * @param begin начальный индекс с которого производить поиск
     * @param endEx коненый индекс, по который исключительно производить поиск
     * @return начало хвоста или null
     */
    Pair<Integer,A> tailEntry(A a, boolean strong, int begin, int endEx );

    // Поиск головы, где значения <= a
    /**
     * Поиск головы, где <b>искомое_значения</b> &lt;= a
     * @param a значение с которым производится сравнение
     * @param strong строгое сравнение: <br>
     * true  - искомое_значения <b>&lt;</b> a <br>
     * false - искомое_значения <b>&lt;=</b> a
     * @param begin начальный индекс с которого производить поиск
     * @param endEx коненый индекс, по который исключительно производить поиск
     * @return конец головы или null
     */
    Pair<Integer,A> headEntry( A a, boolean strong, int begin, int endEx );

    /**
     * Обход элементов в списке
     * @param iter итератор
     */
    void each( Consumer<A> iter );

    /**
     * Получение потока/stream-а значений
     * @return поток значение/индекс
     */
    Stream<Pair<A,Integer>> stream();

    /**
     * Обход элементов в списке
     * @param begin С какого индекса начать
     * @param endEx По какой исключительно закнчить
     * @param consumer Функция fn(index,item):any принимающая значения
     */
    void eachByIndex( int begin, int endEx, BiConsumer<Integer,A> consumer );

    /**
     * Обход элементов в списке
     * @param begin С какого элемента начать
     * @param incBegin Включить элемент в список
     * @param end До какого элемента продолжить
     * @param incEnd Включить конечный элемент в список
     * @param consumer Функция принимающая значения:<br>
     * <code>fn(index,item,visitIndex,visitSize):any</code> ,где: <br>
     * index - индекс элемента в списке <br>
     * item - элемент в списке <br>
     * visitIndex - индекс в выборке <br>
     * visitSize - Объем вборки
     */
    void eachByValue(
            A begin, boolean incBegin,
            A end, boolean incEnd,
            QuadConsumer<Integer,A,Integer,Integer> consumer );

    /**
     * Обход элементов в списке
     * @param begin С какого элемента начать
     * @param incBegin Включить элемент в список
     * @param end До какого элемента продолжить
     * @param incEnd Включить конечный элемент в список
     * @param consumer Функция принимающая значения:<br>
     * <code>fn(index,item):any</code> ,где: <br>
     * index - индекс элемента в списке <br>
     * item - элемент в списке
     */
    void eachByValue(
            A begin, boolean incBegin,
            A end, boolean incEnd,
            BiConsumer<Integer,A> consumer );

    /**
     * Добавление элемента в список
     * @param a элемент
     * @return индекс элемента
     */
    int add( A a );

    /**
     * Добавление элемента в список
     * @param a элементы
     * @return self ссылка
     */
    IndexSet<A> append( A ... a );

    /**
     * Добавление элементов в список
     * @param adds элементы
     * @param added добавленные элементы
     */
    void add( Iterable<A> adds, BiConsumer<Integer,A> added );

    /**
     * Добавление элементов в список
     * @param adds элементы
     * @param added добавленные элементы
     */
    void add( IndexSet<A> adds, BiConsumer<Integer,A> added );

    /**
     * Удаление элемента
     * @param a элемент
     * @return индекс удаленного элемента
     */
    int remove( A a );

    /**
     * Удаление элементов из списока
     * @param removes элементы
     * @param removed удаленные элементы
     */
    void remove( Iterable<A> removes, BiConsumer<Integer,A> removed );

    /**
     * Удаление элементов из списока
     * @param removes элементы
     * @param removed удаленные элементы
     */
    void remove( IndexSet<A> removes, BiConsumer<Integer,A> removed );

    /**
     * Удаляет элемент по его индексу
     * @param idx индекс
     * @return Удаленный элемент
     */
    A removeByIndex( int idx );

    /**
     * Удаление
     */
    void clear();

    /**
     * Возвращает диапазон (мин/макс) значений
     * @return диапазон или null, если список пуст
     */
    Pair<A,A> minMax();

    /**
     * Возвращат минимальное значение
     * @return минимальное значение
     */
    A min();

    /**
     * Возвращат максимальное значение
     * @return максимальное значение
     */
    A max();
}
