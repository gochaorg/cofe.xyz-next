package xyz.cofe.collection;

/**
 * Получение родитеского узла дерева
 * @param <A> Тип узла
 */
public interface GetTreeParent<A extends Tree<A>> {
    /**
     * Возвращает родитеский узлер текущего узла
     * @return Родительский узел или null
     */
    Tree<? extends A> getParent();
}
