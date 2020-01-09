package xyz.cofe.scn;

import xyz.cofe.ecolls.Pair;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class LongScnImpl {
    private static final WeakHashMap<LongScn, AtomicLong> scnValues = new WeakHashMap<>();
    public static AtomicLong getAtomicLong(LongScn inst){
        if( inst == null )throw new IllegalArgumentException( "inst == null" );
        return scnValues.computeIfAbsent(inst,x->new AtomicLong(0));
    }
    public static <OWNER extends LongScn<OWNER,CAUSE>,CAUSE> Pair<Long,Long> incScn(LongScn<OWNER,CAUSE> inst){
        if( inst==null ) throw new IllegalArgumentException("inst==null");
        Long s1,s2;
        synchronized ( inst ){
            AtomicLong v = LongScnImpl.getAtomicLong(inst);
            s1 = v.get();
            s2 = v.incrementAndGet();
        }
        return Pair.of(s1,s2);
    }
}
