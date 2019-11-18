package xyz.cofe.gui.swing.tree;

/**
 * Возвращает настройки форматирования узла/объекта
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public interface TreeTableNodeGetFormatOf {
    /**
     * Возвращает настройки форматирования узла
     * @param value объект/узел
     * @return настройки форматирования или null
     */
    TreeTableNodeFormat getTreeTableNodeFormatOf( Object value );
}
