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
 * Бордюры вокруг ячейки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class Borders {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Borders.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Borders.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(Borders.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Borders.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Borders.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Borders.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Borders.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    private static Border empty = null;
    public static Border empty(){
        if( empty!=null )return empty;
        empty = new Border();
        return empty;
    }

    private static Border asciiLine = null;
    public static Border asciiLine(){
        if( asciiLine!=null )return asciiLine;
        asciiLine = new Border();

        asciiLine.setLeftWidth(1);
        asciiLine.setRightWidth(1);
        asciiLine.setTopHeight(1);
        asciiLine.setBottomHeigth(1);

        asciiLine.setLeftText("|");
        asciiLine.setRightText("|");
        asciiLine.setTopText("-");
        asciiLine.setBottomText("-");

        asciiLine.setLeftTopText("+");
        asciiLine.setRightTopText("+");
        asciiLine.setLeftBottomText("+");
        asciiLine.setRightBottomText("+");

        return asciiLine;
    }

    private static Border singleLine = null;
    public static Border singleLine(){
        if( singleLine!=null )return singleLine;
        singleLine = new Border();

        singleLine.setLeftWidth(1);
        singleLine.setRightWidth(1);
        singleLine.setTopHeight(1);
        singleLine.setBottomHeigth(1);

        singleLine.setLeftText("\u2502");
        singleLine.setRightText("\u2502");
        singleLine.setTopText("\u2500");
        singleLine.setBottomText("\u2500");

        singleLine.setLeftTopText("\u250C");
        singleLine.setRightTopText("\u2510");
        singleLine.setLeftBottomText("\u2514");
        singleLine.setRightBottomText("\u2518");

        return singleLine;
    }

    private static Border doubleLine = null;
    public static Border doubleLine(){
        if( doubleLine!=null )return doubleLine;
        doubleLine = new Border();

        doubleLine.setLeftWidth(1);
        doubleLine.setRightWidth(1);
        doubleLine.setTopHeight(1);
        doubleLine.setBottomHeigth(1);

        doubleLine.setLeftText("\u2551");
        doubleLine.setRightText("\u2551");
        doubleLine.setTopText("\u2550");
        doubleLine.setBottomText("\u2550");

        doubleLine.setLeftTopText("\u2554");
        doubleLine.setRightTopText("\u2557");
        doubleLine.setLeftBottomText("\u255A");
        doubleLine.setRightBottomText("\u255D");

        return doubleLine;
    }
}
