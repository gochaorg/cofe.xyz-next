package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

/**
 * Информация о буферах данных
 */
public interface PageBuffersProperties extends PageBuffers {
    /**
     * Возвращает данные кэша страниц
     * @return данные кэша страниц
     */
    @Override
    default ContentBuffer getFastBuffer(){ return PageBuffersPropertyHolder.getFastBuffer(this); }

    default void setFastBuffer(ContentBuffer buff){
        PageBuffersPropertyHolder.setFastBuffer(this,buff);
    }

    /**
     * Возвращает данные страниц
     * @return данные страниц
     */
    @Override
    default ContentBuffer getSlowBuffer(){ return PageBuffersPropertyHolder.getSlowBuffer(this); }

    default void setSlowBuffer(ContentBuffer buff){
        PageBuffersPropertyHolder.getSlowBuffer(this,buff);
    }
}
