package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

/**
 * Информация о используемых данных, где распложены сами данные и кэш.
 * <br>
 *     В качетсве реализации использует {@link PageBuffersPropertyHolder}
 */
public interface PageBuffersProperties extends PageBuffers {
    /**
     * Возвращает данные кэша страниц
     * @return данные кэша страниц
     */
    @Override
    default ContentBuffer getFastBuffer(){ return PageBuffersPropertyHolder.getFastBuffer(this); }

    /**
     * Устанавливает данные кэша страниц
     * @param buff данные кэша страниц
     */
    default void setFastBuffer(ContentBuffer buff){
        PageBuffersPropertyHolder.setFastBuffer(this,buff);
    }

    /**
     * Возвращает данные страниц
     * @return данные страниц
     */
    @Override
    default ContentBuffer getSlowBuffer(){ return PageBuffersPropertyHolder.getSlowBuffer(this); }

    /**
     * Устанавливает данные страниц
     * @param buff данные страниц
     */
    default void setSlowBuffer(ContentBuffer buff){
        PageBuffersPropertyHolder.getSlowBuffer(this,buff);
    }
}
