package xyz.cofe.cbuffer.page;

/**
 * Информация о изменении fast страниц (кэша)
 */
public interface PageFastDirty {
    /**
     * Проверка, что указанная fast станица содержит не сохранненые изменения
     * @param fastPageIndex fast страница
     * @return true - есть изменения которые не зафиксированы
     */
    boolean dirty(int fastPageIndex);

    /**
     * Указыает, что fast станица содержит (не)сохранненые изменения
     * @param fastPageIndex fast страница
     * @param dirty - есть изменения
     */
    void dirty(int fastPageIndex, boolean dirty);
}
