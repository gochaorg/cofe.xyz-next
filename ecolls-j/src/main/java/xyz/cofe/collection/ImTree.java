package xyz.cofe.collection;

import xyz.cofe.iter.Eterable;

/**
 * Получение скписка/итератора дочерних узлов текущего узла дерева
 * @param <A> Тип узла
 */
public interface ImTree<A extends ImTree<? extends A>> {
    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    Eterable<A> nodes();
}
