package xyz.cofe.gui.swing.tree;

import xyz.cofe.collection.TreeEvent;

public class TreeTableDataChanged extends TreeEvent implements NodeGetSource {
    /**
     * Конструктор
     * @param source узел дерева
     */
    public TreeTableDataChanged(TreeTableNode source) {
        if( source==null ) throw new IllegalArgumentException("source==null");
        this.source = source;
    }

    /**
     * Конструктор
     * @param source узел дерева
     * @param oldData предыдущее значение
     * @param newData текущее значение
     */
    public TreeTableDataChanged(TreeTableNode source, Object oldData, Object newData) {
        if( source==null ) throw new IllegalArgumentException("source==null");
        this.source = source;
        this.oldData = oldData;
        this.newData = newData;
    }

    private final TreeTableNode source;

    /**
     * Источник события - узел дерева который сгенерировал событие
     * @return зел дерева
     */
    public TreeTableNode getSource(){
        return source;
    }

    protected Object oldData;

    /**
     * Возвращает предыдущее значение
     * @return предыдущее значение
     */
    public Object getOldData() { return oldData; }

    /**
     * Указывает предыдущее значение
     * @param oldData предыдущее значение
     */
    public void setOldData(Object oldData) { this.oldData = oldData; }

    protected Object newData;

    /**
     * Возвращает текущее значение
     * @return текущее значение
     */
    public Object getNewData() { return newData; }

    /**
     * Указывает текущее значение
     * @param newData текущее значение
     */
    public void setNewData(Object newData) { this.newData = newData; }
}
