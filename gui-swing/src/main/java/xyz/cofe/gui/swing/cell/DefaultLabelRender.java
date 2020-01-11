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

package xyz.cofe.gui.swing.cell;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import xyz.cofe.ecolls.Fn2;
import xyz.cofe.gui.swing.properties.Property;
import xyz.cofe.gui.swing.properties.PropertyValue;
import xyz.cofe.gui.swing.tree.FormattedValue;
import xyz.cofe.gui.swing.tree.TreeTableNode;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormat;
import xyz.cofe.gui.swing.tree.TreeTableNodeGetFormat;
import xyz.cofe.gui.swing.tree.TreeTableNodeValue;
import xyz.cofe.text.Text;
import xyz.cofe.typeconv.ExtendedCastGraph;
import xyz.cofe.typeconv.TypeCastGraph;

/**
 * отображение текстовой метки с использованием функций обработки данных группы/артифакта xyz.cofe/...
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class DefaultLabelRender extends LabelRender {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(DefaultLabelRender.class.getName());
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
        logger.entering(DefaultLabelRender.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(DefaultLabelRender.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(DefaultLabelRender.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию <br>
     * формат времени yyyy-MM-dd HH:mm:ss.SSSZZZ
     */
    public DefaultLabelRender() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZZZ");
    }

    /**
     * Конструктор копирования <br>
     * формат времени yyyy-MM-dd HH:mm:ss.SSSZZZ
     * @param cellFormat образец для колпирования
     */
    public DefaultLabelRender(CellFormat cellFormat) {
        super(cellFormat);
        if( dateFormat==null )dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZZZ");
    }

    /**
     * Конструктор копирования
     * @param sample образец для колпирования
     */
    public DefaultLabelRender(LabelRender sample) {
        super(sample);
        //dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZZZ");
        if( sample instanceof DefaultLabelRender ){
            dateFormat = ((DefaultLabelRender) sample).dateFormat;
        }else{
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZZZ");
        }
    }

    protected Fn2<Graphics,Rectangle,Object> customPainter;

    protected static Icon blobIcon;
    protected static Icon clobIcon;
    protected static Icon nullIcon;

    static {
        URL uBlob = DefaultLabelRender.class.getResource("blob-23x14.png");
        if( uBlob!=null ){ blobIcon = new ImageIcon(uBlob); }

        URL uClob = DefaultLabelRender.class.getResource("clob-23x14.png");
        if( uBlob!=null ){ clobIcon = new ImageIcon(uClob); }

        URL uNull = DefaultLabelRender.class.getResource("null-23x14.png");
        if( uBlob!=null ){ nullIcon = new ImageIcon(uNull); }
    }

    /**
     * Создание клона
     * @return клон
     */
    @Override
    public synchronized DefaultLabelRender clone(){
        return new DefaultLabelRender(this);
    }

    protected NumberFormat numberFormat;
    /**
     * Возвращает формат чисел
     * @return формат чисел
     */
    public synchronized NumberFormat getNumberFormat(){ return numberFormat; }
    /**
     * Указывает формат чисел
     * @param df формат чисел
     */
    public synchronized void setNumberFormat(NumberFormat df){ numberFormat = df; }

    protected DateFormat dateFormat;
    /**
     * Возвращает формат времени
     * @return формат времени
     */
    public synchronized DateFormat getDateFormat() { return dateFormat; }
    /**
     * Указывает формат времени
     * @param dateFormat формат времени
     */
    public synchronized void setDateFormat(DateFormat dateFormat) { this.dateFormat = dateFormat; }

    protected TypeCastGraph typeCastGraph;
    /**
     * Указывает граф преобразования типов данных
     * @return граф преобразования типов
     */
    public synchronized TypeCastGraph getTypeCastGraph() {
        if( typeCastGraph==null ){
            typeCastGraph = new ExtendedCastGraph();
        }
        return typeCastGraph;
    }
    /**
     * Указывает граф преобразования типов данных
     * @param typeCastGraph граф преобразования типов
     */
    public synchronized void setTypeCastGraph(TypeCastGraph typeCastGraph) { this.typeCastGraph = typeCastGraph; }

    private static final byte[] byteArr1 = new byte[]{};
    private static final Byte[] byteArr2 = new Byte[]{};

    /**
     * Возвращет текстовое представление данных
     * @param renderval данные
     * @param cf формат ячейки
     * @param nullvalue тексто для отображения null значения
     * @return текст
     */
    private String asText( Object renderval, CellFormat cf, String nullvalue ){
        if( renderval==null )return nullvalue;

        if( renderval instanceof PropertyValue ){
            PropertyValue pv = (PropertyValue)renderval;
            renderval = pv.getValue();
        }else{
            if( renderval instanceof TreeTableNode ){
                TreeTableNode ttnode = (TreeTableNode)renderval;
                renderval = ttnode.getData();
            }else if( renderval instanceof TreeTableNodeValue ){
                TreeTableNodeValue ttnv = (TreeTableNodeValue)renderval;
                renderval = ttnv.getValue();
            }

            if( renderval instanceof Property ){
                renderval = ((Property)renderval).getName();
            }
        }

        if( renderval instanceof Number ){
            NumberFormat cnf = cf instanceof GetNumberFormat ? ((GetNumberFormat)cf).getNumberFormat() : null;
            NumberFormat nf = cnf !=null ? cnf : numberFormat;
            if( nf!=null ){
                //renderval = nf.format(renderval);
                return nf.format(renderval);
            }
        }

        if( renderval instanceof Date ){
            DateFormat cdf = cf instanceof GetDateFormat ? ((GetDateFormat)cf).getDateFormat(): null;
            DateFormat df = cdf !=null ? cdf : dateFormat;
            if( df!=null ){
                return df.format(renderval);
            }else{
                return renderval.toString();
            }
        }

        if( renderval instanceof Boolean ){
            return ((Boolean)renderval).toString();
        }

        if( Objects.equals(renderval.getClass(),byteArr1.getClass()) ){
            byte[] bytes = (byte[])renderval;
            StringBuilder sb = new StringBuilder();
            sb.append("0x");
            int bptr = 0;
            int blimit = 64;
            while( true ){
                if( bptr>=bytes.length )break;
                if( bptr>=blimit && blimit>0 )break;
                sb.append(Text.getHex(bytes[bptr]));
                bptr++;
            }
            return sb.toString();
        }else if( Objects.equals(renderval.getClass(),byteArr2.getClass()) ){
            Byte[] bytes = (Byte[])renderval;
            StringBuilder sb = new StringBuilder();
            sb.append("0x");
            int bptr = 0;
            int blimit = 64;
            while( true ){
                if( bptr>=bytes.length )break;
                if( bptr>=blimit && blimit>0 )break;
                sb.append(Text.getHex(bytes[bptr]));
                bptr++;
            }
            return sb.toString();
        }

        TypeCastGraph tcast = getTypeCastGraph();
        if( tcast!=null ){
            try{
                String str = tcast.cast(renderval, String.class);
                return str;
            }catch( Throwable err ){
                //logFiner(err);
                return renderval.toString();
            }
        }

        return renderval.toString();
    }

    /**
     * Пожготавливает ячейку для отображения данных
     * @param gs контекст куда происходит рендер
     * @param context контекст ячейки
     * @param cf формат ячейки
     * @param rendervalref ссылка на отображаемое значение
     */
    protected synchronized void prepareCellContext(
        Graphics2D gs,
        CellContext context,
        CellFormat cf,
        AtomicReference rendervalref )
    {
        Object renderval = rendervalref.get();

        if( renderval instanceof Number ){
            rendervalref.set(asText(renderval, cf, renderval.toString()));
        }else if( renderval instanceof Date ){
            rendervalref.set(asText(renderval, cf, renderval.toString()));
        }else if( renderval instanceof Boolean ){
            rendervalref.set(asText(renderval, cf, renderval.toString()));
        }else if( renderval!=null ){
            String str = asText(renderval, cf, null);
            if( str!=null ){
                rendervalref.set(str);
            }else{
                rendervalref.set("");
                cf.setIcon(nullIcon);
            }
        }else if( renderval==null ){
            rendervalref.set("");
            cf.setIcon(nullIcon);
        }
    }

    private final AtomicReference renderValRef = new AtomicReference(null);

    /**
     * Подгатавливает ячейку для отображения
     * @param gs рендер
     * @param context контекст отображения, влючая данные для отображения
     * @param cf формат
     * @return true - успешно
     */
    @Override
    public synchronized boolean prepare(Graphics2D gs, CellContext context, CellFormat cf) {
        customPainter = null;
        if( cf==null )cf = getFormat().clone();

        if( context!=null ){
            Object val = context.getValue();

            TreeTableNodeGetFormat fmtval = val instanceof FormattedValue ? (FormattedValue)val : null;
            Object renderval = val; //fmtval!=null ? fmtval.getValue() : val;

            if( val instanceof TreeTableNodeValue ){
                TreeTableNodeValue ttnv = (TreeTableNodeValue)val;
                Fn2<Graphics,Rectangle,Object> cutmp = ttnv.getCustomPainter();
                if(cutmp!=null){
                    customPainter = cutmp;
                }else{
                    renderval = ttnv.getValue();
                }

                fmtval = ttnv;
            }

            if( val instanceof PropertyValue ){
                PropertyValue pv = (PropertyValue)val;
                //if( pv )
                renderval = pv.getValue();
            }

            if( fmtval!=null ){
                try{
                    TreeTableNodeFormat ttnf = fmtval.getTreeTableNodeFormat();
                    if( ttnf!=null ){
                        if( ttnf.getIcons().size()>0 ){
                            cf.setIcon(ttnf.getIcons().get(0));
                            cf.iconPadRight(3.0);
                        }

                        if( ttnf.getFontFamily()!=null ){
                            cf.font(ttnf.getFontFamily(), 11f, false, false);
                        }

                        if( ttnf.getItalic()!=null ){
                            cf.italic(ttnf.getItalic());
                        }

                        if( ttnf.getBold()!=null ){
                            cf.bold(ttnf.getBold());
                        }

                        if( ttnf.getForeground()!=null ){
                            cf.color(ttnf.getForeground());
                        }

                        if( ttnf.getBackground()!=null ){
                            cf.backgroundColor(ttnf.getBackground());
                        }
                    }
                }catch( Throwable err ){
                    logException(err);
                    String msg = err.getLocalizedMessage();
                    if( msg==null ){
                        msg = err.getMessage();
                        if( msg==null ){
                            msg = err.getClass().getSimpleName();
                        }
                    }
                    context.value(msg);
                }
            }

            renderValRef.set(renderval);
            prepareCellContext( gs, context, cf, renderValRef);

            renderval = renderValRef.get();

            if( !Objects.equals(val, renderval) ){
                context.value(renderval);
            }
        }

        return super.prepare(gs, context, cf);
    }

    /**
     * Отображение
     * @param gs рендер
     * @param context контекст, включая данные отображения
     */
    @Override
    public synchronized void cellRender(Graphics2D gs, CellContext context) {
        if( !prepare(gs, context, getFormat().clone()) )return;
        if( customPainter!=null && context!=null ){
            //////////////////////////////// рендер ////////////////////////////
            // рендер фона
            backgroundRender(gs);

            // рендер текста
            //textRender(gs);

            // рендер иконки
            //imageRender(gs);

            Rectangle2D bnds = context.getBounds();
            if( bnds!=null ){
                Rectangle r = new Rectangle(
                    (int)bnds.getMinX(),
                    (int)bnds.getMinY(),
                    (int)bnds.getWidth(),
                    (int)bnds.getHeight()
                );
                customPainter.apply(gs, r);
            }

            // рендер рамки
            borderRender(gs);
        }else{
            //////////////////////////////// рендер ////////////////////////////
            // рендер фона
            backgroundRender(gs);

            // рендер текста
            textRender(gs);

            // рендер иконки
            imageRender(gs);

            // рендер рамки
            borderRender(gs);
        }
    }
}
