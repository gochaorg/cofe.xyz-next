package xyz.cofe.gui.swing.tree;

import xyz.cofe.collection.TreeEvent;

/**
 * Событие завершения (синхронного) извлечения дочерних узлов
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class TreeNodeFollowed extends TreeEvent {
    /**
     * Конструктор
     * @param source родительский узел
     */
    public TreeNodeFollowed( TreeTableNode source) {
        if( source==null ) throw new IllegalArgumentException("source==null");
        this.source = source;
    }

    private final TreeTableNode source;

    /**
     * Источник события - узел дерева который сгенерировал событие
     * @return зел дерева
     */
    public TreeTableNode getSource(){
        return source;
    }
}
