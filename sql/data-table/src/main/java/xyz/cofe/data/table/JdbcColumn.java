/*
 * The MIT License
 *
 * Copyright 2017 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package xyz.cofe.data.table;

import xyz.cofe.data.events.*;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Колонка данных из Jdbc источника
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class JdbcColumn extends DataColumn
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(JdbcColumn.class.getName());
    private static final Level logLevel = logger.getLevel();
    
    private static final boolean isLogSevere = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.SEVERE.intValue();
    
    private static final boolean isLogWarning = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.WARNING.intValue();
    
    private static final boolean isLogInfo = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.INFO.intValue();
    
    private static final boolean isLogFine = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.FINE.intValue();
    
    private static final boolean isLogFiner = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.FINER.intValue();
    
    private static final boolean isLogFinest = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.FINEST.intValue();

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
        logger.entering(JdbcColumn.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(JdbcColumn.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(JdbcColumn.class.getName(), method, result);
    }
    //</editor-fold>

    public JdbcColumn( String name, Class dataType) {
        super(name, dataType);
    }

    public JdbcColumn( JdbcColumn sample) {
        super(sample);
        if( sample!=null ){
            columnLabel = sample.columnLabel;
            schemaName = sample.schemaName;
            catalogName = sample.catalogName;
            tableName = sample.tableName;
            columnName = sample.columnName;
            columnType = sample.columnType;
            columnTypeName = sample.columnTypeName;
            displaySize = sample.displaySize;
            precision = sample.precision;
            scale = sample.scale;
            autoIncrement = sample.autoIncrement;
            caseSensitive = sample.caseSensitive;
            currency = sample.currency;
            definitelyWritable = sample.definitelyWritable;
            nullable = sample.nullable;
            searchable = sample.searchable;
            signed = sample.signed;
            writable = sample.writable;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="clone()">
    @Override
    public JdbcColumn clone() {
        return new JdbcColumn(this);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="columnLabel : String">
    protected String columnLabel;
    public String getColumnLabel(){ return columnLabel; }
    public JdbcColumn columnLabel( String label ){
        JdbcColumn c = clone();
        c.columnLabel = label;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="schemaName : String">
    protected String schemaName;
    public String getSchemaName() { return schemaName; }
    public JdbcColumn schemaName( String name ){
        JdbcColumn c = new JdbcColumn(this);
        c.schemaName = name;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="catalogName : String">
    protected String catalogName;
    public String getCatalogName(){ return catalogName; }
    public JdbcColumn catalogName( String name ){
        JdbcColumn c = clone();
        c.catalogName = name;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="tableName : String">
    protected String tableName;
    public String getTableName(){ return tableName; }
    public JdbcColumn tableName( String name ){
        JdbcColumn c = clone();
        c.tableName = name;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="columnName : String">
    protected String columnName;
    public String getColumnName(){ return columnName; }
    public JdbcColumn columnName( String name){
        JdbcColumn c = clone();
        c.columnName = name;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="columnType : int">
    protected int columnType;
    public int getColumnType(){ return columnType; }
    public JdbcColumn columnType( int t){
        JdbcColumn c = clone();
        c.columnType = t;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="columnTypeName : String">
    protected String columnTypeName;
    public String getColumnTypeName(){ return columnTypeName; }
    public JdbcColumn columnTypeName( String name){
        JdbcColumn c = clone();
        c.columnTypeName = name;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="displaySize : int">
    protected int displaySize;
    public int getDisplaySize(){ return displaySize; }
    public JdbcColumn displaySize( int displaySize){
        JdbcColumn c = clone();
        c.displaySize = displaySize;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="precision : int">
    protected int precision;
    public int getPrecision(){ return precision; }
    public JdbcColumn precision( int precision){
        JdbcColumn c = clone();
        c.precision = precision;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="scale : int">
    protected int scale;
    public int getScale(){ return scale; }
    public JdbcColumn scale( int scale){
        JdbcColumn c = clone();
        c.scale = scale;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="autoIncrement : boolean">
    protected boolean autoIncrement;
    public boolean isAutoIncrement(){ return autoIncrement; }
    public JdbcColumn autoIncrement( boolean autoIncrement){
        JdbcColumn c = clone();
        c.autoIncrement = autoIncrement;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="caseSensitive : boolean">
    protected boolean caseSensitive;
    public boolean isCaseSensitive(){ return caseSensitive; }
    public JdbcColumn caseSensitive( boolean caseSensitive){
        JdbcColumn c = clone();
        c.caseSensitive = caseSensitive;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="currency : boolean">
    protected boolean currency;
    public boolean isCurrency(){ return currency; }
    public JdbcColumn currency( boolean currency){
        JdbcColumn c = clone();
        c.currency = currency;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="definitelyWritable : boolean">
    protected boolean definitelyWritable;
    public boolean isDefinitelyWritable(){ return definitelyWritable; }
    public JdbcColumn definitelyWritable( boolean definitelyWritable){
        JdbcColumn c = clone();
        c.definitelyWritable = definitelyWritable;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="nullable : boolean">
    protected int nullable;
    public int getNullable(){ return nullable; }
    public JdbcColumn nullable( int nullable){
        JdbcColumn c = clone();
        c.nullable = nullable;
        if( nullable== ResultSetMetaData.columnNullableUnknown ){
            c.allowNull = true;
        }else{
            switch (nullable) {
                case ResultSetMetaData.columnNullable:
                    c.allowNull = true;
                    break;
                case ResultSetMetaData.columnNoNulls:
                    c.allowNull = false;
                    break;
                default:
                    c.allowNull = true;
                    break;
            }
        }
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="searchable : boolean">
    protected boolean searchable;
    public boolean isSearchable(){ return searchable; }
    public JdbcColumn searchable( boolean searchable){
        JdbcColumn c = clone();
        c.searchable = searchable;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="readOnly : boolean">
    protected boolean readOnly;
    public boolean isReadOnly(){ return readOnly; }
    public JdbcColumn readOnly( boolean readOnly){
        JdbcColumn c = clone();
        c.readOnly = readOnly;
        return c;
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="signed : boolean">
    protected boolean signed;
    public boolean isSigned(){ return signed; }
    public JdbcColumn signed( boolean signed){
        JdbcColumn c = clone();
        c.signed = signed;
        return c;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="writable : boolean">
    protected boolean writable;
    public boolean isWritable(){ return writable; }
    public JdbcColumn writable( boolean writable){
        JdbcColumn c = clone();
        c.writable = writable;
        return c;
    }
    //</editor-fold>
    
    public static JdbcColumn createFrom( ResultSetMetaData meta, int column, ClassLoader cl )
        throws SQLException, ClassNotFoundException
    {
        if( meta==null )throw new IllegalArgumentException("meta == null");
        if( cl==null ){
            cl = Thread.currentThread().getContextClassLoader();
            if( cl==null )cl = JdbcColumn.class.getClassLoader();
        }
        
        String label = meta.getColumnLabel(column);
        String clsName = meta.getColumnClassName(column);
        
        Class cls = Class.forName(clsName, true, cl);
        
        JdbcColumn jc = new JdbcColumn(label, cls);
        jc = jc.columnLabel(label);
        
        String schemaName = meta.getSchemaName(column);
        jc = jc.schemaName(schemaName);
        
        String catalogName = meta.getCatalogName(column);
        jc = jc.catalogName(catalogName);
        
        String tableName = meta.getTableName(column);
        jc = jc.tableName(tableName);
        
        String columnName = meta.getColumnName(column);
        jc = jc.columnName(columnName);
        
        int colType = meta.getColumnType(column);
        jc = jc.columnType(colType);
        
        String colTypeName = meta.getColumnTypeName(column);
        jc = jc.columnTypeName(colTypeName);

        int displaySize = meta.getColumnDisplaySize(column);
        jc = jc.displaySize(displaySize);
        
        int precision = meta.getPrecision(column);
        jc = jc.precision(precision);
        
        int scale = meta.getScale(column);
        jc = jc.scale(scale);
        
        boolean ai = meta.isAutoIncrement(column);
        jc = jc.autoIncrement(ai);
        
        boolean caseSens = meta.isCaseSensitive(column);
        jc = jc.caseSensitive(caseSens);
        
        jc = jc.currency(meta.isCurrency(column));
        
        jc = jc.definitelyWritable(meta.isDefinitelyWritable(column));
        
        jc = jc.nullable(meta.isNullable(column));
        
        jc = jc.searchable(meta.isSearchable(column));
        
        jc = jc.readOnly(meta.isReadOnly(column));
        
        jc = jc.signed(meta.isSigned(column));
        
        jc = jc.writable(meta.isWritable(column));
        
        return jc;
    }
}
