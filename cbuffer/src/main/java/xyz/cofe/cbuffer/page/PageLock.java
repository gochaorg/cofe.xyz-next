package xyz.cofe.cbuffer.page;

/**
 * Блокировка диапазона страниц
 */
public interface PageLock {
    /**
     * Блокировка диапазона страниц для записи - эксклюзивная блокировка
     * @param from начало диапазона
     * @param toExc (исключительно) конец диапазона
     * @param code код исполняемый во время блокировки
     */
    void writePageLock( int from, int toExc, Runnable code );

    /**
     * Блокировка диапазона страниц для чтения - shared блокировка
     * @param from начало диапазона
     * @param toExc (исключительно) конец диапазона
     * @param code код исполняемый во время блокировки
     */
    void readPageLock( int from, int toExc, Runnable code );
}
