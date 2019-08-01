package xyz.cofe.collection.graph;

import java.util.List;

/**
 * Описывает путь в графе
 * @author gocha
 * @param <N> Тип вершины графа
 * @param <E> Тип ребра между вершинами
 */
public interface Path<N,E>
{
    /**
     Описывает напарвления движения
     */
    public static enum Direction
    {
        /**
         Из вершины A в вершину B
         */
        AB,
        /**
         Из вершины B в вершину A
         */
        BA
    }

    /**
     * Создание клона
     * @return клон
     */
    Path<N,E> clone();

    /**
     Проверяет содержит ли путь вершину
     @param a вершина A
     @return true - вершина содержится в пути
     */
    boolean has(N a);

    /**
     * Кол-во определенной вершины в пути
     * @param n вершина
     * @return кол-во
     */
    int count(N n);

    /**
     * Возвращает кол-во вершин в пути
     * @return кол-во вершин
     */
    int nodeCount();

    /**
     * Возвращает вершину
     * @param nodeIndex индекс вершины
     * @return вершина
     */
    N node(int nodeIndex);

    /**
     * Получение ребер между указанными вершинами
     * @param beginIndex начальная вершина
     * @param endExc конечная (исключительно) вершина
     * @return список ребер
     */
    List<E> edges(int beginIndex, int endExc);

    /**
     * Получение ребра между указаными вершинами. Растояние между вершинами, должно быть 1 ребро.
     * @param beginIndex начальная вершина
     * @param endExc конечная (исключительно) вершина
     * @return ребро
     */
    E edge(int beginIndex, int endExc);

    /**
     * Получение ребер между указанными вершинами
     * @param beginIndex начальная вершина
     * @param endExc конечная (исключительно) вершина
     * @return список ребер
     */
    List<Edge<N,E>> fetch(int beginIndex, int endExc);

    /**
     * Возвращает признак что путь пустой - не содержит вершин и ребер
     * @return true - путь пустой
     */
    public boolean isEmpty();

    /**
     * Создает новый путь с начальной вершиной
     * @param n Начальная вершина
     * @return новый путь
     */
    public Path<N,E> start(N n);

    /**
     * Создает новый путь с добавленным ребром в конце
     * @param e Ребро/дуга
     * @param n Вершина
     * @return новый путь
     */
    public Path<N,E> join(N n, E e);

    /**
     * Создает новый пустой путь
     * @return путь
     */
    public Path<N,E> clear();

    /**
     * Проверят путь на наличие циклов
     * @return true - в пути присуствуют циклы
     */
    public boolean hasCycles();

    /**
     * Возвращает циклы в пути
     * @return список циклов
     */
    public List<Path<N,E>> cycles();

    /**
     * Возвращает под путь
     * @param beginIdx начальная вершина
     * @param endExc конечная (исключительно) вершина
     * @return Под путь
     */
    public Path<N,E> segment(int beginIdx,int endExc);
}