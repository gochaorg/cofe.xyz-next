package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.ContentBuffer;

public class CopyOnWritePages implements PagedData<UsedPagesInfo> {
    protected PagedData<UsedPagesInfo> source;
    protected ContentBuffer snapshot;

    @Override
    public UsedPagesInfo memoryInfo() {
        return null;
    }

    @Override
    public byte[] readPage(int page) {
        return new byte[0];
    }

    @Override
    public void writePage(int page, byte[] data) {
    }
}
