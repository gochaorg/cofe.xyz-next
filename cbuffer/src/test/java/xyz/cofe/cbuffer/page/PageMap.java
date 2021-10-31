package xyz.cofe.cbuffer.page;

/**
 * Отображение страниц
 */
public interface PageMap {
    /**
     * Возвращает отображение страницы fast = на =&gt; slow
     * @param fastPageIndex Индекс страницы в fast буфере
     * @return индес страницы в slow соответ fast или -1, если нет соответствия
     */
    int fastToSlow(int fastPageIndex);

    /**
     * Возвращает отображение страницы slow = на =&gt; fast
     * @param slowPageIndex Индекс страницы в slow буфере
     * @return индес страницы в fast соответ slow или -1, если нет соответствия
     */
    int slowToFast(int slowPageIndex);
}
