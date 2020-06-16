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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Пишет данные в QueryStream
 * @author nt.gocha@gmail.com
 */
public class StreamQueryWriter implements QueryWriter {
    /**
     * Конструктор
     */
    public StreamQueryWriter(){
    }
    
    /**
     * Конструктор
     * @param qs поток SQL куда будет записан результат выполнения
     */
    public StreamQueryWriter( QueryStream qs){
        this.queryStream = qs;
    }
    
    protected QueryStream queryStream;

    /**
     * Возвращает поток для записи SQL данных
     * @return поток для записи
     */
    public synchronized QueryStream getQueryStream() {
        return queryStream;
    }

    /**
     * Указывает поток для записи SQL данных
     * @param queryStream поток для записи
     */
    public synchronized void setQueryStream(QueryStream queryStream) {
        this.queryStream = queryStream;
    }
    
    @Override
    public void begin() {
        QueryStream qs = getQueryStream();
        if( qs==null )throw new IllegalStateException("queryStream not set");
        
        qs.queryStreamBegin();
    }

    @Override
    public void writeResultSet( ResultSet rs, int rsIndex) {
        writeResultSet(rs, rsIndex, false);
    }
    
    protected void writeResultSet( ResultSet rs, int rsIndex, boolean generated) {
        if( rs==null )throw new IllegalArgumentException("rs == null");

        QueryStream qs = getQueryStream();
        if( qs==null )throw new IllegalStateException("queryStream not set");

        try {
            try{
                if(!generated){
                    qs.tableBegin(rsIndex, null);
                }else{
                    qs.generatedKeysBegin(null);
                }

                ResultSetMetaData rsmeta = rs.getMetaData();
                if( rsmeta==null )return;
                int cc = -1;
                try{
                    qs.metaBegin(null);  
                    cc = rsmeta.getColumnCount();
                    for( int cn=1; cn<=cc; cn++ ){
                        try{
                            qs.columnBegin(cn-1, null);
                            qs.columnProperty("catalogName", rsmeta.getCatalogName(cn), null);                            
                            qs.columnProperty("className", rsmeta.getColumnClassName(cn), null);                            
                            qs.columnProperty("displaySize", rsmeta.getColumnDisplaySize(cn), null);                            
                            qs.columnProperty("label", rsmeta.getColumnLabel(cn), null);                            
                            qs.columnProperty("name", rsmeta.getColumnName(cn), null);                            
                            qs.columnProperty("type", rsmeta.getColumnType(cn), null);                            
                            qs.columnProperty("typeName", rsmeta.getColumnTypeName(cn), null);                            
                            qs.columnProperty("precision", rsmeta.getPrecision(cn), null);                            
                            qs.columnProperty("scale", rsmeta.getScale(cn), null);                            
                            qs.columnProperty("schemaName", rsmeta.getSchemaName(cn), null);                            
                            qs.columnProperty("tableName", rsmeta.getTableName(cn), null);                            
                            qs.columnProperty("autoIncrement", rsmeta.isAutoIncrement(cn), null);                            
                            qs.columnProperty("caseSensitive", rsmeta.isCaseSensitive(cn), null);                            
                            qs.columnProperty("currency", rsmeta.isCurrency(cn), null);                            
                            qs.columnProperty("definitelyWritable", rsmeta.isDefinitelyWritable(cn), null);                            
                            qs.columnProperty("nullable", rsmeta.isNullable(cn), null);                            
                            qs.columnProperty("readOnly", rsmeta.isReadOnly(cn), null);                            
                            qs.columnProperty("searchable", rsmeta.isSearchable(cn), null);                            
                            qs.columnProperty("signed", rsmeta.isSigned(cn), null);                            
                            qs.columnProperty("writable", rsmeta.isWritable(cn), null);                            
                        }finally{
                            qs.columnEnd();
                        }
                    }
                }finally{
                    qs.metaEnd();
                }
                
                try{
                    qs.dataBegin(null);
                    int rowIndex = -1;
                    while(true){
                        try{
                            boolean nx = rs.next();
                            if( !nx )break;
                            rowIndex++;
                            
                            try{
                                qs.rowBegin(rowIndex, null);
                                
                                for(int cn=1; cn<=cc; cn++ ){                                    
                                    qs.cell(cn-1, rs.getObject(cn), null);
                                }
                            }finally{
                                qs.rowEnd();
                            }
                        }catch ( SQLException ex) {
                            Logger.getLogger(StreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
                            qs.error(new Err(ex), null);
                        }
                    }
                }finally{
                    qs.dataEnd();
                }
            }finally{
                if(!generated){
                    qs.tableEnd();
                }else{
                    qs.generatedKeysEnd();
                }
            }
        } catch ( SQLException ex) {
            Logger.getLogger(StreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            qs.error(new Err(ex), null);
        }
    }

    @Override
    public void writeGeneratedKeys( ResultSet rs) {
        QueryStream qs = getQueryStream();
        if( qs==null )throw new IllegalStateException("queryStream not set");
        
        writeResultSet(rs, -1, true);
    }

    @Override
    public void writeUpdateCount(int count) {
        QueryStream qs = getQueryStream();
        if( qs==null )throw new IllegalStateException("queryStream not set");
    }

    @Override
    public void writeMessage( SQLWarning message) {
        QueryStream qs = getQueryStream();
        if( qs==null )throw new IllegalStateException("queryStream not set");
        
        qs.message(new Message(message));
    }

    @Override
    public void end() {
        QueryStream qs = getQueryStream();
        if( qs==null )throw new IllegalStateException("queryStream not set");
        
        qs.queryStreamEnd();
    }

    @Override
    public void writeError( Throwable err) {
        QueryStream qs = getQueryStream();
        if( qs==null )throw new IllegalStateException("queryStream not set");
        
        if( err!=null )qs.error(new Err(err), null);
    }
}
