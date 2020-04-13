package xyz.cofe.gui.swing.tree;

import xyz.cofe.gui.swing.table.Column;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Колонка TreeTableNodeColumn для TreeTableNode
 * @author nt.gocha@gmail.com
 */
public class TreeTableNodeColumn extends Column
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableNodeColumn.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(TreeTableNodeColumn.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(TreeTableNodeColumn.class.getName(), method, result);
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
    //</editor-fold>

    /**
     * Конструктор
     */
    public TreeTableNodeColumn() {
        setName("node");
        setType(TreeTableNode.class);
        //setReader( node -> node );
        setReader((Object from) -> from);
    }

    /**
     * Конструктор
     * @param sync объект для синхронизации
     */
    public TreeTableNodeColumn( ReadWriteLock sync ) {
        super(sync);
    }

    /**
     * Конструктор копирования
     * @param src образец для копирования
     */
    public TreeTableNodeColumn(Column src) {
        super(src);
    }

    /**
     * Конструктор копирования
     * @param sync объект для синхронизации
     * @param src образец для копирования
     */
    public TreeTableNodeColumn(ReadWriteLock sync, Column src) {
        super(sync, src);
    }

    @Override
    public Column clone(){
        return new TreeTableNodeColumn(this);
    }

    @Override
    public Column cloneWith( ReadWriteLock sync ){
        return new TreeTableNodeColumn(sync,this);
    }
}
