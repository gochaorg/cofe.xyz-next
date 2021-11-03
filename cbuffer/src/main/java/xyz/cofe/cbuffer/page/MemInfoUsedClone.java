package xyz.cofe.cbuffer.page;

import java.math.BigInteger;

class MemInfoUsedClone implements UsedPagesInfo, Capacity {
    protected BigInteger capacity;
    protected int pageCount;
    protected int pageSize;
    protected int lastPageSize;

    public MemInfoUsedClone(BigInteger capacity, int pageCount, int pageSize, int lastPageSize) {
        this.capacity = capacity;
        this.pageCount = pageCount;
        this.pageSize = pageSize;
        this.lastPageSize = lastPageSize;
    }

    public MemInfoUsedClone(MemInfoUsedClone sample) {
        if (sample == null) throw new IllegalArgumentException("sample==null");
        this.capacity = sample.capacity;
        this.pageCount = sample.pageCount;
        this.pageSize = sample.pageSize;
        this.lastPageSize = sample.lastPageSize;
    }

    public MemInfoUsedClone(MemInfoUsed sample) {
        if (sample == null) throw new IllegalArgumentException("sample==null");
        this.capacity = sample.capacity();
        this.pageCount = sample.pageCount();
        this.pageSize = sample.pageSize();
        this.lastPageSize = sample.lastPageSize();
    }

    @Override
    public BigInteger capacity() {
        return capacity;
    }

    @Override
    public int pageCount() {
        return pageCount;
    }

    @Override
    public int lastPageSize() {
        return lastPageSize;
    }

    @Override
    public int pageSize() {
        return pageSize;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public UsedPagesInfo clone() {
        return new MemInfoUsedClone(this);
    }
}
