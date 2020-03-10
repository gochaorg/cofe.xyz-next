package xyz.cofe.gui.swing.cell;

import java.awt.geom.Rectangle2D;

/**
 * Контекст (значение/расположение/таблица/...) ячнйки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public interface CellContext {
    /**
     * Клонирование
     * @return клон
     */
    public CellContext clone();

    //<editor-fold defaultstate="collapsed" desc="bounds : Rectangle2D">
    /**
     * Указывает расположение контекста/рамка в которую производится отображение
     * @return Рамка
     */
    public Rectangle2D getBounds();

    /**
     * Указывает расположение контекста/рамка в которую производится отображение
     * @param bounds Рамка
     */
    public void setBounds( Rectangle2D bounds );
    //</editor-fold>

    /**
     * Уменьшает размер прямоугольника (bounds) слева
     * @param pad на сколько уменьшить
     * @return self ссылка
     */
    public CellContext padLeft( double pad );

    /**
     * Уменьшает размер прямоугольника (bounds) сверху
     * @param pad на сколько уменьшить
     * @return self ссылка
     */
    public CellContext padTop( double pad );

    /**
     * Уменьшает размер прямоугольника (bounds) справа
     * @param pad на сколько уменьшить
     * @return self ссылка
     */
    public CellContext padRight( double pad );

    /**
     * Уменьшает размер прямоугольника (bounds) снизу
     * @param pad на сколько уменьшить
     * @return self ссылка
     */
    public CellContext padBottom( double pad );

    /**
     * Смещает прямоугольник контекста
     * @param x на сколько по x
     * @param y на сколько по y
     * @return self ссылка
     */
    public CellContext move( double x, double y );

    /**
     * Устанавливает размер прямоугольника
     * @param width ширина
     * @param height высота
     * @return self ссылка
     */
    public CellContext size( double width, double height );

    //<editor-fold defaultstate="collapsed" desc="value : Object">
    /**
     * Указывает отображаемое значение
     * @return отображаемое значение
     */
    public Object getValue();

    /**
     * Указывает отображаемое значение
     * @param value отображаемое значение
     */
    public void setValue( Object value );

    /**
     * Указывает отображаемое значение
     * @param value отображаемое значение
     * @return self ссылка
     */
    public CellContext value( Object value );
    //</editor-fold>
}
