package xyz.cofe.cbuffer.page;

public interface PageLock {
    void writePageLock( int from, int toExc, Runnable code );
    void readPageLock( int from, int toExc, Runnable code );
}
