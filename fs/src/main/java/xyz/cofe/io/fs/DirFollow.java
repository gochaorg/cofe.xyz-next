/*
 * The MIT License
 *
 * Copyright 2017 user.
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

package xyz.cofe.io.fs;

import java.io.Closeable;
import java.io.IOError;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.iter.Eterable;

/**
 * Функция для обхода дочерних файлов/каталогов
 * @author Kamnev Georgiy
 */
public class DirFollow implements NodesExtracter<File, File>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(DirFollow.class.getName());

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
        logger.entering(DirFollow.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(DirFollow.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(DirFollow.class.getName(), method, result);
    }
    //</editor-fold>

    public DirFollow(){
    }

    protected DirFollow(DirFollow sample){
        if( sample!=null ){
            followLinks = sample.followLinks;
            errorBehavior = sample.errorBehavior;
            checkCycle = sample.checkCycle;
            checkDir = sample.checkDir;
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public DirFollow clone(){
        return new DirFollow(this);
    }

    //<editor-fold defaultstate="collapsed" desc="checkDir : boolean">
    protected volatile boolean checkDir = true;

    /**
     * Провереть что дочерний элемент является каталогом
     * @return true (по умолчанию) - осуществлять проверку
     */
    public synchronized boolean isCheckDir() {
        return checkDir;
    }

    /**
     * Провереть что дочерний элемент является каталогом
     * @param checkDir true (по умолчанию) - осуществлять проверку
     */
    public synchronized void setCheckDir(boolean checkDir) {
        this.checkDir = checkDir;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="checkCycle : boolean">
    protected boolean checkCycle = true;

    /**
     * Проверять наличие закольцованности при обходе дочерних узлов
     * @return true (по умолчанию) - проверять наличие закольцованности
     */
    public synchronized boolean isCheckCycle() {
        return checkCycle;
    }

    /**
     * Проверять наличие закольцованности при обходе дочерних узлов
     * @param checkCycle true - проверять наличие закольцованности
     */
    public synchronized void setCheckCycle(boolean checkCycle) {
        this.checkCycle = checkCycle;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="visited : Set<File>">
    protected volatile Set<File> visited;

    /**
     * Вовзаращет список посещенных файлов/каталогов
     * @return посещенные файлы
     */
    public Set<File> getVisited(){
        if( visited!=null )return visited;
        synchronized(this){
            if( visited!=null )return visited;
            visited = new ConcurrentSkipListSet<File>();
            return visited;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="errorBehavior">
    /**
     * Поведение при возниконовеии ошибки
     */
    public static enum ErrorBehavior {
        /**
         * Продолжить выполнение
         */
        Continue,

        /**
         * Завершить работу
         */
        Stop
    }

    protected volatile ErrorBehavior errorBehavior;

    /**
     * Возвращает поведение при возникновении ошибки
     * @return поведение при возникновении ошибки
     */
    public synchronized ErrorBehavior getErrorBehavior() {
        if( errorBehavior==null )return ErrorBehavior.Continue;
        return errorBehavior;
    }

    /**
     * Указывает поведение при возникновении ошибки
     * @param errorBehavior поведение при возникновении ошибки
     */
    public synchronized void setErrorBehavior(ErrorBehavior errorBehavior) {
        this.errorBehavior = errorBehavior;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="followLinks : boolean">
    protected volatile boolean followLinks = true;

    /**
     * Возвращает следовать ли символичным ссылкам
     * @return true - следовать ссылкам
     */
    public synchronized boolean isFollowLinks() {
        return followLinks;
    }

    /**
     * Указывает следовать ли символичным ссылкам
     * @param followLinks true - следовать ссылкам
     */
    public synchronized void setFollowLinks(boolean followLinks) {
        this.followLinks = followLinks;
    }
    //</editor-fold>

    private static class DirIterable implements Eterable<File>, Closeable
    {
        private DirIterator diter;

        public DirIterable( DirIterator diritr ){
            diter = diritr;
        }

        @Override
        public Iterator<File> iterator() {
            return diter;
        }

        @Override
        public void close() throws IOException {
            if( diter!=null ){
                diter.close();
                diter = null;
            }
        }
    }

    @Override
    public synchronized Eterable<File> extract( File from) {
        if( from==null )return null;

        if( checkCycle && getVisited().contains(from) ){
            return null;
        }

        if( checkDir && !from.isDir() )return null;

        if( checkCycle )getVisited().add(from);

        try {
            DirIterator dirIter = new DirIterator(from);
            return new DirIterable(dirIter);
        } catch (IOException ex) {
            Logger.getLogger(DirFollow.class.getName()).log(Level.SEVERE, null, ex);
            if( getErrorBehavior() == ErrorBehavior.Stop ){
                throw new IOError(ex);
            }
        } catch (IOError err){
            Logger.getLogger(DirFollow.class.getName()).log(Level.SEVERE, null, err);
            if( getErrorBehavior() == ErrorBehavior.Stop ){
                throw err;
            }
        }

        return null;
    }
}
