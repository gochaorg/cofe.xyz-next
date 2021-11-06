package xyz.cofe.cbuffer.page;

public class CachePagedData extends BaseCachePagedData<CachePagedState> {
    protected CachePagedData(CachePagedState state) {
        super(state);
    }

    protected CachePagedData(DirtyPagedData cachePages, ResizablePages persistentPages, CachePagedState state) {
        super(cachePages, persistentPages, state);
    }

    public CachePagedData(DirtyPagedData cachePages, ResizablePages persistentPages) {
        super(cachePages, persistentPages);
    }
}
