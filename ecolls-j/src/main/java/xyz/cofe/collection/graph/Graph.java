package xyz.cofe.collection.graph;

/**
 * Базовый интервейс графа
 * @author GoCha
 * @param <N> Тип вершины
 * @param <E> Тип дуги/ребра
 */
public interface Graph<N,E>
{
    /**
     * Проверяет наличае вершины
     * @param node Вершина
     * @return true -вершина содержиться в графе
     */
    boolean contains(N node);

    /**
     * Добавляет вершину к графу
     * @param node Вершина
     */
    void add(N node);

    /**
     * Удаляет вершину из графа
     * @param node Вершина
     */
    void remove(N node);

    /**
     * Возвращает вершины графа
     * @return вершины
     */
    Iterable<N> getNodes();

    /**
     * Возвращает Ребра и вершины графа
     * @return Ребра и вершины
     */
    Iterable<Edge<N, E>> getEdges();

    /**
     * Возвращает ребра указанной вершины
     * @param node Вершина
     * @return ребра
     */
    Iterable<Edge<N, E>> edgesOf(N node);

    /**
     * Возвращает ребра из вершины А
     * @param nodeA Вершина А
     * @return Ребра
     */
    Iterable<Edge<N, E>> edgesOfNodeA(N nodeA);

    /**
     * Возвращает ребра из вершины Б
     * @param nodeB Вершина Б
     * @return Ребра
     */
    Iterable<Edge<N, E>> edgesOfNodeB(N nodeB);

    /**
     * Удаляет все ребра
     */
    void clearEdges();

    /**
     * Удалес все ребра и вершины
     */
    void clearAll();

    /**
     * Проверка наличия ребра между вершинами
     * @param a Вершина А
     * @param b Вершина Б
     * @return Флаг наличия ребра
     */
    boolean hasEdge(N a, N b);

    /**
     * Удаление ребра
     * @param a Вершина А
     * @param b Вершина Б
     */
    void removeEdge(N a, N b);

    /**
     * Возвращает ребро между вершинами
     * @param a Вершина А
     * @param b Вершина Б
     * @return Ребро
     */
    E getEdge(N a, N b);

    /**
     * Установка ребра между вершинами
     * @param a Вершина А
     * @param edge Ребро
     * @param b Вершина Б
     */
    void setEdge( N a, N b, E edge);

    /**
     * Возвращает ребра между вершинами
     * @param a Вершина А
     * @param b Вершина Б
     * @return Ребра
     */
    Iterable<E> getEdges(N a, N b);

    /**
     * Установка ребр между вершинами
     * @param a Вершина А
     * @param edges Ребра
     * @param b Вершина Б
     */
    void setEdges( N a, N b, Iterable<E> edges);
}
