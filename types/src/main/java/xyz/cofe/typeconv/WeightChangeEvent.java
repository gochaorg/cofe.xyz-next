package xyz.cofe.typeconv;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Сообщение о изменении веса
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class WeightChangeEvent {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(WeightChangeEvent.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(WeightChangeEvent.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(WeightChangeEvent.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(WeightChangeEvent.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(WeightChangeEvent.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(WeightChangeEvent.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(WeightChangeEvent.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected Object source = null;
    protected Double oldWeight;
    protected Double newWeight;

    public WeightChangeEvent( Object source, Double oldWeight, Double newWeight ){
        this.source = source;
        this.oldWeight = oldWeight;
        this.newWeight = newWeight;
    }

    /**
     * Исходный объект (MutableWeightedCaster)
     * @return исходный объект
     */
    public Object getSource() {
        return source;
    }

    /**
     * Предыдущее значение веста
     * @return пред значение
     */
    public Double getOldWeight() {
        return oldWeight;
    }

    /**
     * Текущее значение веса
     * @return текущее значение
     */
    public Double getNewWeight() {
        return newWeight;
    }
}
