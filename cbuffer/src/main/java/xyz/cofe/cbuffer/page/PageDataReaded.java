package xyz.cofe.cbuffer.page;

/**
 * Информация о событии чтения или записи страницы
 */
public interface PageDataReaded {
    /**
     * Запрошенная страница
     * @return страница - сковзная нумерация
     */
    public int page();

    /**
     * Данные страницы
     * @return данные
     */
    public byte[] bytes();

    /**
     * Создание объекта
     * @param page страница
     * @param bytes данные
     * @return событие чтения
     */
    public static PageDataReaded of( int page, byte[] bytes ){
        return new PageDataReaded() {
            @Override
            public int page(){
                return page;
            }

            @Override
            public byte[] bytes(){
                return bytes;
            }
        };
    }
}
