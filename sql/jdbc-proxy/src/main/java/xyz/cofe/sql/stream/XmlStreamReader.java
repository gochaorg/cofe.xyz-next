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

import xyz.cofe.xml.stream.path.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOError;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Читает результат выборки SQL из XML потока
 * @author nt.gocha@gmail.com
 */
public class XmlStreamReader extends DataStreamAbstract {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(XmlStreamReader.class.getName());
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
        logger.entering(XmlStreamReader.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(XmlStreamReader.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(XmlStreamReader.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Читает данные из XML потока и пишет их в QueryStream
     */   
    public static class XmlStreamVisitor extends XVisitorAdapter {
        //<editor-fold defaultstate="collapsed" desc="queryStream : QueryStream">
        protected QueryStream queryStream;
        /**
         * Указывает принимающий поток
         * @return принимающий поток
         */
        public synchronized QueryStream getQueryStream() {
            if( queryStream==null ){
                queryStream = new QueryStreamDummy();
            }
            return queryStream;
        }
        /**
         * Указывает принимающий поток
         * @param queryStream принимающий поток
         */
        public synchronized void setQueryStream( QueryStream queryStream) {
            this.queryStream = queryStream;
        }
        //</editor-fold>
        
        @PathMatch(enter = "query")
        public synchronized void queryBegin( XEventPath path){
            getQueryStream().queryStreamBegin();
        }
        
        @PathMatch(exit = "query")
        public void queryEnd( XEventPath path){
            getQueryStream().queryStreamEnd();
        }
        
        @PathMatch(enter = "generated")
        public void generatedBegin( XEventPath path){
            getQueryStream().generatedKeysBegin(ScnMark.tryRead(path));
        }
        
        @PathMatch(exit = "generated")
        public void generatedEnd( XEventPath path){
            getQueryStream().generatedKeysEnd();
        }
        
        @PathMatch(enter = "resultSet")
        public void resultSetBegin( XEventPath path){
            getQueryStream().tableBegin(
                path.readAttributeAsInteger("index", -1),
                ScnMark.tryRead(path)
            );
        }
        @PathMatch(exit = "resultSet")
        public void resultSetEnd( XEventPath path){
            getQueryStream().tableEnd();
        }
        
        @PathMatch(enter = "meta")
        public void metaBegin( XEventPath path){
            getQueryStream().metaBegin(ScnMark.tryRead(path));
        }
        @PathMatch(exit = "meta")
        public void metaEnd( XEventPath path){
            getQueryStream().metaEnd();
        }
        
        @PathMatch(enter = "meta/column")
        public void columnBegin( XEventPath path){
            getQueryStream().columnBegin(path.readAttributeAsInteger("index", -1), ScnMark.tryRead(path));
        }

        //@PathMatch(enter = "meta/column/*")
        //public void columnProperty(XEventPath path){
        //    String colProp = path.getName();
        //    System.out.println("columnProperty "+colProp);
        //}

        @PathMatch(content = "meta/column/*")
        public void columnPropertyValue( XEventPath path, String value){
            String colProp = path.getName();
            //System.out.println("columnProperty "+colProp);
            getQueryStream().columnProperty(colProp, value, ScnMark.tryRead(path));
        }
        
        @PathMatch(exit = "meta/column")
        public void columnEnd( XEventPath path){
            //System.out.println("columnEnd");
            getQueryStream().columnEnd();
        }
        
        @PathMatch(enter = "data")
        public void dataBegin( XEventPath path){
            //System.out.println("dataBegin");
            getQueryStream().dataBegin(ScnMark.tryRead(path));
        }

        @PathMatch(enter = "data/row")
        public void rowBegin( XEventPath path){
            //System.out.println("rowBegin");
            getQueryStream().rowBegin(path.readAttributeAsInteger("ri", -1), ScnMark.tryRead(path));
        }
        
        @PathMatch(content = "data/row/cell")
        public void cell( XEventPath path, String strvalue){
            //System.out.println("cell");
            Integer ci = path.readAttributeAsInteger("ci", -1);
            Boolean nullValue = path.readAttributeAsBoolean("null", false);
            
            if( Objects.equals(nullValue, true) ){
                getQueryStream().cell(ci, null, ScnMark.tryRead(path));
                return;
            }
            
            Class targetType = null;
            String valTypeName = path.readAttributeAsString("type", null);
            if( valTypeName!=null && valTypeName.length()>0 ){
                try {
                    targetType = Class.forName(valTypeName);
                } catch ( ClassNotFoundException ex) {
                    Logger.getLogger(XmlStreamReader.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOError(ex);
                }
            }
            
            if( targetType==null ){
                targetType = String.class;
            }
            
            Object val = getTypeCastGraph().cast(strvalue,targetType);
            getQueryStream().cell(ci, val, ScnMark.tryRead(path));
        }
        
        @PathMatch(exit = "data/row")
        public void rowEnd( XEventPath path){
            //System.out.println("rowEnd");
            getQueryStream().rowEnd();
        }

        @PathMatch(content = "updateCount")
        public void updateCount( XEventPath path, int updateCount){
            getQueryStream().updateCount(updateCount, ScnMark.tryRead(path));
        }
        
        protected final ConcurrentLinkedDeque<Message> messages
            = new ConcurrentLinkedDeque<>();

        @PathMatch(enter = "message")
        public void messageBegin( XEventPath path){
            Integer codeInteger = path.readAttributeAsInteger("code", null);
            String stateString = path.readAttributeAsString("state", null);
            Long scn = path.readAttributeAsLong("scn", null);
            Long ti = path.readAttributeAsLong("ti", null);
            Long th = path.readAttributeAsLong("th", null);
            //getQueryStream().messageBegin(codeInteger!=null ? codeInteger : Integer.MIN_VALUE, stateString);
            Message mes = new Message();
            mes.setCode(codeInteger!=null ? codeInteger : Integer.MIN_VALUE);
            mes.setState(stateString);
            mes.setScn(scn!=null ? scn : Long.MIN_VALUE);
            mes.setThreadId(th!=null ? th : Long.MIN_VALUE);
            mes.setDate(ti!=null ? new Date(ti) : null);
            messages.push(mes);
        }

        @PathMatch(content = "message/text")
        public void messageText( XEventPath path, String text){
            Message mes = messages.peek();
            if( mes!=null ){
                mes.setMessage(text);
            }
        }
        
        @PathMatch(content = "message/textLocal")
        public void messageTextLocal( XEventPath path, String text){
            Message mes = messages.peek();
            if( mes!=null ){
                mes.setLocalizedMessage(text);
            }
        }
        
        @PathMatch(exit = "message")
        public void messageEnd( XEventPath path){
            //getQueryStream().message(message);
            
            Message mes = messages.pop();
            getQueryStream().message(mes);
        }
        
        @PathMatch(exit = "data")
        public void dataEnd( XEventPath path){
            //System.out.println("dataEnd");
            getQueryStream().dataEnd();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="read()">
    public static class PreparedReader {
        protected Function<XVisitor,Object> builder;
        protected XmlStreamVisitor visiter;
        
        public PreparedReader( Function<XVisitor,Object> xmlReaderBuilder, XmlStreamVisitor xmlVisitor ){
            builder = xmlReaderBuilder;
            visiter = xmlVisitor;
        }
        
        public void run(){
            builder.apply(visiter);
        }
    }
    
    public static class ReadBuilder {
        public ReadBuilder( XmlStreamReader xsr, Function<XVisitor,Object> builder){
            this.builder = builder;
            this.reader = xsr;
        }
        
        protected Function<XVisitor,Object> builder;
        public Function<XVisitor,Object> getBuilder() { return builder; }
        
        protected XmlStreamReader reader;
        public XmlStreamReader getReader() { return reader; }
        
        public PreparedReader into( QueryStream qs ){
            if( qs==null )throw new IllegalArgumentException("qs == null");
            
            XmlStreamVisitor xv = new XmlStreamVisitor();
            xv.setTypeCastGraph(getReader().getTypeCast());
            xv.setQueryStream(qs);
            
            PreparedReader pr = new PreparedReader(builder,xv);
            return pr;
        }
    }
    
    public ReadBuilder read( final String xml ){
        return new ReadBuilder(this, arg -> {
            try {
                return new XmlReader(xml,arg);
            } catch ( XMLStreamException ex) {
                Logger.getLogger(XmlStreamReader.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        });
    }
    
    public ReadBuilder read( final URL url, final Charset cs ){
        if( url==null )throw new IllegalArgumentException("url == null");
        return new ReadBuilder(this, arg -> {
            try {
                return new XmlReader(url, cs==null ? Charset.defaultCharset() : cs,arg);
            } catch ( XMLStreamException | IOException ex) {
                Logger.getLogger(XmlStreamReader.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        });
    }
    
    public ReadBuilder read( final java.io.File file, final Charset cs ){
        if( file==null )throw new IllegalArgumentException("url == null");
        return new ReadBuilder(this, arg -> {
            try {
                return new XmlReader(file, cs==null ? Charset.defaultCharset() : cs,arg);
            } catch ( XMLStreamException | IOException ex) {
                Logger.getLogger(XmlStreamReader.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        });
    }
    
    public ReadBuilder read( final xyz.cofe.io.fs.File file, final Charset cs ){
        if( file==null )throw new IllegalArgumentException("url == null");
        return new ReadBuilder(this, arg -> {
            try {
                return new XmlReader(file, cs==null ? Charset.defaultCharset() : cs,arg);
            } catch ( XMLStreamException | IOException ex) {
                Logger.getLogger(XmlStreamReader.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        });
    }
    
    public ReadBuilder read( final java.io.InputStream file, final Charset cs ){
        if( file==null )throw new IllegalArgumentException("url == null");
        return new ReadBuilder(this, arg -> {
            try {
                return new XmlReader(file, cs==null ? Charset.defaultCharset() : cs,arg);
            } catch ( XMLStreamException ex) {
                Logger.getLogger(XmlStreamReader.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        });
    }
    //</editor-fold>
}
