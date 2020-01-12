package xyz.cofe.gui.swing.tree;

import xyz.cofe.collection.IndexTree;

/**
 * Узел дерева-таблицы (TreeTable)
 * @author user
 * @param <Node> Тип узла дерева
 */
public interface TreeTableNode<Node extends TreeTableNode<Node>>
    extends
    IndexTree<Node>
{
    //<editor-fold defaultstate="collapsed" desc="data : Object">
    /**
     * Возвращает данные (для отображения) узла дерева
     * @return данные узла
     */
    public Object getData();

    /**
     * Указывает данные (для отображения) узла дерева
     * @param v данные узла
     */
    public void setData( Object v );
    //</editor-fold>

//    //<editor-fold defaultstate="collapsed" desc="dataPath : List">
//    /**
//     * Возвращает путь состоящий из "данных" узлов
//     * @return путь данных
//     */
//    public List<Object> getDataPath();
//    //</editor-fold>

//    //<editor-fold defaultstate="collapsed" desc="treeLevel : int">
//    /**
//     * Возвращает уровень вложенности узла в дереве
//     * @return уровень узла в дереве
//     */
//    public int getTreeLevel();
//    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="expanded : boolean">
    /**
     * Возвращает узел развернут (отображаются дочерние узлы) или нет
     * @return true - узел развернут
     */
    public boolean isExpanded();

    /**
     * Указывает узел развернут (отображаются дочерние узлы) или нет
     * @param v - узел развернут
     */
    public void setExpanded( boolean v );

    /**
     * Разворачивает/раскрывает дочерние узлы
     */
    public void expand();

    /**
     * Сворачивает/скрывает дочерние узлы дерева
     */
    public void collapse();
    //</editor-fold>
}
