package xyz.cofe.scn;

import xyz.cofe.ecolls.ListenersHelper;

import java.util.function.BiConsumer;

/**
 * Помощь в реализцаии SCN
 */
public class ScnImpl {
    public static <Owner extends Scn<Owner,SCN,CAUSE>,SCN extends Comparable<?>,CAUSE> BiConsumer<ScnListener<Owner,SCN,CAUSE>, ScnEvent<Owner,SCN,CAUSE>> invoker(){
        return (ls,ev) -> {
            if( ls!=null ){
                ls.scnEvent(ev);
            }
        };
    }

    public static <Owner extends Scn<Owner,SCN,CAUSE>,SCN extends Comparable<?>,CAUSE> ListenersHelper<ScnListener<Owner,SCN,CAUSE>, ScnEvent<Owner,SCN,CAUSE>> listener( Object self ){
        if( self==null )throw new IllegalArgumentException("self==null");
        ListenersHelper lh = ListenersHelper.get(Scn.class,self, invoker());
        return lh;
    }
}
