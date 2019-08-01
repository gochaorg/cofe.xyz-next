package xyz.cofe.scn;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class LongScnImpl {
    private static final WeakHashMap<LongScn, AtomicLong> scnValues = new WeakHashMap<>();
    public static AtomicLong getAtomicLong(LongScn inst){
        if( inst == null )throw new IllegalArgumentException( "inst == null" );
        return scnValues.computeIfAbsent(inst,x->new AtomicLong(0));
    }
}
