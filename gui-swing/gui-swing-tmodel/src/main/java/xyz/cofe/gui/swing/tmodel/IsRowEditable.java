package xyz.cofe.gui.swing.tmodel;

/**
 * Функция проверки что указанный объект таблицы (строка) редактируемый.
 * Применяет при редактировании ячейки
 * @author nt.gocha@gmail.com
 */
 /*
 * @see PropertyColumn
 * @see TreeTableDirectModel
 * @see TreeTableNodeValueColumn
 */
public interface IsRowEditable
{
    /**
     * Проверка что можно редактировать указанный объект-строку
     * @param row объект - строка таблицы
     * @return true - ячейка доступна для редактирования
     */
    boolean isRowEditable( Object row );
}
