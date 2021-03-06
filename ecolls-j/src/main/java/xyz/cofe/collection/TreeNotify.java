package xyz.cofe.collection;

import xyz.cofe.ecolls.ListenersHelper;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Подписка на события изменения структуры дерева
 * @param <A> тип узла дерева
 */
public interface TreeNotify<A extends Tree<A>> {
//    Закоментированно для обратной совместимости с java 8
//    private ListenersHelper<TreeEvent.Listener<A>,TreeEvent<A>> listeners(){
//        //return ListenersHelper.<TreeEvent.Listener<A>,TreeEvent<A>>get(TreeNotify.class, this, notifier());
//        ListenersHelper lh = TreeNotifyImpl.listeners(this);
//        return lh;
//    }

    /**
     * Уведомляет о изменении дерева
     * @param event событие
     */
    default void treeNotify(TreeEvent<A> event){
        TreeNotifyImpl.treeNotify(this,event);
    }

    /**
     * Добавляет подписчика на изменения
     * @param ls подписчик
     * @return отписка от уведомлений
     */
    default AutoCloseable addTreeListener(TreeEvent.Listener<A> ls){
        return TreeNotifyImpl.listeners(this).addListener(ls);
    }

    /**
     * Добавляет подписчика на событие определенного типа
     * @param eventClass тип события
     * @param listener подписчик
     * @param <EV> тип события
     * @return отписка от уведомлений
     */
    default <EV extends TreeEvent<A>> AutoCloseable listen( Class<EV> eventClass, Consumer<EV> listener ){
        if( eventClass==null ) throw new IllegalArgumentException("eventClass==null");
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return TreeNotifyImpl.listeners(this).addListener( ev -> {
            if( ev==null )return;
            Class c = ev.getClass();
            if( eventClass.isAssignableFrom(c) ){
                listener.accept((EV)ev);
            }
        });
    }

    /**
     * Добавляет подписчика на изменения
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик
     * @return отписка от уведомлений
     */
    default AutoCloseable addTreeListener(boolean weak,TreeEvent.Listener<A> ls){
        return TreeNotifyImpl.listeners(this).addListener(ls,weak);
    }

    /**
     * Удаляет подписчика
     * @param ls подписчик
     */
    default void removeTreeListener(TreeEvent.Listener<A> ls){
        TreeNotifyImpl.listeners(this).removeListener(ls);
    }

    /**
     * Возвращает всех одписчиков
     * @return подписчики
     */
    default Set<TreeEvent.Listener<A>> getTreeListeners(){
        return (Set)TreeNotifyImpl.listeners(this).getListeners();
    }

    /**
     * Удалеяет всех подписчиков
     */
    default void removeAllTreeListeners(){
        TreeNotifyImpl.listeners(this).removeAllListeners();
    }
}
