package xyz.cofe.cbuffer.page;


import xyz.cofe.cbuffer.page.UsedPagesInfo;

/**
 * Страничная организация памяти
 */
public interface PagedData<MEMINFO extends UsedPagesInfo> {

    /**
     * Получение информации о памяти
     * @return Получение информации о памяти
     */
    public MEMINFO memoryInfo();

    /**
     * Чтение страницы
     * @param page индекс страницы, от 0 и более
     * @return массив байтов, по размеру равный {@link UsedPagesInfo#pageSize()} или меньше, если последняя страница
     */
    public byte[] readPage(int page);

    /**
     * Запись страницы
     * @param page индекс страницы, от 0 и более
     * @param data массив байтов, размер не должен превышать {@link UsedPagesInfo#pageSize()}
     */
    public void writePage(int page, byte[] data);
}
