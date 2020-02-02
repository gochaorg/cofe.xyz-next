package xyz.cofe.cbuffer.page;

import xyz.cofe.ecolls.ListenersHelper;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Событие связанные с работой кэширующего буфера
 * @param <T> Тип события
 */
@SuppressWarnings("WeakerAccess")
public class PageEvent<T> implements Consumer<T> {
    /**
     * Помошник при работе с подписчиками
     * @param <ListenerType> Подписчик
     * @param <EventType> Событие
     */
    public static class ExtListenersHelper<ListenerType,EventType> extends ListenersHelper<ListenerType,EventType> {
        /**
         * Конструктор.
         * По умолчанию синхронизация включена, объект по которому происходит синх - this.
         *
         * @param callListFunc Функция вызывающая listener&#x002e;<i>method</i>( <i>collectionEvent</i> );
         */
        public ExtListenersHelper( BiConsumer<ListenerType, EventType> callListFunc ){
            super(callListFunc);
        }

        /**
         * Конструктор.
         *
         * @param callListFunc Функция вызывающая listener&#x002e;<i>method</i>( <i>collectionEvent</i> );
         * @param rwLocks      Блокировки чтения/записи
         */
        public ExtListenersHelper( BiConsumer<ListenerType, EventType> callListFunc, ReadWriteLock rwLocks ){
            super(callListFunc, rwLocks);
        }

        /**
         * Возвращает кол-во подписчиков
         * @return кол-во подписичиков
         */
        public int getListenersCount(){
            return this.listeners.size() + this.weakListeners.size();
        }
    }

    /**
     * Помошник по работе с событиями
     */
    public final ExtListenersHelper<Consumer<? super T>,? super T> helper =
        new ExtListenersHelper<>( (ls, ev) -> {
            if( ls!=null )ls.accept(ev);
        });

    /**
     * Возвращает кол-во подписчиков
     * @return кол-во подписичиков
     */
    public int getListenersCount(){ return helper.getListenersCount(); }

    /**
     * Добавление подписчика
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    public AutoCloseable listen( Consumer<? super T> listener ){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return helper.addListener(listener);
    }

    /**
     * Рассылка уведомления подписчикам
     * @param ev уведомление
     */
    public <A extends T> void notify( A ev ){
        helper.fireEvent(ev);
    }

    /**
     * Рассылка уведомления подписчикам,
     * рассылает при условии, что есть подписчики {@link #getListenersCount()}
     * @param ev генератор уведомления
     */
    public <A extends T> void send( Supplier<A> ev ){
        if( ev==null ) throw new IllegalArgumentException("ev==null");
        if( getListenersCount()>0 ){
            A a = ev.get();
            if( a!=null ){
                helper.fireEvent(a);
            }
        }
    }

    /**
     * Рассылка уведомления подписчикам
     * @param ev уведомление
     */
    @Override
    public void accept( T ev ){
        if( ev!=null ){
            helper.fireEvent(ev);
        }
    }
}
