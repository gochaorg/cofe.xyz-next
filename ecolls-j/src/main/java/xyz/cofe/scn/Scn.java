package xyz.cofe.scn;

import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.fn.TripleConsumer;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Поддержка номера изменений
 * @author nt.gocha@gmail.com
 */
public interface Scn<Owner extends Scn<Owner,SCN,CAUSE>,SCN extends Comparable<?>,CAUSE> {
    /**
     * Возвращает помощника издателя для поддержи событий
     * @return помошник издателя
     */
    default ListenersHelper<ScnListener<Owner,SCN,CAUSE>, ScnEvent<Owner,SCN,CAUSE>> scnListenerHelper(){
        return ScnImpl.<Owner,SCN,CAUSE>listener(this);
    }

    /**
     * Возвращает активных подписчиков
     * @return подписчики
     */
    default Set<ScnListener<Owner,SCN,CAUSE>> getScnChangedListeners(){
        return scnListenerHelper().getListeners();
    }

    /**
     * Добавляет подписчика на изменения
     * @param listener подписчик
     * @return Интерфейс отписки от уведомлений
     */
    default AutoCloseable addScnChangedListener(ScnListener<Owner,SCN,CAUSE> listener){
        return scnListenerHelper().addListener(listener);
    }

    /**
     * Добавляет подписчика на изменения
     * @param weakLink true - добавить подписчика на weak ссылку / false - как hard ссылку
     * @param listener подписчик
     * @return Интерфейс отписки от уведомлений
     */
    default AutoCloseable addScnChangedListener(boolean weakLink, ScnListener<Owner, SCN,CAUSE> listener){
        return scnListenerHelper().addListener(listener,weakLink);
    }

    /**
     * Удаляет подписчика от уведомлений
     * @param listener подписчик
     */
    default void removeScnChangedListener(ScnListener<Owner,SCN,CAUSE> listener){
        scnListenerHelper().removeListener(listener);
    }

    /**
     * Удаляет всех подписчиков
     */
    default void removeAllScnChangedListeners(){
        scnListenerHelper().removeAllListeners();
    }

    /**
     * Проверяет что указанный подписчик имеется в списках рассылки
     * @param listener подписчик
     * @return true - подписчик имеется в списках рассылки
     */
    default boolean hasScnChangedListener(ScnListener<Owner,SCN,CAUSE> listener){
        return scnListenerHelper().hasListener(listener);
    }

    /**
     * Возвращает текущий номер изменений
     * @return текущий номер изменения
     */
    SCN scn();

    /**
     * Рассылает подписчикам уведомление о измении номера SCN
     * @param from предыдущее значение
     * @param to текущее значение
     */
    default void fireScnChanged(SCN from,SCN to){
        if( from == null )throw new IllegalArgumentException( "from == null" );
        if( to == null )throw new IllegalArgumentException( "to == null" );
        ScnEvent ev = ScnEvent.create(
            this,
            from,
            to);
        scnListenerHelper().fireEvent(ev);
    }

    /**
     * Рассылает подписчикам уведомление о измении номера SCN
     * @param from предыдущее значение
     * @param to текущее значение
     * @param cause причина изменения номера (событие)
     * @param <CAUSE> тип причины изменения номера
     */
    default <CAUSE> void fireScnChanged(SCN from,SCN to, CAUSE cause){
        if( from == null )throw new IllegalArgumentException( "from == null" );
        if( to == null )throw new IllegalArgumentException( "to == null" );
        ScnEvent ev = ScnEvent.create(
            this,
            from,
            to,
            cause);
        scnListenerHelper().fireEvent(ev);
    }

    /**
     * Добавление подписчика на изменение номера scn
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    default AutoCloseable onScn(TripleConsumer<SCN,SCN,CAUSE> listener){
        if( listener == null )throw new IllegalArgumentException( "listener == null" );
        return addScnChangedListener((ScnListener<Owner, SCN, CAUSE>) scev->listener.accept(scev.getOldScn(),scev.getCurScn(),scev.cause()));
    }

    /**
     * Запуск блока кода, для предотвращения преждевремменого уведомления подписчиков.
     * Подписчик будут уведомлены по завершению блока кода
     * @param run блок кода
     */
    default void scn(Runnable run){
        if( run == null )throw new IllegalArgumentException( "run == null" );
        scnListenerHelper().withQueue(run);
    }

    /**
     * Запуск блока кода, для предотвращения преждевремменого уведомления подписчиков.
     * Подписчик будут уведомлены по завершению блока кода
     * @param <T> тип результата
     * @param run блок кода
     * @return результат выполнения кода
     */
    default <T> T scn(Supplier<T> run){
        if( run == null )throw new IllegalArgumentException( "run == null" );
        return scnListenerHelper().withQueue(run);
    }
}
