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
 * Блок форматированного текста
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

    protected ArrayList<String> data = new ArrayList<String>();
    protected String sourceText = null;

    public TextCell(Iterable<String> data){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        //Iterators.addTo(data, this.data);
        this.data.addAll(Eterable.of(data).toList());
    }

    public TextCell(String[] data){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        this.data.addAll(Arrays.asList(data));
    }

    public TextCell(String[] data,String source){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        this.data.addAll(Arrays.asList(data));
        this.sourceText = source;
    }

    public String[] getTextLines(){
        return data.toArray(new String[]{});
    }

    protected int maxWidth = Integer.MIN_VALUE;
    protected int minWidth = Integer.MAX_VALUE;

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

    public int getMaxWidth(){
        if( maxWidth>Integer.MIN_VALUE )return maxWidth;
        evalMinMaxWidth();
        return maxWidth;
    }

    public int getMinWidth(){
        if( minWidth<Integer.MAX_VALUE )return minWidth;
        evalMinMaxWidth();
        return minWidth;
    }

    public int getHeight(){
        return data.size();
    }

    public TextCell join( Iterable<TextCell> tcells ){
        Eterable<TextCell> cells = Eterable.of(tcells);
        Eterable<TextCell> single = Eterable.single(this);
        //cells = Iterators.sequence(single,cells);
        cells = single.union(cells);

        return new TextCell( joinAsList(cells) );
    }

    public TextCell join( TextCell ... tcells ){
        List<TextCell> cells = Arrays.asList(tcells);
        cells.add(0, this);
        return new TextCell( joinAsList(cells) );
    }

    public static List<String> joinAsList( Iterable<TextCell> tcells ){
        return joinAsList(Eterable.of(tcells).toList().toArray(new TextCell[]{}));
    }

    public static TextCell joinAsTextCell( Iterable<TextCell> tcells ){
        return new TextCell( joinAsList(tcells) );
    }

    public static TextCell joinAsTextCell( TextCell ... tcells ){
        return new TextCell( joinAsList(tcells) );
    }

    public static List<String> joinAsList( TextCell ... tcells ){
        List<String> lines = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        Bounds b = Bounds.get(tcells);
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
