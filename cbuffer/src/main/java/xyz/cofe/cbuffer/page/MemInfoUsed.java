package xyz.cofe.cbuffer.page;

import java.math.BigInteger;

//region memInfo, memoryInfo(),
class MemInfoUsed implements UsedPagesInfo, Capacity {
    private final MemPagedData memPagedData;

    public MemInfoUsed(MemPagedData memPagedData) {
        this.memPagedData = memPagedData;
    }

    @Override
    public int pageCount() {
        int pc = memPagedData.dataSize / memPagedData.pageSize;
        int pc_d = memPagedData.dataSize % memPagedData.pageSize;
        return pc_d > 0 ? pc + 1 : pc;
    }

    @Override
    public int lastPageSize() {
        return memPagedData.dataSize % memPagedData.pageSize;
    }

    @Override
    public int pageSize() {
        return memPagedData.pageSize;
    }

    @Override
    public BigInteger capacity() {
        return BigInteger.valueOf(memPagedData.buffer.length);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public UsedPagesInfo clone() {
        return new MemInfoUsedClone(this);
    }
}
