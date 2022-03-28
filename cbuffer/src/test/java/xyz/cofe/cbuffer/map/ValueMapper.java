package xyz.cofe.cbuffer.map;

import xyz.cofe.cbuffer.ContentBuffer;

public abstract class ValueMapper {
    // TODO хз что делать с variable size (aka String)
    protected int offset;
    public int offset(){ return offset; }

    // TODO хз что делать с variable size (aka String)
    protected int valueSize;
    public int valueSize(){ return valueSize; }

    public abstract void read(Object obj, byte[] buff, int offset);
    public abstract void write(byte[] buff, int offset, Object obj);
}
