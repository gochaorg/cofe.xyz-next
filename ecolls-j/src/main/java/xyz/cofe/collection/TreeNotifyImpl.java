package xyz.cofe.collection;

import xyz.cofe.ecolls.ListenersHelper;

import java.util.function.BiConsumer;

public class TreeNotifyImpl {
    public static BiConsumer<TreeEvent.Listener,TreeEvent> notifier(){
        return (ls,ev) -> {
            if( ls!=null )ls.treeEvent(ev);
        };
    }

    public static ListenersHelper<TreeEvent.Listener,TreeEvent> listeners(Object inst){
        if( inst == null )throw new IllegalArgumentException("inst==null");
        return ListenersHelper.<TreeEvent.Listener,TreeEvent>get(TreeNotify.class, inst, notifier());
    }

    public static void treeNotify(Object inst, TreeEvent event){
        if( inst == null )throw new IllegalArgumentException("inst==null");
        listeners(inst).fireEvent(event);

        Object prnt = inst instanceof GetTreeParent ? ((GetTreeParent)inst).getParent() : null;
        if( prnt instanceof TreeNotify ){
            ((TreeNotify)prnt).treeNotify(event);
        }
    }
}
