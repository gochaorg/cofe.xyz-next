package xyz.cofe.gui.swing.tree;

import xyz.cofe.collection.TreeEvent;

import java.util.LinkedHashSet;
import java.util.Set;

public class TreeNodeCacheDropped extends TreeEvent implements NodeGetSource {
    /**
     * Конструктор
     * @param source родительский узел
     */
    public TreeNodeCacheDropped( TreeTableNode source) {
        if( source==null ) throw new IllegalArgumentException("source==null");
        this.source = source;
    }

    private final TreeTableNode source;

    /**
     * Источник события - узел дерева который сгенерировал событие
     * @return зел дерева
     */
    @Override
    public TreeTableNode getSource(){
        return source;
    }


    protected Set<TreeTableNode> dropped;

    /**
     * Возвращает кэшированные и удаленные узлы
     * @return кэированные узлы
     */
    public synchronized Set<TreeTableNode> getDropped(){
        if( dropped!=null )return dropped;
        dropped = new LinkedHashSet<>();
        return dropped;
    }
}
