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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Создание функции следования каталогам
 * @author Kamnev Georgiy
 */
public class DirFollowBuilder
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(DirFollowBuilder.class.getName());

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
        logger.entering(DirFollowBuilder.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(DirFollowBuilder.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(DirFollowBuilder.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public DirFollowBuilder(){
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public DirFollowBuilder( DirFollowBuilder sample ){
        if( sample!=null ){
            checkCycle = sample.checkCycle;
            followLinks = sample.followLinks;
            errorBehavior = sample.errorBehavior;
        }
    }

    /**
     * Клонирование
     * @return клон
     */
    @Override
    public DirFollowBuilder clone(){
        return new DirFollowBuilder(this);
    }

    //<editor-fold defaultstate="collapsed" desc="checkCycle">
    protected boolean checkCycle = true;

    /**
     * Возвращает проверять зациклинность при обходе
     * @return true - проверять
     */
    public boolean isCheckCycle() {
        return checkCycle;
    }

    /**
     * Указывает проверять зациклинность при обходе
     * @param checkCycle true - проверять
     */
    public void setCheckCycle(boolean checkCycle) {
        this.checkCycle = checkCycle;
    }

    /**
     * Указывает проверять зациклинность при обходе
     * @param check true - проверять
     * @return self ссылка
     */
    public DirFollowBuilder checkCycle( boolean check ){
        this.checkCycle = check;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="followLinks">
    protected boolean followLinks = true;

    /**
     * Возвращает следовать ли символичным ссылкам
     * @return true - следовать ссылкам
     */
    public boolean isFollowLinks() {
        return followLinks;
    }

    /**
     * Указывает следовать ли символичным ссылкам
     * @param followLinks true - следовать ссылкам
     */
    public void setFollowLinks(boolean followLinks) {
        this.followLinks = followLinks;
    }

    /**
     * Указывает следовать ли символичным ссылкам
     * @param follow true - следовать ссылкам
     * @return self ссылка
     */
    public DirFollowBuilder followLinks(boolean follow){
        this.followLinks = follow;
        return this;
    }
    //</editor-fold>

    protected DirFollow.ErrorBehavior errorBehavior;

    /**
     * Возвращает поведение при возникновении ошибки
     * @return поведение при возникновении ошибки
     */
    public DirFollow.ErrorBehavior getErrorBehavior() {
        return errorBehavior;
    }

    /**
     * Указывает поведение при возникновении ошибки
     * @param errorBehavior поведение при возникновении ошибки
     */
    public void setErrorBehavior(DirFollow.ErrorBehavior errorBehavior) {
        this.errorBehavior = errorBehavior;
    }

    public DirFollowBuilder errorBehavior(DirFollow.ErrorBehavior errorBehavior) {
        setErrorBehavior(errorBehavior);
        return this;
    }

    public DirFollow build(){
        DirFollow df = new DirFollow();
        df.setCheckCycle(checkCycle);
        df.setFollowLinks(followLinks);
        if( errorBehavior!=null ){
            df.setErrorBehavior(errorBehavior);
        }
        return df;
    }
}
