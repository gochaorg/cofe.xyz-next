package xyz.cofe.scn;

import xyz.cofe.fn.Pair;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Поддержка Long номеров изменений
 */
public class LongScnImpl {
    private static final WeakHashMap<LongScn, AtomicLong> scnValues = new WeakHashMap<>();

    /**
     * Полчение атомарного счетчика изменений
     * @param inst экземпляр объекта
     * @return счетчик экземпляра
     */
    public static AtomicLong getAtomicLong(LongScn inst){
        if( inst == null )throw new IllegalArgumentException( "inst == null" );
        return scnValues.computeIfAbsent(inst,x->new AtomicLong(0));
    }

    /**
     * Увеличение счетчика изменений
     * @param inst экземпляр объекта
     * @param <OWNER> Тип владельца SCN
     * @param <CAUSE> Причина изменения
     * @return Предыдущий и текущий номер изменения
     */
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
