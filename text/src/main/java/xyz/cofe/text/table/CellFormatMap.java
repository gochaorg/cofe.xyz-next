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


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class CellFormatMap extends LinkedHashMap<Object, CellFormat>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(CellFormatMap.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(CellFormatMap.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(CellFormatMap.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(CellFormatMap.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(CellFormatMap.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(CellFormatMap.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(CellFormatMap.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public static class ColumnIndexPredicate implements Predicate<Integer>
    {
        public int columnIndex = 0;

        public ColumnIndexPredicate(){
        }

        public ColumnIndexPredicate(int colIdx){
            this.columnIndex = colIdx;
        }

        @Override
        public boolean test(Integer c) {
            if(c==null)return false;
            return c==columnIndex;
        }
    }

    public static class ColumnNamePredicate implements Predicate<String>
    {
        public String columnName = null;
        public boolean ignoreCase = true;

        public ColumnNamePredicate(){
        }

        public ColumnNamePredicate(String columnName){
            this.columnName = columnName;
        }

        public ColumnNamePredicate(String columnName,boolean ignoreCase){
            this.columnName = columnName;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public boolean test(String c) {
            if(c==null)return false;
            if( columnName==null )return false;

            return ignoreCase ? columnName.equalsIgnoreCase(c) : columnName.equals(c);
        }
    }

    public void put( int colIndex, CellFormat cf ){
        if( cf==null )throw new IllegalArgumentException( "cf==null" );
        put( new ColumnIndexPredicate(colIndex), cf );
    }

    public void put( String colName, CellFormat cf ){
        if( cf==null )throw new IllegalArgumentException( "cf==null" );
        if( colName==null )throw new IllegalArgumentException( "colName==null" );
        put( new ColumnNamePredicate(colName), cf );
    }

    public void put( String colName, boolean ignoreCase, CellFormat cf ){
        if( cf==null )throw new IllegalArgumentException( "cf==null" );
        if( colName==null )throw new IllegalArgumentException( "colName==null" );
        put( new ColumnNamePredicate(colName,ignoreCase), cf );
    }

    public CellFormatMap(){
    }

    public CellFormatMap(CellFormatMap src){
        if( src!=null ){
            putAll(src);
        }
    }

    @Override
    public CellFormatMap clone(){
        return new CellFormatMap(this);
    }

    public CellFormat[] getByName( String colname ){
        CellFormat[] res = new CellFormat[]{};
        if( colname==null )return res;
        for( Object k : keySet() ){
            if( k instanceof ColumnNamePredicate ){
                ColumnNamePredicate cp = (ColumnNamePredicate)k;
                if( cp.test(colname) ){
                    res = Arrays.copyOf(res, res.length+1);
                    res[res.length-1] = get(k);
                }
            }
        }
        return res;
    }

    public CellFormat[] getByIndex( int colidx ){
        CellFormat[] res = new CellFormat[]{};
        for( Object k : keySet() ){
            if( k instanceof ColumnIndexPredicate ){
                ColumnIndexPredicate cp = (ColumnIndexPredicate)k;
                if( cp.test(colidx) ){
                    res = Arrays.copyOf(res, res.length+1);
                    res[res.length-1] = get(k);
                }
            }
        }
        return res;
    }
}
