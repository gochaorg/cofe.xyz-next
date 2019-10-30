package xyz.cofe.cbuffer.page;

import xyz.cofe.ecolls.ListenersHelper;

import java.util.function.Consumer;

public class PageEvent<T> implements Consumer<T> {
    public final ListenersHelper<Consumer<T>,T> helper =
        new ListenersHelper<>( (ls, ev) -> {
            if( ls!=null )ls.accept(ev);
        });

    public AutoCloseable listen( Consumer<T> listener ){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return helper.addListener(listener);
    }

    public void notify( T ev ){
        helper.fireEvent(ev);
    }

    @Override
    public void accept( T ev ){
        if( ev!=null ){
            helper.fireEvent(ev);
        }
    }
}
