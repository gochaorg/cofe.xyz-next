package xyz.cofe.typeconv;

/**
 * Уведомление о измениии веса
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public interface WeightChangeListener {
    /**
     * Получает уведомление о изменении веста
     * @param event уведомление
     */
    public void weightChanged( WeightChangeEvent event );
}
