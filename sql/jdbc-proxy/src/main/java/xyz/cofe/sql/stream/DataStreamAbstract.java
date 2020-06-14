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

import xyz.cofe.text.Text;
import xyz.cofe.typeconv.ExtendedCastGraph;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Общие утилиты по работе с ResultSet
 * @author nt.gocha@gmail.com
 */
public class DataStreamAbstract {
    //<editor-fold defaultstate="collapsed" desc="ByteArrToStrHex">
    /**
     * Конвертор из byte[]/Byte[] в string (hex)
     */
    public static class ByteArrToStrHex implements Function<Object, Object> {
        @Override
        public Object apply( Object from) {
            if( from instanceof Byte[] ){
                Byte[] bytes = (Byte[])from;
                return "0x"+ Text.encodeHex(bytes, 0, bytes.length);
            }else if( from instanceof byte[] ){
                byte[] bytes = (byte[])from;
                return "0x"+ Text.encodeHex(bytes, 0, bytes.length);
            }
            throw new ClassCastException("can't cast "+from+" (as bytes[]) to string");
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="StrHexToNByteArr">
    /**
     * Конвертор из string(hex) в byte[]
     */
    public static class StrHexToNByteArr implements Function<Object, Object> {
        @Override
        public Object apply( Object from) {
            if( !(from instanceof String) )throw new ClassCastException("can't cast "+from+" as string");
            
            String str = (String)from;
            if( !str.startsWith("0x") )throw new Error("hex prefix (0x) not matched");
            
            return Text.decodeHex(str,2,str.length()-2);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="StrHexToOByteArr">
    /**
     * Конвертор из string(hex) в Byte[]
     */
    public static class StrHexToOByteArr implements Function<Object, Object> {
        @Override
        public Object apply( Object from) {
            if( !(from instanceof String) )throw new ClassCastException("can't cast "+from+" as string");
            
            String str = (String)from;
            if( !str.startsWith("0x") )throw new Error("hex prefix (0x) not matched");
            
            return Text.decodeHexBytes(str,2,str.length()-2);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="BlobToStrHex">
    /**
     * Конвертор из Blob в string (hex)
     */
    public static class BlobToStrHex implements Function<Object, Object> {
        public static void write( Appendable out, Blob blb){
            if( out==null )throw new IllegalArgumentException("out == null");
            if( blb==null )throw new IllegalArgumentException("blb == null");
            //StringBuilder sb = new StringBuilder();
            //Blob blb = (Blob)from;
            try {
                out.append("0x");
                InputStream strm = blb.getBinaryStream();
                byte[] buff = new byte[1024*8];
                while(true){
                    int readed = strm.read(buff);
                    if( readed<0 ){
                        break;
                    }
                    if( readed>0 ){
                        //sb.append(Text.encodeHex(buff, 0, readed));
                        out.append(Text.encodeHex(buff, 0, readed));
                    }
                }
                strm.close();
                //return "0x"+sb.toString();
            } catch ( SQLException ex) {
                Logger.getLogger(DataStreamAbstract.class.getName()).log(Level.SEVERE, null, ex);
                throw new Error(
                        "can't cast "+blb+" to "+ Blob.class.getName()+
                                " SQLException:"+ex.getSQLState()+" "+ex.getMessage(),
                        ex
                );
            } catch ( IOException ex) {
                Logger.getLogger(DataStreamAbstract.class.getName()).log(Level.SEVERE, null, ex);
                throw new Error(
                        "can't cast "+blb+" to "+ Blob.class.getName()+
                                " IOException:"+" "+ex.getMessage(),
                        ex
                );
            }
        }
        
        @Override
        public Object apply( Object from) {
            if( from instanceof Blob ){
                StringBuilder sb = new StringBuilder();
                write(sb, (Blob)from);
                return sb.toString();
            }
            throw new ClassCastException("can't cast "+from+" to "+ Blob.class.getName());
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="StrHexToBlob">
    /**
     * Конвертор из string(hex) в Blob
     */
    public static class StrHexToBlob implements Function<Object, Object> {
        public static Blob read( Reader reader){
            try {
                if( reader==null )throw new IllegalArgumentException("reader == null");
                
                char[] cbuff = new char[1024*8];
                int totRead = 0;
                StringBuilder sb = new StringBuilder();
                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                boolean prefixChecked = false;
                
                while( true ){
                    int readed = reader.read(cbuff);
                    if( readed<0 )break;
                    if( readed>0 ){
                        sb.append(cbuff,0,readed);
                        totRead += readed;
                        
                        if( totRead>=2 && !prefixChecked ){
                            String prefix = sb.substring(0, 2);
                            if( !Objects.equals(prefix, "0x") ){
                                throw new IOException("hex prefix (0x) not matched");
                            }
                            prefixChecked = true;
                            sb.delete(0, 2);
                        }
                        
                        if( prefixChecked ){
                            int bufflen = sb.length();
                            int flushSize = bufflen / 2;
                            if( flushSize>0 ){
                                String strenc = sb.substring(0, flushSize*2);
                                byte[] bytes = Text.decodeHex(strenc);
                                sb.delete(0, flushSize*2);
                                ba.write(bytes);
                            }
                        }
                    }
                }
                
                SerialBlob sblb = new SerialBlob(ba.toByteArray());
                return sblb;
            } catch ( IOException ex) {
                Logger.getLogger(DataStreamAbstract.class.getName()).log(Level.SEVERE, null, ex);
                throw new Error("IO Error: "+ex.getMessage(),ex);
            } catch ( SQLException ex) {
                Logger.getLogger(DataStreamAbstract.class.getName()).log(Level.SEVERE, null, ex);
                throw new Error("SQLException: "+ex.getMessage(),ex);
            }
        }
        
        @Override
        public Object apply( Object from) {
            if( !(from instanceof String) ){
                throw new ClassCastException("can't cast "+from+" to "+ String.class.getName());
            }
            
            String str = (String)from;
            return read(new StringReader(str));
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="NClobSer">
    /**
     * Clob значение
     */
    public static class NClobSer extends SerialClob implements NClob {
        public NClobSer( String str) throws SerialException, SQLException{
            super(str.toCharArray());
        }
        
        public NClobSer( StringBuilder str) throws SerialException, SQLException{
            super(str.toString().toCharArray());
        }

        public NClobSer(char[] ch) throws SerialException, SQLException{
            super(ch);
        }
        
        public NClobSer( Clob clob) throws SerialException, SQLException{
            super(clob);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="StrToClob">
    /**
     * Конвертор из String в Clob
     */
    public static class StrToClob implements Function<Object, Object> {
        public static NClobSer read( Reader reader){
            try {
                if( reader==null )throw new IllegalArgumentException("reader == null");
                
                char[] cbuff = new char[1024*8];
                StringBuilder sb = new StringBuilder();
                
                while( true ){
                    int readed = reader.read(cbuff);
                    if( readed<0 )break;
                    if( readed>0 ){
                        sb.append(cbuff,0,readed);
                    }
                }
                
                NClobSer clob = new NClobSer(sb);
                return clob;
            } catch ( IOException ex) {
                Logger.getLogger(DataStreamAbstract.class.getName()).log(Level.SEVERE, null, ex);
                throw new Error("IO Error: "+ex.getMessage(),ex);
            } catch ( SQLException ex) {
                Logger.getLogger(DataStreamAbstract.class.getName()).log(Level.SEVERE, null, ex);
                throw new Error("SQLException: "+ex.getMessage(),ex);
            }
        }
        
        @Override
        public Object apply( Object from) {
            if( !(from instanceof String) ){
                throw new ClassCastException("can't cast "+from+" to "+ String.class.getName());
            }
            
            String str = (String)from;
            return read(new StringReader(str));
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="typeCast">
    protected ExtendedCastGraph typeCast;
    /**
     * Возвращает граф конвертор данных
     * 
     * <p>
     * Создает граф из ExtendedCastGraph с дополнительными конверторами Clob/Blob/(B)byte[]
     * @return конвертор данных
     */
    public ExtendedCastGraph getTypeCast() {
        synchronized(this){
            ExtendedCastGraph ecg = new ExtendedCastGraph();
            
            BlobToStrHex blob2str = new BlobToStrHex();
            ecg.set(Blob.class, String.class, blob2str);
            
            StrHexToBlob str2blob = new StrHexToBlob();
            ecg.set(String.class, Blob.class, str2blob);
            
            StrToClob str2clob = new StrToClob();
            ecg.set(String.class, Clob.class, str2clob);
            ecg.set(String.class, NClob.class, str2clob);
            
            ByteArrToStrHex bytes2str = new ByteArrToStrHex();
            ecg.set(byte[].class, String.class, bytes2str);
            ecg.set(Byte[].class, String.class, bytes2str);
            
            // str -> byte[]
            StrHexToNByteArr str2nbytes = new StrHexToNByteArr();
            ecg.set(String.class, byte[].class, str2nbytes);
            
            // str -> Byte[]
            StrHexToOByteArr str2obytes = new StrHexToOByteArr();
            ecg.set(String.class, Byte[].class, str2obytes);
            
            typeCast = ecg;
            return typeCast;
        }
    }
    /**
     * Указывает конвертор данных
     * @param typeCast конвертор данных
     */
    public void setTypeCast( ExtendedCastGraph typeCast) {
        synchronized(this){
            this.typeCast = typeCast;
        }
    }
    //</editor-fold>
}
