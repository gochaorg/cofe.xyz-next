/*
 * The MIT License
 *
 * Copyright 2018 user.
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

package xyz.cofe.num;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Математические операции над AtomicLong
 * @author Kamnev Georgiy
 */
public class AtomicLongNumbers implements Numbers<AtomicLong>{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(AtomicLongNumbers.class.getName());

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
        logger.entering(AtomicLongNumbers.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(AtomicLongNumbers.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(AtomicLongNumbers.class.getName(), method, result);
    }
    //</editor-fold>

    @Override
    public AtomicLong zero() {
        return new AtomicLong(0);
    }

    @Override
    public AtomicLong one() {
        return new AtomicLong(1);
    }

    @Override
    public boolean zero(AtomicLong n) {
        return n==null ? false : n.get()==0L;
    }

    @Override
    public boolean undefined(AtomicLong n) {
        return n==null;
    }

    @Override
    public boolean infinity(AtomicLong n) {
        return false;
    }

    @Override
    public AtomicLong add(AtomicLong a, AtomicLong b) {
        if( a==null || b==null )return null;
        return new AtomicLong( a.get() + b.get() );
    }

    @Override
    public AtomicLong sub(AtomicLong a, AtomicLong b) {
        if( a==null || b==null )return null;
        return new AtomicLong( a.get() - b.get() );
    }

    @Override
    public AtomicLong mul(AtomicLong a, AtomicLong b) {
        if( a==null || b==null )return null;
        return new AtomicLong( a.get() * b.get() );
    }

    @Override
    public AtomicLong div(AtomicLong a, AtomicLong b) {
        if( a==null || b==null )return null;
        long la = a.get();
        long lb = b.get();
        if( lb==0 )return null;
        return new AtomicLong( la / lb );
    }

    @Override
    public AtomicLong remainder(AtomicLong a, AtomicLong b) {
        if( a==null || b==null )return null;
        long la = a.get();
        long lb = b.get();
        if( lb==0 )return null;
        return new AtomicLong( la % lb );
    }

    @Override
    public boolean equals(AtomicLong a, AtomicLong b) {
        if( a==null && b==null )return true;
        if( a==null )return false;
        long la = a.get();
        long lb = b.get();
        return la == lb;
    }

    @Override
    public boolean more(AtomicLong a, AtomicLong b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        long la = a.get();
        long lb = b.get();
        return la > lb;
    }

    @Override
    public boolean less(AtomicLong a, AtomicLong b) {
        if( a==null && b==null )return false;
        if( a==null )return true;
        long la = a.get();
        long lb = b.get();
        return la < lb;
    }

    @Override
    public AtomicLong next(AtomicLong n) {
        if( n==null )return null;
        long l = n.get();
        if( l==Long.MAX_VALUE )return null;
        return new AtomicLong(l + 1);
    }

    @Override
    public AtomicLong prev(AtomicLong n) {
        if( n==null )return null;
        long l = n.get();
        if( l==Long.MIN_VALUE )return null;
        return new AtomicLong(l - 1);
    }
}
