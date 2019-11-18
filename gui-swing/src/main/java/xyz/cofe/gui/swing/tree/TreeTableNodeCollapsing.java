package xyz.cofe.gui.swing.tree;

import xyz.cofe.collection.TreeEvent;

public class TreeTableNodeCollapsing extends TreeEvent {
    /**
     * Конструктор
     * @param source узел дерева
     */
    public TreeTableNodeCollapsing(TreeTableNode source) {
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
