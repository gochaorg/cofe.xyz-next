package xyz.cofe.cbuffer.page;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

public interface PageListener {
    public void pageEvent(PageEvent event);

    public static class PageListenerSupport {
        private final Collection<PageListener> listeners = new CopyOnWriteArraySet<>();
        public void addListener(PageListener listener){
            if( listener==null )throw new IllegalArgumentException("listener==null");
            listeners.add(listener);
        }
        public void removeListener(PageListener listener){
            if( listener==null )throw new IllegalArgumentException("listener==null");
            listeners.remove(listener);
        }
        public boolean hasListener(PageListener listener){
            if( listener==null )throw new IllegalArgumentException("listener==null");
            return listeners.contains(listener);
        }
        public void fire(PageEvent event){
            for( var ls : listeners ){
                ls.pageEvent(event);
            }
        }
    }
}
