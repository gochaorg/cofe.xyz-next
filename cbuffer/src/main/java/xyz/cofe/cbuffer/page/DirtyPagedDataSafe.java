package xyz.cofe.cbuffer.page;

public class DirtyPagedDataSafe extends DirtyPagedDataBase<UsedPagesInfo, DirtyPagedState.VolatileState> {
    public DirtyPagedDataSafe(ResizablePages<UsedPagesInfo> pagedData){
        super(pagedData);
    }

    private final DirtyPagedState.VolatileState state = new DirtyPagedState.VolatileState();

    @Override
    protected DirtyPagedState.VolatileState state() {
        return state;
    }
}
