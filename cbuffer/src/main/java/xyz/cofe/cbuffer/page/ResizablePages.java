package xyz.cofe.cbuffer.page;

/**
 * Изменение кол-ва выделенных страниц
 */
public interface ResizablePages {
    public static class ResizedPages {
        private UsedPagesInfo before;
        private UsedPagesInfo after;
        public ResizedPages(UsedPagesInfo before,UsedPagesInfo after) {
            if( before==null )throw new IllegalArgumentException("before==null");
            if( after==null )throw new IllegalArgumentException("after==null");
            this.before = before;
            this.after = after;
        }
        public UsedPagesInfo before(){ return before; }
        public UsedPagesInfo after(){ return after; }
    }

    /**
     * Изменение кол-ва выделенных страниц
     * @param pages целевое кол-во страниц
     * @return Сколько было и сколько стало памяти
     */
    public ResizedPages resizePages(int pages);
}
