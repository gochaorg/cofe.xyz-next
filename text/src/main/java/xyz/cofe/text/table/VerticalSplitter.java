/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

package xyz.cofe.text.table;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class VerticalSplitter
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(VerticalSplitter.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(VerticalSplitter.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(VerticalSplitter.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(VerticalSplitter.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(VerticalSplitter.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(VerticalSplitter.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(VerticalSplitter.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public VerticalSplitter(){
    }

    public VerticalSplitter(int width,String topText,String text,String bottomText){
        this.text = text==null ? this.text : text;
        this.topText = topText==null ? this.topText : topText;
        this.bottomText = bottomText==null ? this.bottomText : bottomText;
        this.width = width;
    }

    public VerticalSplitter(VerticalSplitter src){
        if( src!=null ){
            text =
                src.text!=null ?
                    src.text :
                    text;

            topText =
                src.topText!=null ?
                    src.topText :
                    topText;

            bottomText =
                src.bottomText!=null ?
                    src.bottomText :
                    bottomText;

            width = src.width;
        }
    }

    @Override
    public VerticalSplitter clone(){
        return new VerticalSplitter(this);
    }

    //<editor-fold defaultstate="collapsed" desc="text">
    private String text = "\u2502";

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="topText">
    private String topText = "\u252C";

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottomText">
    private String bottomText = "\u2534";

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="width">
    private int width = 1;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    //</editor-fold>
}
