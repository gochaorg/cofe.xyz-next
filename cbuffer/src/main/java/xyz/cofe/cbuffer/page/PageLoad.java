package xyz.cofe.cbuffer.page;

/**
 * Загрузка страницы из slow в fast
 */
public interface PageLoad {
    /**
     * Загрузка страницы из slow в fast
     * @param slowPageIndex страница
     * @return индекс fast страницы
     */
    int map(int slowPageIndex);
}
