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

package xyz.cofe.gui.swing.tree;

import xyz.cofe.collection.ClassMap;
import xyz.cofe.ecolls.Fn1;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Редакторы для значений различных типов по умолчанию.
 *
 * <p>
 * Поддерживается редактирование для следующих типов
 * <ul>
 * <li>int
 * <li>short
 * <li>byte
 * <li>long
 * <li>float
 * <li>double
 * <li>boolean
 * <li>String
 * </ul>
 *
 * Возможно расширение этого списка
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TreeTableNodeValueEditorDef extends TreeTableNodeValueEditor {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableNodeValueEditorDef.class.getName());
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
        logger.entering(TreeTableNodeValueEditorDef.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeTableNodeValueEditorDef.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeTableNodeValueEditorDef.class.getName(), method, result);
    }
    //</editor-fold>

    public static class ConvertTextFieldEditor<T> extends TextFieldEditor {
        //<editor-fold defaultstate="collapsed" desc="parser">
        protected Fn1<String,T> parser;

        public Fn1<String, T> getParser() {
            return parser;
        }

        public void setParser(Fn1<String,T> parser) {
            this.parser = parser;
        }

        @Override
        public Object getCellEditorValue() {
            try{
                Object ostr = super.getCellEditorValue();

                var prsr = parser;
                if( prsr==null )throw new IllegalStateException("parser not set");

                if( ostr==null ){
                    throw new IllegalStateException("return null string");
                }

                return prsr.apply(ostr.toString());
            }catch( Throwable err ){
                JOptionPane.showMessageDialog(null, err.getMessage(), "parse error", JOptionPane.ERROR_MESSAGE);
                return defValue;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="serializer">
        protected Fn1<T,String> serializer;

        public Fn1<T,String> getSerializer() {
            return serializer;
        }

        public void setSerializer(Fn1<T,String> serializer) {
            this.serializer = serializer;
        }

        @Override
        public void startEditing(Object value, Object context) {
            try{
                var srlz = getSerializer();
                if( srlz==null ){
                    throw new IllegalStateException("serializer not set");
                }

                String str = srlz.apply((T)value);

                super.startEditing(str, context);
            }catch( Throwable err ){
                JOptionPane.showMessageDialog(null, err.getMessage(), "serilize error", JOptionPane.ERROR_MESSAGE);
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="defValue">
        protected T defValue;

        public T getDefValue() {
            return defValue;
        }

        public void setDefValue(T defValue) {
            this.defValue = defValue;
        }
        //</editor-fold>

        public ConvertTextFieldEditor( Fn1<String,T> parser, Fn1<T,String> serializer, T defValue ){
            this.parser = parser;
            this.serializer = serializer;
            this.defValue = defValue;
        }
    }

    @Override
    protected ClassMap<Editor> createTypeEditors() {
        ClassMap<Editor> editors = super.createTypeEditors();

        //<editor-fold defaultstate="collapsed" desc="int">
        editors.put(Integer.class, new ConvertTextFieldEditor<Integer>(
            str -> "null".equals(str) ? null : (Integer)Integer.parseInt(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );

        editors.put(int.class, new ConvertTextFieldEditor<Integer>(
            str -> Integer.parseInt(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="short">
        editors.put(short.class, new ConvertTextFieldEditor<Short>(
            str -> "null".equals(str) ? null : Short.parseShort(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );

        editors.put(Short.class, new ConvertTextFieldEditor<Short>(
            str -> (Short)Short.parseShort(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="byte">
        editors.put(byte.class, new ConvertTextFieldEditor<Byte>(
            str -> "null".equals(str) ? null : Byte.parseByte(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );

        editors.put(Byte.class, new ConvertTextFieldEditor<Byte>(
            str -> Byte.parseByte(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="long">
        editors.put(long.class, new ConvertTextFieldEditor<Long>(
            str -> "null".equals(str) ? null : Long.parseLong(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );

        editors.put(Long.class, new ConvertTextFieldEditor<Long>(
            str -> "null".equals(str) ? null : Long.parseLong(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="float">
        editors.put(float.class, new ConvertTextFieldEditor<Float>(
            str -> "null".equals(str) ? null : Float.parseFloat(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );

        editors.put(Float.class, new ConvertTextFieldEditor<Float>(
            str -> "null".equals(str) ? null : Float.parseFloat(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="double">
        editors.put(double.class, new ConvertTextFieldEditor<Double>(
            str -> "null".equals(str) ? null : Double.parseDouble(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );

        editors.put(Double.class, new ConvertTextFieldEditor<Double>(
            str -> "null".equals(str) ? null : Double.parseDouble(str),
            val -> val!=null ? val.toString() : "null",
            null
        ) );
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="boolean">
        editors.put(boolean.class, new ConvertTextFieldEditor<Boolean>(
            str -> "null".equals(str) ? null : (boolean)( str.trim().equalsIgnoreCase("true") ),
            val -> val!=null ? val.toString() : "null",
            null
        ) );

        editors.put(Boolean.class, new ConvertTextFieldEditor<Boolean>(
            str -> "null".equals(str) ? null : (Boolean)( str.trim().equalsIgnoreCase("true") ),
            val -> val!=null ? val.toString() : "null",
            null
        ) );
        //</editor-fold>

        return editors;
    }
}
