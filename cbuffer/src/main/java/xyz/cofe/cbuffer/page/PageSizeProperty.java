package xyz.cofe.cbuffer.page;

/**
 * Свойство pageSize - задает размер страницы в байтах.
 * Данные храняться в {@link PageSizePropertyHolder}
 */
public interface PageSizeProperty extends GetPageSize {
    /**
     * Возвращает размер страницы
     * @return размер страницы в байтах
     */
    @Override
    default int getPageSize(){ return PageSizePropertyHolder.get(this); }

    /**
     * Указывает размер страницы
     * @return размер страницы в байтах
     */
    default void setPageSize(int size){
        PageSizePropertyHolder.set(this, size);
    }
}
