package xyz.cofe.cbuffer.page;

public class CachePagedData extends CachePagedDataBase<
    CachePagedState<UsedPagesInfo, DirtyPagedData>,
    UsedPagesInfo,
    DirtyPagedData
    > {
    protected CachePagedData(CachePagedState<UsedPagesInfo, DirtyPagedData> state) {
        super(state);
    }

    protected CachePagedData(DirtyPagedData cachePages, ResizablePages<UsedPagesInfo> persistentPages, CachePagedState<UsedPagesInfo, DirtyPagedData> state) {
        super(cachePages, persistentPages, state);
    }

    public CachePagedData(DirtyPagedData cachePages, ResizablePages<UsedPagesInfo> persistentPages) {
        super(cachePages, persistentPages);
    }
}
