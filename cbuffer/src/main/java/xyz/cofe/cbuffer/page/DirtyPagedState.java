package xyz.cofe.cbuffer.page;

public interface DirtyPagedState {
    /**
     * Отмечает время вызова {@link DirtyPagedDataBase#flushPage(int)}
     */
    long[] flushTime();
    void flushTime(long[] times);

    /**
     * Отмечает время вызова {@link DirtyPagedDataBase#writePage(int, byte[])}
     */
    long[] writeTime();
    void writeTime(long[] times);

    /**
     * Отмечает время вызова {@link DirtyPagedDataBase#readPage(int)}
     */
    long[] readTime();
    void readTime(long[] times);

    public static class NonThreadSafe implements DirtyPagedState {
        protected long[] flushTime;

        @Override
        public long[] flushTime() {
            return flushTime;
        }

        @Override
        public void flushTime(long[] times) {
            flushTime = times;
        }

        protected long[] writeTime;

        @Override
        public long[] writeTime() {
            return writeTime;
        }

        @Override
        public void writeTime(long[] times) {
            writeTime = times;
        }

        protected long[] readTime;

        @Override
        public long[] readTime() {
            return readTime;
        }
        @Override
        public void readTime(long[] times) {
            readTime = times;
        }
    }
    public static class VolatileState implements DirtyPagedState {
        protected volatile long[] flushTime;

        @Override
        public long[] flushTime() {
            return flushTime;
        }

        @Override
        public void flushTime(long[] times) {
            flushTime = times;
        }

        protected volatile long[] writeTime;

        @Override
        public long[] writeTime() {
            return writeTime;
        }

        @Override
        public void writeTime(long[] times) {
            writeTime = times;
        }

        protected volatile long[] readTime;

        @Override
        public long[] readTime() {
            return readTime;
        }
        @Override
        public void readTime(long[] times) {
            readTime = times;
        }    }
}
