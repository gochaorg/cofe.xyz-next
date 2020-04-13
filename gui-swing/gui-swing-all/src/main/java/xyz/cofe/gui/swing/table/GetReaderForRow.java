package xyz.cofe.gui.swing.table;

import java.util.function.Function;

/**
 * Чтение значения ячейки с учетом строки
 * @author user
 */
public interface GetReaderForRow {
    /**
     * Чтение значения ячейки. <br>
     * Вызывается так: <code>column.getReader( row ).convert( Элемент списка )</code> - должен вернуть значение элемента для соответ. колонки.
     * @param row Индекс строки
     * @return чтение значения ячейки
     */
    public Function<Object, Object> getReader( int row );
}
