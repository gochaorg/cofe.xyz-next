package xyz.cofe.gui.swing.tree;

import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn3;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Значение прочитанное из узла древа
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TreeTableNodeValue implements TreeTableNodeGetFormat {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableNodeValue.class.getName());
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
        logger.entering(TreeTableNodeValue.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeTableNodeValue.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeTableNodeValue.class.getName(), method, result);
    }
    //</editor-fold>

    public TreeTableNodeValue(){
    }

    public TreeTableNodeValue(Object value, Object dataOfNode, TreeTableNode node){
        this.value = value;
        this.dataOfNode = dataOfNode;
        this.node = node;
    }

    //<editor-fold defaultstate="collapsed" desc="valueType">
    protected Class valueType;

    /**
     * Возвращает тип данных значения
     * @return тип отображаемых данных
     */
    public Class getValueType() {
        return valueType;
    }

    /**
     * Указывает тип данных значения
     * @param valueType тип отображаемых данных
     */
    public void setValueType(Class valueType) {
        this.valueType = valueType;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="value">
    protected Object value;

    /**
     * Возвращает отображаемые данные
     * @return отображаемые данные
     */
    public Object getValue() {
        return value;
    }

    /**
     * Указывает отображаемые данные
     * @param value отображаемые данные
     */
    public void setValue(Object value) {
        this.value = value;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="customPainter">
    protected Fn2<Graphics,Rectangle,Object> customPainter;

    public Fn2<Graphics,Rectangle,Object> getCustomPainter() {
        return customPainter;
    }

    public void setCustomPainter(Fn2<Graphics,Rectangle,Object> customPainter) {
        this.customPainter = customPainter;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="editor">
    protected TreeTableNodeValueEditor.Editor editor;

    public TreeTableNodeValueEditor.Editor getEditor() {
        return editor;
    }

    public void setEditor(TreeTableNodeValueEditor.Editor editor) {
        this.editor = editor;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valueWriter">
    protected Fn3<TreeTableNode, Object, Object, Object> valueWriter;

    public Fn3<TreeTableNode, Object, Object, Object> getValueWriter() {
        return valueWriter;
    }

    public void setValueWriter(Fn3<TreeTableNode, Object, Object, Object> valueWriter) {
        this.valueWriter = valueWriter;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valueReader">
    protected Fn2<Object, TreeTableNode, Object> valueReader;

    public Fn2<Object, TreeTableNode, Object> getValueReader() {
        return valueReader;
    }

    public void setValueReader(Fn2<Object, TreeTableNode, Object> valueReader) {
        this.valueReader = valueReader;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dataOfNode">
    protected Object dataOfNode;

    public Object getDataOfNode() {
        return dataOfNode;
    }

    public void setDataOfNode(Object dataOfNode) {
        this.dataOfNode = dataOfNode;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="node">
    protected TreeTableNode node;

    public TreeTableNode getNode() {
        return node;
    }

    public void setNode(TreeTableNode node) {
        this.node = node;
    }
    //</editor-fold>

    @Override
    public TreeTableNodeFormat getTreeTableNodeFormat() {
        return getFormat();
    }

    //<editor-fold defaultstate="collapsed" desc="format">
    protected TreeTableNodeFormat format;

    public TreeTableNodeFormat getFormat() {
        return format;
    }

    public void setFormat(TreeTableNodeFormat format) {
        this.format = format;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "TreeTableNodeValue{" + "value=" + value + '}';
    }
}
