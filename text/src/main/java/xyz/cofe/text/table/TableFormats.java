/*
 * The MIT License
 *
 * Copyright 2015 Kamnev Georgiy (nt.gocha@gmail.com).
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
 * Форматы таблиц
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TableFormats {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(TableFormats.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(TableFormats.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(TableFormats.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(TableFormats.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(TableFormats.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(TableFormats.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(TableFormats.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Формат таблицы с ascii набором графики">
    private static TableFormat asciiTable = null;
    public static TableFormat asciiTable(){
        if( asciiTable!=null )return asciiTable;
        asciiTable = new TableFormat();

        CellFormat defcf = CellFormats.def().clone();
        defcf.setThreeDots("..");

        Border brdHeader = Borders.asciiLine().clone();
        brdHeader.setBottomText("=");

        VerticalSplitter vspt = new VerticalSplitter();
        vspt.setText("|");
        vspt.setTopText("+");
        vspt.setBottomText("+");

        asciiTable.setDefaultCell(defcf);

        asciiTable.setFirstCell(defcf);
        asciiTable.setMiddleCell(defcf);
        asciiTable.setLastCell(defcf);

        asciiTable.getHeader().setBorder(brdHeader);
        asciiTable.getHeader().setVerticalSplitter(vspt);

        Border brd2 = Borders.asciiLine().clone();
        brd2.setTopHeight(0);

        asciiTable.getFirstRow().setBorder(brd2);
        asciiTable.getFirstRow().setVerticalSplitter(vspt);

        asciiTable.getMiddleRow().setBorder(brd2);
        asciiTable.getMiddleRow().setVerticalSplitter(vspt);

        asciiTable.getLastRow().setBorder(brd2);
        asciiTable.getLastRow().setVerticalSplitter(vspt);

        asciiTable.setBorder( Borders.empty().clone() );

        return asciiTable;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="unicode формат таблицы">
    private static TableFormat unicodeTable = null;
    public static TableFormat unicodeTable(){
        if( unicodeTable!=null )return unicodeTable;
        unicodeTable = new TableFormat();
        return unicodeTable;
    }
    //</editor-fold>

//    public static class DefCellBuilder {
//    }

    public static class TableBuilder {
        private TableFormat tf = null;

        private boolean headerVisible = true;
        private boolean innerHLines = true;
        private boolean innerVLines = true;

        private boolean outerHLines = true;
        private boolean outerVLines = true;

        private Border headerBorder = null;
        private Border firstRowBorder = null;
        private Border middleRowBorder = null;
        private Border lastRowBorder = null;

        private VerticalSplitter vsplit = null;
        private boolean vsplitTextOnly = false;

        //<editor-fold defaultstate="collapsed" desc="defVSplitText">
        private String defVSplitText = "\u2502";

        public String getDefVSplitText() {
            if( defVSplitText==null )defVSplitText = "\u2502";
            return defVSplitText;
        }

        public void setDefVSplitText(String defVsplitText) {
            this.defVSplitText = defVsplitText;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="defVSplitText">
        private String defVSplitTopText = "\u252C";

        public String getDefVSplitTopText() {
            if( defVSplitTopText==null )defVSplitTopText = "\u252C";
            return defVSplitTopText;
        }

        public void setDefVSplitTopText(String defVsplitText) {
            this.defVSplitTopText = defVsplitText;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="defVSplitText">
        private String defVSplitBottomText = "\u2534";

        public String getDefVSplitBottomText() {
            if( defVSplitBottomText==null )defVSplitBottomText = "\u2534";
            return defVSplitBottomText;
        }

        public void setDefVSplitBottomText(String defVsplitText) {
            this.defVSplitBottomText = defVsplitText;
        }
        //</editor-fold>

        public TableBuilder(TableFormat tableFormat) {
            if( tableFormat==null )throw new IllegalArgumentException( "tableFormat==null" );

            this.tf = tableFormat;

            headerBorder = tableFormat.getHeader().getBorder().clone();
            firstRowBorder = tableFormat.getFirstRow().getBorder().clone();
            middleRowBorder = tableFormat.getMiddleRow().getBorder().clone();
            lastRowBorder = tableFormat.getLastRow().getBorder().clone();
        }

        public TableFormat get(){
            if( vsplit!=null ){
                if( vsplitTextOnly ){
                    tf.getHeader().getVerticalSplitter().setText(vsplit.getText());
                    tf.getFirstRow().getVerticalSplitter().setText(vsplit.getText());
                    tf.getMiddleRow().getVerticalSplitter().setText(vsplit.getText());
                    tf.getLastRow().getVerticalSplitter().setText(vsplit.getText());
                }else{
                    tf.getHeader().setVerticalSplitter(vsplit);
                    tf.getFirstRow().setVerticalSplitter(vsplit);
                    tf.getMiddleRow().setVerticalSplitter(vsplit);
                    tf.getLastRow().setVerticalSplitter(vsplit);
                }
            }

            tf.setHeaderVisible(headerVisible);
            if( headerVisible ){
//                tf.getFirstRow().setBorder(firstRowBorder);
            }else{
                tf.getFirstRow().getBorder().setTopHeight(headerBorder.getBottomHeigth());

                tf.getFirstRow().getBorder().setTopText(headerBorder.getTopText());
                tf.getFirstRow().getBorder().setLeftTopText(headerBorder.getLeftTopText());
                tf.getFirstRow().getBorder().setRightTopText(headerBorder.getRightTopText());
            }

            if( !innerHLines ){
                tf.getFirstRow().getBorder().bottomHeigth(0);
                tf.getFirstRow().getBorder().topHeight(0);

                tf.getMiddleRow().getBorder().bottomHeigth(0);
                tf.getMiddleRow().getBorder().topHeight(0);

                tf.getLastRow().getBorder().topHeight(0);
            }

            if( !innerVLines ){
                tf.getHeader().getVerticalSplitter().setWidth(0);
                tf.getFirstRow().getVerticalSplitter().setWidth(0);
                tf.getMiddleRow().getVerticalSplitter().setWidth(0);
                tf.getLastRow().getVerticalSplitter().setWidth(0);
            }

            if( !outerHLines ){
                tf.getHeader().getBorder().topHeight(0);
                tf.getLastRow().getBorder().bottomHeigth(0);
            }

            if( !outerVLines ){
                tf.getHeader().getBorder().leftWidth(0);
                tf.getHeader().getBorder().rightWidth(0);

                tf.getFirstRow().getBorder().leftWidth(0);
                tf.getFirstRow().getBorder().rightWidth(0);

                tf.getMiddleRow().getBorder().leftWidth(0);
                tf.getMiddleRow().getBorder().rightWidth(0);

                tf.getLastRow().getBorder().leftWidth(0);
                tf.getLastRow().getBorder().rightWidth(0);
            }

            if( innerHLines && vsplitTextOnly && vsplit!=null ){
                tf.getHeader().getVerticalSplitter().setTopText(tf.getHeader().getBorder().getTopText());
                tf.getHeader().getVerticalSplitter().setBottomText(tf.getHeader().getBorder().getBottomText());

                tf.getFirstRow().getVerticalSplitter().setTopText(tf.getFirstRow().getBorder().getTopText());
                tf.getFirstRow().getVerticalSplitter().setBottomText(tf.getFirstRow().getBorder().getBottomText());

                tf.getMiddleRow().getVerticalSplitter().setTopText(tf.getMiddleRow().getBorder().getTopText());
                tf.getMiddleRow().getVerticalSplitter().setBottomText(tf.getMiddleRow().getBorder().getBottomText());

                tf.getLastRow().getVerticalSplitter().setTopText(tf.getLastRow().getBorder().getTopText());
                tf.getLastRow().getVerticalSplitter().setBottomText(tf.getLastRow().getBorder().getBottomText());
            }

            return tf;
        }

        public TableBuilder header( boolean visible ){
            headerVisible = visible;
            return this;
        }

        public TableBuilder innerHLines( boolean visible ){
            innerHLines = visible;
            return this;
        }

        public TableBuilder innerVLines( boolean visible ){
            innerVLines = visible;
            return this;
        }

        public TableBuilder outerHLines( boolean visible ){
            outerHLines = visible;
            return this;
        }

        public TableBuilder outerVLines( boolean visible ){
            outerVLines = visible;
            return this;
        }

        public TableBuilder vsplit( VerticalSplitter vsplit ){
            this.vsplit = vsplit;
            return this;
        }

        public TableBuilder vsplit( int width, String topText, String text, String bottomText ){
            text = text==null ? getDefVSplitText() :
                (text.length()<1 ? getDefVSplitText() : text);

            topText = topText==null ? getDefVSplitTopText() :
                (topText.length()<1 ? getDefVSplitTopText() : topText);

            bottomText = bottomText==null ? getDefVSplitBottomText() :
                (bottomText.length()<1 ? getDefVSplitBottomText() : bottomText);

            this.vsplit = new VerticalSplitter(width, topText, text, bottomText);
            return this;
        }

        public TableBuilder vsplit( int width, String text ){
            text = text==null ? getDefVSplitText() : (text.length()<1 ? getDefVSplitText() : text);
            vsplitTextOnly = true;
            this.vsplit = new VerticalSplitter(width, text, text, text);
            return this;
        }

        public TableBuilder vsplit( String text ){
            text = text==null ? getDefVSplitText() : (text.length()<1 ? getDefVSplitText() : text);
            vsplitTextOnly = true;
            this.vsplit = new VerticalSplitter(text.length(), text, text, text);
            return this;
        }
    }

    public static TableBuilder unicode(){
        TableBuilder b = new TableBuilder(unicodeTable().clone());
        return b;
    }

    public static TableBuilder ascii(){
        TableBuilder b = new TableBuilder(asciiTable().clone());
        b.setDefVSplitBottomText("+");
        b.setDefVSplitTopText("+");
        b.setDefVSplitText("|");
        return b;
    }
}
