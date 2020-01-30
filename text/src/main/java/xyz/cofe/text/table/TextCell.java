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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.iter.Eterable;
import xyz.cofe.text.Text;

/**
 * Блок форматированного текста.
 * Умеет объединять текстовые блоки по ширине
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TextCell {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(TextCell.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(TextCell.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(TextCell.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(TextCell.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(TextCell.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(TextCell.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(TextCell.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * данные блока
     */
    protected ArrayList<String> data = new ArrayList<String>();

    /**
     * исходный текст
     */
    protected String sourceText = null;

    /**
     * Конструктор
     * @param data данные блока
     */
    public TextCell(Iterable<String> data){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        //Iterators.addTo(data, this.data);
        this.data.addAll(Eterable.of(data).toList());
    }

    /**
     * Конструктор
     * @param data данные блока
     */
    public TextCell(String[] data){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        this.data.addAll(Arrays.asList(data));
    }

    /**
     * Конструктор
     * @param data данные блока
     * @param source исходный текст
     */
    public TextCell(String[] data,String source){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        this.data.addAll(Arrays.asList(data));
        this.sourceText = source;
    }

    /**
     * Возвращает текстовые линии в блоке
     * @return текст
     */
    public String[] getTextLines(){
        return data.toArray(new String[]{});
    }

    /**
     * Максмальная ширира текстового блока
     */
    protected int maxWidth = Integer.MIN_VALUE;

    /**
     * Минимальная ширина текстового блока
     */
    protected int minWidth = Integer.MAX_VALUE;

    /**
     * Вычисляет минимальную и максимальную ширину,
     * обновляет {@link #maxWidth}, {@link #minWidth}
     */
    protected void evalMinMaxWidth(){
        int minw = Integer.MAX_VALUE;
        int maxw = Integer.MIN_VALUE;
        for( String line : data ){
            int w = line.length();
            if( w>maxw )maxw = w;
            if( w<minw )minw = w;
        }
        maxWidth = maxw;
        minWidth = minw;
    }

    /**
     * Возвращает максимальную ширину
     * @return максимальная ширина
     */
    public int getMaxWidth(){
        if( maxWidth>Integer.MIN_VALUE )return maxWidth;
        evalMinMaxWidth();
        return maxWidth;
    }

    /**
     * Возвращает минимальную ширину
     * @return минимальная ширина
     */
    public int getMinWidth(){
        if( minWidth<Integer.MAX_VALUE )return minWidth;
        evalMinMaxWidth();
        return minWidth;
    }

    /**
     * Возвращает высоту блока
     * @return высота блока
     */
    public int getHeight(){
        return data.size();
    }

    /**
     * Создает прямойгольный текстовй блок, объеденяя блоки по горизонтали
     * @param tcells блоки
     * @return широкий блок текста
     */
    public TextCell hjoin( Iterable<TextCell> tcells ){
        Eterable<TextCell> cells = Eterable.of(tcells);
        Eterable<TextCell> single = Eterable.single(this);
        //cells = Iterators.sequence(single,cells);
        cells = single.union(cells);

        return new TextCell( horizontalJoin(cells) );
    }

    /**
     * Создает прямойгольный текстовй блок, объеденяя блоки по горизонтали
     * @param tcells блоки
     * @return широкий блок текста
     */
    public TextCell hjoin( TextCell ... tcells ){
        List<TextCell> cells = Arrays.asList(tcells);
        cells.add(0, this);
        return new TextCell( horizontalJoin(cells) );
    }

    /**
     * Создает прямойгольный текстовй блок, объеденяя блоки по горизонтали
     * @param tcells блоки
     * @return широкий блок текста
     */
    public static List<String> horizontalJoin( Iterable<TextCell> tcells ){
        return horizontalJoin(Eterable.of(tcells).toList().toArray(new TextCell[]{}));
    }

    /**
     * Создает прямойгольный текстовй блок, объеденяя блоки по горизонтали
     * @param tcells блоки
     * @return широкий блок текста
     */
    public static TextCell horizontalJoinAsTextCell( Iterable<TextCell> tcells ){
        return new TextCell( horizontalJoin(tcells) );
    }

    /**
     * Создает прямойгольный текстовй блок, объеденяя блоки по горизонтали
     * @param tcells блоки
     * @return широкий блок текста
     */
    public static TextCell horizontalJoinAsTextCell( TextCell ... tcells ){
        return new TextCell( horizontalJoin(tcells) );
    }

    /**
     * Создает прямойгольный текстовй блок, объеденяя блоки по горизонтали
     * @param tcells блоки
     * @return широкий блок текста
     */
    public static List<String> horizontalJoin( TextCell ... tcells ){
        List<String> lines = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        Bounds b = Bounds.max(tcells);
        for( int y=0; y<b.getHeight(); y++ ){
            sb.setLength(0);
            for( TextCell tc : tcells ){
                String[] tcLines = tc.getTextLines();
                if( y < tcLines.length ){
                    sb.append(tcLines[y]);
                }else{
                    sb.append( Text.repeat( " ", tc.getMaxWidth()) );
                }
            }
            lines.add(sb.toString());
        }

        return lines;
    }

    /**
     * Создает текстовый блок заполненый повторяющимся текстом
     * @param repeatText текст
     * @param width ширина блока
     * @param height высота блока
     * @return блок
     */
    public static TextCell createBlock( String repeatText, int width, int height ){
        if( repeatText==null || repeatText.length()<1 )repeatText = " ";
        if( height<1 ){
            TextCell tc = new TextCell(new String[]{});
            return tc;
        }else if( width<1 && height>=1 ){
            String[] data = new String[height];
            for( int i=0; i<height; i++ ){
                data[i] = "";
            }
            return new TextCell(data);
        }

        ArrayList<String> l = new ArrayList<String>();
        for( int y=0; y<height; y++ ){
            String t = repeatText;
            while( t.length()<width ){
                t = t + repeatText;
            }
            if( t.length()>width )t = t.substring(0, width);
            l.add(t);
        }

        return new TextCell(l.toArray(new String[]{}));
    }
}
