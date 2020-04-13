package xyz.cofe.ecolls;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Помошник в реализации Listener. <br>
 * Обеспечивает синхронность при вызове методов:
 * <ul>
 * <li>getListeners</li>
 * <li>addListener</li>
 * <li>removeListener</li>
 * <li>fireEvent</li>
 * </ul>
 * Умеет хранить как hard так и soft ссылки на listener-ы
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @param <ListenerType> Тип издателья
 * @param <EventType> Тип подписчика
 */
public class ListenersHelper<ListenerType,EventType> implements ReadWriteLockSupport, ReadWriteLockProperty {
    //<editor-fold desc="getReadLock() / getWriteLock()">
    @Override
    public Lock getReadLock() {
        return ReadWriteLockProperty.super.getReadLock();
    }

    @Override
    public Lock getWriteLock() {
        return ReadWriteLockProperty.super.getWriteLock();
    }
    //</editor-fold>

    //<editor-fold desc="ListenersHelper()">
    /**
     * Функция вызывающая listener&#x002e;<i>method</i>( <i>collectionEvent</i> );
     */
    protected BiConsumer<ListenerType,EventType> callListener = null;

    /**
     * Конструктор.
     * По умолчанию синхронизация включена, объект по которому происходит синх - this.
     * @param callListFunc Функция вызывающая listener&#x002e;<i>method</i>( <i>collectionEvent</i> );
     */
    public ListenersHelper(BiConsumer<ListenerType,EventType> callListFunc){
        if( callListFunc==null )throw new IllegalArgumentException( "callListFunc==null" );
        this.callListener = callListFunc;
    }

