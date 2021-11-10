package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Consumer1;
import xyz.cofe.fn.Tuple2;

import java.util.Arrays;

/**
 * Обвертка над {@link PagedData} с учетом грязных/чистых страниц.
 */
public class DirtyPagedData extends DirtyPagedDataBase<UsedPagesInfo> {
    public DirtyPagedData(ResizablePages<UsedPagesInfo> pagedData){
        super(pagedData);
    }
}
