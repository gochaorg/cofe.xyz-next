package xyz.cofe.cbuffer.page;

/**
 * Страничная организация памяти
 */
public interface Paged {

    /**
     * Получение информации о памяти
     * @return Получение информации о памяти
     */
    public UsedPagesInfo memoryInfo();

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