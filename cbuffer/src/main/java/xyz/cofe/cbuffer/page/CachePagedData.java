package xyz.cofe.cbuffer.page;

import java.util.Map;

/**
 * Кеширование страниц
 */
public class CachePagedData {
    /**
     * Кеш страниц
     */
    protected DirtyPagedData cachePages;

    /**
     * Основная память
     */
    protected ResizablePages hardPages;

    /**
     * Отображение кеша страниц на основную память.
     *
     * <p>
     * Семантика:
     * <code>{@link #cache2hard}[ cache_page_index ] = hard_pages_index</code>
     *
     * <p>
     * Значение (hard_pages_index):
     * <ul>
     *     <li> <b>-1 или меньше</b> - нет отображения</li>
     *     <li> <b>0 или больше</b> - отображение </li>
     * </ul>
     */
    protected int[] cache2hard;
}
