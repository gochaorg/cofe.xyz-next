package xyz.cofe.ecolls;

import xyz.cofe.fn.Consumer1;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Шаблон "обсервер" / подписчик-издатель.
 *
 * <p>
 * Создан для быстрой передачи сообщений, не использует средства синхронизации -
 * по этому он потока - не безопасен <b>not thread safe</b>.
 *
 * <p>
 * Коллекция не допускает наличие null ссылок.
 *
 * @param <A> тип события
 */
public class Observers<A> extends ArrayList<Consumer1<Observers.Event<A>>> {
    /**
     * Событие
     * @param <A> тип события
     */
    public static class Event<A> {
        /**
         * Издатель
         */
        public final Observers<A> observers;

        /**
         * Событие
         */
        public final A event;

        /**
         * подписчик
         */
        public final Consumer1<Observers.Event<A>> listener;

        /**
         * Коллекция подписчиков которые будет отсоединены после этого сообщения
         */
        public final Collection<Consumer1<Observers.Event<A>>> removeListeners;

        /**
         * Конструктор
         * @param observers подписчики
         * @param event событие
         * @param listener подписчик
         * @param removeListeners Коллекция подписчиков которые будет отсоединены после этого сообщения
         */
        public Event(Observers<A> observers, A event, Consumer1<Observers.Event<A>> listener, Collection<Consumer1<Observers.Event<A>>> removeListeners) {
            this.observers = observers;
            this.event = event;
            this.listener = listener;
            this.removeListeners = removeListeners;
        }

        /**
         * Отписка от уведомлений текущего подписчика {@link #listener}
         */
        public void unsubscribe(){
            if( listener!=null && removeListeners!=null ){
                removeListeners.add(listener);
            }
        }
    }

    /**
     * Уведомление о событии
     * @param a событие
     */
    public void fire( A a ){
        Set<Consumer1<Event<A>>> removeSet = new LinkedHashSet<>();
        //noinspection unchecked
        for( Consumer1<Event<A>> el : this.<Consumer1<Event<A>>>toArray(new Consumer1[]{}) ){
            if( el!=null ){
                el.accept(new Event<>(this,a,el,removeSet));
            }
        }
        removeAll(removeSet);
    }

    /**
     * Подписка на события
     * @param listener подписчик
     * @return Отписка от уведомлений
     */
    public Runnable listen( Consumer1<Observers.Event<A>> listener ){
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        add( listener );
        WeakReference<Consumer1<Observers.Event<A>>> ref = new WeakReference<Consumer1<Observers.Event<A>>>(listener);
        return ()->{
            Consumer1<Observers.Event<A>> r = ref.get();
            if( removeIf( c -> c==r ) ){
                //System.out.println("removed listener");
            }
        };
    }

    /**
     * Замена элементов коллекции, не допускается замена на null ссылку
     * @param operator оператор замены
     */
    @Override
    public void replaceAll(UnaryOperator<Consumer1<Observers.Event<A>>> operator) {
        UnaryOperator<Consumer1<Observers.Event<A>>> op = new UnaryOperator<Consumer1<Observers.Event<A>>>(){
            @Override
            public Consumer1<Observers.Event<A>> apply(Consumer1<Observers.Event<A>> aConsumer1) {
                Consumer1<Observers.Event<A>> res = operator.apply(aConsumer1);
                if( res==null )throw new IllegalArgumentException( "res==null" );
                return res;
            }
        };
        super.replaceAll(operator);
    }

    /**
     * Замена элемента коллекции, не допускается замена на null ссылку
     * @param index индекс элемента
     * @param observer замена
     * @return
     */
    @Override
    public Consumer1<Observers.Event<A>> set(int index, Consumer1<Observers.Event<A>> observer) {
        if( observer==null )throw new IllegalArgumentException( "observer==null" );
        return super.set(index, observer);
    }

    /**
     * Добавление подписчика, не допускается добавление null ссылки
     * @param observer подписчик, не допускается null
     * @return true - успешное добавление
     */
    @Override
    public boolean add(Consumer1<Observers.Event<A>> observer) {
        return super.add(observer);
    }

    /**
     * Добавление подписчика, не допускается добавление null ссылки
     * @param index позиция вставки, 0 - начало списка
     * @param observer подписчик, не допускается null
     */
    @Override
    public void add(int index, Consumer1<Observers.Event<A>> observer) {
        super.add(index, observer);
    }

    /**
     * Добавление подписчика, не допускается добавление null ссылки
     * @param c подписчики, не допускается null
     * @return true - успешное добавление
     */
    @Override
    public boolean addAll(Collection<? extends Consumer1<Observers.Event<A>>> c) {
        if( c==null )throw new IllegalArgumentException( "c==null" );
        if(c.stream().anyMatch(Objects::nonNull)){
            throw new IllegalArgumentException( "collection contains null item" );
        }
        return super.addAll(c);
    }

    /**
     * Добавление подписчика, не допускается добавление null ссылки
     * @param index позиция вставки, 0 - начало списка
     * @param c подписчики, не допускается null
     * @return true - успешное добавление
     */
    @Override
    public boolean addAll(int index, Collection<? extends Consumer1<Observers.Event<A>>> c) {
        if( c==null )throw new IllegalArgumentException( "c==null" );
        if(c.stream().anyMatch(Objects::nonNull)){
            throw new IllegalArgumentException( "collection contains null item" );
        }
        return super.addAll(index, c);
    }
}