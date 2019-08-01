package xyz.cofe.collection;

import xyz.cofe.ecolls.ListenersHelper;

import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Подписка на события изменения структуры дерева
 * @param <A> тип узла дерева
 */
public interface TreeNotify<A extends Tree<A>> {
    private ListenersHelper<TreeEvent.Listener<A>,TreeEvent<A>> listeners(){
        //return ListenersHelper.<TreeEvent.Listener<A>,TreeEvent<A>>get(TreeNotify.class, this, notifier());
        ListenersHelper lh = TreeNotifyImpl.listeners(this);
        return lh;
    }

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
        return listeners().addListener(ls);
    }

    /**
     * Добавляет подписчика на изменения
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик
     * @return отписка от уведомлений
     */
    default AutoCloseable addTreeListener(boolean weak,TreeEvent.Listener<A> ls){
        return listeners().addListener(ls,weak);
    }

    /**
     * Удаляет подписчика
     * @param ls подписчик
     */
    default void removeTreeListener(TreeEvent.Listener<A> ls){
        listeners().removeListener(ls);
    }

    /**
     * Возвращает всех одписчиков
     * @return подписчики
     */
    default Set<TreeEvent.Listener<A>> getTreeListeners(){
        return listeners().getListeners();
    }

    /**
     * Удалеяет всех подписчиков
     */
    default void removeAllTreeListeners(){
        listeners().removeAllListeners();
    }
}