    /**
     * Конструктор.
     * @param callListFunc Функция вызывающая listener&#x002e;<i>method</i>( <i>collectionEvent</i> );
     * @param rwLocks Блокировки чтения/записи
     */
    public ListenersHelper(BiConsumer<ListenerType,EventType> callListFunc, ReadWriteLock rwLocks){
        if( callListFunc==null )throw new IllegalArgumentException( "callListFunc==null" );
        this.callListener = callListFunc;
        if( rwLocks!=null ){
            // readLock = rwLocks.readLock();
            // writeLock = rwLocks.writeLock();
            setReadWriteLock(rwLocks);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="listeners - Hard ссылки">
    /**
     * Hard ссылки на подписчиков
     */
    protected HashSet<ListenerType> listeners = new LinkedHashSet<>();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="weakListeners - Soft/Weak ссылки">
    /**
     * Soft/Weak ссылки на подписчиков
     */
    protected WeakHashMap<ListenerType,Object> weakListeners = new WeakHashMap<ListenerType,Object>();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hasListener(l):boolean - Проверка наличия подписчика">
    /**
     * Проверка наличия подписчика в списке обработки
     * @param listener подписчик
     * @return true - есть в списке обработки
     */
    public boolean hasListener( ListenerType listener ){
        if( listener==null )return false;
        return readLock(()->{
            if( listeners.contains(listener) )return true;
            return weakListeners.containsKey(listener);
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="getListeners():Set<ListenerType> - подписчики">
    /**
     * Получение списка подписчиков
     * @return подписчики
     */
    public Set<ListenerType> getListeners() {
        return readLock(()->{
            HashSet<ListenerType> res = new LinkedHashSet<ListenerType>();
            res.addAll(listeners);
            res.addAll(weakListeners.keySet());
            return res;
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="addListener(Listener):Closeable - Добавление подписчика">
    /**
     * Добавление подписчика.
     * @param listener Подписчик.
     * @return Интерфес для отсоединения подписчика
     */
    public AutoCloseable addListener(ListenerType listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener==null");
        }

        return writeLock(()->addListener0(listener));
    }

    private AutoCloseable createRemover(ListenerType listener){
        WeakReference<ListenerType> ref = new WeakReference<>(listener);
        return ()->{
            ListenerType ls = ref.get();
            if( ls==null )return;

            ListenersHelper.this.removeListener(ls);
            ref.clear();
        };
    }

    private AutoCloseable addListener0(ListenerType listener) {
        listeners.add(listener);
        return createRemover(listener);
    }

    /**
     * Добавление подписчика.
     * @param listener Подписчик.
     * @param weakLink true - добавить как weak ссылку / false - как hard ссылку
     * @return Интерфес для отсоединения подписчика
     */
    public AutoCloseable addListener(ListenerType listener, boolean weakLink) {
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return writeLock(()->{
            if( weakLink ){
                return addListenerWeak0(listener);
            }else{
                return addListener0(listener);
            }
        });
    }

    private AutoCloseable addListenerWeak0(ListenerType listener) {
        weakListeners.put(listener, true);
        return createRemover(listener);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="removeListener(l)">
    /**
     * Удаление подписчика из списка обработки
     * @param listener подписчик
     */
    public void removeListener(ListenerType listener) {
        if( listener==null )return;
        writeLock(()->{
            listeners.remove(listener);
            weakListeners.remove(listener);
            return null;
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="removeAllListeners()">
    private void removeAllListeners0(){
        weakListeners.clear();
        listeners.clear();
    }

    public void removeAllListeners(){
        writeLock(()->{
            removeAllListeners0();
            return null;
        });
    }
    //</editor-fold>

    //<editor-fold desc="event queue">
    protected final AtomicInteger eventBlockLevel = new AtomicInteger(0);

    /**
     * Запустить выполнение кода в блоке, и не рассылать уведомления до завершения блока кода
     * @param run блок кода
     */
    public void withQueue( Runnable run ){
        if( run == null )throw new IllegalArgumentException( "run == null" );
        try {
            eventBlockLevel.incrementAndGet();
            run.run();
        } finally {
            int lvl = eventBlockLevel.decrementAndGet();
            if( lvl<=0 ){
                runEventQueue();
            }
        }
    }

    /**
     * Запустить выполнение кода в блоке, и не рассылать уведомления до завершения блока кода
     * @param run блок кода
     * @param <T> тип возвращаемого занчения из блока
     * @return возвращаемое значение
     */
    public <T> T withQueue( Supplier<T> run ){
        if( run == null )throw new IllegalArgumentException( "run == null" );
        try {
            eventBlockLevel.incrementAndGet();
            return run.get();
        } finally {
            int lvl = eventBlockLevel.decrementAndGet();
            if( lvl<=0 ){
                runEventQueue();
            }
        }
    }

    /**
     * Рассылка уведомления подписчикам
     * @param event уведомление
     */
    public void fireEvent(EventType event) {
        if( eventBlockLevel.get()>0 ){
            getEventQueue().add(event);
        }else {
            if( event != null ){
                for( ListenerType ls : readLock(()->new LinkedHashSet<>(getListeners())) ){
                    if( ls != null ){
                        callListener.accept(ls, event);
                    }
                }
            }
        }
    }

    private volatile ConcurrentLinkedQueue<EventType> eventQueue;

    /**
     * Очередь событий
     * @return Очередь событий
     */
    public ConcurrentLinkedQueue<EventType> getEventQueue(){
        if( eventQueue!=null )return eventQueue;
        synchronized(this){
            if( eventQueue!=null )return eventQueue;
            eventQueue = new ConcurrentLinkedQueue<>();
            return eventQueue;
        }
    }

    /**
     * Добавляет событие в очередь
     * @param ev событие
     */
    public void addEvent( EventType ev ){
        if( ev!=null ){
            getEventQueue().add(ev);
        }
    }

    /**
     * Отправляет события из очереди подписчикам
     */
    public void runEventQueue(){
        while( true ){
            EventType e = getEventQueue().poll();
            if( e!=null ){
                fireEvent(e);
            }else{
                break;
            }
        }
    }
    //</editor-fold>

    private static Map<Class,WeakHashMap<Object,ListenersHelper>> eventListeners = new WeakHashMap<>();

    /**
     * Поддержка в реализации подписчиков на события
     * @param cls Класс/интерфейс реализующий логику работы
     * @param inst Экземпляр класса/интерфейса
     * @param invoker Функция передачи уведомления подписчику
     * @param <LS> Тип подписчика
     * @param <EV> Тип уведомления
     * @return Помошник в реализции Listener
     */
    public static <LS,EV> ListenersHelper<LS,EV> get(Class cls,Object inst,BiConsumer<LS,EV> invoker){
        if( cls == null )throw new IllegalArgumentException( "cls == null" );
        if( inst == null )throw new IllegalArgumentException( "inst == null" );
        if( invoker == null )throw new IllegalArgumentException( "invoker == null" );
        //WeakHashMap<Object,ListenersHelper> map = eventListeners.computeIfAbsent(cls, x -> new WeakHashMap<>());
        return eventListeners.computeIfAbsent(cls, x -> new WeakHashMap<>()).computeIfAbsent(inst, x -> new ListenersHelper(invoker));
    }
}
