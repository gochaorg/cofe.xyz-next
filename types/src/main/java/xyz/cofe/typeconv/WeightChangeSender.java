package xyz.cofe.typeconv;

/**
 * Sender на изменения веса
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public interface WeightChangeSender {
    /**
     * Добавляет подписчика на изменение веса
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    public AutoCloseable addWeightChangeListener( WeightChangeListener listener );
    /**
     * Добавляет подписчика на изменение веса
     * @param listener подписчик
     * @param softLink добавить подписчика как weak/soft ссылку
     * @return отписка от уведомлений
     */
    public AutoCloseable addWeightChangeListener( WeightChangeListener listener, boolean softLink );
    /**
     * Удаляет подписчика на изменение веса
     * @param listener подписчик
     */
    public void removeWeightChangeListener( WeightChangeListener listener );
}
