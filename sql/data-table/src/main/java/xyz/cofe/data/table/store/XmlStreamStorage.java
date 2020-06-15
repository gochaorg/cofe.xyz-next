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

package xyz.cofe.data.table.store;

import xyz.cofe.collection.graph.Path;
import xyz.cofe.data.table.DataColumn;
import xyz.cofe.data.table.DataRow;
import xyz.cofe.data.table.DataTable;
import xyz.cofe.simpletypes.SimpleTypes;
import xyz.cofe.typeconv.ExtendedCastGraph;
import xyz.cofe.typeconv.TypeCastGraph;
import xyz.cofe.xml.FormatXMLWriter;
import xyz.cofe.xml.stream.path.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Сохранение таблицы в XML
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class XmlStreamStorage {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(XmlStreamStorage.class.getName());
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
        logger.entering(XmlStreamStorage.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(XmlStreamStorage.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(XmlStreamStorage.class.getName(), method, result);
    }
    //</editor-fold>
    
    private static final String TABLE_TAG = "datatable";
    private static final String COLUMN_TAG = "column";
    private static final String COLUMN_NAME_ATTR = "name";
    private static final String ROW_TAG = "row";
    
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
    
    //<editor-fold defaultstate="collapsed" desc="write()">
    public synchronized void write( XMLStreamWriter xout, DataColumn memColumn ) throws XMLStreamException {
        if( xout==null )throw new IllegalArgumentException("xout==null");
        if( memColumn==null )throw new IllegalArgumentException("memColumn==null");
        
        xout.writeStartElement(COLUMN_TAG);
        xout.writeAttribute(COLUMN_NAME_ATTR, memColumn.getName());
        xout.writeAttribute("dataType", memColumn.getDataType().getName());
        xout.writeAttribute("allowNull", memColumn.isAllowNull() ? "true" : "false");
        xout.writeAttribute("allowSubTypes", memColumn.isAllowSubTypes() ? "true" : "false");
        xout.writeEndElement();
    }
    
    public synchronized void write( XMLStreamWriter xout, DataRow memRow )
        throws XMLStreamException 
    {
        if( xout==null )throw new IllegalArgumentException("xout==null");
        if( memRow==null )throw new IllegalArgumentException("memRow==null");
        
        xout.writeStartElement(ROW_TAG);
        
        xout.writeAttribute("changed", memRow.isChanged()  ? "true" : "false");
        xout.writeAttribute("deleted", memRow.isDeleted()  ? "true" : "false");
        xout.writeAttribute("inserted", memRow.isInserted() ? "true" : "false");
        xout.writeAttribute("updated", memRow.isUpdated()  ? "true" : "false");
        
        Object[] vals = memRow.getData();
        Object[] orig = memRow.getOrigin();
        int changeCnt = memRow.getChangeCount();
        xout.writeAttribute("changeCounter", Integer.toString(changeCnt));
        
        List<DataRow.ChangedValue> chvals = memRow.getChangedValues();
        if( chvals!=null && chvals.size()>0 ){
            xout.writeStartElement("changes");
            for( DataRow.ChangedValue chval : chvals ){
                xout.writeStartElement("changed");
                xout.writeAttribute(COLUMN_TAG, Integer.toString(chval.getColumn()));
                
                Object from = chval.getFrom();
                Object to = chval.getTo();
                
                if( from!=null ){
                    xout.writeStartElement("from");
                    xout.writeAttribute("type",from.getClass().getName());
                    xout.writeCharacters(asString(from));
                    xout.writeEndElement();
                }
                
                if( to!=null ){
                    xout.writeStartElement("to");
                    xout.writeAttribute("type",to.getClass().getName());
                    xout.writeCharacters(asString(to));
                    xout.writeEndElement();
                }
                
                xout.writeEndElement();
            }
            xout.writeEndElement();
        }
        
        if( orig!=null ){
            xout.writeStartElement("origin");
            for( int i=0; i<orig.length; i++ ){
                Object v = orig[i];
                
                xout.writeStartElement("value");
                if( v==null ){
                    xout.writeAttribute("isnull", "true");
                }else{
                    xout.writeAttribute("isnull", "false");
                    xout.writeAttribute("type", v.getClass().getName());
                    
                    xout.writeCharacters(asString(v));
                }
                xout.writeEndElement();
            }
            xout.writeEndElement();
        }
        
        if( vals!=null ){
            xout.writeStartElement("current");
            for( int i=0; i<vals.length; i++ ){
                Object v = vals[i];
                
                xout.writeStartElement("value");
                if( v==null ){
                    xout.writeAttribute("isnull", "true");
                }else{
                    xout.writeAttribute("isnull", "false");
                    xout.writeAttribute("type", v.getClass().getName());
                    
                    xout.writeCharacters(asString(v));
                }
                xout.writeEndElement();
            }
            xout.writeEndElement();
        }
        
        xout.writeEndElement();
    }
    
    public static final String COLUMNS_TAG = "columns";
    public static final String ROWS_TAG = "rows";
    
    public synchronized void write( XMLStreamWriter xout, DataTable dataTable )
        throws XMLStreamException 
    {
        if( xout==null )throw new IllegalArgumentException("xout==null");
        if( dataTable==null )throw new IllegalArgumentException("dataTable==null");
        
        synchronized(dataTable){
            xout.writeStartElement(TABLE_TAG);
            
            xout.writeStartElement(COLUMNS_TAG);
            for( DataColumn mc : dataTable.getColumns()){
                write(xout, mc);
            }
            xout.writeEndElement();
            
            xout.writeStartElement(ROWS_TAG);
            for( int ri=0; ri<dataTable.getRowsCount(); ri++ ){
                DataRow mrow = dataTable.getRow(ri);
                if( mrow==null )throw new IllegalStateException("dataTable.getRow("+ri+")==null");
                if( mrow.isInserted() )continue;
                if( mrow.isDeleted() )continue;
                
                write(xout, mrow);
            }
            xout.writeEndElement();

            for( DataRow dr : dataTable.getRowsIterableAll() ){
                if( dr.isInserted() ){
                    xout.writeStartElement("inserted");
                    write(xout, dr);
                    xout.writeEndElement();
                }else if( dr.isDeleted() ){
                    xout.writeStartElement("deleted");
                    write(xout, dr);
                    xout.writeEndElement();
                }
            }
            
            xout.writeEndElement();
        }
    }
    
    public synchronized void write( Writer xout, DataTable mtable ){
        FormatXMLWriter fxml;
        try {
            fxml = new FormatXMLWriter(xout);
            fxml.setWriteOutline(true);
            
            XmlStreamStorage mtStorage = new XmlStreamStorage();
            mtStorage.write(fxml, mtable);
            
            fxml.flush();
        } catch (XMLStreamException ex) {
            //Logger.getLogger(MemTableTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public synchronized void write( OutputStream xout, Charset cs, DataTable mtable ){
        if( cs==null )cs = Charset.forName("utf-8");
        FormatXMLWriter fxml;
        try {
            fxml = new FormatXMLWriter(xout,cs.name());
            fxml.setWriteOutline(true);
            
            XmlStreamStorage mtStorage = new XmlStreamStorage();
            mtStorage.write(fxml, mtable);
            
            fxml.flush();
        } catch (XMLStreamException ex) {
            //Logger.getLogger(MemTableTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public synchronized void write( OutputStream xout, DataTable mtable ){
        write(xout, Charset.forName("utf-8"), mtable);
    }
    
    public synchronized void write( File xout, Charset cs, DataTable mtable ){
        if( cs==null )cs = Charset.forName("utf-8");
        FormatXMLWriter fxml;
        try {
            fxml = new FormatXMLWriter(xout,cs);
            fxml.setWriteOutline(true);
            
            XmlStreamStorage mtStorage = new XmlStreamStorage();
            mtStorage.write(fxml, mtable);
            
            fxml.flush();
        } catch (XMLStreamException ex) {
            //Logger.getLogger(MemTableTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public synchronized void write( File xout, DataTable mtable ){
        FormatXMLWriter fxml;
        try {
            fxml = new FormatXMLWriter(xout, Charset.forName("utf-8"));
            fxml.setWriteOutline(true);
            
            XmlStreamStorage mtStorage = new XmlStreamStorage();
            mtStorage.write(fxml, mtable);
            
            fxml.flush();
        } catch (XMLStreamException ex) {
            //Logger.getLogger(MemTableTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    //</editor-fold>
    
    public class XmlTableVisitor 
        extends XVisitorAdapter
    {
        public final TableBuilder tbuilder;
        
        public XmlTableVisitor(TableBuilder tbuilder){
            this.tbuilder = tbuilder;
        }
        
        @PathMatch(enter = TABLE_TAG)
        public void begin( XEventPath path ){
            tbuilder.begin();
        }
        @PathMatch(exit = TABLE_TAG)
        public void end( XEventPath path ){
            tbuilder.end();
        }

        public ClassLoader classLoader(){
            ClassLoader cl = tbuilder.getClassLoader();
            if( cl==null )cl = Thread.currentThread().getContextClassLoader();
            if( cl==null )cl = XmlStreamStorage.class.getClassLoader();
            return cl;
        }

        //<editor-fold defaultstate="collapsed" desc="columns">
        @PathMatch(enter = COLUMNS_TAG)
        public void beginColumn(XEventPath path){
            tbuilder.beginColumns();
        }

        @PathMatch(enter = COLUMN_TAG)
        public void column(XEventPath path){
            String name = path.readAttributeAsString(COLUMN_NAME_ATTR, "?noAttrib_name");
            String type = path.readAttributeAsString("dataType", "?noAttrib_dataType");

            ClassLoader cl = classLoader();

            Class dataType = null;
            try {
                dataType = Class.forName(type, true, cl);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(XmlStreamStorage.class.getName()).log(Level.SEVERE, null, ex);
                dataType = String.class;
            }

            DataColumn mc = new DataColumn(name, dataType);

            switch( path.readAttributeAsString("allowNull", "?") ){
                case "true": mc = mc.allowNull(true); break;
                case "false": mc = mc.allowNull(false); break;
            }
            switch( path.readAttributeAsString("allowSubTypes", "?") ){
                case "true": mc = mc.allowSubTypes(true); break;
                case "false": mc = mc.allowSubTypes(false); break;
            }

            tbuilder.addColumn(mc);
        }

        @PathMatch(exit = COLUMNS_TAG)
        public void endColumn(XEventPath path){
            tbuilder.endColumns();
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="rows">
        public final ArrayList origin = new ArrayList();
        public final ArrayList current = new ArrayList();
        public boolean rowChanged = false;
        public boolean rowDeleted = false;
        public boolean rowInserted = false;
        public boolean rowUpdated = false;
        public int rowChangeCounter = 0;

        @PathMatch(enter = ROW_TAG)
        public void beginRow( XEventPath path ){
            origin.clear();
            current.clear();
            rowChanged = false;
            rowDeleted = false;
            rowInserted = false;
            rowUpdated = false;
            rowChangeCounter = 0;

            rowChanged = path.readAttributeAsBoolean("changed", false);
            rowDeleted = path.readAttributeAsBoolean("deleted", false);
            rowInserted = path.readAttributeAsBoolean("inserted", false);
            rowUpdated = path.readAttributeAsBoolean("updated", false);
            rowChangeCounter = path.readAttributeAsInteger("changeCounter", 0);
        }

        @PathMatch(enter = "row/origin/value")
        public void rowOriginNullValue( XEventPath path ){
            if( path.readAttributeAsBoolean("isnull", false) ){
                origin.add(null);
            }
        }

        @PathMatch(content = "row/origin/value")
        public void rowOriginValue( XEventPath path, String content ){
            if( path.readAttributeAsBoolean("isnull", false) ){
                return;
            }

            String type = path.readAttributeAsString("type", "?noAttrib_type");

            try {
                Class cls = Class.forName(type, true, classLoader());
                Object v = asValueOf(content, cls);
                origin.add(v);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(XmlStreamStorage.class.getName()).log(Level.SEVERE, null, ex);
                origin.add(content);
            }
        }

        @PathMatch(enter = "row/current/value")
        public void rowCurrentNullValue( XEventPath path ){
            if( path.readAttributeAsBoolean("isnull", false) ){
                current.add(null);
            }
        }

        @PathMatch(content = "row/current/value")
        public void rowCurrentValue( XEventPath path, String content ){
            if( path.readAttributeAsBoolean("isnull", false) ){
                return;
            }

            String type = path.readAttributeAsString("type", "?noAttrib_type");

            try {
                Class cls = Class.forName(type, true, classLoader());
                Object v = asValueOf(content, cls);
                current.add(v);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(XmlStreamStorage.class.getName()).log(Level.SEVERE, null, ex);
                current.add(content);
            }
        }

        @PathMatch(exit = ROW_TAG)
        public void endRow( XEventPath path ){
            DataTable mt = tbuilder.getDataTable();
            if( mt==null ){
                Logger.getLogger(XmlStreamStorage.class.getName()).log(Level.SEVERE, "table builder return null table");
                return;
            }

            DataRow mrow = new DataRow(mt, current.toArray(), origin.toArray(), rowChangeCounter);

            if( rowInserted ){
                tbuilder.insertedRow(mrow);
            }else if( rowDeleted ){
                tbuilder.deletedRow(mrow);
            }else if( rowChanged ){
                tbuilder.changedRow(mrow);
            }else{
                tbuilder.unchangedRow(mrow);
            }
        }
        //</editor-fold>
    }
    
    public XVisitor createXVisitor( final TableBuilder tbuilder ){
        if( tbuilder==null )throw new IllegalArgumentException("tbuilder == null");
        return new XmlTableVisitor(tbuilder);
    }
    
    public synchronized void read( TableBuilder tbuilder, URL url ) {
        if( tbuilder==null )throw new IllegalArgumentException("tbuilder==null");
        if( url==null )throw new IllegalArgumentException("url==null");
        
        try {
            new XmlReader(url, createXVisitor(tbuilder));
        } catch (IOException | XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    public synchronized void read( TableBuilder tbuilder, Reader xml ) {
        if( tbuilder==null )throw new IllegalArgumentException("tbuilder==null");
        if( xml==null )throw new IllegalArgumentException("xml == null");
        
        try {
            new XmlReader(xml, createXVisitor(tbuilder));
        } catch (XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    public synchronized void read( TableBuilder tbuilder, InputStream xml, Charset cs ) {
        if( tbuilder==null )throw new IllegalArgumentException("tbuilder==null");
        if( xml==null )throw new IllegalArgumentException("xml == null");
        
        try {
            if( cs!=null ){
                new XmlReader(xml, cs, createXVisitor(tbuilder));
            }else{
                new XmlReader(xml, createXVisitor(tbuilder));
            }
        } catch (XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    public synchronized void read( TableBuilder tbuilder, InputStream xml ) {
        if( tbuilder==null )throw new IllegalArgumentException("tbuilder==null");
        if( xml==null )throw new IllegalArgumentException("xml == null");
        
        try {
            new XmlReader(xml, createXVisitor(tbuilder));
        } catch (XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    public synchronized void read( TableBuilder tbuilder, File xml, Charset cs ) {
        if( tbuilder==null )throw new IllegalArgumentException("tbuilder==null");
        if( xml==null )throw new IllegalArgumentException("xml == null");
        
        try {
            if( cs!=null ){
                new XmlReader(xml, cs, createXVisitor(tbuilder));
            }else{
                new XmlReader(xml, createXVisitor(tbuilder));
            }
        } catch (IOException | XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    public synchronized void read( TableBuilder tbuilder, File xml ) {
        if( tbuilder==null )throw new IllegalArgumentException("tbuilder==null");
        if( xml==null )throw new IllegalArgumentException("xml == null");
        
        try {
            new XmlReader(xml, createXVisitor(tbuilder));
        } catch (IOException | XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
}
