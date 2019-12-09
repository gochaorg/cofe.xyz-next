package xyz.cofe.gui.swing.properties;

import xyz.cofe.gui.swing.SwingListener;
import xyz.cofe.gui.swing.properties.editor.CustomEditor;
import xyz.cofe.gui.swing.typeconv.impl.RGB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nt.gocha@gmail.com
 */
public class TColorEditor extends CustomEditor
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TColorEditor.class.getName());

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
        logger.entering(TColorEditor.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(TColorEditor.class.getName(), method, result);
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

    public TColorEditor(){
        buildUI();
    }

    public TColorEditor(TColorEditor sample){
        super(sample);
        buildUI();
    }

    protected void buildUI(){
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.setLayout(new BorderLayout());

        panel.add( nameColor, BorderLayout.CENTER );
        panel.add( selectColor, BorderLayout.EAST );

        selectColor.setText("..");
        SwingListener.onActionPerformed(selectColor, ae -> {
                //JColorChooser colorChooser = new JColorChooser(color == null ? Color.black : color);
                color = JColorChooser.showDialog(panel, "Select color (test)", color == null ? Color.black : color);
                nameColor.setText(RGB.rgb(color));

                fireEditingStopped(this);
            });
    }

    @Override
    public TColorEditor clone() {
        return new TColorEditor(this);
    }

    protected final JPanel panel = new JPanel();
    protected final JButton selectColor = new JButton();
    protected final JLabel nameColor = new JLabel();

    protected Color color;

    @Override
    protected JComponent createComponent() {
        return panel;
    }

    @Override
    public void setValue(Object value) {
        if( value instanceof Color ){
            color = ((Color)value);
        }else{
            color = Color.BLACK;
        }

        nameColor.setText(RGB.rgb(color));
    }

    @Override
    public Object getValue() {
        if( color==null ){
            return Color.black;
        }
        return color;
    }

    @Override
    public String getJavaInitializationString() {
        return RGB.rgb(color==null ? color.black : color);
    }

    @Override
    public String getAsText() {
        return RGB.rgb(color==null ? color.black : color);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        color = RGB.rgb(text==null ? "#000000" : text);
    }
}
