package xyz.cofe.cbuffer.page;

/**
 * Информация о размере fast страниц
 */
public interface FastDataSize {
    /**
     * Возвращает кол-во занятых данных в странице
     * @param fastPageIndex индекс fast страницы
     * @return кол-во байт
     */
    int fastDataSize( int fastPageIndex);

    /**
     * Указывает кол-во занятых данных в странице
     * @param fastPageIndex индекс fast страницы
     * @param dataSize кол-во байт
     */
    void fastDataSize( int fastPageIndex, int dataSize);
}
