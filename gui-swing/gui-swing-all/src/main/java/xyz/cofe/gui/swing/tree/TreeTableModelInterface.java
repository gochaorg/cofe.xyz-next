package xyz.cofe.gui.swing.tree;

import javax.swing.table.TableModel;

/**
 * Модель дерева отображенное на таблицу
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public interface TreeTableModelInterface extends TableModel
{
    /**
     * Возвращает отображается ли корень дерева в таблице
     * @return true - корень дерева виден / false - корень скрыт, отображаются дочерние узлы дерева
     */
    boolean isRootVisible();

    /**
     * Указывает корень дерева
     * @return корень дерева
     */
    public TreeTableNode getRoot();

    /**
     * Указывает корень дерева
     * @param root корень дерева
     */
    public void setRoot( TreeTableNode root );

    /**
     * Возвращает узел для указанной строки
     * @param row строка
     * @return узел или null
     */
    public TreeTableNode getNodeOf( int row );

    /**
     * Возвращает номер строки для указанного узла
     * @param node узел
     * @return номер строки или -1
     */
    public int getRowOf( TreeTableNode node );
}