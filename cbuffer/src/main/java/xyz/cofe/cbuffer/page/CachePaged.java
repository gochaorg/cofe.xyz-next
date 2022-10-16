package xyz.cofe.cbuffer.page;

public class CachePaged implements Paged {
    public CachePaged(Paged cache, Paged persistent){
        if( cache==null )throw new IllegalArgumentException("cache==null");
        if( persistent==null )throw new IllegalArgumentException("persistent==null");

        if( cache.memoryInfo().pageSize()!=persistent.memoryInfo().pageSize() )
            throw new PageError("pageSize different between cache and persistent");

        this.cache = cache;
        this.persistent = persistent;
        this.cacheMap = new CacheMap();
        cacheMap.resize(cache.memoryInfo().pageCount(),req -> flushCachePageOnResize(req.cachedPageIndex, req.persistentPageIndex));
    }

    private final CacheMap cacheMap;
    public int getCacheSize(){
        return cacheMap.size();
    }

    private final Paged cache;
    private final Paged persistent;

    private void flushCachePageOnResize(int cachePage, int persistentPageIndex){
        persistent.writePage(persistentPageIndex,cache.readPage(cachePage));
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        return persistent.memoryInfo();
    }

    @Override
    public byte[] readPage(int page) {
        // 1 найти в кеше -> вернуть из кеша
        var fromCache = cacheMap.find(cp->cp.getTarget().map(t->t==page).orElse(false)).forRead(cp->{
            cp.markReads();
            return persistent.readPage(cp.getTarget().get());
        });

        // 2 загрузить в кеш -> вернуть из кеша
        throw new PageError("not implement");
    }

    @Override
    public void writePage(int page, byte[] data) {
        // 1 найти в кеше -> записать в кеш
        // 2 загрузить в кеш -> записать в кеш
        throw new PageError("not implement");
    }
}
