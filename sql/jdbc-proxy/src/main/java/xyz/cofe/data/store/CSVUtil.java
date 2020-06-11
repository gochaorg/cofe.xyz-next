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

package xyz.cofe.data.store;


import xyz.cofe.fn.Pair;
import xyz.cofe.text.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class CSVUtil {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(CSVUtil.class.getName()).log(Level.FINE, message, args);
    }
    
    private static void logFiner(String message,Object ... args){
        Logger.getLogger(CSVUtil.class.getName()).log(Level.FINER, message, args);
    }
    
    private static void logFinest(String message,Object ... args){
        Logger.getLogger(CSVUtil.class.getName()).log(Level.FINEST, message, args);
    }
    
    private static void logInfo(String message,Object ... args){
        Logger.getLogger(CSVUtil.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(CSVUtil.class.getName()).log(Level.WARNING, message, args);
    }
    
    private static void logSevere(String message,Object ... args){
        Logger.getLogger(CSVUtil.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(CSVUtil.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>
    
    /**
     * Парсинг разделителя между ячейками
     * @param line Исходный текст
     * @param beginIndex С какого индекса производить поиск
     * @param desc Описание CSV
     * @return Пара разделитель + с какой далее производить поиск или null если не найдено
     */
    public Pair<String,Integer> parseCellDelimiter( String line, int beginIndex, CSVDesc desc ){
        if( line==null )return null;
        if( beginIndex<0 )return null;
        if( desc==null )return null;
        
        String delim = desc.getCellDelimiter();
        if( delim==null )return null;
        if( delim.length()<1 )return null;
        int delimLen = delim.length();

        int dStart = line.indexOf(delim, beginIndex);
        if( dStart<0 )return null;
        
        return Pair.of(
            line.substring(beginIndex, dStart + delimLen),
            dStart + delimLen);
    }
    
    /**
     * Парсинг экранированной строки (ячейки).<br>
     * <font style="font-family:monospaced">
     * quotedLine ::= {any_char} cellQuote { ( nonQuotedChar | quotedChar ) } cellQuote. <br>
     * nonQuotedChar ::= ! cellQuote <br>
     * quotedChar ::= cellQuote cellQuote <br>
     * </font>
     * @param line Исходный текст
     * @param beginIndex С какого индекса производить поиск
     * @param desc Описание CSV
     * @return Пара декодированный текст + с какой далее производить поиск или null если не найдено
     */
    public Pair<String,Integer> parseQoutedString( String line, int beginIndex, CSVDesc desc ){
        if( line==null )return null;
        if( beginIndex<0 )return null;
        if( desc==null )return null;
        
        String quote = desc.getCellQuote();
        if( quote==null )return null;
        if( quote.length()<1 )return null;
        int quoteLen = quote.length();
        
        if( beginIndex>=line.length() )return null;
        
        int qStart = line.indexOf(quote, beginIndex);
        if( qStart<0 )return null;
        
        int ptr = qStart + quoteLen;
        int nextPTR = -1;
        
        StringBuilder sb = new StringBuilder();
        
        while( true ){
            String CHR = Text.lookupText(line, ptr, 1);
            
            String L0 = Text.lookupText(
                line, 
                ptr, 
                quoteLen);
            String L1 = Text.lookupText(
                line, 
                ptr+quoteLen, 
                quoteLen);
            
            if( CHR.length()<1 ){
                nextPTR = ptr;
                break;
            }
            
            if( L0.equals(quote) &&  L1.equals(quote) ){
                ptr += quoteLen * 2;
                sb.append(quote);
                continue;
            }

            if( L0.equals(quote) && !L1.equals(quote) ){
                nextPTR = ptr + quoteLen;
                break;
            }
            
            sb.append(CHR);
            ptr += CHR.length();
        }
        
        return Pair.of(sb.toString(), nextPTR);
    }
    
    /**
     * Парсинг CSV строки
     * @param line строка
     * @param desc Описание CSV
     * @return Значения CSV
     */
    public String[] parseLine( String line, CSVDesc desc ){
        if( line==null )throw new IllegalArgumentException( "line==null" );
        if( desc==null )throw new IllegalArgumentException( "desc==null" );
        
        if( desc.isFixedWidth() ){
            return parseFixedWidthLine(line, desc);
        }else{
            return parseNonFixedWidthLine(line, desc);
        }
    }
    
    /**
     * Парсинг CSV строки
     * @param line строка
     * @param desc Описание CSV
     * @return Значения CSV
     */
    public String[] parseFixedWidthLine( String line, CSVDesc desc ){
        if( line==null )throw new IllegalArgumentException( "line==null" );
        if( desc==null )throw new IllegalArgumentException( "desc==null" );
        List<FixedColumn> lcolumns = desc.getFixedColumns();
        
        String[] acolumns = new String[lcolumns.size()];
        for( int icol = 0; icol<acolumns.length; icol++ ){
            acolumns[icol] = "";
        }
        
        int idxCol = -1;
        for( FixedColumn fcol : lcolumns ){
            idxCol++;
            int begin = fcol==null ? -1 : fcol.getBegin();
            int len = fcol==null ? -1 : fcol.getLength();
            
            if( len<1 || begin<0 )continue;
            if( begin>=line.length() )continue;
            
            int end = begin + len;
            if( end>line.length() )end = line.length();
            
            String data = line.substring(begin, end);
            acolumns[idxCol] = data;
        }
            
        return acolumns;
    }
    
    /**
     * Парсинг CSV строки
     * @param line строка
     * @param desc Описание CSV
     * @return Значения CSV
     */
    public String[] parseNonFixedWidthLine( String line, CSVDesc desc ){
        if( line==null )throw new IllegalArgumentException( "line==null" );
        if( desc==null )throw new IllegalArgumentException( "desc==null" );
        
        if( line.length()==0 )return new String[]{};
        
        // Символ экранирования ячейки
        String cellQuote = desc.getCellQuote();
        if( cellQuote==null )throw new IllegalStateException("desc.getCellQuote()==null");
        if( cellQuote.length()<1 )throw new IllegalStateException("desc.getCellQuote().length()<1");
        
        // Символ - разделитель ячеек
        String cellDelim = desc.getCellDelimiter();
        if( cellDelim==null )throw new IllegalStateException("desc.getCellDelimiter()==null");
        if( cellDelim.length()<1 )throw new IllegalStateException("desc.getCellDelimiter().length()<1");
        
        // Декодированные ячейки
        ArrayList<String> cellData = new ArrayList<String>();

        int ptr = 0;
        int co = 0;
        Pair<String,Integer> res = null;
        
        if( desc.isSkipFirstWS() ){
            boolean found = false;
            for( int p=0; p<line.length(); p++ ){
                char c = line.charAt(p);
                if( !Character.isWhitespace(c) ){
                    ptr = p;
                    found = true;
                    break;
                }
            }
            if( !found )return new String[]{};
        }
        
        while( true ){
            if( ptr>=line.length() ){
                break;
            }
            
            if( co>0 ){
                res = parseCellDelimiter(line, ptr, desc);
                if( res==null ){
                    break;
                }else{
                    ptr = res.b();
                }
            }
            
            boolean stopRead = false;
            
            switch( desc.getQuoteVariants() ){
                case Always:
                    {
                        res = parseQoutedString(line, ptr, desc);
                        if( res==null ){
                            stopRead = true;
                        }else{
                            ptr = res.b();
                            cellData.add( res.a() );
                            co++;
                        }
                    }
                    break;
                case Never:
                    {
                        int di = line.indexOf(cellDelim,ptr);
                        if( di<0 ){
                            String data = line.substring(ptr);
                            cellData.add( data );
                            co++;
                            stopRead = true;
                        }else{
                            if( di>ptr ){
                                int from = ptr;
                                int toEx = di;
                                ptr = di;
                                cellData.add( line.substring(from, toEx) );
                                co++;
                            }else{
                                ptr = di;
                                cellData.add( "" );
                                co++;
                            }
                        }
                    }
                    break;
                case Sometimes:
                    {
                        String q = Text.lookupText(line, ptr, cellQuote.length());
                        if( q.equals(cellQuote) ){
                            res = parseQoutedString(line, ptr, desc);
                            if( res==null ){
                                stopRead = true;
                            }else{
                                ptr = res.b();
                                cellData.add( res.a() );
                                co++;
                            }
                        }else{
                            int di = line.indexOf(cellDelim,ptr);
                            if( di<0 ){
                                String data = line.substring(ptr);
                                cellData.add( data );
                                co++;
                                stopRead = true;
                            }else{
                                if( di>ptr ){
                                    int from = ptr;
                                    int toEx = di;
                                    ptr = di;
                                    cellData.add( line.substring(from, toEx) );
                                    co++;
                                }else{
                                    ptr = di;
                                    cellData.add( "" );
                                    co++;
                                }
                            }
                        }
                    }
                    break;
            }
            
            if( stopRead ){
                break;
            }
        }
        
        return cellData.toArray(new String[]{});
    }
    
    /**
     * Преобразование значений в строку CSV
     * @param cells значения
     * @param desc описание структуры CSV
     * @return Строка CSV
     */
    public String toString( String[] cells, CSVDesc desc ){
        if( cells==null )return null;
        if( desc==null )return null;
        if( cells.length==0 )return "";
        if( desc.isFixedWidth() ){
            return toStringFixedWidthLine(cells, desc.getFixedColumns());
        }else{
            return toStringNonFixedWidthLine(cells, desc);
        }
    }
    
    /**
     * Преобразование значений в строку CSV
     * @param cells значения
     * @param desc описание структуры CSV
     * @return Строка CSV
     */
    public String toString( List<String> cells, CSVDesc desc ){
        if( cells==null )return null;
        if( desc==null )return null;
        if( cells.isEmpty() )return "";
        if( desc.isFixedWidth() ){
            return toStringFixedWidthLine(cells.toArray(new String[]{}), desc.getFixedColumns());
        }else{
            return toStringNonFixedWidthLine(cells.toArray(new String[]{}), desc);
        }
    }
    
    /**
     * Преобразование значений в строку CSV
     * @param cells значения
     * @param desc описание структуры CSV
     * @return Строка CSV
     */
    public String toString( Iterable<String> cells, CSVDesc desc ){
        if( cells==null )return null;
        if( desc==null )return null;
        
        List<String> cdata = new ArrayList<>();
        for( String str : cells ){
            cdata.add(str);
        }
        return toString(cdata, desc);
    }
    
    protected String toStringFixedWidthLine( String[] cells, List<FixedColumn> fcolumns ){
        if( fcolumns.isEmpty() )return "";
        
        List<Pair<Integer,String>> cellValues = new LinkedList<Pair<Integer,String>>();
        
        int strWidth = 0;
        for( int coli=0; coli<fcolumns.size(); coli++ ){
            if( coli>=cells.length )break;

            String cellValue = cells[coli];
            if( cellValue==null ) cellValue = "";

            if( cellValue.contains("\r\n") ) cellValue = cellValue.replace("\r\n", " ");
            if( cellValue.contains("\n\r") ) cellValue = cellValue.replace("\n\r", " ");
            if( cellValue.contains("\n") ) cellValue = cellValue.replace("\n", " ");
            if( cellValue.contains("\r") ) cellValue = cellValue.replace("\r", " ");
            
            FixedColumn fcol = fcolumns.get(coli);
            
            int begin = fcol.getBegin();
            int len = fcol.getLength();
            
            if( len<0 )continue;
            if( len==0 ){
                cellValue = "";
            }else{
                if( cellValue.length()<len ){
                    StringBuilder sb = new StringBuilder();
                    int padSize = len - cellValue.length();
                    for( int pi=0; pi<padSize; pi++ ){
                        sb.append(" ");
                    }
                    cellValue = cellValue + sb.toString();
                }else if( cellValue.length()>=len ){
                    cellValue = cellValue.substring(0, len);
                }
            }
            
            if( begin<0 )continue;
            
            int cellValueEnd = begin + len;
            if( strWidth<cellValueEnd )strWidth = cellValueEnd;
            
            cellValues.add( Pair.of( begin, cellValue ) );
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(Text.repeat(" ", strWidth));
        
        for( Pair<Integer,String> cellVal : cellValues ){
            sb.replace(cellVal.a(), cellVal.a()+cellVal.b().length(), cellVal.b());
        }
        
        return sb.toString();
    }

    protected String toStringNonFixedWidthLine( String[] cells, CSVDesc desc ){
        StringBuilder csvLine = new StringBuilder();
        
        for( int celi=0; celi<cells.length; celi++ ){
            String cellValue = cells[celi];
            if( cellValue==null )cellValue = "";
            
            if( desc.isSkipFirstWS() && celi==0 ){
                int state = 0;
                StringBuilder sbCell = new StringBuilder();
                for( int ci=0; ci<cellValue.length(); ci++ ){
                    char ch = cellValue.charAt(ci);
                    switch( state ){
                        case 0:
                            if( Character.isWhitespace(ch) ){
                            }else{
                                sbCell.append(ch);
                                state = 1;
                            }
                            break;
                        case 1:
                            sbCell.append(ch);
                            break;
                    }
                }
                cellValue = sbCell.toString();
            }
            
            String delim = desc.getCellDelimiter();
            
            boolean replaceDelim = true;
            String delimReplacer = " ";
            
            String cellQuete = desc.getCellQuote();
            cellQuete = cellQuete==null ? "\"" : cellQuete;
            
            if( delim!=null && delim.length()>0 && celi>0 ){
                csvLine.append(delim);
            }
            
            if( cellValue.contains("\r\n") ) cellValue = cellValue.replace("\r\n", " ");
            if( cellValue.contains("\n\r") ) cellValue = cellValue.replace("\n\r", " ");
            if( cellValue.contains("\n") ) cellValue = cellValue.replace("\n", " ");
            if( cellValue.contains("\r") ) cellValue = cellValue.replace("\r", " ");

            switch( desc.getQuoteVariants() ){
                case Never:
                    if( replaceDelim && replacableDelimiter(cellValue,delim,delimReplacer) ){
                        csvLine.append(replaceDelimiter(cellValue, delim, delimReplacer));
                    }else{
                        csvLine.append(cellValue);
                    }
                    break;
                case Sometimes:
                    if( needQuete_sometime( cellValue, cellQuete, delim ) ){
                        csvLine.append(queteCellValue(cellValue, cellQuete));
                    }else{
                        csvLine.append(cellValue);
                    }
                    break;
                case Always:
                default:
                    csvLine.append(queteCellValue(cellValue, cellQuete));
                    break;
            }
        }
        return csvLine.toString();
    }
    
    private boolean needQuete_sometime( String cellValue, String quete, String delim ){
        if( cellValue==null || cellValue.length()<1 )return false;
        
        for( int ci=0; ci<cellValue.length(); ci++ ){
            if( Text.matchText(cellValue, quete, ci, false) ){
                return true;
            }
            if( delim!=null && delim.length()>0 && Text.matchText(cellValue, delim, ci, false) ){
                return true;
            }
        }
        
        return false;
    }
    
    private boolean containsDelimeter( String cellValue, String delim ){
        if( cellValue==null || cellValue.length()<1 )return false;
        return delim!=null && delim.length()>0 && !delim.equals(" ") && cellValue.contains(delim);
    }
    
    private String replaceDelimiter( String cellValue, String delim, String replacer ){
        return cellValue.replace(delim, replacer);
    }
    
    private boolean replacableDelimiter( String cellValue, String delim, String replacer ){
        return containsDelimeter(cellValue, delim) && delim!=null && !delim.equals(replacer);
    }
    
    private String queteCellValue( String cellValue, String cellQuete ){
        StringBuilder sbCell = new StringBuilder();

        for( int ci=0; ci<cellValue.length(); ci++ ){
            boolean matched = Text.matchText(cellValue, cellQuete, ci, false);
            if( matched ){
                sbCell.append(cellQuete);
                sbCell.append(cellQuete);
                if( cellQuete.length()>1 ){
                    ci += cellQuete.length()-1;
                }
            }else{
                sbCell.append(cellValue.charAt(ci));
            }
        }

        return cellQuete + sbCell.toString() + cellQuete;
    }
}
