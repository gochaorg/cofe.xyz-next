package xyz.cofe.scn;

import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.ecolls.TripleConsumer;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Поддержка номера изменений
 * @author nt.gocha@gmail.com
 */
public interface Scn<Owner extends Scn<Owner,SCN,CAUSE>,SCN extends Comparable<?>,CAUSE> {
//    private BiConsumer<ScnListener<Owner,SCN,CAUSE>, ScnEvent<Owner,SCN,CAUSE>> invoker(){
//        return (ls,ev) -> {
//            if( ls!=null ){
//                ls.scnEvent(ev);
//            }
//        };
//    }
//
//    private ListenersHelper<ScnListener<Owner,SCN,CAUSE>, ScnEvent<Owner,SCN,CAUSE>> listener(){
//        ListenersHelper lh = ListenersHelper.get(Scn.class,this, invoker());
//        return lh;
//    }

    /**
     * Возвращает активных подписчиков
     * @return подписчики
     */
    default Set<ScnListener<Owner,SCN,CAUSE>> getScnChangedListeners(){
        return ScnImpl.<Owner,SCN,CAUSE>listener(this).getListeners();
    }

    /**
     * Добавляет подписчика на изменения
     * @param listener подписчик
     * @return Интерфейс отписки от уведомлений
     */
    default AutoCloseable addScnChangedListener(ScnListener<Owner,SCN,CAUSE> listener){
        return ScnImpl.<Owner,SCN,CAUSE>listener(this).addListener(listener);
    }

    /**
     * Добавляет подписчика на изменения
     * @param weakLink true - добавить подписчика на weak ссылку / false - как hard ссылку
     * @param listener подписчик
     * @return Интерфейс отписки от уведомлений
     */
    default AutoCloseable addScnChangedListener(boolean weakLink, ScnListener<Owner, SCN,CAUSE> listener){
        return ScnImpl.<Owner,SCN,CAUSE>listener(this).addListener(listener,weakLink);
    }

    /**
     * Удаляет подписчика от уведомлений
     * @param listener подписчик
     */
    default void removeScnChangedListener(ScnListener<Owner,SCN,CAUSE> listener){
        ScnImpl.<Owner,SCN,CAUSE>listener(this).removeListener(listener);
    }

    /**
     * Удаляет всех подписчиков
     */
    default void removeAllScnChangedListeners(){
        ScnImpl.<Owner,SCN,CAUSE>listener(this).removeAllListeners();
    }

    /**
     * Проверяет что указанный подписчик имеется в списках рассылки
     * @param listener подписчик
     * @return true - подписчик имеется в списках рассылки
     */
    default boolean hasScnChangedListener(ScnListener<Owner,SCN,CAUSE> listener){
        return ScnImpl.<Owner,SCN,CAUSE>listener(this).hasListener(listener);
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
        ScnImpl.<Owner,SCN,CAUSE>listener(this).fireEvent(ev);
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
        ScnImpl.listener(this).fireEvent(ev);
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
        ScnImpl.<Owner,SCN,CAUSE>listener(this).withQueue(run);
    }

    /**
     * Запуск блока кода, для предотвращения преждевремменого уведомления подписчиков.
     * Подписчик будут уведомлены по завершению блока кода
     * @param run блок кода
     * @return результат выполнения кода
     */
    default <T> T scn(Supplier<T> run){
        if( run == null )throw new IllegalArgumentException( "run == null" );
        return ScnImpl.<Owner,SCN,CAUSE>listener(this).withQueue(run);
    }
}
