/*
 * The MIT License
 *
 * Copyright 2016 Kamnev Georgiy (nt.gocha@gmail.com).
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


import xyz.cofe.ecolls.Fn1;
import xyz.cofe.ecolls.Pair;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Делегирование сообщений TableModelEvent. <br>
 * Из исходного сообщения esrc : TableModelEvent (таблица tsrc : TableModel), <br>
 * Создает аналогичные сообщения edest : TableModelEvent (таблица tdest : TableModel). <br>
 * <br>
 * Предполагается что таблица tdest является отображением (возможно частичным) таблицы tsrc.<br><br>
 *
 * <b>mapColumnToOutside</b> <i>: fn( srcColumn ) =&gt; destColumn</i> - функция отображения исходной колонки на конечную.<br>
 * <b>mapRowToOutside</b> <i>: fn( srcRow ) =&gt; destRow</i> - функция отображения исходной строки на конечную.<br>
 * <b>sender</b> <i>: fn( edest )</i> - функция которая отправляет подписчикам готовое сообщение.<br>
 * <br>
 *
 * Для работы необходимо определить следующие свойства:
 * <ul>
 * <li>sourceModel : TableModel - Исходная таблица</li>
 * <li>targetModel : TableModel - Целевая таблица</li>
 * <li>sender : Reciver&lt;TableModelEvent&gt; - Функция отправки готового сообщения (см. EventSupport)</li>
 * <li>mapColumnToOutside : Func1&lt;Integer, Integer&gt; - Функция отображения исходной колонки на конечную</li>
 * <li>mapRowToOutside : Func1&lt;Integer, Integer&gt; - Функция отображения исходной строки на конечную.</li>
 * </ul>
 * и вызывать метод start().
 * @see EventSupport
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TableModelEventDelegator implements Closeable {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TableModelEventDelegator.class.getName());

    private static Level logLevel(){
        return logger.getLevel() ;
    }

    private static boolean isLogSevere(){
        Level level = logLevel();
        return level==null
            ? true
            : level.intValue() <= Level.SEVERE.intValue();
    }

    private static boolean isLogWarning(){
        Level level = logLevel();
        return level==null
            ? true
            : level.intValue() <= Level.WARNING.intValue();
    }

    private static boolean isLogInfo(){
        Level level = logLevel();
        return level==null
            ? true
            : level.intValue() <= Level.INFO.intValue();
    }

    private static boolean isLogFine(){
        Level level = logLevel();
        return level==null
            ? true
            : level.intValue() <= Level.FINE.intValue();
    }

    private static boolean isLogFiner(){
        Level level = logLevel();
        return level==null
            ? false
            : level.intValue() <= Level.FINER.intValue();
    }

    private static boolean isLogFinest(){
        Level level = logLevel();
        return level==null
            ? false
            : level.intValue() <= Level.FINEST.intValue();
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

    private static void logEntering(String method,Object ... params){
        logger.entering(TableModelEventDelegator.class.getName(),method,params);
    }

    private static void logExiting(String method,Object result){
        logger.exiting(TableModelEventDelegator.class.getName(),method,result);
    }

    private static void logExiting(String method){
        logger.exiting(TableModelEventDelegator.class.getName(),method);
    }
    //</editor-fold>

    @Override
    public synchronized void close(){
        stop();
        mapColumnToOutside = null;
        mapRowToOutside = null;
        sender = null;
        sourceModel = null;
        targetModel = null;
    }

    @Override
    protected void finalize()
        throws Throwable
    {
        try{
            close();
        }finally{
            super.finalize();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="targetModel">
    protected TableModel targetModel;

    public synchronized TableModel getTargetModel()
    {
        return targetModel;
    }

    public synchronized void setTargetModel(TableModel targetModel)
    {
        this.targetModel = targetModel;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="sourceModel">
    protected TableModel sourceModel;

    /**
     * Возвращает исходную модель, ее события прослушиваются и перенаправляются в целевую модель
     * @return исходная модель
     * @see #getTargetModel()
     */
    public synchronized TableModel getSourceModel()
    {
        return sourceModel;
    }

    /**
     * Указывает исходную модель, ее события прослушиваются и перенаправляются в целевую модель
     * @param sourceModel исходная модель
     * @see #getTargetModel()
     */
    public synchronized void setSourceModel(TableModel sourceModel)
    {
        stop();
        this.sourceModel = sourceModel;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="start/stop/isRunning">
    protected boolean listen = false;

    /**
     * Запускает прослушивание исходной модели
     */
    public synchronized void start(){
        if( isRunning() )return;
        if( sourceModel==null )return;

        sourceModel.addTableModelListener(listener);
        listen = true;
    }

    /**
     * Останавливает прослушку исходной модели
     */
    public synchronized void stop(){
        if( !isRunning() )return;
        if( sourceModel==null )return;

        sourceModel.removeTableModelListener(listener);
        listen = false;
    }

    /**
     * Возвращает признак, что исходная модель прослушивается
     * @return true - прослушка установлена
     */
    public synchronized boolean isRunning(){
        return listen;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="sender">
    protected Consumer<TableModelEvent> sender;

    /**
     * Возвращает функцию которая вызывает соответ метод отправки уведомления подписчикам целевой таблицы
     * @return функция fireTableModelEvent
     */
    public synchronized Consumer<TableModelEvent> getSender()
    {
        return sender;
    }

    /**
     * Указывает функцию которая вызывает соответ метод отправки уведомления подписчикам целевой таблицы
     * @param sender fireTableModelEvent
     */
    public synchronized void setSender(Consumer<TableModelEvent> sender)
    {
        this.sender = sender;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="listener">
    /**
     * Подписчик на события исходной модели
     */
    protected TableModelListener listener = new TableModelListener()
    {
        @Override
        public void tableChanged(TableModelEvent e)
        {
            Fn1 row2out = getMapRowToOutside();
            Fn1 col2out = getMapColumnToOutside();
            TableModel trgt = getTargetModel();
            Consumer sndr = getSender();
            if( row2out==null || col2out==null || trgt==null || sndr==null )return;

            //deletageTMEvent(e, trgt, row2out, col2out)
            //.forEach( de -> sndr.recive(de) );

            for( Object de : deletageTMEvent(e, trgt, row2out, col2out) ){
                //if( de instanceof TableModelEvent ){
                sndr.accept( de );
                //}
            }
        }
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mapRowToOutside">
    protected Fn1<Integer,Integer> mapRowToOutside;

    /**
     * Возвращает функцию отображения исходной строки на целевую строку
     * @return функция source row =&gt; target row
     */
    public synchronized Fn1<Integer, Integer> getMapRowToOutside()
    {
        return mapRowToOutside;
    }

    /**
     * Указывает функцию отображения исходной строки на целевую строку
     * @param mapRowToOutside функция source row =&gt; target row
     */
    public synchronized void setMapRowToOutside(Fn1<Integer, Integer> mapRowToOutside)
    {
        this.mapRowToOutside = mapRowToOutside;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mapColumnToOutside">
    /**
     * Возвращает функцию отображения исходной колонки на целевую колонку
     * @return функция source column =&gt; target column
     */
    public synchronized Fn1<Integer, Integer> getMapColumnToOutside()
    {
        return mapColumnToOutside;
    }

    /**
     * Указывает функцию отображения исходной колонки на целевую колонку
     * @param mapColumnToOutside функция source column =&gt; target column
     */
    public synchronized void setMapColumnToOutside(Fn1<Integer, Integer> mapColumnToOutside)
    {
        this.mapColumnToOutside = mapColumnToOutside;
    }

    protected Fn1<Integer,Integer> mapColumnToOutside;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deletageTMEvent()">
    /**
     * Делегирует пришедшее событие из оригинальной таблицы к своим подписчикам
     * @param e Оригинальное событие
     * @param newsrc Новый источник данных / модель таблицы
     * @param mapRowToOutside Функция отображения строк
     * @param mapColumnToOutside Функция отображения колонок
     * @return Соот. собственное событие
     */
    public List<TableModelEvent> deletageTMEvent(
        TableModelEvent e,
        TableModel newsrc,
        Fn1<Integer,Integer> mapRowToOutside,
        Fn1<Integer,Integer> mapColumnToOutside
    ){
        if( e==null )throw new IllegalArgumentException( "e==null" );
        if( newsrc==null )throw new IllegalArgumentException( "newsrc==null" );
        if( mapColumnToOutside==null )throw new IllegalArgumentException( "mapColumnToOutside==null" );
        if( mapRowToOutside==null )throw new IllegalArgumentException( "mapRowToOutside==null" );

        List<TableModelEvent> res = new ArrayList<TableModelEvent>();
        if( e==null )return res;

        int srcFirstRow = e.getFirstRow();
        int srcLastRow = e.getLastRow();
        int etype = e.getType();
        int srcColumn = e.getColumn();

        //boolean delegate = false;

        int outFirstRow = -1;
        int outLastRow = -1;
        int outType = -1;
        int outColumn = -1;

        // Глобальность события:
        //   1 - изменение таблицы целиком (изменения колонок/всех строк/...)
        //         => Передать событие TableModelEvent(source, HEADER_ROW);
        //   2 - изменение набора строк (изменения/добавление/удаление/...)
        //         => По пробовать отобразить и передать соот. событие
        //   3 - изменение отдельный ячеек (изменения)
        //         => По пробовать отобразить и передать соот. событие

        // global
        if( srcFirstRow==0 && srcLastRow==Integer.MAX_VALUE ){
            res.add( new TableModelEvent( newsrc, TableModelEvent.HEADER_ROW ) );
            return res;
        }else if( srcFirstRow<0 ){
            res.add( new TableModelEvent( newsrc, TableModelEvent.HEADER_ROW ) );
            return res;
        }

        // row modifications:
        if( srcFirstRow>=0 && srcLastRow>=srcFirstRow ){
            if( srcColumn>=0 ){
                outColumn = mapColumnToOutside.apply(srcColumn);
                if( outColumn<0 )return null; // нет отображения: inner -> outter = 0
            }

            // строки (outter) которые изменились (insert/update/delete)
            TreeSet<Integer> modifiedRows = new TreeSet<Integer>();
            for( int irow=srcFirstRow; irow<=srcLastRow; irow++ ){
                int orow = mapRowToOutside.apply(irow);
                if( orow<0 )continue;
                modifiedRows.add(orow);
            }

            if( modifiedRows.size()>0 ){
                List<Pair<Integer,Integer>> beginEnd = new ArrayList<Pair<Integer, Integer>>();
                int begin = -1;
                int end = -1;
                int nxt = -1;
                for( int orow : modifiedRows ){
                    if( begin<0 ){
                        begin = orow;
                        end = orow;
                        nxt = orow+1;
                    }else{
                        if( nxt==orow ){
                            nxt = orow+1;
                            end = orow;
                        }else{
                            beginEnd.add(Pair.of(begin, end));
                            begin = orow;
                            end = orow;
                            nxt = orow+1;
                        }
                    }
                }
                //if( begin>0 && end>=begin )beginEnd.add(new BasicPair<Integer, Integer>(begin, end));
                beginEnd.add(Pair.of(begin, end));

                if( etype==TableModelEvent.UPDATE ){
                    for( Pair<Integer,Integer> p : beginEnd ){
                        begin = p.a();
                        end = p.b();
                        res.add(
                            new TableModelEvent(
                                newsrc, begin, end, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
                    }
                    return res;
                }else if( etype==TableModelEvent.INSERT ){
                    for( Pair<Integer,Integer> p : beginEnd ){
                        begin = p.a();
                        end = p.b();
                        res.add(
                            new TableModelEvent(
                                newsrc, begin, end, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
                    }
                    return res;
                }else if( etype==TableModelEvent.DELETE ){
                    for( Pair<Integer,Integer> p : beginEnd ){
                        begin = p.a();
                        end = p.b();
                        res.add( 0,
                            new TableModelEvent(
                                newsrc, begin, end, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
                    }
                    return res;
                }
            }
        }

        res.clear();
        res.add( new TableModelEvent( newsrc, TableModelEvent.HEADER_ROW ) );
        return res;
    }
    //</editor-fold>
}
