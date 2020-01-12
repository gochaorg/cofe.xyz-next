package xyz.cofe.gui.swing.tree;

/**
 * Полчение дочерних узлов/объектов поддерева.
 * @see FollowableExtracter
 * @see TreeTableNodeBasic
 * @see FollowerMap
 * @author nt.gocha@gmail.com
 */
public interface TreeNodesExtracter {
    /**
     * Получение дочерних узлов поддерва
     * @param node поддерево
     * @return дочерние узлы
     */
    Iterable extract( TreeTableNode node );
}
