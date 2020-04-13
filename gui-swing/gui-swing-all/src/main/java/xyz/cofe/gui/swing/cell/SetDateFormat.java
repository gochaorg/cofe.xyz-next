package xyz.cofe.gui.swing.cell;

import java.text.DateFormat;

/**
 * Указывает форматирование дат
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public interface SetDateFormat {
    /**
     * Указывает формат даты
     * @param df формат
     */
    void setDateFormat( DateFormat df );
}