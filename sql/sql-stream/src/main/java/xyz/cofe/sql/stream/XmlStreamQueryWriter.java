/*
 * The MIT License
 *
 * Copyright 2018 user.
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
package xyz.cofe.sql.stream;

import xyz.cofe.sql.qexec.QueryWriter;
import xyz.cofe.xml.FormatXMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOError;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class XmlStreamQueryWriter extends DataStreamAbstract implements QueryWriter {
    protected FormatXMLWriter out;
    
    public XmlStreamQueryWriter( XMLStreamWriter xw){
        if( xw==null )throw new IllegalArgumentException("xw == null");
        if( xw instanceof FormatXMLWriter ){
            out = (FormatXMLWriter)xw;
        }else{
            out = (FormatXMLWriter)new FormatXMLWriter(xw);
        }
    }
    public XmlStreamQueryWriter( FormatXMLWriter xw){
        if( xw==null )throw new IllegalArgumentException("xw == null");
        out = xw;
    }
    public XmlStreamQueryWriter( Writer wr){
        if( wr==null )throw new IllegalArgumentException("wr");
        try {
            out = new FormatXMLWriter(wr);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("XMLStreamException "+ex.toString(),ex);
        }
    }
    public XmlStreamQueryWriter( OutputStream out, Charset cs){
        if( out==null )throw new IllegalArgumentException("out");
        try {
            this.out = new FormatXMLWriter(out, cs!=null ? cs : Charset.defaultCharset());
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("XMLStreamException "+ex.toString(),ex);
        }
    }
    public XmlStreamQueryWriter( OutputStream out, String cs){
        if( out==null )throw new IllegalArgumentException("out");
        try {
            this.out = new FormatXMLWriter(out, cs!=null ? Charset.forName(cs) : Charset.defaultCharset());
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("XMLStreamException "+ex.toString(),ex);
        }
    }
    
    private int beginCall = 0;
    private int endCall = 0;

    @Override
    public synchronized void begin() {
        beginCall++;
        try {
            writeBegin();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected void writeBegin() throws XMLStreamException{
        out.writeStartElement("query");
    }
    protected void writeEnd() throws XMLStreamException{
        out.writeEndElement();
    }

    @Override
    public synchronized void writeResultSet( ResultSet rs, int rsIndex) {
        try {
            writeResultSet("resultSet", rs, rsIndex);
        } catch ( SQLException | XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected void writeResultSet( String tag, ResultSet rs, int rsIndex) throws XMLStreamException, SQLException{
        out.writeStartElement(tag);
        if( rsIndex>=0 )out.writeAttribute("index", Integer.toString(rsIndex));
        
        ResultSetMetaData meta = rs.getMetaData();
        writeResultSetMeta(meta);
        writeResultSetData(rs);
        
        out.writeEndElement();
        out.flush();
    }
    
    protected void writeResultSetMeta( ResultSetMetaData meta) throws XMLStreamException, SQLException{
        out.writeStartElement("meta");
        int cc = meta.getColumnCount();
        out.writeAttribute("columns", Integer.toString(cc));
        for( int cn=1; cn<=cc; cn++ ){
            writeResultSetMeta(meta,cn);
        }
        out.writeEndElement();
    }
    
    private void writeAttr( String attr, Object val, boolean skipNull, String nullval) throws XMLStreamException{
        if( val==null ){
            if( skipNull )return;
            out.writeAttribute(attr, nullval);
            return;
        }
        out.writeAttribute(attr, getTypeCast().cast(val, String.class));
    }
    private void writeAttr( String attr, Object val) throws XMLStreamException{
        writeAttr(attr, val, true, "null");
    }
    private void writeTag( String tag, Object content) throws XMLStreamException{
        if( content!=null ){
            out.writeStartElement(tag);
            out.writeCharacters(getTypeCast().cast(content, String.class));
            out.writeEndElement();
        }
    }
    
    protected Map<Integer, String> columnsLabel = new LinkedHashMap<>();
    //protected Map<Integer,String> columnsName = new LinkedHashMap<>();
    //protected Map<Integer,String> columnsName = new LinkedHashMap<>();

    protected void writeResultSetMeta( ResultSetMetaData meta, int colnum) throws XMLStreamException, SQLException{
        out.writeStartElement("column");
        
        writeAttr("index", colnum-1);
        writeTag("label", meta.getColumnLabel(colnum));
        
        columnsLabel.put(colnum-1, meta.getColumnLabel(colnum));
        
        writeTag("className", meta.getColumnClassName(colnum));
        writeTag("name",meta.getColumnName(colnum));
        writeTag("type",meta.getColumnType(colnum));
        writeTag( "typeName", meta.getColumnTypeName(colnum) );
        writeTag( "displaySize", meta.getColumnDisplaySize(colnum) );
        writeTag( "catalog", meta.getCatalogName(colnum) );
        writeTag( "schema", meta.getSchemaName(colnum) );
        writeTag( "table", meta.getTableName(colnum) );
        writeTag( "scale", meta.getScale(colnum) );
        writeTag( "precision", meta.getPrecision(colnum) );
        writeTag( "autoIncrement", meta.isAutoIncrement(colnum) );
        writeTag( "caseSensitive", meta.isCaseSensitive(colnum) );
        writeTag( "currency", meta.isCurrency(colnum) );
        writeTag( "definitelyWritable", meta.isDefinitelyWritable(colnum) );
        switch( meta.isNullable(colnum) ){
            case ResultSetMetaData.columnNoNulls: writeTag("nullable", "false"); break;
            case ResultSetMetaData.columnNullable: writeTag("nullable", "true"); break;
            case ResultSetMetaData.columnNullableUnknown: writeTag("nullable", "unknow"); break;
        }
        writeTag( "readOnly", meta.isReadOnly(colnum) );
        writeTag( "searchable", meta.isSearchable(colnum) );
        writeTag( "signed", meta.isSigned(colnum) );
        writeTag( "writable", meta.isWritable(colnum) );
        //meta.;
        out.writeEndElement();
    }
    protected void writeResultSetData( ResultSet rs) throws XMLStreamException, SQLException{
        ResultSetMetaData meta = rs.getMetaData();
        int cc = meta.getColumnCount();
        out.writeStartElement("data");
        int rowIdx = -1;
        while(rs.next()){
            rowIdx++;
            writeRow(rs, rowIdx, cc);
        }
        out.writeEndElement();
    }
    protected void writeRow( ResultSet rs, int rowIndex, int columnCount) throws XMLStreamException, SQLException{
        out.writeStartElement("row");
        writeAttr("ri", rowIndex);
        for( int cn=1; cn<=columnCount; cn++){
            Object v = rs.getObject(cn);
            writeCell(v, rowIndex, cn-1, rs);
        }
        out.writeEndElement();
    }
    protected void writeCell( Object val, int rowIndex, int columnIndex, ResultSet rs) throws XMLStreamException, SQLException{
        out.writeStartElement("cell");
        writeAttr("ci", columnIndex);
        
        String colLabel = columnsLabel.get(columnIndex);
        if( colLabel!=null )writeAttr("label", colLabel);
        
        if( val!=null ){
            out.writeAttribute("null", "false");
            out.writeAttribute("type", val.getClass().getName());
            String str = null;
            try{
                str = getTypeCast().cast(val, String.class);
            }catch( Throwable err){
                throw new Error("can't cast from ("+val+") to String; rowIndex="+rowIndex+", columnIndex"+columnIndex,err);
            }        
            out.writeCharacters(str);
        }else{
            out.writeAttribute("null", "true");
        }
        out.writeEndElement();
    }
    
    @Override
    public synchronized void writeGeneratedKeys( ResultSet rs) {
        try {
            writeResultSet("generated", rs, -1);
        } catch ( SQLException | XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    @Override
    public synchronized void writeUpdateCount(int count) {
        try {
            out.writeStartElement("updateCount");
            out.writeCharacters(Integer.toString(count));
            out.writeEndElement();
            out.flush();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected List<SQLWarning> messages = new CopyOnWriteArrayList<>();

    @Override
    public synchronized void writeMessage( SQLWarning message) {
//        try {
            messages.add(message);
//        } catch (XMLStreamException ex) {
//            Logger.getLogger(XmlStreamWriter.class.getName()).log(Level.SEVERE, null, ex);
//            throw new IOError(ex);
//        }
    }
    
    protected synchronized void writeMessages( List<SQLWarning> messages) {
        try {
            if( messages!=null ){
                out.writeStartElement("messages");
                for( SQLWarning mes : messages ){
                    if(mes!=null){
                        postWriteMessage(mes);
                    }
                }
                out.writeEndElement();
                out.flush();
            }
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void postWriteMessage( SQLWarning message) throws XMLStreamException{
        out.writeStartElement("message");
        
        int c = message.getErrorCode();
        String lmes = message.getLocalizedMessage();
        String mes = message.getMessage();
        String st = message.getSQLState();
        
        out.writeAttribute("code", Integer.toString(c));
        if( st!=null )out.writeAttribute("state", st);

        if( lmes!=null ){
            out.writeStartElement("textLocal");
            out.writeCharacters(lmes);
            out.writeEndElement();
        }
        
        if( mes!=null ){
            out.writeStartElement("text");
            out.writeCharacters(mes);
            out.writeEndElement();
        }
        
        out.writeEndElement();
    }

    @Override
    public synchronized void end() {
        endCall++;
        try {
            if( messages!=null ){
                writeMessages(messages);
            }
            writeEnd();
            out.flush();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    @Override
    public void writeError( Throwable err) {
    }
}
