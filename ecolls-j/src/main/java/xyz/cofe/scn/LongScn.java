package xyz.cofe.scn;

import xyz.cofe.ecolls.Pair;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Поддержка номера изменений объекта
 * @param <OWNER> Тип владельца объекта
 */
public interface LongScn<OWNER extends LongScn<OWNER,CAUSE>,CAUSE> extends Scn<OWNER,Long,CAUSE> {
    @Override
    default Long scn(){
        return LongScnImpl.getAtomicLong(this).get();
    }

    private Pair<Long,Long> incScn(){
        Long s1,s2;
        synchronized ( this ){
            AtomicLong v = LongScnImpl.getAtomicLong(this);
            s1 = v.get();
            s2 = v.incrementAndGet();
        }
        return Pair.of(s1,s2);
    }

    default Pair<Long,Long> nextscn(){
        Pair<Long,Long> scnpair = incScn();
        fireScnChanged(scnpair.a(),scnpair.b());
        return scnpair;
    }

    default <CAUSE> Pair<Long,Long> nextscn(CAUSE cause){
        Pair<Long,Long> scnpair = incScn();
        fireScnChanged(scnpair.a(),scnpair.b(),cause);
        return scnpair;
    }
}
