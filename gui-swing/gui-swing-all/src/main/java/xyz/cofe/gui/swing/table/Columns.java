package xyz.cofe.gui.swing.table;

import xyz.cofe.collection.BasicEventList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Колонки таблицы
 * @author gocha
 */
public class Columns extends BasicEventList<Column>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Columns.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Columns.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Columns.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Columns.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Columns.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Columns.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public Columns(){
        super(new ArrayList<>(), null);
    }

    /**
     * Конструктор
     * @param wraplist колонки
     * @param sync объект для синхронизации или null, тогда this
     */
    public Columns( List<Column> wraplist, ReadWriteLock sync) {
        super(wraplist, sync);
    }

    /*public Columns(List<Column> wraplist, Object syncRead, Object syncWrite) {
        super(wraplist, syncRead, syncWrite);
    }*/

    /**
     * Конструктор копирования
     * @param sample Образец для копирования
     * @param sync объект для синхронизации или null, тогда this
     */
    public Columns( Columns sample, ReadWriteLock sync ){
        super( new ArrayList<Column>(), sync );
        if( sample!=null ){
            sample.readLock(()->{
                for( Column c : sample ){
                    if( c==null )continue;
                    add( c.cloneWith(sync) );
                }
            });
        }
    }

    @Override
    public Columns clone(){
        return new Columns(this, null);
    }

    /**
     * Клонирование
     * @param sync объект для синхронизации
     * @return клон
     */
    public Columns cloneWith(ReadWriteLock sync){
        return new Columns(this, sync);
    }
}
