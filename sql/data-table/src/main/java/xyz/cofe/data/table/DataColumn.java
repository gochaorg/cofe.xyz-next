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

package xyz.cofe.data.table;

import xyz.cofe.data.events.*;
import xyz.cofe.fn.Fn0;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kamnev Georgiy
 */
public class DataColumn {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(DataColumn.class.getName());

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
        logger.entering(DataColumn.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(DataColumn.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(DataColumn.class.getName(), method, result);
    }
    //</editor-fold>
    
    public DataColumn( String name, Class dataType ){
        if( name==null )throw new IllegalArgumentException("name == null");
        if( dataType==null )throw new IllegalArgumentException("dataType == null");
        
        this.name = name;
        this.dataType = dataType;
    }
    
    public DataColumn( DataColumn sample ){
        if( sample==null )throw new IllegalArgumentException("sample == null");
        this.name = sample.name;
        this.dataType = sample.dataType;
        this.generator = sample.generator;
        this.allowNull = sample.allowNull;
        this.allowSubTypes = sample.allowSubTypes;
    }
    
    @Override
    public DataColumn clone(){
        return new DataColumn(this);
    }
    
    //<editor-fold defaultstate="collapsed" desc="name : String">
    protected String name;
    
    public String getName() {
        return name;
    }
    
    public DataColumn name( String name ){
        if( name==null )throw new IllegalArgumentException("name == null");
        DataColumn mc = clone();
        mc.name = name;
        return mc;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="dataType : String">
    protected Class dataType;
    
    public Class getDataType() {
        return dataType;
    }
    
    public DataColumn dataType( Class dtype ){
        if( dtype==null )throw new IllegalArgumentException("dtype == null");
        DataColumn mc = clone();
        mc.dataType = dtype;
        return mc;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="allowNull : boolean">
    protected boolean allowNull = true;
    
    public boolean isAllowNull() {
        return allowNull;
    }
    
    public DataColumn allowNull( boolean allow){
        DataColumn mc = clone();
        mc.allowNull = allow;
        return mc;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="allowSubTypes : boolean">
    protected boolean allowSubTypes = true;
    
    public boolean isAllowSubTypes() {
        return allowSubTypes;
    }
    
    public DataColumn allowSubTypes( boolean allow ){
        DataColumn mc = clone();
        mc.allowSubTypes = allow;
        return mc;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="generator : Func0">
    public Fn0 generator;
    
    public Fn0 getGenerator() {
        return generator;
    }
    
    public DataColumn generator( Fn0 gen ){
        DataColumn mc = clone();
        mc.generator = gen;
        return mc;
    }
    //</editor-fold>
}
