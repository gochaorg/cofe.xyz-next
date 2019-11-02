package xyz.cofe.text.template;

/**
 * Контроллер значения
 * @author gocha
 */
public interface ValueController
{
    /**
     * Вовращает тип значения
     * @return Тип
     */
    Class getType();

    /**
     * Возвращает название
     * @return Название
     */
    String getName();

    /**
     * Возвращает значение
     * @return Значение
     * @throws java.lang.Throwable Если не возможно прочесть
     */
    Object getValue() throws Throwable;

    /**
     * Устанавливает значение
     * @param value значение
     * @throws java.lang.Throwable Если не возможно установить
     */
    void setValue(Object value) throws Throwable;
}
