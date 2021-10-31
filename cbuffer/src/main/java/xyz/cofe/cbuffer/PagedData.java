package xyz.cofe.cbuffer;


/**
 * Страничная организация памяти
 */
public interface PagedData {
    public interface MemoryInfo {
        /**
         * Кол-во доступных страниц
         * @return кол-во страниц
         */
        public int pageCount();

        /**
         * Возвращает размер последней страницы
         * @return размер последней страницы в байтах, значение может быть 1 или больше, но не больше {@link #pageSize()}
         */
        public int lastPageSize();

        /**
         * Возвращает размер страницы
         * @return размер страницы в байтах, значение должно быть не меньше 1 байта
         */
        public int pageSize();

        /**
         * Создание информации о памяти
         * @param pageSize размер страницы
         * @param pageCount кол-во доступных страниц
         * @param lastPageSize размер последней страницы
         * @return
         */
        public static MemoryInfo of(int pageSize, int pageCount, int lastPageSize ){
            return new MemoryInfo() {
                @Override
                public int pageCount() {
                    return pageCount;
                }

                @Override
                public int lastPageSize() {
                    return lastPageSize;
                }

                @Override
                public int pageSize() {
                    return pageSize;
                }
            };
        }
    }

    /**
     * Получение информации о памяти
     * @return Получение информации о памяти
     */
    public MemoryInfo memoryInfo();

    /**
     * Чтение страницы
     * @param page индекс страницы, от 0 и более
     * @return массив байтов, по размеру равный {@link MemoryInfo#pageSize()} или меньше, если последняя страница
     */
    public byte[] readPage(int page);

    /**
     * Запись страницы
     * @param page индекс страницы, от 0 и более
     * @param data массив байтов, размер не должен превышать {@link MemoryInfo#pageSize()}
     */
    public void writePage(int page, byte[] data);
}
