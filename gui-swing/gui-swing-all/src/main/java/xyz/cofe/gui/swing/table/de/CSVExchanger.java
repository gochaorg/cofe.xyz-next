/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy <nt.gocha@gmail.com>.
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

package xyz.cofe.gui.swing.table.de;

import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import xyz.cofe.collection.graph.Edge;
import xyz.cofe.collection.graph.Path;
import xyz.cofe.fn.Fn1;
import xyz.cofe.gui.swing.properties.Property;
import xyz.cofe.gui.swing.properties.PropertyValue;
import xyz.cofe.gui.swing.table.Column;
import xyz.cofe.gui.swing.table.Columns;
import xyz.cofe.gui.swing.table.PropertyColumn;
import xyz.cofe.gui.swing.table.PropertyTable;
import xyz.cofe.gui.swing.tree.TreeTableNode;
import xyz.cofe.iter.Eterable;
import xyz.cofe.text.out.Output;
import xyz.cofe.typeconv.ExtendedCastGraph;
import xyz.cofe.typeconv.SequenceCaster;
import xyz.cofe.typeconv.TypeCastGraph;

/**
 * Экспорт/импорт CSV
 * @author nt.gocha@gmail.com
 */
public class CSVExchanger
    implements PropertyTableExchanger
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(CSVExchanger.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(CSVExchanger.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(CSVExchanger.class.getName(), method, result);
    }

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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="csvFormat">
    protected CSVDesc csvFormat;
    public CSVDesc getCsvFormat(){
        if( csvFormat!=null )return csvFormat;
        csvFormat = new CSVDesc();
        csvFormat.setQuoteVariants(CSVDesc.QuoteVariants.Sometimes);
        csvFormat.setCellDelimiter(",");
        csvFormat.setCellQuote("\"");
        csvFormat.setFixedWidth(false);
        return csvFormat;
    }
    public void setCsvFormat(CSVDesc desc){
        this.csvFormat = desc;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="columnMaping">
    protected Map<String,Integer> columnMaping;

    public Map<String, Integer> getColumnMaping() {
        if( columnMaping==null )columnMaping = new LinkedHashMap<String, Integer>();
        return columnMaping;
    }

    public void setColumnMaping(Map<String, Integer> columnMaping) {
        this.columnMaping = columnMaping;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="typeCast">
    protected TypeCastGraph typeCast;

    public TypeCastGraph getTypeCast() {
        if( typeCast!=null ){
            return typeCast;
        }
        typeCast = new ExtendedCastGraph();
        initNumCast(typeCast);
        initBoolCast(typeCast);
        return typeCast;
    }

    private void initBoolCast(TypeCastGraph tc){
        /*List<SequenceCaster> casters =
            tc.getCastPaths(String.class, Boolean.class)
            .stream()
            .map( pth -> new SequenceCaster(pth) ).collect(Collectors.toList());*/

        final List<SequenceCaster> casters = new ArrayList<SequenceCaster>();
        for( Path p : tc.getCastPaths(String.class, Boolean.class) ){
            SequenceCaster sc = new SequenceCaster(p);
            casters.add( sc );
        }

        Function<Object,Object> cnv = str -> {
                if( str instanceof String ){
                    String s = (String)str;
                    if( s.equalsIgnoreCase("") ){
                        return null;
                    }else{
                        Boolean[] res = new Boolean[0];
                        AtomicBoolean succConv = new AtomicBoolean(false);

                        for( SequenceCaster cst : casters ){
                            if( !succConv.get() ){
                                try{
                                    Object v = cst.apply(str);
                                    if( v instanceof Boolean ){
                                        res[0] = (Boolean)v;
                                    }
                                    succConv.set(true);
                                }catch(Throwable err){
                                }
                            }
                        };

                        if( !succConv.get() ){
                            throw new IllegalStateException("can't convert from "+str+" to bool");
                        }

                        return res[0];
                    }
                }
                throw new IllegalArgumentException("can't cast from "+str+" to bool");
            };

        ArrayList<Function<Object,Object>> convs = new ArrayList<>();
        convs.add(cnv);

        SequenceCaster sc = new SequenceCaster(convs);
        sc.setWeight(0.8);

        tc.set(String.class, Boolean.class, sc);
    }

    private void initNumCast(TypeCastGraph tc){
        List<Path<Class,Function<Object,Object>>> lp1 = tc.getCastPaths(String.class, BigDecimal.class);
        for( Path<Class,Function<Object,Object>> p : lp1 ){
            for( Edge e : p.fetch(0, p.nodeCount()) ){
                logFiner("initNumCast {0}", e.getEdge());
            }
        }

        List<Path<Class,Function<Object,Object>>> lp2 = tc.getCastPaths(BigDecimal.class, int.class);
        for( Path<Class,Function<Object,Object>> p : lp2 ){
            for( Edge e : p.fetch(0, p.nodeCount()) ){
                logFiner("initNumCast {0}", e.getEdge());
            }
        }

        for( Path<Class,Function<Object,Object>> p1 : lp1 ){
            for( Path<Class,Function<Object,Object>> p2 : lp2 ){
                List<Function<Object,Object>> path = new ArrayList<Function<Object,Object>>();

                for( Edge<Class,Function<Object,Object>> e1 : p1.fetch(0, p1.nodeCount()) ){
                    path.add( e1.getEdge() );
                }

                //p2.forEach( e2 -> { path.add(e2.getEdge()); } );
                for( Edge<Class,Function<Object,Object>> e2 : p2.fetch(0, p2.nodeCount()) ){
                    path.add( e2.getEdge() );
                }

                SequenceCaster sc = new SequenceCaster( path );
                sc.setWeight(0.8);

                tc.set(String.class, int.class, sc);

            }
        }
    }

    public void setTypeCast(TypeCastGraph typeCast) {
        this.typeCast = typeCast;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="exportTable()">
    @Override
    public void exportTable(Writer wr, Iterable objs, PropertyTable pt) {
        if( wr==null )throw new IllegalArgumentException( "wr==null" );
        if( objs==null )throw new IllegalArgumentException( "objs==null" );
        if( pt==null )throw new IllegalArgumentException( "pt==null" );

        CSVDesc csv = getCsvFormat().clone();
        Columns columns = pt.getColumns().clone();
        TypeCastGraph tc = getTypeCast().clone();
        Map<String,Integer> colMap = new LinkedHashMap<String,Integer>(getColumnMaping());

        Output out = wr instanceof Output ? ((Output)wr) : new Output(wr);

        if( csv.isFirstLineAsName() ){
            exportHeader(out, columns, csv, tc, colMap);
        }

        for( Object obj : objs ){
            if( Thread.interrupted() )break;
            if( obj==null )continue;
            exportObject(out, obj, columns, csv, tc, colMap);
        }

        out.flush();
    }

    private TreeMap<Integer,Column> rmapCols( Columns cols, Map<String,Integer> colMap ){
        TreeMap<Integer,Column> cm = new TreeMap();
        LinkedHashSet<Column> unmapped = new LinkedHashSet<Column>();

        for( Column col : cols ){
            if( col==null )continue;

            String colName = col.getName();
            if( colName==null )continue;

            Integer coli = colMap.get(colName);
            if( coli!=null ){
                if( coli<0 ){
                    continue;
                }else{
                    cm.put( coli, col );
                }
            }else{
                unmapped.add(col);
            }
        }

        for( Column c : unmapped ){
            if( cm.isEmpty() ){
                cm.put(0, c);
            }else{
                Integer k = cm.lastKey() + 1;
                cm.put(k, c);
            }
        }

        return cm;
    }

    private void exportHeader(Output out,Columns cols, CSVDesc csv, TypeCastGraph tc, Map<String,Integer> colMap ){
        List<String> cells = new LinkedList<String>();

        TreeMap<Integer,Column> rmapcol = rmapCols(cols, colMap);
        if( rmapcol.isEmpty() )return;

        Integer lk = rmapcol.lastKey();
        if( lk==null && lk<0 )return;

        for( int ci=0; ci<lk; ci++ ){
            Column col = rmapcol.get(ci);
            if( col!=null ){
                cells.add( col.getName() );
            }else{
                cells.add( "" );
            }
        }

        CSVUtil utl = new CSVUtil();
        out.println(utl.toString(cells.toArray(new String[]{}), csv));
    }

    private void exportObject(Output out, Object ob, Columns cols, CSVDesc csv, TypeCastGraph tc, Map<String,Integer> colMap ){
        List<String> cells = new LinkedList<String>();

        TreeMap<Integer,Column> rmapcol = rmapCols(cols, colMap);
        if( rmapcol.isEmpty() )return;

        Integer lk = rmapcol.lastKey();
        if( lk==null && lk<0 )return;

        for( int ci=0; ci<lk; ci++ ){
            Column col = rmapcol.get(ci);
            if( col!=null ){
                String txt = getTextOfCell(ob, col, tc,
                    (Throwable err) ->
                            logger.log(Level.SEVERE, "can't export cell data", err)
                );
                cells.add( txt==null ? "" : txt );
            }else{
                cells.add( "" );
            }
        }

        CSVUtil utl = new CSVUtil();
        out.println(utl.toString(cells.toArray(new String[]{}), csv));
    }

    private String getTextOfCell( Object ob, Column col, TypeCastGraph tc, Consumer<Throwable> errReciver ){
        Function<Object,Object> conv = col.getReader();
        if( conv==null )return null;

        try {
            Object val = conv.apply(ob);
            if( val==null )return "";

            if( val instanceof PropertyValue ){
                val = ((PropertyValue)val).getValue();
            }else if( val instanceof Supplier ){
                val = ((Supplier)val).get();
            }else if( val instanceof TreeTableNode ){
                val = ((TreeTableNode)val).getData();
            }

            if( val==null )return "";

            String txt = tc.cast(val, String.class);
            if( txt==null )return "";

            return txt;
        } catch (Throwable err){
            if( errReciver!=null ){
                errReciver.accept(err);
            }else{
                logger.log(Level.SEVERE, "can't export cell data", err);
            }
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="importTable()">
    @Override
    public Iterable importTable(Reader reader, PropertyTable pt) {
        if( reader==null )throw new IllegalArgumentException( "reader==null" );
        if( pt==null )throw new IllegalArgumentException( "pt==null" );

        CSVDesc csv = getCsvFormat().clone();
        Columns columns = pt.getColumns().clone();
        final TypeCastGraph tc = getTypeCast().clone();
        Map<String,Integer> colMap = new LinkedHashMap<String,Integer>(getColumnMaping());

        TreeMap<Integer,Column> rmapcol = rmapCols(columns, colMap);
        final Supplier defItmBuilder = pt.getDefaultItemBuilder();

        final Scanner scn = new Scanner(reader);

        final Iterator<String> strLineIterator = new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return scn.hasNextLine();
            }

            @Override
            public String next() {
                String line = scn.nextLine();
                if( line==null )return null;
                if( line.contains("\r\n") ) line = line.replace("\r\n", "");
                if( line.contains("\n\r") ) line = line.replace("\n\r", "");
                if( line.contains("\n") ) line = line.replace("\n", "");
                if( line.contains("\r") ) line = line.replace("\r", "");
                return line;
            }

            @Override
            public void remove() {
            }
        };

        Eterable<String> strIterable = () -> strLineIterator;
        Eterable<String> emptyLineSkip = strIterable.filter( str -> str!=null && str.trim().length()>0 );

        Fn1<Map<Column,String>,Object> itemBuilder = map -> {
                if( defItmBuilder==null )return null;

                Object obj = defItmBuilder.get();
                if( obj==null )return null;

                if( map!=null ){
                    for( Map.Entry<Column, String> enMap : map.entrySet() ){
                        Column col = enMap.getKey();
                        String str = enMap.getValue();

                        if( !(col instanceof PropertyColumn) )continue;

                        PropertyColumn pc = (PropertyColumn)col;

                        PropertyValue pv = pc.read(obj);
                        if( pv==null )continue;

                        Property prop = pv.getProperty();
                        if( prop==null )continue;
                        if( prop.isReadOnly() )continue;

                        if( pc.getForceReadOnly()!=null && pc.getForceReadOnly() )continue;

                        Class pcls = prop.getPropertyType();
                        if( pcls==null )continue;

                        Object casted = null;

                        if( casted==null ){
                            try{
                                casted = tc.cast(str, pcls);
                            }catch( Throwable err ){
                                logger.log( Level.SEVERE, "can't cast str to "+pcls, err);
                                continue;
                            }
                        }

                        pv.setValue(casted);
                        try{
                            pc.write(obj, pv);
                        }catch( Throwable err ){
                            logger.log( Level.SEVERE,
                                "can't set property "+pv.getProperty().getName(),
                                err);
                        }
                    }
                }

                return obj;
            };

        Function<String,Object> strConv = lineToObject(csv, rmapcol, itemBuilder);

        Eterable<Object> convIter = emptyLineSkip.map(strConv);
        Iterable<Object> skipNullObjects = convIter.notNull();

        return skipNullObjects;
    }

    private Function<String,Object> lineToObject(
        final CSVDesc csv,
        final TreeMap<Integer,Column> rmapcol,
        final Fn1<Map<Column,String>,Object> itemBuilder
    ){
        final CSVUtil utl = new CSVUtil();

        return csvLine -> {
                if( csvLine==null )return null;

                Map<Column,String> m = new LinkedHashMap<Column,String>();

                String[] cells = utl.parseLine(csvLine, csv);

                for( Map.Entry<Integer,Column> en : rmapcol.entrySet() ){
                    Integer celli = en.getKey();
                    Column col = en.getValue();
                    // rmapcol.forEach( (celli, col) -> {
                    if( celli<0 || celli>=cells.length )continue;
                    if( col==null )continue;
                    m.put(col, cells[celli]);
                }

                if( itemBuilder!=null ){
                    Object itm = itemBuilder.apply(m);
                    return itm;
                }

                return null;
            };
    }
    //</editor-fold>
}
