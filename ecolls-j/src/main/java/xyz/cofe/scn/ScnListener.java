package xyz.cofe.scn;

/**
 * Подписчик на изменение свойства/значения SCN
 * @author georgiy
 */
public interface ScnListener<Owner,SCN extends Comparable<?>,CAUSE> {
    /**
     * Получения увемления о измении объекта SCN
     * @param scev сообщение
     */
    void scnEvent(ScnEvent<Owner,SCN,CAUSE> scev);
}
