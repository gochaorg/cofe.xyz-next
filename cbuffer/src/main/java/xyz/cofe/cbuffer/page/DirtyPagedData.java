package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Consumer1;
import xyz.cofe.fn.Tuple2;

import java.util.Arrays;

/**
 * Обвертка над {@link PagedData} с учетом грязных/чистых страниц.
 */
public class DirtyPagedData extends DirtyPagedDataBase<UsedPagesInfo, DirtyPagedState.NonThreadSafe> {
    public DirtyPagedData(ResizablePages<UsedPagesInfo> pagedData){
        super(pagedData);
    }

    private volatile DirtyPagedState.NonThreadSafe state;

    @Override
    protected DirtyPagedState.NonThreadSafe state() {
        if( state!=null )return state;
        synchronized (this) {
            if( state!=null )return state;
            state = new DirtyPagedState.NonThreadSafe();
            return state;
        }
    }
}
