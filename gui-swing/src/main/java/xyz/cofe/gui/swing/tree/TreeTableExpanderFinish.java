package xyz.cofe.gui.swing.tree;

import xyz.cofe.collection.TreeEvent;

/**
 * Событие начала (синхронного) извлечения дочерних узлов
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class TreeTableExpanderFinish extends TreeEvent {
    /**
     * Конструктор
     * @param source родительский узел
     */
    public TreeTableExpanderFinish( TreeTableNode source) {
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
