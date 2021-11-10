package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

/**
 * Уменьшение кол-ва используемых страниц
 */
public interface ReduciblePages<MEM extends UsedPagesInfo> extends PagedData<MEM> {
    /**
     * Уменьшение кол-ва используемых страниц
     * @param pages сколько страниц нужно убрать
     * @return Сколько было и сколько стало памяти
     */
    Tuple2<UsedPagesInfo,UsedPagesInfo> reducePages(int pages);
}
