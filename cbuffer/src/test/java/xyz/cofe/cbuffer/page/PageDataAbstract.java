package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;
import xyz.cofe.fn.Pair;

import java.util.logging.Logger;

/**
 * Заготовка для страничной организации памяти
 */
@SuppressWarnings("WeakerAccess")
public class PageDataAbstract implements PageSizeProperty, PageBuffersProperties
{
    private final static Logger log = Logger.getLogger(PageDataAbstract.class.getName());

    //region pageSize : int = 8k - Размер страницы в байтах
    /**
     * Уведомление о изменении размера страницы: <br>
     * значение a() - старое значение <br>
     * значение b() - текущее значение <br>
     */
    @SuppressWarnings("WeakerAccess")
    public final PageEvent<Changes<Integer>> onPageSizeChanged = new PageEvent<>();

    /**
     * Размер страницы в байтах
     */
    @SuppressWarnings("WeakerAccess")
    protected volatile int pageSize = 1024 * 8;

    /**
     * Возвращает размер страницы
     *
     * @return размер страницы в байтах
     */
    @Override
    public synchronized int getPageSize(){
        return pageSize;
    }

    /**
     * Минимальный размер страницы
     */
    public static final int PAGE_SIZE_MIN = 256;

    /**
     * Указывает размер страницы
     *
     * @param size размер страницы в байтах
     */
    @Override
    public void setPageSize( int size ){
        if( size<1 ) throw new IllegalArgumentException("size<1");
        if( size<PAGE_SIZE_MIN ){
            log.warning("setPageSize( "+PAGE_SIZE_MIN+") to small");
        }
        int oldsize = this.pageSize;
        synchronized( this ){
            this.pageSize = size;
        }
        onPageSizeChanged.notify(Changes.of(oldsize,size));
    }
    //endregion
    //region fastBuffer : ContentBuffer - кэш страниц
    /**
     * Событие смена кэша
     */
    public final PageEvent<Changes<ContentBuffer>> onFastBufferChanged = new PageEvent<>();

    /**
     * данные кэша страниц
     */
    protected volatile ContentBuffer fastBuffer;

    /**
     * Возвращает данные кэша страниц
     * @return данные кэша страниц
     */
    @Override
    public ContentBuffer getFastBuffer(){
        synchronized( this ){
            return fastBuffer;
        }
    }

    /**
     * Устанавливает данные кэша страниц
     * @param buff данные кэша страниц
     */
    @Override
    public void setFastBuffer( ContentBuffer buff ){
        ContentBuffer old;
        synchronized( this ){
            old = this.fastBuffer;
            this.fastBuffer = buff;
        }
        onFastBufferChanged.notify(Changes.of(old,buff));
    }
    //endregion
    //region slowBuffer : ContentBuffer - данные
    /**
     * Событие смена Данные страниц
     */
    public final PageEvent<Changes<ContentBuffer>> onSlowBufferChanged = new PageEvent<>();

    /**
     * Данные страниц
     */
    protected volatile ContentBuffer slowBuffer;

    /**
     * Возвращает данные страниц
     *
     * @return данные страниц
     */
    @Override
    public ContentBuffer getSlowBuffer(){
        synchronized( this ){
            return slowBuffer;
        }
    }

    /**
     * Устанавливает данные страниц
     *
     * @param buff данные страниц
     */
    @Override
    public void setSlowBuffer( ContentBuffer buff ){
        ContentBuffer old;
        synchronized( this ){
            old = this.slowBuffer;
            this.slowBuffer = buff;
        }
        onSlowBufferChanged.notify(Changes.of(old,buff));
    }
    //endregion
}
