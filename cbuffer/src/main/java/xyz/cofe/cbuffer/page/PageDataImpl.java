package xyz.cofe.cbuffer.page;

import xyz.cofe.collection.IndexSet;
import xyz.cofe.collection.IndexSetBasic;
import xyz.cofe.ecolls.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PageDataImpl implements PageData, PageSizeProperty, PageBuffersProperties, PageSlowWrite {
    public final PageEvent<Pair<Integer,byte[]>> onFastDataWrited = new PageEvent<>();

    @Override
    public void fastData( int pageIndex, byte[] bytes ){
        //((PageFastWrite)this).fastData(pageIndex,bytes);
        PageData.super.fastData(pageIndex,bytes);
        onFastDataWrited.notify(Pair.of(pageIndex,bytes));
    }

    //region fast data size
    public final PageEvent<Pair<Integer,Integer>> onFastDataSize = new PageEvent<>();

    private final Map<Integer,Integer> fastDataSize = new HashMap<>();

    @Override
    public void fastDataSize( int fastPageIndex, int dataSize ){
        if( fastPageIndex<0 ) throw new IllegalArgumentException("fastPageIndex<0");
        fastDataSize.put(fastPageIndex,dataSize);
        onFastDataSize.notify( Pair.of(fastPageIndex,dataSize) );
    }

    @Override
    public int fastDataSize( int fastPageIndex ){
        if( fastPageIndex<0 ) throw new IllegalArgumentException("fastPageIndex<0");
        return fastDataSize.getOrDefault(fastPageIndex, getPageSize());
    }
    //endregion
    //region dirty fast pages
    public final Map<Integer,Boolean> dirtyFastPages = new HashMap<>();

    @Override
    public boolean dirty( int fastPageIndex ){
        return dirtyFastPages.getOrDefault(fastPageIndex,false);
    }

    public final PageEvent<Pair<Integer,Boolean>> onDirty = new PageEvent<>();

    @Override
    public void dirty( int fastPageIndex, boolean dirty ){
        dirtyFastPages.put(fastPageIndex,dirty);
        onDirty.notify(Pair.of(fastPageIndex,dirty));
    }

    public int getDirtyPageCount(){
        return getDirtyPages().size();
    }

    public IndexSet<Integer> getDirtyPages(){
        IndexSet<Integer> pages = new IndexSetBasic<>();
        dirtyFastPages.forEach( (page,dirty)->{if(dirty){pages.add(page);}} );
        return pages;
    }
    //endregion

    private final Map<Integer,Integer> fast2slow = new HashMap<>();
    private final Map<Integer,Integer> slow2fast = new HashMap<>();
    private final IndexSet<Integer> freepages = new IndexSetBasic<>();
    private final IndexSet<Integer> usedpages = new IndexSetBasic<>();

    @Override
    public int fastPageCount(){
        return fast2slow.size();
    }

    private volatile int maxFastPageCount = 16;
    public int getMaxFastPageCount(){
        return maxFastPageCount;
    }
    public void setMaxFastPageCount(int max){ maxFastPageCount = max; }

    private int getMaxFastPageIndex(){
        return fast2slow.keySet().stream().max((a,b)->a-b).orElse(-1);
    }

    public final PageEvent<Integer> onAllocFreePage = new PageEvent<>();
    public final PageEvent<Integer> onAllocNewPage = new PageEvent<>();
    public final PageEvent<Integer> onAllocExistsPage = new PageEvent<>();
    public final PageEvent<Integer> onAlloc;
    {
        onAlloc = new PageEvent<>();
        onAlloc.listen(onAllocExistsPage);
        onAlloc.listen(onAllocFreePage);
        onAlloc.listen(onAllocNewPage);
    }

    protected int allocatePage(){
        // поиск среди свободных
        if( freepages.size()>0 ){
            var p = freepages.removeByIndex(0);
            onAllocFreePage.notify(p);
            return p;
        }

        // поиск среди не распределенных
        int unallocated = getMaxFastPageCount() - fastPageCount();
        if( unallocated>0 ){
            int maxPi = getMaxFastPageIndex();
            int p = maxPi>=0 ? maxPi+1 : 0;
            onAllocNewPage.notify(p);
            return p;
        }

        // поиск среди уже использованых
        int used = usedpages.size();
        if( used>0 ){
            int rndint = Math.abs(ThreadLocalRandom.current().nextInt());
            int trgt = rndint % used;
            if( dirty(trgt) ){
                saveFastPage(trgt);
            }
            onAllocExistsPage.notify(trgt);
            return trgt;
        }

        // выделение первой
        onAllocNewPage.notify(0);
        return 0;
    }

    public final PageEvent<Pair<Integer,Integer>> onMap = new PageEvent<>();

    @Override
    public int map( int slowPageIndex ){
        if( slowPageIndex<0 ) throw new IllegalArgumentException("slowPageIndex<0");

        int trgt = allocatePage();
        if( trgt<0 )throw new IllegalStateException("can't allocate page");

        usedpages.add(trgt);
        freepages.remove(trgt);

        fast2slow.put(trgt,slowPageIndex);
        slow2fast.put(slowPageIndex,trgt);

        byte[] data = slowData(slowPageIndex);
        fastData(trgt,data);
        dirty(trgt,false);

        onMap.notify(Pair.of(trgt,slowPageIndex));

        return trgt;
    }

    @Override
    public int fastToSlow( int fastPageIndex ){
        return fast2slow.getOrDefault(fastPageIndex,-1);
    }

    @Override
    public int slowToFast( int slowPageIndex ){
        return slow2fast.getOrDefault(slowPageIndex,-1);
    }

    public final PageEvent<Integer> onSaveFastPage = new PageEvent<>();

    public void saveFastPage(int pi){
        int slowPage = fastToSlow(pi);
        if( slowPage>=0 ){
            byte[] data = fastData(pi);
            slowData(slowPage,data);
            dirty(pi,false);
            onSaveFastPage.notify(pi);
        }
    }
}
