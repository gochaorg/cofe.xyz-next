package xyz.cofe.scn;

import xyz.cofe.fn.Pair;

/**
 * Поддержка номера изменений объекта
 * @param <OWNER> Тип владельца объекта
 */
public interface LongScn<OWNER extends LongScn<OWNER,CAUSE>,CAUSE> extends Scn<OWNER,Long,CAUSE> {
    /**
     * Получение текущего номера изменений
     * @return номер изменений
     */
    @Override
    default Long scn(){
        return LongScnImpl.getAtomicLong(this).get();
    }

    /**
     * Получение следующего номера SCN
     * @return предыдущий и текущий номер SCN
     */
    default Pair<Long,Long> nextscn(){
        Pair<Long,Long> scnpair = LongScnImpl.incScn(this);
        fireScnChanged(scnpair.a(),scnpair.b());
        return scnpair;
    }

    /**
     * Получение следующего номера SCN
     * @param cause причина изменения
     * @param <CAUSE> причина изменения
     * @return предыдущий и текущий номер SCN
     */
    default <CAUSE> Pair<Long,Long> nextscn(CAUSE cause){
        Pair<Long,Long> scnpair = LongScnImpl.incScn(this);
        fireScnChanged(scnpair.a(),scnpair.b(),cause);
        return scnpair;
    }
}
