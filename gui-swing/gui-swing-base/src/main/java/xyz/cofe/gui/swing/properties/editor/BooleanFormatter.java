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

package xyz.cofe.gui.swing.properties.editor;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormat;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormatBasic;
import xyz.cofe.gui.swing.tree.TreeTableNodeGetFormatOf;
import xyz.cofe.text.Text;

/**
 * Отображение иконки для boolean значений
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class BooleanFormatter
    extends AbstractPropertyEditor
    implements PropertyDBService, TreeTableNodeGetFormatOf
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(BooleanFormatter.class.getName());
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
        logger.entering(BooleanFormatter.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(BooleanFormatter.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(BooleanFormatter.class.getName(), method, result);
    }
    //</editor-fold>

    public BooleanFormatter(){
        prepareIcons();
    }

    public BooleanFormatter(BooleanFormatter sample){
        prepareIcons();
        this.checkedIcon = sample.checkedIcon;
        this.unCheckedIcon = sample.unCheckedIcon;
    }

    @Override
    public BooleanFormatter clone() {
        return new BooleanFormatter(this);
    }

    @Override
    public void register(PropertyDB pdb) {
        if( pdb==null )return;

        pdb.registerTypeEditor(Boolean.class, this, 0.5);
        pdb.registerTypeEditor(boolean.class, this, 0.5);
    }

    private ImageIcon checkedIcon = null;
    private ImageIcon unCheckedIcon = null;

    private void prepareIcons(){
        URL checkIconUrl = BooleanFormatter.class.getResource(
            "/xyz/cofe/gui/swing/properties/editor/checked-2.png");

        if( checkIconUrl!=null ){
            checkedIcon = new ImageIcon(checkIconUrl);
        }

        URL uncheckIconUrl = BooleanFormatter.class.getResource(
            "/xyz/cofe/gui/swing/properties/editor/unchecked-2.png");

        if( uncheckIconUrl!=null ){
            unCheckedIcon = new ImageIcon(uncheckIconUrl);
        }
    }

    @Override
    public TreeTableNodeFormat getTreeTableNodeFormatOf(Object value) {
        if( value==null )return null;
        if( !(value instanceof Boolean) )return null;

        Boolean bval = (Boolean)value;

        TreeTableNodeFormatBasic fmt = new TreeTableNodeFormatBasic();
        if( checkedIcon!=null && unCheckedIcon!=null ){
            if( bval ){
                fmt.getIcons().add(checkedIcon);
            }else{
                fmt.getIcons().add(unCheckedIcon);
            }
        }

        return fmt;
    }

    protected Boolean value;

    @Override
    public void setValue(Object value) {
        if( value instanceof Boolean ){
            this.value = (Boolean)value;
        }else if( value==null ){
            this.value = null;
        }
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getJavaInitializationString() {
        if( value != null ){
            return value ? "true" : "false";
        }else{
            return "null";
        }
    }

    @Override
    public String getAsText() {
        if( value != null ){
            return value ? "true" : "false";
        }else{
            return "null";
        }
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if( text==null ){
            this.value = null;
        }else{
            if( text.equalsIgnoreCase("null") ){
                this.value = null;
            }else{
                this.value = Text.in(text.trim().toLowerCase(), "true", "1", "yes", "on" );
            }
        }
    }
}
