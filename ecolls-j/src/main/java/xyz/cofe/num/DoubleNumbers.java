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

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Математические операции над Double
 * @author Kamnev Georgiy
 */
public class DoubleNumbers implements Numbers<Double>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(DoubleNumbers.class.getName());

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
        logger.entering(DoubleNumbers.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(DoubleNumbers.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(DoubleNumbers.class.getName(), method, result);
    }
    //</editor-fold>

    @Override
    public Double zero() {
        return 0.0;
    }

    @Override
    public Double one() {
        return 1.0;
    }

    @Override
    public boolean zero(Double n) {
        return n==null ? false : n==0.0;
    }

    @Override
    public boolean undefined(Double n) {
        return n==null ? true : n.isNaN();
    }

    @Override
    public boolean infinity(Double n) {
        return n==null ? false : n.isInfinite();
    }

    @Override
    public Double add(Double a, Double b) {
        if( a==null || b==null )return Double.NaN;
        return a + b;
    }

    @Override
    public Double sub(Double a, Double b) {
        if( a==null || b==null )return Double.NaN;
        return a - b;
    }

    @Override
    public Double mul(Double a, Double b) {
        if( a==null || b==null )return Double.NaN;
        return a * b;
    }

    @Override
    public Double div(Double a, Double b) {
        if( a==null || b==null )return Double.NaN;
        if( b==0.0 )return Double.NaN;
        return a / b;
    }

    @Override
    public Double remainder(Double a, Double b) {
        if( a==null || b==null )return Double.NaN;
        if( b==0.0 )return Double.NaN;
        return a % b;
    }

    @Override
    public boolean equals(Double a, Double b) {
        return Objects.equals(a, b);
    }

    @Override
    public boolean more(Double a, Double b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        return a.compareTo(b) > 0;
    }

    @Override
    public boolean less(Double a, Double b) {
        if( a==null && b==null )return false;
        if( a==null )return true;
        return a.compareTo(b) < 0;
    }

    @Override
    public Double next(Double n) {
        return Double.NaN;
    }

    @Override
    public Double prev(Double n) {
        return Double.NaN;
    }
}
