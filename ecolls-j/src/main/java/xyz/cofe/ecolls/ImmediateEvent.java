package xyz.cofe.ecolls;

/**
 * Интерфейс для событий {@link ListenersHelper}
 * Если событие вернет isImmediateEvent() = true,
 * то данное событие быдет передано обработчиком без ожидания очереди
 */
public interface ImmediateEvent {
    /**
     * Надо ли передовать событие немедленно подписчикам или можно в очередь поставить
     * @return true - передавать немедленно
     */
    default boolean isImmediateEvent() { return true; }
}
