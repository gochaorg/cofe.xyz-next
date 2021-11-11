package xyz.cofe.cbuffer.page;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public interface DirtyPagedState {
    public interface PageState {
        int page();
        long flushTime();
        long writeTime();
        long readTime();
        default boolean dirty(){
            long ft = flushTime();
            long wt = writeTime();
            return ft <= wt;
        }
        static PageState create(int page, long readTime, long writeTime, long flushTime){
            return new PageState() {
                @Override
                public int page() {
                    return page;
                }

                @Override
                public long flushTime() {
                    return flushTime;
                }

                @Override
                public long writeTime() {
                    return writeTime;
                }

                @Override
                public long readTime() {
                    return readTime;
                }
            };
        }
    }

    int pageCount();
    Optional<PageState> page(int page);

    /**
     * Отмечает время вызова {@link DirtyPagedDataBase#flushPage(int)}
     */
    void flushed(int page, long time);

    /**
     * Отмечает время вызова {@link DirtyPagedDataBase#writePage(int, byte[])}
     */
    void written(int page, long time);

    /**
     * Отмечает время вызова {@link DirtyPagedDataBase#readPage(int)}
     */
    void read(int page,long time);

    void resizePages(int pages);
    void each(Consumer<PageState> pageState);

    public static class NonThreadSafe implements DirtyPagedState {
        @Override
        public int pageCount() {
            long[] pages = flushTime;
            return pages!=null ? pages.length : 0;
        }

        @Override
        public Optional<PageState> page(int page) {
            long[] flush_t = flushTime;
            long[] write_t = writeTime;
            long[] read_t = readTime;
            if( flush_t==null || write_t==null || read_t==null )return Optional.empty();
            if( flush_t.length!=write_t.length || write_t.length!=read_t.length )return Optional.empty();
            if( page>=flush_t.length || page<0 )return Optional.empty();
            return Optional.of( PageState.create(page, read_t[page], write_t[page], flush_t[page]) );
        }

        @Override
        public void each(Consumer<PageState> pageState) {
            if( pageState==null )throw new IllegalArgumentException( "pageState==null" );
            long[] flush = flushTime;
            long[] write = writeTime;
            long[] read = readTime;
            if( flush==null || write==null || read==null )return;
            if( flush.length!=write.length || write.length!=read.length )return;
            for( int i=0; i<flush.length; i++ ){
                pageState.accept(PageState.create(i, read[i], write[i], flush[i]));
            }
        }

        //region flushTime
        protected long[] flushTime = new long[0];

        @Override
        public void flushed(int page, long time) {
            long[] flushTime = this.flushTime;
            if( flushTime==null )throw new IllegalStateException( "flushTime==null" );

            if( page<0 )throw new IllegalArgumentException( "page<0" );
            if( page>=flushTime.length )throw new IllegalArgumentException( "page>=flushTime.length" );

            flushTime[page] = time;
        }
        //endregion
        //region writeTime
        protected long[] writeTime = new long[0];

        @Override
        public void written(int page, long time){
            long[] times = this.writeTime;
            if( times==null )throw new IllegalStateException( "writeTime==null" );

            if( page<0 )throw new IllegalArgumentException( "page<0" );
            if( page>=times.length )throw new IllegalArgumentException( "page>=writeTime.length" );

            times[page] = time;
        }
        //endregion
        //region readTime
        protected long[] readTime = new long[0];

        @Override
        public void read(int page,long time){
            long[] times = this.readTime;
            if( times==null )throw new IllegalStateException( "readTime==null" );

            if( page<0 )throw new IllegalArgumentException( "page<0" );
            if( page>=times.length )throw new IllegalArgumentException( "page>=readTime.length" );

            times[page] = time;
        }
        //endregion

        @Override
        public void resizePages(int pages){
            if( pages<0 )throw new IllegalArgumentException( "pages<0" );

            long[] flushTime = this.flushTime;
            long[] writeTime = this.writeTime;
            long[] readTime = this.readTime;

            int before = flushTime!=null ? flushTime.length : 0;
            int null_c = (flushTime==null ? 1 : 0) + (writeTime==null ? 1 : 0) + (readTime==null ? 1 : 0);
            if( !(null_c==0 || null_c==3) )throw new IllegalStateException("bug!! null ref (flushTime|writeTime|readTime) count not match 0|3");

            //noinspection ConstantConditions
            if( flushTime!=null && writeTime!=null && readTime!=null &&
                !((flushTime.length == readTime.length) && (readTime.length == writeTime.length))
            ){
                throw new IllegalStateException("bug! internal arrays length, not matched");
            }

            flushTime = flushTime != null ? Arrays.copyOf(flushTime, pages) : new long[pages];
            writeTime = writeTime != null ? Arrays.copyOf(writeTime, pages) : new long[pages];
            readTime = readTime != null ? Arrays.copyOf(readTime, pages) : new long[pages];

            int extCnt = pages - before;

            if( extCnt>0 ){
                for( int i=before; i<pages; i++ ){
                    flushTime[i] = 0;
                    writeTime[i] = 0;
                    readTime[i] = 0;
                }
            }

            this.flushTime = flushTime;
            this.writeTime = writeTime;
            this.readTime = readTime;
        }
    }
    public static class VolatileState implements DirtyPagedState {
        @Override
        public int pageCount() {
            long[] pages = flushTime;
            return pages!=null ? pages.length : 0;
        }

        @Override
        public Optional<PageState> page(int page) {
            long[] flush_t = flushTime;
            long[] write_t = writeTime;
            long[] read_t = readTime;
            if( flush_t==null || write_t==null || read_t==null )return Optional.empty();
            if( flush_t.length!=write_t.length || write_t.length!=read_t.length )return Optional.empty();
            if( page>=flush_t.length || page<0 )return Optional.empty();
            return Optional.of( PageState.create(page, read_t[page], write_t[page], flush_t[page]) );
        }

        @Override
        public void each(Consumer<PageState> pageState) {
            if( pageState==null )throw new IllegalArgumentException( "pageState==null" );
            long[] flush = flushTime;
            long[] write = writeTime;
            long[] read = readTime;
            if( flush==null || write==null || read==null )return;
            if( flush.length!=write.length || write.length!=read.length )return;
            for( int i=0; i<flush.length; i++ ){
                pageState.accept(PageState.create(i, read[i], write[i], flush[i]));
            }
        }

        //region flushTime
        protected volatile long[] flushTime = new long[0];

        @Override
        public void flushed(int page, long time) {
            long[] flushTime = this.flushTime;
            if( flushTime==null )throw new IllegalStateException( "flushTime==null" );

            if( page<0 )throw new IllegalArgumentException( "page<0" );
            if( page>=flushTime.length )throw new IllegalArgumentException( "page>=flushTime.length" );

            flushTime[page] = time;
        }
        //endregion
        //region writeTime
        protected volatile long[] writeTime = new long[0];

        @Override
        public void written(int page, long time){
            long[] times = this.writeTime;
            if( times==null )throw new IllegalStateException( "writeTime==null" );

            if( page<0 )throw new IllegalArgumentException( "page<0" );
            if( page>=times.length )throw new IllegalArgumentException( "page>=writeTime.length" );

            times[page] = time;
        }
        //endregion
        //region readTime
        protected volatile long[] readTime = new long[0];

        @Override
        public void read(int page,long time){
            long[] times = this.readTime;
            if( times==null )throw new IllegalStateException( "readTime==null" );

            if( page<0 )throw new IllegalArgumentException( "page<0" );
            if( page>=times.length )throw new IllegalArgumentException( "page>=readTime.length" );

            times[page] = time;
        }
        //endregion

        @Override
        public synchronized void resizePages(int pages){
            if( pages<0 )throw new IllegalArgumentException( "pages<0" );

            long[] flushTime = this.flushTime;
            long[] writeTime = this.writeTime;
            long[] readTime = this.readTime;

            int before = flushTime!=null ? flushTime.length : 0;
            int null_c = (flushTime==null ? 1 : 0) + (writeTime==null ? 1 : 0) + (readTime==null ? 1 : 0);
            if( !(null_c==0 || null_c==3) )throw new IllegalStateException("bug!! null ref (flushTime|writeTime|readTime) count not match 0|3");

            //noinspection ConstantConditions
            if( flushTime!=null && writeTime!=null && readTime!=null &&
                !((flushTime.length == readTime.length) && (readTime.length == writeTime.length))
            ){
                throw new IllegalStateException("bug! internal arrays length, not matched");
            }

            flushTime = flushTime != null ? Arrays.copyOf(flushTime, pages) : new long[pages];
            writeTime = writeTime != null ? Arrays.copyOf(writeTime, pages) : new long[pages];
            readTime = readTime != null ? Arrays.copyOf(readTime, pages) : new long[pages];

            int extCnt = pages - before;

            if( extCnt>0 ){
                for( int i=before; i<pages; i++ ){
                    flushTime[i] = 0;
                    writeTime[i] = 0;
                    readTime[i] = 0;
                }
            }

            this.flushTime = flushTime;
            this.writeTime = writeTime;
            this.readTime = readTime;
        }
    }
}
