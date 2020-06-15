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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Вспомогательные функции
 * @author Kamnev Georgiy
 */
public class JdbcUtils {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(JdbcUtils.class.getName());

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

    private static void logFine( String message, Object... args){
        logger.log(Level.FINE, message, args);
    }
    
    private static void logFiner( String message, Object... args){
        logger.log(Level.FINER, message, args);
    }
    
    private static void logFinest( String message, Object... args){
        logger.log(Level.FINEST, message, args);
    }
    
    private static void logInfo( String message, Object... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning( String message, Object... args){
        logger.log(Level.WARNING, message, args);
    }
    
    private static void logSevere( String message, Object... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException( Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }

    private static void logEntering( String method, Object... params){
        logger.entering(JdbcUtils.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(JdbcUtils.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(JdbcUtils.class.getName(), method, result);
    }
    //</editor-fold>
    
    /**
     * Создание таблицы из SQL набора
     * @param rs SQL набор
     * @return таблица
     * @throws SQLException проблемы с SQL
     * @throws ClassNotFoundException Указанный класс в metaData не может быть найден
     */
    public static DataTable tableOf( ResultSet rs ) throws SQLException, ClassNotFoundException{
        if( rs==null )throw new IllegalArgumentException("rs == null");
        
        ResultSetMetaData rsmeta = rs.getMetaData();
        
        ArrayList<DataColumn> dcList = new ArrayList<>();
        int ccnt = rsmeta.getColumnCount();
        for( int cn=1; cn<=ccnt; cn++ ){
            DataColumn dc = null;
            
            String clsName = rsmeta.getColumnClassName(cn);
            String label = rsmeta.getColumnLabel(cn);
            label = label!=null ? label : "column"+cn;
            
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if( cl==null )cl = JdbcUtils.class.getClassLoader();
            
            Class cls = Class.forName(clsName, true, cl);
            dc = new DataColumn(label, cls);
            
            int sqlnull = rsmeta.isNullable(cn);
            if( sqlnull!= ResultSetMetaData.columnNullableUnknown ){
                if( sqlnull== ResultSetMetaData.columnNullable ){
                    dc = dc.allowNull(true);
                }else if( sqlnull== ResultSetMetaData.columnNoNulls ){
                    dc = dc.allowNull(false);
                }
            }
            
            dcList.add(dc);
        }
        
        DataTable dt = new DataTable(dcList.toArray(new DataColumn[]{}));
        
        while(true){
            boolean hasNext = rs.next();
            if( !hasNext )break;
            
            Object[] values = new Object[ccnt];
            for( int cn=1; cn<=ccnt; cn++ ){
                values[cn-1] = rs.getObject(cn);                
            }
            
            //DataRow dr = new DataRow(dt, values);
            //dt.getWorkedRows().add(dr);
            //dr.fixed();
            
            dt.insert(values).fixed().go();
        }
        
        return dt;
    }
}
