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
package xyz.cofe.gui.swing.table;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.TableModel;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import xyz.cofe.ecolls.Predicates;
import xyz.cofe.gui.swing.al.DocumentAdapter;
import xyz.cofe.text.Text;

/**
 * Интерфейс-посредник для доступа к строке таблицы
 * @author gocha
 */
public class RowData {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(RowData.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(RowData.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(RowData.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(RowData.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(RowData.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(RowData.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="tableModel">
    protected TableModel tableModel = null;

    public TableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="rowIndex">
    protected int rowIndex = -1;

    /**
     * Возвращает индекс строки таблицы
     * @return индекс строки
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Указывает индекс строки таблицы
     * @param rowIndex индекс строки
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }// </editor-fold>

    /**
     * Возвращает кол-во колонок в таблице
     * @return кол-во колонок
     */
    public int getColumnCount(){
        if( tableModel==null )return 0;
        return tableModel.getColumnCount();
    }

    /**
     * Возвращает значение колоноки
     * @param column индекс колонки
     * @return значение
     */
    public Object getValue(int column){
        if( tableModel==null )return null;
        return tableModel.getValueAt(getRowIndex(), column);
    }

    /**
     * Возвращает имя колонки
     * @param column индекс колонки
     * @return имя колонки
     */
    public String getName(int column){
        if( column<0 )return null;
        if( tableModel==null )return null;
        return tableModel.getColumnName(column);
    }

    /**
     * Создает предикат/фильтр проверяющий значение на совпадение значения согласно заданной маске в любой колонке
     * @param text маска
     * @return предикат
     * @see Text#wildcard(String, boolean, boolean) Фильтр создается
     * для поиска без учета регистра символов и без символов экранирования
     */
    public static Predicate<RowData> like( String text){
        if( text==null ) throw new IllegalArgumentException( "text==null" );
//        final String txt = text;
        final Pattern ptrn = Text.wildcard(text, false, true);
        return new Predicate<RowData>() {
            @Override
            public boolean test(RowData frow) {
                if( frow==null )return false;
                for( int iC=0; iC<frow.getColumnCount(); iC++ ){
                    Object val = frow.getValue(iC);
                    if( val==null )continue;
                    String txt = val.toString();
                    Matcher m = ptrn.matcher(txt);
                    if( m.matches() ){
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private static Predicate<String> falseTextPredicate = new Predicate<String>() {
        @Override
        public boolean test(String value) {
            return false;
        }
    };

    /**
     * Создает фильтр/предикат проверящий что значение содержит текст (contains)
     * @param column индекс колоноки
     * @param wildcard искомый текст
     * @return предикат
     */
    public static Predicate<RowData> textContains( int column, String wildcard ){
        final String txt = wildcard;
        return stringValue(column, new Predicate<String>() {
            @Override
            public boolean test(String value) {
                if( value==null )return true;
                if( txt==null )return true;

                if( txt.length()==0 )return true;

                return value.contains(txt);
            }
        });
    }

    /**
     * Создает текстовый предикат проверяющий текст согласно маске
     * @param woldcard маска
     * @return предикат
     */
    public static Predicate<String> textLikeWildcard( String woldcard ){
        if( woldcard==null )return falseTextPredicate;
//        return xyz.cofe.text.Text.Predicates.matchWildcard(text);
        return Text.Predicates.matchRegex(Text.wildcard(woldcard, false, true));
    }

//    public static Predicate<String> textLikeWildcard( String text,
//            boolean ignoreCase,
//            boolean escapeAllowed,
//            char any,
//            char anyrepeat,
//            char escape
//            ){
//        if( text==null )return falseTextPredicate;
//        return xyz.cofe.text.Text.Predicates.matchWildcard(text,ignoreCase,escapeAllowed,any, anyrepeat, escape);
//    }

//    public static class C

    /**
     * Сравнивает текстовое представление данных (toString()) с значением (pred)
     * и соответственно возвращает результат сравнения.
     * @param columnIndex колонка для сравнения или -1 - для любой колонки
     * @param pred функция сравнения
     * @return результат сравнения
     */
    public static Predicate<RowData> stringValue( int columnIndex, Predicate<String> pred){
        final int cIdx = columnIndex;
        final Predicate<String> p = pred;
        return new Predicate<RowData>() {
            @Override
            public boolean test(RowData frow) {
                if( frow==null )return false;
                if( p==null )return false;
                if( cIdx >= frow.getColumnCount() )return false;
                if( cIdx==-1 ){
                    for( int c = 0; c<frow.getColumnCount(); c++ ){
                        Object val = frow.getValue(c);
                        boolean matched = p.test(val!=null ? val.toString() : null);
                        if( matched )return true;
                    }
                }
                Object val = frow.getValue(cIdx);
                return p.test(val!=null ? val.toString() : null);
            }
        };
    }

    /**
     * Создает предикат OR (или) из указанных предикатов
     * @param predicates предикаты входищие в условие OR
     * @return предикат
     */
    public static Predicate<RowData> or(Predicate<RowData> ... predicates){
        if( predicates==null )throw new IllegalArgumentException( "predicates==null" );
        return Predicates.or(predicates);
    }

    /**
     * Создает предикат AND (и) из указанных предикатов
     * @param predicates предикаты входищие в условие AND
     * @return предикат
     */
    public static Predicate<RowData> and(Predicate<RowData> ... predicates){
        if( predicates==null )throw new IllegalArgumentException( "predicates==null" );
        return Predicates.and(predicates);
    }

    /**
     * Создает предикат NOT (инверсия) из указанного
     * @param predicate исходный предикат
     * @return предикат
     */
    public static Predicate<RowData> not(Predicate<RowData> predicate){
        if( predicate==null )throw new IllegalArgumentException( "predicate==null" );
        return Predicates.not(predicate);
    }

    /**
     * Конвертор филльтра: contains
     * @param column колонка или -1 - для любой колонки
     * @return конвертор строка таблицы в фильтр
     */
    public static Function<String,Predicate<RowData>> getContainsConvertor( final int column){
        return new Function<String, Predicate<RowData>>() {
            @Override
            public Predicate<RowData> apply(String from) {
                return RowData.textContains(-1, from);
            }
        };
    }

    /**
     * Создает конвертор множества предикатов в один предикат AND
     * @param predicates исходные  конверторы - предикаты
     * @return конвертор
     */
    public static Function<String,Predicate<RowData>> andConvertor(
        final Function<String,Predicate<RowData>> ... predicates
    ){
        if( predicates==null )throw new IllegalArgumentException( "predicates==null" );

        return new Function<String, Predicate<RowData>>() {
            @Override
            public Predicate<RowData> apply(String from) {
                ArrayList<Predicate<RowData>> al = new ArrayList();

                for( Function<String,Predicate<RowData>> c : predicates ){
                    if( c==null )continue;

                    Predicate<RowData> p = c.apply(from);
                    if( p==null )continue;

                    al.add( p );
                }

                return and(
                    al.toArray(new Predicate[]{})
                );
            }
        };
    }

    /**
     * Связывает событие изменения текста, текстового поля содержащее выражение фильтрации,
     * с моделью таблицы
     * @param filterRowTM модель таблицы
     * @param filterTextCmpt текстовое поле
     * @param convertToFilter функция преобразования текстового значения в фильтр
     * @return отписка от уведомлений текстового поля
     */
    public static Closeable bind(
        final FilterRowTM filterRowTM,
        final JTextComponent filterTextCmpt,
        final Function<String,Predicate<RowData>> convertToFilter )
    {
        if( filterRowTM==null )throw new IllegalArgumentException( "filterRowTM==null" );
        if( filterTextCmpt==null )throw new IllegalArgumentException( "filterTextCmpt==null" );
        if( convertToFilter==null )throw new IllegalArgumentException( "convertToFilter==null" );

        Predicate<RowData> fltr = convertToFilter.apply(filterTextCmpt.getText());
        filterRowTM.setRowFilter(fltr);

        final DocumentAdapter da = new DocumentAdapter(){
            @Override
            protected void onTextChanged() {
                if( filterTextCmpt!=null && convertToFilter!=null ){
                    Predicate<RowData> fltr = convertToFilter.apply(filterTextCmpt.getText());
                    filterRowTM.setRowFilter(fltr);
                }
            }
        };

        filterTextCmpt.getDocument().addDocumentListener(da);

        return new Closeable(){
            JTextComponent ffilterTextCmpt = filterTextCmpt;
            DocumentAdapter fda = da;

            @Override
            public void close() throws IOException {
                if( ffilterTextCmpt!=null && fda!=null ){
                    Document doc = ffilterTextCmpt.getDocument();
                    doc.removeDocumentListener(fda);

                    fda = null;
                    ffilterTextCmpt = null;
                }
            }
        };
    }
}
