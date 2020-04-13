package xyz.cofe.collection.graph;

/**
 * Описывает ребро между вершинами
 * @author Kamnev Georgiy
 * @param <N> Тип вершины
 * @param <E> Тип дуги/ребра
 */
public interface Edge<N,E>
{
    /**
     * Возвращает вершину А
     * @return Вершина А
     */
    N getNodeA();

    /**
     * Возвращает вершину Б
     * @return Вершина Б
     */
    N getNodeB();

    /**
     * Возвращает ребро между вершинами А и Б
     * @return Ребро
     */
    E getEdge();
}