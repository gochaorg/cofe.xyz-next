package xyz.cofe.gui.swing.cell;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.gui.swing.color.NColorModificator;
import xyz.cofe.gui.swing.color.ColorModificator;

/**
 * Заливка ячнйки цветом
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class FillRender implements CellRender {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(FillRender.class.getName());
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
        logger.entering(FillRender.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(FillRender.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(FillRender.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public FillRender() {
    }

    /**
     * Конструктор
     * @param color цвет заливки
     */
    public FillRender(Color color) {
        this.baseColor = color;
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public FillRender(FillRender sample) {
        if( sample!=null ){
            this.baseColor = sample.baseColor;
            this.width = sample.width;
            this.widthRelative = sample.widthRelative;
            this.height = sample.height;
            this.heightRelative = sample.heightRelative;
            this.valign = sample.valign;
            this.halign = sample.halign;

            this.focusModificator = sample.focusModificator!=null ?
                sample.focusModificator.clone() : null;
            this.selectModificator = sample.selectModificator!=null ?
                sample.selectModificator.clone() : null;

            for( NColorModificator cm : sample.getRowModificators() ){
                if( cm!=null ){
                    getRowModificators().add( cm.clone() );
                }
            }
            for( NColorModificator cm : sample.getColumnModificators() ){
                if( cm!=null ){
                    getColumnModificators().add( cm.clone() );
                }
            }
        }
    }

    /**
     * Клонирование
     * @return клон
     */
    @Override
    public FillRender clone(){
        return new FillRender(this);
    }

    //<editor-fold defaultstate="collapsed" desc="baseColor : Color">
    protected Color baseColor;
    /**
     * Указывает базовый цвет заливки
     * @return базовый цвет заливки
     */
    public synchronized Color getBaseColor() { return baseColor; }

    /**
     * Указывает базовый цвет заливки
     * @param baseColor базовый цвет заливки
     */
    public synchronized void setBaseColor(Color baseColor) { this.baseColor = baseColor; }

    /**
     * Указывает базовый цвет заливки.
     *
     * <p>
     * Конечный цвет оперделяется:<br>
     * <i>цвет</i> = Если color==null, то baseColor → rowModif → colModif → selectModif → focusModif <br>
     * <i>цвет</i> = Если color!=null, то color <br>
     * @param v базовый цвет заливки
     * @return self ссылка
     */
    public synchronized FillRender baseColor(Color v){
        setBaseColor(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="color : color">
    protected Color color;
    /**
     * Указывает цвет заливки
     * @return цвет заливки
     */
    public synchronized Color getColor() { return color; }

    /**
     * Указывает цвет заливки
     * @param сolor цвет заливки
     */
    public synchronized void setColor(Color сolor) { this.color = сolor; }

    /**
     * Указывает цвет заливки
     * @param v цвет заливки
     * @return self ссылка
     */
    public synchronized FillRender сolor(Color v){
        setColor(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="width : double">
    protected double width = 1;

    /**
     * Указывает ширину блока заливки (см. widthRelative).
     *
     * <p>
     * Если widthRelative = true, то width - указывает ширину относительно (0..1) контекста <br>
     * Если widthRelative = false, то width - указывает асолютную ширину
     * @return ширина блока заливки
     */
    public double getWidth() { return width; }

    /**
     * Указывает ширину блока заливки (см. widthRelative).
     * @param width Ширина
     */
    public void setWidth(double width) { this.width = width; }

    /**
     * Указывает ширину блока заливки (см. widthRelative).
     *
     * <p>
     * Если widthRelative = true, то width - указывает ширину относительно (0..1) контекста <br>
     * Если widthRelative = false, то width - указывает асолютную ширину
     * @param v Ширина
     * @return self ссылка
     */
    public FillRender width(double v) {
        setWidth(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="widthRelative : boolean">
    protected boolean widthRelative = true;
    /**
     * Указывает как интерпретировать свойство width.
     * @return true - относительно контекста, false - абсолютно
     */
    public boolean isWidthRelative() { return widthRelative; }

    /**
     * Указывает как интерпретировать свойство width.
     * @param widthRelative true - относительно контекста, false - абсолютно
     */
    public void setWidthRelative(boolean widthRelative) { this.widthRelative = widthRelative; }

    /**
     * Указывает как интерпретировать свойство width.
     * @param v true - относительно контекста, false - абсолютно
     * @return self ссылка
     */
    public FillRender widthRelative(boolean v) {
        setWidthRelative(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="height : double">
    protected double height = 1;

    /**
     * Указывает высоту блока заливки (см. heightRelative).
     *
     * <p>
     * Если heightRelative = true, то height - указывает высоту относительно (0..1) контекста <br>
     * Если heightRelative = false, то height - указывает асолютную высоту
     * @return Высота блока
     */
    public double getHeight() {
        return height;
    }

    /**
     * Указывает высоту блока заливки (см. heightRelative).
     *
     * <p>
     * Если heightRelative = true, то height - указывает высоту относительно (0..1) контекста <br>
     * Если heightRelative = false, то height - указывает асолютную высоту
     * @param height Высота блока
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Указывает высоту блока заливки (см. heightRelative).
     *
     * <p>
     * Если heightRelative = true, то height - указывает высоту относительно (0..1) контекста <br>
     * Если heightRelative = false, то height - указывает асолютную высоту
     * @param v Высота блока
     * @return self ссылка
     */
    public FillRender height(double v) {
        setHeight(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="heightRelative : boolean">
    protected boolean heightRelative = true;

    /**
     * Указывает как интерпретировать свойство height.
     * @return true - относительно контекста, false - абсолютно
     */
    public boolean isHeightRelative() { return heightRelative; }

    /**
     * Указывает как интерпретировать свойство height.
     * @param heightRelative true - относительно контекста, false - абсолютно
     */
    public void setHeightRelative(boolean heightRelative) {
        this.heightRelative = heightRelative;
    }

    /**
     * Указывает как интерпретировать свойство height.
     * @param v true - относительно контекста, false - абсолютно
     * @return self ссылка
     */
    public FillRender heightRelative(boolean v) {
        setHeightRelative(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="halign : double">
    protected double halign = 0.0;

    /**
     * Указывает горизонтальное выравнивание
     * @return выравнивание по горизонтали
     */
    public double getHalign() {
        return halign;
    }

    /**
     * Указывает горизонтальное выравнивание
     * @param halign выравнивание по горизонтали
     */
    public void setHalign(double halign) {
        this.halign = halign;
    }

    /**
     * Указывает горизонтальное выравнивание
     * @param v выравнивание по горизонтали
     * @return self ссылка
     */
    public FillRender halign(double v) {
        setHalign(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="valign : double">
    protected double valign = 0.0;

    /**
     * Указывает вертикальное выравнивание
     * @return вертикальное выравнивание
     */
    public double getValign() {
        return valign;
    }

    /**
     * Указывает вертикальное выравнивание
     * @param valign вертикальное выравнивание
     */
    public void setValign(double valign) {
        this.valign = valign;
    }

    /**
     * Указывает вертикальное выравнивание
     * @param v вертикальное выравнивание
     * @return self ссылка
     */
    public FillRender valign(double v) {
        setValign(v);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rowModificators : List<NColorModificator>">
    protected List<NColorModificator> rowModificators = new ArrayList<>();

    /**
     * Возвращает модификатор цвета для строк
     * @return выборочный модификатор
     */
    public synchronized List<NColorModificator> getRowModificators() {
        if( rowModificators==null )rowModificators = new ArrayList<>();
        return rowModificators;
    }

    /**
     * Указывает модификатор цвета для строк
     * @param mods выборочный модификатор
     */
    public synchronized void setRowModificators( List<NColorModificator> mods ){
        rowModificators = mods;
    }

    /**
     * Добавляет модицикатор цвета строк
     * @param cm модификатор
     * @return self ссылка
     */
    public synchronized FillRender addRowModificator(NColorModificator cm){
        if( cm!=null )getRowModificators().add(cm);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="columnModificators : List<NColorModificator>">
    protected List<NColorModificator> columnModificators = new ArrayList<>();

    /**
     * Возвращает модификатор цвета для колонок
     * @return выборочный модификатор
     */
    public synchronized List<NColorModificator> getColumnModificators() {
        if( columnModificators==null )columnModificators = new ArrayList<>();
        return columnModificators;
    }

    /**
     * Указывает модификатор цвета для колонок
     * @param mods выборочный модификатор
     */
    public synchronized void setColumnModificators(List<NColorModificator> mods) {
        columnModificators = mods;
    }

    /**
     * Добавляет модификатор цвета для колонок
     * @param cm модификатор
     * @return self ссылка
     */
    public synchronized FillRender addColumnModificator(NColorModificator cm){
        if( cm!=null )getColumnModificators().add(cm);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="selectModificator : ColorModificator">
    protected ColorModificator selectModificator;

    /**
     * Возвращает модификатор цвета для выбранных пользователем ячеек
     * @return модификатор
     */
    public synchronized ColorModificator getSelectModificator() {
        return selectModificator;
    }

    /**
     * Указывает модификатор цвета для выбранных пользователем ячеек
     * @param selectModificator модификатор
     */
    public synchronized void setSelectModificator(ColorModificator selectModificator) {
        this.selectModificator = selectModificator;
    }

    /**
     * Указывает модификатор цвета для выбранных пользователем ячеек
     * @param cm модификатор цвета
     * @return self ссылка
     */
    public synchronized FillRender selectModificator(ColorModificator cm){
        setSelectModificator(cm);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="focusModificator : ColorModificator">
    protected ColorModificator focusModificator;

    /**
     * Возвращает модификатор цвета для ячеек содержащих фокус ввода
     * @return модификатор цвета
     */
    public synchronized ColorModificator getFocusModificator() {
        return focusModificator;
    }

    /**
     * Указывает модификатор цвета для ячеек содержащих фокус ввода
     * @param focusModificator модификатор цвета
     */
    public synchronized void setFocusModificator(ColorModificator focusModificator) {
        this.focusModificator = focusModificator;
    }

    /**
     * Указывает модификатор цвета для ячеек содержащих фокус ввода
     * @param cm модификатор цвета
     * @return self ссылка
     */
    public synchronized FillRender focusModificator(ColorModificator cm){
        setFocusModificator(cm);
        return this;
    }
    //</editor-fold>

    @Override
    public Rectangle2D cellRectangle(Graphics2D gs, CellContext context) {
        return null;
    }

    @Override
    public synchronized void cellRender(Graphics2D gs, CellContext context) {
        if( gs==null )return;
        if( context==null )return;

        Rectangle2D ctxrect = context.getBounds();
        if( ctxrect==null )return;

        Color bc = gs.getColor();

        //this.columnModificators;

        Color c = this.color != null ? this.color : this.baseColor;
        if( c==null )return;

        if( context instanceof TableContext && this.color==null && this.baseColor!=null ){
            int row = ((TableContext)context).getRow();
            int col = ((TableContext)context).getColumn();
            boolean sel = ((TableContext)context).isSelected();
            boolean foc = ((TableContext)context).isFocus();

            for( NColorModificator cm : getRowModificators() ){
                if( cm==null )continue;
                int cycle = cm.getCycle();
                int ph = cm.getPhase();
                if( cycle<1 )continue;
                if( cycle==1 ){
                    c = cm.apply(c);
                    continue;
                }
                if( (row % cycle)==ph ){
                    c = cm.apply(c);
                }
            }

            for( NColorModificator cm : getColumnModificators()){
                if( cm==null )continue;
                int cycle = cm.getCycle();
                int ph = cm.getPhase();
                if( cycle<1 )continue;
                if( cycle==1 ){
                    c = cm.apply(c);
                    continue;
                }
                if( (col % cycle)==ph ){
                    c = cm.apply(c);
                }
            }

            ColorModificator selMod = selectModificator;
            if( selMod!=null && sel ){
                c = selMod.apply(c);
            }

            ColorModificator focMod = focusModificator;
            if( focMod!=null && foc ){
                c = focMod.apply(c);
            }
        }

        double w = widthRelative ? width * ctxrect.getWidth() : width;
        double h = heightRelative ? height * ctxrect.getHeight(): height;
        double x = ctxrect.getMinX() + (ctxrect.getWidth()-w) * halign;
        double y = ctxrect.getMinY() + (ctxrect.getHeight()-h) * valign;

        Rectangle2D.Double rect = new Rectangle2D.Double(x, y, w, h);

        gs.setColor(c);
        gs.fill(rect);

        gs.setColor(bc);
    }
}
