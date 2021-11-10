package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

/**
 * Расширение кол-ва используемых страниц
 */
public interface ExtendablePages<MEM extends UsedPagesInfo> extends PagedData<MEM> {
    /**
     * Расширение кол-ва используемых страниц
     * @param pages сколько страниц нужно добавить
     * @return Сколько было и сколько стало памяти
     */
    Tuple2<UsedPagesInfo,UsedPagesInfo> extendPages(int pages);
}
