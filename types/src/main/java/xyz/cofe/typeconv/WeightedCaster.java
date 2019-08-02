package xyz.cofe.typeconv;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class WeightedCaster
    implements
    Function<Object, Object>,
    GetWeight
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(WeightedCaster.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(WeightedCaster.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(WeightedCaster.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(WeightedCaster.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(WeightedCaster.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(WeightedCaster.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(WeightedCaster.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public WeightedCaster(){
    }

    public WeightedCaster(double weight){
        this.weight = weight;
    }

    protected Double weight = (double)1;

    @Override
    public Double getWeight(){
        return weight;
    }
}
