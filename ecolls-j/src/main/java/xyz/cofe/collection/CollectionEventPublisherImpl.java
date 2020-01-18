package xyz.cofe.collection;

import xyz.cofe.ecolls.ListenersHelper;

import java.util.Collection;
import java.util.WeakHashMap;

/**
 * Поддержка реализации подписчиков на события CollectionEventPublisher
 */
@SuppressWarnings("WeakerAccess")
public class CollectionEventPublisherImpl {
    private static final WeakHashMap<CollectionEventPublisher, ListenersHelper> eventListeners = new WeakHashMap<>();

    /**
     * Помошник в реализации подписки на события
     * @param publisher издаель события
     * @param <COL> тип коллекции
     * @param <EL> тип элемента коллекции
     * @param <L> тип подписчика
     * @param <E> тип события
     * @return
     */
    @SuppressWarnings({ "unchecked", "Convert2MethodRef", "WeakerAccess" })
    public static <COL, EL, L extends CollectionListener<COL,EL>, E extends CollectionEvent<COL,EL>>
    ListenersHelper<L, E>
    listenersHelperOf(CollectionEventPublisher<COL,EL> publisher) {
        if( publisher == null ) throw new IllegalArgumentException("publisher == null");
        synchronized ( eventListeners ) {
            return eventListeners.computeIfAbsent(publisher,
                (x)->new ListenersHelper<CollectionListener, CollectionEvent>(
                    (ls, ev)->ls.collectionEvent(ev)
                ));
        }
    }
}
