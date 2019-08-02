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

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.iter.TreeStep;

/**
 * Итератор по дереву каталогов/файлов
 * @author Kamnev Georgiy
 */
public class FileTreeIterable implements Iterable<TreeStep<File>>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(FileTreeIterable.class.getName());

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
        logger.entering(FileTreeIterable.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(FileTreeIterable.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(FileTreeIterable.class.getName(), method, result);
    }
    //</editor-fold>

    public final File start;

    //<editor-fold defaultstate="collapsed" desc="dirFollow : DirFollow">
    protected volatile DirFollow dirFollow;

    /**
     * Возвращает функцию перехода к дочерним элементам
     * @return функция перехода к дочерним элементам
     */
    public synchronized DirFollow getDirFollow() {
        if( dirFollow==null ){
            DirFollow df = new DirFollow();
            df.setCheckCycle(true);
            df.setCheckDir(true);
            df.setErrorBehavior(DirFollow.ErrorBehavior.Continue);
            df.setFollowLinks(true);
            return df;
        }
        return dirFollow;
    }

    /**
     * Указывает функцию перехода к дочерним элементам
     * @param dirFollow функция перехода к дочерним элементам
     */
    public synchronized void setDirFollow(DirFollow dirFollow) {
        this.dirFollow = dirFollow;
    }
    //</editor-fold>

//    //<editor-fold defaultstate="collapsed" desc="treeWalkType">
//    protected TreeWalkType treeWalkType;
//
//    /**
//     * Указывает способ обхода дочерних каталогов/файлов
//     * @return способ обхода дочерних каталогов/файлов
//     */
//    public synchronized TreeWalkType getTreeWalkType() {
//        if( treeWalkType==null )return TreeWalkType.ByBranchForward;
//        return treeWalkType;
//    }
//
//    /**
//     * Указывает способ обхода дочерних каталогов/файлов
//     * @param treeWalkType способ обхода дочерних каталогов/файлов
//     */
//    public synchronized void setTreeWalkType(TreeWalkType treeWalkType) {
//        this.treeWalkType = treeWalkType;
//    }
//    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fileFilter">
    protected volatile Predicate<File> fileFilter;

    public synchronized Predicate<File> getFileFilter() {
        return fileFilter;
    }

    public synchronized void setFileFilter(Predicate<File> fileFilter) {
        this.fileFilter = fileFilter;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="treeWalkFilter">
    protected volatile Predicate<TreeStep<File>> treeWalkFilter;

    public synchronized Predicate<TreeStep<File>> getTreeWalkFilter() {
        return treeWalkFilter;
    }

    public synchronized void setTreeWalkFilter(Predicate<TreeStep<File>> treeWalkFilter) {
        this.treeWalkFilter = treeWalkFilter;
    }
    //</editor-fold>

    public FileTreeIterable( File start ){
        if( start==null )throw new IllegalArgumentException("start == null");
        this.start = start;
    }

    @Override
    public synchronized FileTreeIterator iterator() {
        DirFollow df = getDirFollow();
        FileTreeIterator fti = new FileTreeIterator(start, df);
        return fti;
    }
}
