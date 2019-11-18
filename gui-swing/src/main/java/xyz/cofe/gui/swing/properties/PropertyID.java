/*
 * The MIT License
 *
 * Copyright 2016 Kamnev Georgiy <nt.gocha@gmail.com>.
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

package xyz.cofe.gui.swing.properties;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
//import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Индентификатор свойства
 * @author nt.gocha@gmail.com
 */
public class PropertyID
    implements Comparable<PropertyID>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PropertyID.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(PropertyID.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(PropertyID.class.getName(), method, result);
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
    //</editor-fold>

    /**
     * Конструктор
     * @param pd описание свойства
     */
    public PropertyID( PropertyDescriptor pd ){
        if( pd==null )throw new IllegalArgumentException( "pd==null" );

        Method mread  = pd.getReadMethod();
        Method mwrite = pd.getWriteMethod();

        if( mread==null && mwrite==null ){
            throw new IllegalArgumentException("can't fetch property owner class, read/write method not exists");
        }

        Method meth = mread!=null ? mread : mwrite;
        Class decl = meth.getDeclaringClass();
        if( decl==null ){
            throw new IllegalArgumentException("can't fetch property owner class, mread/mwrite.getDeclaringClass() - return null");
        }

        this.type = decl.getName();
        this.name = pd.getName();
    }

    /**
     * Конструктор
     * @param type имя класса владельца свойства
     * @param name имя свойства
     */
    public PropertyID(String type,String name){
        this.type = type;
        this.name = name;
    }

    /**
     * Конструктор копирования
     * @param sample обрахец для копирования
     */
    public PropertyID( PropertyID sample ){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        synchronized( sample ){
            this.type = sample.type;
            this.name = sample.name;
        }
    }

    @Override
    public PropertyID clone(){
        return new PropertyID(this);
    }

    /**
     * Клонирование с указанными параметрами
     * @param type имя класса владельца свойства
     * @param name имя свойства
     * @return Идентификатор свойства
     */
    public PropertyID cloneWith(String type,String name){
        return new PropertyID(type,name);
    }

    //<editor-fold defaultstate="collapsed" desc="type : String - имя типа">
    protected final String type;

    /**
     * Возвращат имя класса владельца свойства
     * @return имя класса
     */
    public String getType() {
        return type;
    }

    /**
     * Указыание имя класса владельца свойства
     * @param type имя класса
     * @return Идентификатор с указанным именем
     */
    public PropertyID type(String type) {
        return cloneWith(type, name);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="name : String - имя свойства">
    protected final String name;

    /**
     * Возвращает имя свойства
     * @return имя свойства
     */
    public String getName() {
        return name;
    }

    /**
     * Указание имени свойства
     * @param name имя свойства
     * @return Идентификатор с указанным именем
     */
    public PropertyID name(String name) {
        return cloneWith(type, name);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="equals/compareTo/hashCode">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)return true;
        if(obj == null)return false;
        if(getClass() != obj.getClass())return false;
        final PropertyID other = (PropertyID)obj;

        if( this.type==null && other.type==null  &&
            this.name==null && other.name==null )return true;

        if( this.type!=null ){
            if( other.type==null )return false;
            if( !this.type.equals(other.type) )return false;
            if( this.name!=null ){
                if( other.name==null )return false;
                if( !this.name.equals(other.name) )return false;
                return true;
            }else{
                if( other.name!=null )return false;
                return true;
            }
        }else{
            if( other.type!=null )return false;
            if( this.name!=null ){
                if( other.name==null )return false;
                if( !this.name.equals(other.name) )return false;
                return true;
            }else{
                if( other.name!=null )return false;
                return true;
            }
        }
    }

    @Override
    public int compareTo(PropertyID o) {
        if( o==null )return -1;

        if( !o.getClass().equals(PropertyID.class) ){
            throw new IllegalArgumentException("can't compare with "+o.getClass()+" = "+o);
        }

        PropertyID pid = (PropertyID)o;

        int cmptype = 0;
        if( type==null && pid.type==null )cmptype = 0;
        else if( type!=null && pid.type==null )cmptype = -1;
        else if( type==null && pid.type!=null )cmptype = 1;
        else if( type!=null && pid.type!=null )cmptype = type.compareTo(pid.type);

        int cmpname = 0;
        if( name==null && pid.name==null )cmpname = 0;
        else if( name!=null && pid.name==null )cmpname = -1;
        else if( name==null && pid.name!=null )cmpname = 1;
        else if( name!=null && pid.name!=null )cmpname = name.compareTo(pid.name);

        if( cmptype!=0 )return cmptype;
        return cmpname;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="toString()">
    @Override
    public String toString() {
        return "PropertyID{" + "type=" + type + ", name=" + name + '}';
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="parse():PropertyID">
    /**
     * Парсинг, пример:
     * <pre>
     * PropertyID { type=org.pkg.cls1, name=propA }
     * </pre>
     * @param text Текстовое представление
     * @return Идентификатор или null
     */
    public static PropertyID parse( String text ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        Pattern ptrn = Pattern.compile(
            "(?is)"
                + "^"
                + "PropertyID"
                + "\\s*"
                + "\\{"
                + "\\s*"
                + "type=([^,]+)" // gr 1
                + ","
                + "\\s*"
                + "name=([^\\}]+)" // gr 2
                + "\\s*?\\}");

        Matcher m = ptrn.matcher(text);
        if( m.matches() ){
            String type = m.group(1);
            String name = m.group(2);
            if( type!=null && name!=null ){
                type = type.trim();
                name = name.trim();
                return new PropertyID(type, name);
            }
        }

        return null;
    }
    //</editor-fold>
}
