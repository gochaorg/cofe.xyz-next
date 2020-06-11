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

package xyz.cofe.data.store;

import xyz.cofe.collection.graph.Path;
import xyz.cofe.data.DataColumn;
import xyz.cofe.data.DataRow;
import xyz.cofe.data.DataTable;
import xyz.cofe.simpletypes.SimpleTypes;
import xyz.cofe.text.out.Output;
import xyz.cofe.typeconv.ExtendedCastGraph;
import xyz.cofe.typeconv.TypeCastGraph;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class CSVStore extends CSVDesc
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(CSVStore.class.getName());
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
        logger.entering(CSVStore.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(CSVStore.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(CSVStore.class.getName(), method, result);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="castGraph : TypeCastGraph">
    protected TypeCastGraph castGraph;
    public TypeCastGraph getCastGraph() {
        synchronized(this){
            if( castGraph!=null )return castGraph;
            castGraph = new ExtendedCastGraph();
            return castGraph;
        }
    }
    public void setCastGraph(TypeCastGraph castGraph) {
        synchronized(this){
            this.castGraph = castGraph;
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="asString(val):String">
    public String asString( Object val ){
        if( val==null )return null;
        TypeCastGraph tc = getCastGraph();
        return tc.cast(val, String.class);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="asValueOf(str,cls):Object">
    public Object asValueOf( String str, Class cls ){
        if( cls==null )throw new IllegalArgumentException("cls == null");
        if( str==null ){
            if( !SimpleTypes.isSimple(cls) ) return null;
            if( SimpleTypes.boolObject().equals(cls) )return Boolean.FALSE;
            if( SimpleTypes.byteObject().equals(cls) )return Byte.valueOf((byte)0);
            if( SimpleTypes.charObject().equals(cls) )return Character.valueOf((char)(int)0);
            if( SimpleTypes.doubleObject().equals(cls) )return Double.valueOf(0d);
            if( SimpleTypes.floatObject().equals(cls) )return Float.valueOf(0f);
            if( SimpleTypes.intObject().equals(cls) )return Integer.valueOf(0);
            if( SimpleTypes.longObject().equals(cls) )return Long.valueOf(0L);
            if( SimpleTypes.shortObject().equals(cls) )return Short.valueOf((short)0);
            return null;
        }
        TypeCastGraph tc = getCastGraph();
        return tc.cast(str, cls);
    }
    //</editor-fold>    
    //<editor-fold defaultstate="collapsed" desc="support(De)Serialize(cls):boolean">
    public boolean supportDeserialize( Class cls ){
        if( cls==null )throw new IllegalArgumentException("cls == null");
        synchronized(this){
            TypeCastGraph tc = getCastGraph();
            Path path = tc.findPath(String.class, cls);
            return path != null;
        }
    }
    public boolean supportSerialize( Class cls ){
        if( cls==null )throw new IllegalArgumentException("cls == null");
        synchronized(this){
            TypeCastGraph tc = getCastGraph();
            Path path = tc.findPath(cls, String.class);
            return path != null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Конструктор">
    public CSVStore(){
    }
    public CSVStore( CSVStore sample ){
        super(sample);
    }
    //</editor-fold>

    @Override
    public CSVStore clone() {
        return new CSVStore(this);
    }
    
    protected CSVUtil csvUtil = new CSVUtil();
    
    //<editor-fold defaultstate="collapsed" desc="writeAllRows : boolean">
    protected boolean writeAllRows = false;
    
    public boolean isWriteAllRows() {
        return writeAllRows;
    }
    
    public void setWriteAllRows(boolean writeAllRows) {
        Object old,cur;
        synchronized(this){
            old = this.writeAllRows;
            this.writeAllRows = writeAllRows;
            cur = this.writeAllRows;
        }
        firePropertyChange("writeAllRows", old, cur);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="specialColumns : List<CSVSpecialColumn>">
    protected List<CSVSpecialColumn> specialColumns;
    public List<CSVSpecialColumn> getSpecialColumns(){
        synchronized(this){
            if( specialColumns!=null ){
                return specialColumns;
            }
            specialColumns = new ArrayList<>();
            return specialColumns;
        }
    }
    public void setSpecialColumns(List<CSVSpecialColumn> list){
        synchronized(this){
            this.specialColumns = list;
        }
    }
    //</editor-fold>
    
    public synchronized void write( Writer out, DataTable table ){
        if( out==null )throw new IllegalArgumentException("out == null");
        if( table==null )throw new IllegalArgumentException("table == null");
        
        Output output = new Output(out);
        write(output, table);
        output.flush();
    }
    
    public synchronized void write( Output out, DataTable table ){
        if( out==null )throw new IllegalArgumentException("out == null");
        if( table==null )throw new IllegalArgumentException("table == null");
        
        if( isFirstLineAsName() ){
            writeHeader(out, table);
        }
        
        Iterable<DataRow> itr = table.getRowsIterable();
        if( writeAllRows )itr = table.getRowsIterableAll();
        
        for( DataRow dr : itr ){
            if( dr==null )continue;
            
            String[] cells = toStringArray(table, dr);
            if( cells==null )continue;
            
            String line = csvUtil.toString(cells, this);
            out.println(line);
        }
    }
    
    protected synchronized void writeHeader( Output out, DataTable dt ){
        ArrayList<String> cells = new ArrayList(dt.getColumnsCount());
        
        for( CSVSpecialColumn sc : getSpecialColumns() ){
            cells.add( sc.getName() );
        }
        
        for( DataColumn dc : dt.getColumns()){
            cells.add(dc.getName());
        }
        
        String[] arr = cells.toArray(new String[0]);
        String line = csvUtil.toString(arr, this);
        
        out.println(line);
    }
    
    protected synchronized String[] toStringArray( DataTable dt, DataRow row ){
        ArrayList<String> cells = new ArrayList(dt.getColumnsCount());
        
        for( CSVSpecialColumn sc : getSpecialColumns() ){
            String str = sc.asString(row);
            if( str==null )str = "";
            
            cells.add(str);
        }
        
        for( int ci=0; ci<dt.getColumnsCount(); ci++ ){
            Object val = row.get(ci);
            
            String str = asString(val);
            if( str==null )str = "";
            
            cells.add(str);
        }
        
        String[] arr = cells.toArray(new String[0]);
        return arr;
    }
}
