package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

/**
 * Информация о буферах данных
 */
public interface PageBuffers {
    /**
     * Возвращает данные кэша страниц
     * @return данные кэша страниц
     */
    ContentBuffer getFastBuffer();

    /**
     * Возвращает данные страниц
     * @return данные страниц
     */
    ContentBuffer getSlowBuffer();
}
