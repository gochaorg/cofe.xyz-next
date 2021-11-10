package xyz.cofe.cbuffer.page;

public class CachePagedData extends CachePagedDataBase<CachePagedState<UsedPagesInfo>, UsedPagesInfo> {
    protected CachePagedData(CachePagedState<UsedPagesInfo> state) {
        super(state);
    }

    protected CachePagedData(DirtyPagedData cachePages, ResizablePages<UsedPagesInfo> persistentPages, CachePagedState<UsedPagesInfo> state) {
        super(cachePages, persistentPages, state);
    }

    public CachePagedData(DirtyPagedData cachePages, ResizablePages<UsedPagesInfo> persistentPages) {
        super(cachePages, persistentPages);
    }
}
