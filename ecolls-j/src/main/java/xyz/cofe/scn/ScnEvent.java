package xyz.cofe.scn;

/**
 * Событие изменения SCN объекта
   @param <Owner> Тип владельца SCN
   @param <SCN> Тип счетчика SCN
   @param <CAUSE> Тип причины события
 * @author georgiy
 */
public interface ScnEvent<Owner,SCN extends Comparable<?>,CAUSE> {
    /**
     * Возвращает владельца SCN номера
     * @return владелец чей номер SCN изменился
     */
    Owner getScnOwner();

    /**
     * Предыдущее значение SCN
     * @return предыдущее значение SCN
     */
    SCN getOldScn();

    /**
     * Текущее значение SCN
     * @return Текущее значение SCN
     */
    SCN getCurScn();

    /**
     * Возвращает причину события ScnEvent
     * @return причина или null
     */
    default CAUSE cause(){ return null; }

    /**
     Создание экземпляра
     @param <Owner> Тип владельца SCN
     @param <SCN> Тип счетчика SCN
     @param <CAUSE> Тип причины события
     @param owner владелец чей номер SCN изменился
     @param oldScn предыдущее значение SCN
     @param curScn Текущее значение SCN
     @return Экземпляр объекта
     */
    static <Owner extends Scn,SCN extends Comparable<?>, CAUSE> ScnEvent<Owner,SCN,CAUSE> create(Owner owner, SCN oldScn, SCN curScn){
        return new ScnEvent<Owner, SCN, CAUSE>() {
            @Override
            public Owner getScnOwner() {
                return owner;
            }

            @Override
            public SCN getOldScn() {
                return oldScn;
            }

            @Override
            public SCN getCurScn() {
                return curScn;
            }
        };
    }

    /**
     Создание экземпляра
     @param <Owner> Тип владельца SCN
     @param <SCN> Тип счетчика SCN
     @param <CAUSE> Тип причины события
     @param owner владелец чей номер SCN изменился
     @param oldScn предыдущее значение SCN
     @param curScn Текущее значение SCN
     @param cause Связанное событие
     @return Экземпляр объекта
     */
    static <Owner extends Scn,SCN extends Comparable<?>, CAUSE> ScnEvent<Owner,SCN,CAUSE> create(Owner owner, SCN oldScn, SCN curScn, CAUSE cause){
        return new ScnEvent<Owner, SCN, CAUSE>() {
            @Override
            public Owner getScnOwner() {
                return owner;
            }

            @Override
            public SCN getOldScn() {
                return oldScn;
            }

            @Override
            public SCN getCurScn() {
                return curScn;
            }

            @Override
            public CAUSE cause() {
                return cause;
            }
        };
    }
}

