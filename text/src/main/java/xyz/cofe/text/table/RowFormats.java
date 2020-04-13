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
 *
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class RowFormats {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(RowFormats.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(RowFormats.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(RowFormats.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(RowFormats.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(RowFormats.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(RowFormats.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(RowFormats.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected static RowFormat asciiRow = null;
    public static RowFormat asciiRow(){
        if( asciiRow!=null )return asciiRow;
        asciiRow = new RowFormat();
        asciiRow.setBorder(Borders.asciiLine().clone());
        asciiRow.getVerticalSplitter().setWidth(1);
        asciiRow.getVerticalSplitter().setText("|");
        asciiRow.getVerticalSplitter().setTopText("+");
        asciiRow.getVerticalSplitter().setBottomText("+");
        return asciiRow;
    }

    protected static RowFormat singleRow = null;
    public static RowFormat singleRow(){
        if( singleRow!=null )return singleRow;
        singleRow = new RowFormat();
        singleRow.setBorder(Borders.singleLine().clone());
        singleRow.getVerticalSplitter().setWidth(1);
        singleRow.getVerticalSplitter().setText("\u2502");
        singleRow.getVerticalSplitter().setTopText("\u252C");
        singleRow.getVerticalSplitter().setBottomText("\u2534");
        return singleRow;
    }

    protected static RowFormat doubleRow = null;
    public static RowFormat doubleRow(){
        if( singleRow!=null )return singleRow;
        singleRow = new RowFormat();
        singleRow.setBorder(Borders.doubleLine().clone());
        singleRow.getVerticalSplitter().setWidth(1);
        singleRow.getVerticalSplitter().setText("\u2551");
        singleRow.getVerticalSplitter().setTopText("\u2566");
        singleRow.getVerticalSplitter().setBottomText("\u2569");
        return singleRow;
    }
}
