package xyz.cofe.text.lex;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Парсер - комбинатор из нескольких парсеров
 * @author Kamnev Georgiy
 */
public class OrParser implements TokenParser
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(OrParser.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }

    private static boolean isLogSevere(){
        Level logLevel = logger.getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }

    private static boolean isLogWarning(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }

    private static boolean isLogInfo(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }

    private static boolean isLogFine(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }

    private static boolean isLogFiner(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }

    private static boolean isLogFinest(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
    }

    private static void logFine(String message,Object ... args){
        logger.log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        logger.log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        logger.log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        logger.log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }

    private static void logEntering(String method,Object ... params){
        logger.entering(OrParser.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(OrParser.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(OrParser.class.getName(), method, result);
    }
    //</editor-fold>

    public OrParser(){
    }

    public OrParser( TokenParser ... variants ){
        if( variants!=null ){
            for( TokenParser tp : variants ){
                if( tp!=null ){
                    getVariants().add(tp);
                }
            }
        }
    }

    protected volatile List<TokenParser> variants;

    public synchronized List<TokenParser> getVariants(){
        if( variants!=null ){
            return variants;
        }
        variants = new ArrayList<>();
        return variants;
    }

    protected int parseLevelCall = 0;

    @Override
    public synchronized Token parse(String source, int offset) {
        try{
            parseLevelCall++;
            if( getVariants().isEmpty() )return null;
            for( TokenParser tp : getVariants() ){
                if( tp!=null ){
                    Token t = tp.parse(source, offset);
                    if( t!=null ){
                        return t;
                    }
                }
            }
            return null;
        }finally{
            parseLevelCall--;
        }
    }
}
