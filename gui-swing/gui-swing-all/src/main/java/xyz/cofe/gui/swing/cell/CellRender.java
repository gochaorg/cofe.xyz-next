package xyz.cofe.gui.swing.cell;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Рендер ячейки
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public interface CellRender {
    /**
     * Получение размера отображаемых данных
     * @param gs объект граф вывода
     * @param context Контекст отображения
     * @return Размер и расположение отображаемых данных
     * или null если нет определенного размера (залива и т.д.)
     */
    Rectangle2D cellRectangle( Graphics2D gs, CellContext context );

    /**
     * Отоброжение/рендер данных
     * @param gs объект граф вывода
     * @param context Контекст отображения
     */
    void cellRender( Graphics2D gs, CellContext context );

    /**
     * Создание клона
     * @return клон рендера
     */
    CellRender clone();
}
