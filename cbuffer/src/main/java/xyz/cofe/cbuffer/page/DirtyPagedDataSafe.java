package xyz.cofe.cbuffer.page;

public class DirtyPagedDataSafe extends DirtyPagedDataBase<UsedPagesInfo, DirtyPagedState.VolatileState> {
    public DirtyPagedDataSafe(ResizablePages<UsedPagesInfo> pagedData){
        super(pagedData);
    }

    private volatile DirtyPagedState.VolatileState state;

    @Override
    protected DirtyPagedState.VolatileState state() {
        if( state!=null )return state;
        synchronized (this) {
            if( state!=null )return state;
            state = new DirtyPagedState.VolatileState();
            return state;
        }
    }
}
