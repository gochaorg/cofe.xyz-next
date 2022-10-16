package xyz.cofe.cbuffer.page;

/**
 * Информация о памяти
 */
public interface UsedPagesInfo extends Cloneable {
    /**
     * Кол-во доступных страниц
     *
     * @return кол-во страниц
     */
    public int pageCount();

    /**
     * Возвращает размер последней страницы
     *
     * @return размер последней страницы в байтах, значение может быть 1 или больше, но не больше {@link #pageSize()}
     */
    public int lastPageSize();

    /**
     * Возвращает размер страницы
     *
     * @return размер страницы в байтах, значение должно быть не меньше 1 байта
     */
    public int pageSize();

    /**
     * Создание информации о памяти
     *
     * @param pageSize     размер страницы
     * @param pageCount    кол-во доступных страниц
     * @param lastPageSize размер последней страницы
     * @return  информации о памяти
     */
    public static UsedPagesInfo of(int pageSize, int pageCount, int lastPageSize) {
        return new UsedPagesInfo() {
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

            @Override
            public UsedPagesInfo clone() {
                return this;
            }
        };
    }

    /**
     * Клонирование
     * @return клон
     */
    public default UsedPagesInfo clone(){
        return of( pageSize(), pageCount(), lastPageSize() );
    }
}