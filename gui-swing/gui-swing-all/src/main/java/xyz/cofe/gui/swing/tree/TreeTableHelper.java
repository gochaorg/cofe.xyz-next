/*
 * The MIT License
 *
 * Copyright 2017 user.
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

package xyz.cofe.gui.swing.tree;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumn;

import xyz.cofe.collection.ClassMap;
import xyz.cofe.collection.ClassNode;
import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn3;
import xyz.cofe.gui.swing.table.Column;

/**
 * Помошник по работе с TreeTable
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TreeTableHelper {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableHelper.class.getName());
    private static final Level logLevel = logger.getLevel();

    private static final boolean isLogSevere =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.SEVERE.intValue();

    private static final boolean isLogWarning =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.WARNING.intValue();

    private static final boolean isLogInfo =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.INFO.intValue();

    private static final boolean isLogFine =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINE.intValue();

    private static final boolean isLogFiner =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINER.intValue();

    private static final boolean isLogFinest =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINEST.intValue();

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
        logger.entering(TreeTableHelper.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeTableHelper.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeTableHelper.class.getName(), method, result);
    }
    //</editor-fold>

    public final TreeTable treeTable;

    //<editor-fold defaultstate="collapsed" desc="Конструктор">
    /**
     * Конструктор
     * @param treeTable дерево с которым происходит работа
     */
    public TreeTableHelper(TreeTable treeTable){
        if (treeTable== null) {
            throw new IllegalArgumentException("treeTable==null");
        }
        this.treeTable = treeTable;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rootConf">
    /**
     * Настройка корня
     */
    public class RootConf {
        public Object root;

        /**
         * Указывает корень древа
         * @param root корень
         * @return this ссылка
         */
        public RootConf set( Object root ){
            this.root = root;
            return this;
        }

        /**
         * Отображать/скрывать корень древа
         */
        public Boolean visible;

        /**
         * Отображать/скрывать корень древа
         * @param v true - отображать корень
         * @return true - отображать корень
         */
        public RootConf visible(boolean v){
            this.visible = v;
            return this;
        }

        /**
         * Применяет настройки
         * @return Помошник по работе с TreeTable
         */
        public TreeTableHelper apply(){
            if( root!=null ){
                TreeTableNodeBasic troot = treeTable.getRoot();
                if( troot==null ){
                    troot = new TreeTableNodeBasic(root);
                    treeTable.setRoot(troot);
                }else{
                    troot.setData(root);
                }
            }
            if( visible!=null )treeTable.setRootVisible(visible);
            return TreeTableHelper.this;
        }
    }

    /**
     * Указывает корень дерева
     * @param root корень дерева
     * @return  Настройка корня
     */
    public RootConf root( Object root ){
        RootConf rc = new RootConf();
        rc.root = root;
        return rc;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="NodesConf">
    /**
     * Настройки доступа к узлам древа
     */
    public class NodesConf {
        //<editor-fold defaultstate="collapsed" desc="followers">
        /**
         * Проверка на возможность извлечение дочерних объектов из узлов
         */
        protected ClassMap<Fn1<Object,Boolean>> followable = new ClassMap<>();

        /**
         * "Следование" / извлечение дочерних объектов из узлов
         */
        protected ClassNode followers = new ClassNode();

        /**
         * Добавляет функцию следования для определенного типа
         * @param <T> Тип
         * @param cls Тип
         * @param follower функция следования
         * @return self ссылка
         */
        public <T> NodesConf follow( Class<T> cls, NodesExtracter<T,?> follower ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (follower== null) {
                throw new IllegalArgumentException("follower==null");
            }

            followers.adds(cls, follower);

            return this;
        }

        /**
         * Добавляет функцию проверки следования для определенного типа
         * @param <T> Тип
         * @param cls Тип
         * @param followable функция проверки следования
         * @return self ссылка
         */
        public <T> NodesConf followable( Class<T> cls, Fn1<T,Boolean> followable ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (followable== null) {
                throw new IllegalArgumentException("followable==null");
            }

            this.followable.put(cls, (Fn1)followable);

            return this;
        }

        protected Long cacheLifeTime = null;

        /**
         * Устанавливает время жизни кэша
         * @param clifeTime время жизни кэша мс. или null - неопределенно долгое
         * @return self ссылка
         */
        public NodesConf cacheLifeTime( Long clifeTime ){
            cacheLifeTime = clifeTime;
            return this;
        }

        /**
         * Устанавливает время жизни кэша
         * @param clifeTime время жизни кэша мс. или null - неопределенно долгое
         * @return self ссылка
         */
        public NodesConf cacheLifeTime( int clifeTime ){
            cacheLifeTime = (long)clifeTime;
            return this;
        }

        /**
         * Применяет настройки "следования" узлов
         * @return self ссылка
         */
        public NodesConf applyFollowers(){
            TreeTableNodeBasic troot = treeTable.getRoot();
            if( troot==null ){
                troot = new TreeTableNodeBasic(null);
                treeTable.setRoot(troot);
            }

            return applyFollowersTo(troot);
        }

        /**
         * Применяет настройки "следования" узлов
         * @param node Узел к которому применяетсяя настройка
         * @return self ссылка
         */
        public NodesConf applyFollowersTo( TreeTableNodeBasic node ){
            if (node== null) {
                throw new IllegalArgumentException("node==null");
            }

            final ClassNode cfollowers = followers.clone();
            final ClassMap<Fn1<Object,Boolean>> cextr = new ClassMap<>();
            cextr.putAll(followable);

            node.setDataFollower(cfollowers);

            Fn1<Object,Boolean> defExtractable =
                (Object data) -> {
                        if( data==null )return false;

                        Fn1<Object, Boolean> extrble = cextr.fetch(data.getClass());
                        if( extrble!=null )return extrble.apply(data);

                        NodesExtracter[] extrs = cfollowers.extractersOf(data.getClass());
                        return (extrs != null && extrs.length >= 1);
                    };

            node.setDataFollowable(defExtractable);

            if( cacheLifeTime!=null )node.setCacheLifeTime(cacheLifeTime);

            return this;
        }
        //</editor-fold>

        protected ClassMap<Fn1<Object,TreeTableNodeFormat>> formats
            = new ClassMap<>();

        /**
         * Применяет настройки именования узлов
         * @param node Узел к которому применяется настройка
         * @return self ссылка
         */
        public NodesConf applyFormatsTo(TreeTableNodeBasic node){
            if( node==null )throw new IllegalArgumentException("node == null");

            final ClassMap<Fn1<Object,TreeTableNodeFormat>> cmap = new ClassMap<>();
            cmap.putAll(formats);

            TreeTableNodeGetFormatOf formatter = new TreeTableNodeGetFormatOf() {
                @Override
                public TreeTableNodeFormat getTreeTableNodeFormatOf(Object nodeData) {
                    if( nodeData!=null ){
                        Fn1<Object,TreeTableNodeFormat> ffmt = cmap.fetch(nodeData.getClass());
                        if( ffmt!=null ){
                            TreeTableNodeFormat nfmt = ffmt.apply(nodeData);
                            if( nfmt==null )return nfmt;
                        }
                    }
                    return null;
                }
            };

            node.setDataFormatter(formatter);

            return this;
        }

        //<editor-fold defaultstate="collapsed" desc="naming">
        /**
         * Чтение имени объекта
         */
        protected ClassMap<Fn1<Object,String>> names = new ClassMap<>();

        /**
         * Указывает как выводить текст узла
         * @param <T> Тип узла
         * @param cls Тип узла
         * @param naming Именование fn(type, data -&gt; data.name())
         * @return self ссылка
         */
        public <T> NodesConf naming(Class<T> cls, Fn1<T,String> naming){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (naming== null) {
                throw new IllegalArgumentException("naming==null");
            }

            final Class fcls = cls;
            final Fn1<T,String> fnamingArg = naming;

            Fn1<Object,String> fnaming = (Object arg) -> {
                    if( arg==null )return null;
                    if( fcls.isAssignableFrom(arg.getClass()) ){
                        return fnamingArg.apply((T)arg);
                    }
                    return null;
                };

            names.put(fcls, fnaming);

            return this;
        }

        protected String nullName = "?null";

        /**
         * Применяет настройки именования узлов
         * @return self ссылка
         */
        public NodesConf applyNaming(){
            TreeTableNodeBasic troot = treeTable.getRoot();
            if( troot==null ){
                troot = new TreeTableNodeBasic(null);
                treeTable.setRoot(troot);
            }

            return applyNamingTo(troot);
        }

        /**
         * Применяет настройки именования узлов
         * @param node Узел к которому применяется настройка
         * @return self ссылка
         */
        public NodesConf applyNamingTo(TreeTableNodeBasic node){
            if( node==null )throw new IllegalArgumentException("node == null");

            final String fNullName = nullName;
            final ClassMap<Fn1<Object,String>> cmap = new ClassMap<>();
            cmap.putAll(names);

            node.setDataTextReader( //nodeData -> {
                nodeData -> {
                    if( nodeData!=null ){
                        if( nodeData!=null ){
                            Fn1<Object, String> fText = cmap.fetch(nodeData.getClass());
                            if( fText!=null ){
                                String ntext = fText.apply(nodeData);
                                if( ntext==null )return fNullName;
                                return ntext;
                            }else{
                                return nodeData.toString();
                            }
                        }else{
                            return fNullName;
                        }
                    }else{
                        return fNullName;
                    }
                });

            return this;
        }
        //</editor-fold>

        /**
         * Применяет настройки именования узлов
         * @return TreeTableHelper self ссылка
         */
        public TreeTableHelper apply(){
            applyNaming();
            applyFollowers();
            return TreeTableHelper.this;
        }

        /**
         * Применяет настройки именования узлов
         * @param node Узел к которому применяетсяя настройка
         * @return TreeTableHelper self ссылка
         */
        public TreeTableHelper applyTo( TreeTableNodeBasic node ){
            if (node== null) {
                throw new IllegalArgumentException("node==null");
            }
            applyNamingTo(node);
            applyFollowersTo(node);
            return TreeTableHelper.this;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nodes()">
    protected final NodesConf nodes = new NodesConf();

    /**
     * Настройки доступа к узлам древа
     * @return настройки
     */
    public NodesConf nodes(){
        return nodes;
    }
    //</editor-fold>

    /**
     * Настройки доступа к узлам древа
     * @param <T> тип узла
     */
    public class NodeColumnConf<T> {
        //<editor-fold defaultstate="collapsed" desc="nodeClass">
        protected Class nodeClass = null;

        /**
         * Возвращает тип данных узла
         * @return тип данных
         */
        public Class getNodeClass() {
            return nodeClass;
        }

        /**
         * Указывает тип данных узла
         * @param nodeClass тип данных
         */
        public void setNodeClass(Class nodeClass) {
            this.nodeClass = nodeClass;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="nodeConf">
        protected NodeConf<T> nodeConf;

        /**
         * Возвращает конфигурацию узла
         * @return конфигурация
         */
        public NodeConf<T> getNodeConf() {
            return nodeConf;
        }

        /**
         * Указывает конфигурацию узл
         * @param nodeConf конфигурация
         */
        public void setNodeConf(NodeConf<T> nodeConf) {
            this.nodeConf = nodeConf;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="name">
        protected String name = null;

        /**
         * Указывает имя колонки
         * @return имя колонки
         */
        public String getName() {
            return name;
        }

        /**
         * Указывает имя колонки
         * @param name имя колонки
         */
        public void setName(String name) {
            this.name = name;
        }
        //</editor-fold>

        /**
         * Конструктор
         * @param cls тип данных в узле дерева
         * @param nodeConf конфигурация
         * @param column имя колонки
         */
        public NodeColumnConf( Class<T> cls, NodeConf<T> nodeConf, String column ){
            if( cls==null )throw new IllegalArgumentException( "cls==null" );
            if( nodeConf==null )throw new IllegalArgumentException( "nodeConf==null" );
            if( column==null )throw new IllegalArgumentException( "column==null" );

            this.nodeClass = cls;
            this.nodeConf = nodeConf;
            this.name = column;
        }

        /**
         * Указывает функцию чтения данных колонки
         * @param reader функция чтения
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> reader( Fn1<T,Object> reader ){
            if( reader==null )throw new IllegalArgumentException( "reader==null" );
            TreeTableHelper.this.column(name).reader(nodeClass, reader);
            return this;
        }

        /**
         * Указывает функцию записи в колонку для данного типа узла
         * @param writer функция записи данных в узел
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> writer( Fn2<T,Object,Object> writer ){
            if( writer==null )throw new IllegalArgumentException( "writer==null" );
            TreeTableHelper.this.column(name).writer(nodeClass, writer);
            return this;
        }

        /**
         * Указывает тип данных в этой колонке
         * @param valueType тип данных
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> type( Fn2<T,TreeTableNode,Class> valueType ){
            if( valueType==null )throw new IllegalArgumentException( "valueType==null" );
            TreeTableHelper.this.column(name).type(nodeClass, (Fn2)valueType);
            return this;
        }

        /**
         * Указывает форматирование данных
         * @param valueFormat функция форматирования
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> format( Fn2<T,TreeTableNode,TreeTableNodeFormat> valueFormat ){
            if( valueFormat==null )throw new IllegalArgumentException( "valueFormat==null" );
            TreeTableHelper.this.column(name).format(nodeClass, (Fn2)valueFormat);
            return this;
        }

        /**
         * Указывает функцию раскраски значения в колонке
         * @param customPainter функция раскраски
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> painter( Fn1<T,Fn2<Graphics,Rectangle,Object>> customPainter ){
            if( customPainter==null )throw new IllegalArgumentException( "valueFormat==null" );
            TreeTableHelper.this.column(name).painter(nodeClass, (Fn1)customPainter);
            return this;
        }

        /**
         * Указывает функцию раскраски значения в колонке
         * @param customPainter функция раскраски
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> painter( Fn2<T,TreeTableNode,Fn2<Graphics,Rectangle,Object>> customPainter ){
            if( customPainter==null )throw new IllegalArgumentException( "valueFormat==null" );
            TreeTableHelper.this.column(name).painter(nodeClass, (Fn2)customPainter);
            return this;
        }

        /**
         * Указывает функцию редактирования значения в колонке
         * @param customEditor функция редактирования
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> editor( Fn1<T,TreeTableNodeValueEditor.Editor> customEditor ){
            if( customEditor==null )throw new IllegalArgumentException( "customEditor==null" );
            TreeTableHelper.this.column(name).editor(nodeClass, (Fn1)customEditor);
            return this;
        }

        /**
         * Указывает функцию редактирования значения в колонке
         * @param customEditor функция редактирования
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> editor( Fn2<T, TreeTableNode,TreeTableNodeValueEditor.Editor> customEditor ){
            if( customEditor==null )throw new IllegalArgumentException( "customEditor==null" );
            TreeTableHelper.this.column(name).editor(nodeClass, (Fn2)customEditor);
            return this;
        }

        /**
         * Указывает имя колонки
         * @param name имя колонки
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> column( String name ){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            NodeColumnConf<T> ncc = new NodeColumnConf<T>( nodeClass, nodeConf, name );
            return ncc;
        }

        /**
         * Применение конфигурации
         * @return Конфигурация узла дерева
         */
        public NodeConf<T> apply(){
            if( nodeConf!=null )nodeConf.apply();

            TreeTableHelper.this.columns().apply();
            return nodeConf;
        }
    }

    /**
     * Конфигурация узла дерева
     * @param <T> Тип данных в узле дерева
     */
    public class NodeConf<T> {
        //<editor-fold defaultstate="collapsed" desc="nodeClass">
        protected Class nodeClass = null;

        /**
         * Возвращает тип данных в узле деева
         * @return тип данных в узле дерева
         */
        public Class getNodeClass() {
            return nodeClass;
        }

        /**
         * Указывает тип данных в узле дерева
         * @param nodeClass тип данных в узле дерева
         */
        public void setNodeClass(Class nodeClass) {
            this.nodeClass = nodeClass;
        }
        //</editor-fold>

        protected List<NodeColumnConf<T>> columnsConfig = new LinkedList<NodeColumnConf<T>>();

        /**
         * Конструктор
         * @param cls тип данных в узле дерева
         */
        public NodeConf( Class<T> cls ){
            if( cls==null )throw new IllegalArgumentException( "cls==null" );
            this.nodeClass = cls;
        }

        /**
         * Указывает функцию получения дочерних узлов поддерева
         * @param follower функция получения дочерних узлов
         * @return Конфигурация узла
         */
        public NodeConf<T> follow( NodesExtracter<T,?> follower ){
            if( follower==null )throw new IllegalArgumentException( "follower==null" );
            TreeTableHelper.this.nodes().follow(nodeClass, follower);
            return this;
        }

        /**
         * Указывает функцию проверки наличия дочерних узлов поддерева
         * @param followable функцию проверки наличия дочерних узлов поддерева
         * @return Конфигурация узла
         */
        public NodeConf<T> followable( Fn1<T,Boolean> followable ){
            if( followable==null )throw new IllegalArgumentException( "followable==null" );
            TreeTableHelper.this.nodes().followable(nodeClass,followable);
            return this;
        }

        /**
         * Указывает функция получения имени узла дерева
         * @param naming функция получения имени узла
         * @return Конфигурация узла
         */
        public NodeConf<T> naming(Fn1<T,String> naming){
            if( naming==null )throw new IllegalArgumentException( "naming==null" );
            TreeTableHelper.this.nodes().naming(nodeClass,naming);
            return this;
        }

        /**
         * Указывает конфигурацию колонки для данного типа узла дерева
         * @param name имя колонки
         * @return Конфигурация колонки
         */
        public NodeColumnConf<T> column( String name ){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            NodeColumnConf<T> ncc = new NodeColumnConf<T>( nodeClass, this, name );
            return ncc;
        }

        /**
         * Применять конфигурацию
         * @return ссылка на помощника TreeTable
         */
        public TreeTableHelper apply(){
            TreeTableHelper.this.nodes().apply();
            return TreeTableHelper.this;
        }

        /**
         * Применяет конфигурацию к поддереву
         * @param node поддерерво
         * @return ссылка на помощника TreeTable
         */
        public TreeTableHelper apply(TreeTableNodeBasic node){
            if (node== null) {
                throw new IllegalArgumentException("node==null");
            }
//            columnsConfig.forEach( ncc -> ncc.apply() );
            TreeTableHelper.this.nodes().applyTo(node);
            return TreeTableHelper.this;
        }
    }

    /**
     * Создает конфигурацию для определенного типа данных в узле
     * @param <T> Тип данных узла дерева
     * @param cls Тип данных узла дерева
     * @return конфигурация узла
     */
    public <T> NodeConf<T> node( Class<T> cls ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        return new NodeConf<T>(cls);
    }

    /**
     * Конфигурация колонки данных
     */
    public class DataColumnConf {
        //<editor-fold defaultstate="collapsed" desc="dataColumn">
        protected Column dataColumn;

        public Column getDataColumn() {
            return dataColumn;
        }

        public void setDataColumn(Column dataColumn) {
            this.dataColumn = dataColumn;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="tableColumn">
        protected TableColumn tableColumn;

        public TableColumn getTableColumn() {
            return tableColumn;
        }

        public void setTableColumn(TableColumn tableColumn) {
            this.tableColumn = tableColumn;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="name">
        protected String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="addDataColumnIfNotExists">
        protected boolean addDataColumnIfNotExists = true;

        public boolean isAddDataColumnIfNotExists() {
            return addDataColumnIfNotExists;
        }

        public void setAddDataColumnIfNotExists(boolean addDataColumnIfNotExists) {
            this.addDataColumnIfNotExists = addDataColumnIfNotExists;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="addTableColumnIfNotExists">
        protected boolean addTableColumnIfNotExists = true;

        public boolean isAddTableColumnIfNotExists() {
            return addTableColumnIfNotExists;
        }

        public void setAddTableColumnIfNotExists(boolean addTableColumnIfNotExists) {
            this.addTableColumnIfNotExists = addTableColumnIfNotExists;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="dataColumnsConf">
        protected DataColumnsConf dataColumnsConf = null;

        public DataColumnsConf getDataColumnsConf() {
            return dataColumnsConf;
        }

        public void setDataColumnsConf(DataColumnsConf dataColumnsConf) {
            this.dataColumnsConf = dataColumnsConf;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="valueReaders">
        protected ClassMap<Object> valueReaders = new ClassMap<Object>();

        public ClassMap<Object> getValueReaders() {
            if( valueReaders==null )valueReaders = new ClassMap<Object>();
            return valueReaders;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="valueWriters">
        protected ClassMap<Object> valueWriters = new ClassMap<Object>();

        public ClassMap<Object> getValueWriters() {
            if( valueWriters==null )valueWriters = new ClassMap<Object>();
            return valueWriters;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="valueType">
        protected ClassMap<Object> valueType = new ClassMap<Object>();

        public ClassMap<Object> getValueType() {
            if( valueType==null )valueType = new ClassMap<Object>();
            return valueType;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="valueFormat">
        protected ClassMap<Object> valueFormat = new ClassMap<Object>();

        public ClassMap<Object> getValueFormat() {
            if( valueFormat==null )valueFormat = new ClassMap<Object>();
            return valueFormat;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="customPainter">
        protected ClassMap<Object> customPainter = new ClassMap<Object>();

        public ClassMap<Object> getCustomPaintert() {
            if( customPainter==null )customPainter = new ClassMap<Object>();
            return customPainter;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="customPainter">
        protected ClassMap<Object> valueEditor = new ClassMap<Object>();

        public ClassMap<Object> getValueEditor() {
            if( valueEditor==null )valueEditor = new ClassMap<Object>();
            return valueEditor;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="getTableColumnOf()">
        protected TableColumn getTableColumnOf(Column col){
            if( col==null )return null;

            int idx = treeTable.getDataTreeColumns().indexOf(col);
            if( idx<0 )return null;

            for( int ci=0; ci<treeTable.getColumnModel().getColumnCount(); ci++ ){
                TableColumn tc = treeTable.getColumnModel().getColumn(ci);
                if( tc!=null && tc.getModelIndex()==idx ){
                    return tc;
                }
            }

            return null;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="getNodeValueColumn()">
        protected TreeTableNodeValueColumn getNodeValueColumn(){
            if( dataColumn instanceof TreeTableNodeValueColumn )
                return (TreeTableNodeValueColumn)dataColumn;

            if( dataColumn != null )
                throw new Error( "can't fetch TreeTableNodeValueColumn from exists "+dataColumn.getClass() );

            if( name!=null ){
                TreeTableNodeValueColumn ttnvc = getNodeValueColumnByName( name );

                TableColumn tc = getTableColumnOf(ttnvc);

                if( tc==null && ttnvc!=null && addTableColumnIfNotExists ){
                    int mi = treeTable.getDataTreeColumns().size()-1;

                    tc = new TableColumn(mi);

                    String tcHeaderVal = ttnvc.getName();
                    if( tcHeaderVal==null )tcHeaderVal = "column#"+mi;
                    tc.setHeaderValue(tcHeaderVal);

                    treeTable.getColumnModel().addColumn(tc);
                    tableColumn = tc;
                }

                return ttnvc;
            }

            return null;
        }

        protected TreeTableNodeValueColumn getNodeValueColumnByName( String name ){
            if( name==null )return null;

            for( Column col : treeTable.getDataTreeColumns() ){
                if( col==null ){
                    continue;
                }

                String ename = col.getName();
                if( name.equals(ename) ){
                    if( col instanceof TreeTableNodeValueColumn )
                        return (TreeTableNodeValueColumn)col;
                }
            }

            if( addDataColumnIfNotExists ){
                TreeTableNodeValueColumn col = new TreeTableNodeValueColumn();
                col.name(name);
                dataColumn = col;

                treeTable.getDataTreeColumns().add(col);

                TableColumn tc = getTableColumnOf(col);
                if( tc==null && addTableColumnIfNotExists ){
                    int mi = treeTable.getDataTreeColumns().size()-1;

                    tc = new TableColumn(mi);

                    String tcHeaderVal = col.getName();
                    if( tcHeaderVal==null )tcHeaderVal = "column#"+mi;
                    tc.setHeaderValue(tcHeaderVal);


                    treeTable.getColumnModel().addColumn(tc);
                }

                return col;
            }

            return null;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="reader()">
        public <T> DataColumnConf reader( Class<T> cls, Fn1<T,Object> reader ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (reader== null) {
                throw new IllegalArgumentException("reader==null");
            }

            getValueReaders().put(cls, reader);

            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="writer()">
        public <T> DataColumnConf writer( Class<T> cls, Fn3<TreeTableNode, T, Object, Object> writer ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (writer== null) {
                throw new IllegalArgumentException("reader==null");
            }

            getValueWriters().put(cls, writer);

            return this;
        }

        public <T> DataColumnConf writer( Class<T> cls, Fn2<T, Object,Object> writer ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (writer== null) {
                throw new IllegalArgumentException("reader==null");
            }

            getValueWriters().put(cls, writer);

            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="type()">
        public <T> DataColumnConf type( Class<T> cls, Fn2<T,TreeTableNode,Class> valueTyp ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (valueTyp== null) {
                throw new IllegalArgumentException("valueTyp==null");
            }

            getValueType().put(cls, valueTyp);

            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="format()">
        public <T> DataColumnConf format( Class<T> cls, Fn2<T,TreeTableNode,TreeTableNodeFormat> valueFormat ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (valueFormat== null) {
                throw new IllegalArgumentException("valueTyp==null");
            }

            getValueFormat().put(cls, valueFormat);

            return this;
        }

        public <T> DataColumnConf format( Class<T> cls, Fn1<T,TreeTableNodeFormat> valueFormat ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (valueFormat== null) {
                throw new IllegalArgumentException("valueTyp==null");
            }

            getValueFormat().put(cls, valueFormat);

            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="painter()">
        public <T> DataColumnConf painter( Class<T> cls, Fn1<T,Fn2<Graphics,Rectangle,Object>> customPainter ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (customPainter== null) {
                throw new IllegalArgumentException("valueTyp==null");
            }

            getCustomPaintert().put(cls, customPainter);

            return this;
        }

        public <T> DataColumnConf painter( Class<T> cls, Fn2<T,TreeTableNode,Fn2<Graphics,Rectangle,Object>> customPainter ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (customPainter== null) {
                throw new IllegalArgumentException("valueTyp==null");
            }

            getCustomPaintert().put(cls, customPainter);

            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="editor">
        public <T> DataColumnConf editor( Class<T> cls, Fn2<Object, TreeTableNode,TreeTableNodeValueEditor.Editor> customEditor ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (customEditor== null) {
                throw new IllegalArgumentException("valueTyp==null");
            }

            getValueEditor().put(cls, customEditor);

            return this;
        }

        public <T> DataColumnConf editor( Class<T> cls, Fn1<T,TreeTableNodeValueEditor.Editor> customEditor ){
            if (cls== null) {
                throw new IllegalArgumentException("cls==null");
            }
            if (customEditor== null) {
                throw new IllegalArgumentException("valueTyp==null");
            }

            getValueEditor().put(cls, customEditor);

            return this;
        }
        //</editor-fold>

        public DataColumnsConf apply(){
            ClassMap<Object> vreaders = getValueReaders();
            if( vreaders.size()>0 ){
                TreeTableNodeValueColumn ttnvc = getNodeValueColumn();
                if( ttnvc!=null ){
                    for( Map.Entry<Class,Object> en : vreaders.entrySet() ){
                        Class cls = en.getKey();
                        Object rd = en.getValue();
                        if( cls!=null && rd!=null && rd instanceof Fn1 ){
                            ttnvc.addValueReader(cls, (Fn1)rd);
                        }else if( cls!=null && rd!=null && rd instanceof Fn2 ){
                            ttnvc.addValueReader(cls, (Fn2)rd);
                        }
                    }
                }
            }

            ClassMap<Object> vwriters = getValueWriters();
            if( vwriters.size()>0 ){
                TreeTableNodeValueColumn ttnvc = getNodeValueColumn();
                if( ttnvc!=null ){
                    for( Map.Entry<Class,Object> en : vwriters.entrySet() ){
                        Class cls = en.getKey();
                        Object wd = en.getValue();
                        if( cls!=null && wd!=null && wd instanceof Fn2 ){
                            ttnvc.addValueWriter(cls, (Fn2)wd);
                        }else if( cls!=null && wd!=null && wd instanceof Fn3 ){
                            ttnvc.addValueWriter(cls, (Fn3)wd);
                        }
                    }
                }
            }

            ClassMap<Object> vtype = getValueType();
            if( vtype.size()>0 ){
                TreeTableNodeValueColumn ttnvc = getNodeValueColumn();
                if( ttnvc!=null ){
                    for( Map.Entry<Class,Object> en : vtype.entrySet() ){
                        Class cls = en.getKey();
                        Object vt = en.getValue();
                        //vtype.forEach( (cls,vt) -> {
                        if( cls!=null && vt!=null && vt instanceof Fn2 ){
                            Fn2 fn = (Fn2)vt;
                            ttnvc.addValueType(cls, fn);
                        }
                    }
                }
            }

            ClassMap<Object> vformat = getValueFormat();
            if( vformat.size()>0 ){
                TreeTableNodeValueColumn ttnvc = getNodeValueColumn();
                if( ttnvc!=null ){
                    //vformat.forEach( (cls,vf) -> {
                    for( Map.Entry<Class,Object> en : vformat.entrySet() ){
                        Class cls = en.getKey();
                        Object vf = en.getValue();
                        if( cls!=null && vf!=null && vf instanceof Fn2 ){
                            Fn2 fn = (Fn2)vf;
                            ttnvc.addValueFormat(cls, fn);
                        }else if( cls!=null && vf!=null && vf instanceof Fn1 ){
                            Fn1 fn = (Fn1)vf;
                            ttnvc.addValueFormat(cls, fn);
                        }
                    }
                }
            }

            ClassMap<Object> vcustomPainter = getCustomPaintert();
            if( vcustomPainter.size()>0 ){
                TreeTableNodeValueColumn ttnvc = getNodeValueColumn();
                if( ttnvc!=null ){
                    //vcustomPainter.forEach( (cls,vf) -> {
                    for( Map.Entry<Class,Object> en : vcustomPainter.entrySet() ){
                        Class cls = en.getKey();
                        Object vf = en.getValue();
                        if( cls!=null && vf!=null && vf instanceof Fn2 ){
                            Fn2 fn = (Fn2)vf;
                            ttnvc.addCustomPainter(cls, fn);
                        }else if( cls!=null && vf!=null && vf instanceof Fn1 ){
                            Fn1 fn = (Fn1)vf;
                            ttnvc.addCustomPainter(cls, fn);
                        }
                    }
                }
            }

            ClassMap<Object> veditor = getValueEditor();
            if( veditor.size()>0 ){
                TreeTableNodeValueColumn ttnvc = getNodeValueColumn();
                if( ttnvc!=null ){
                    //veditor.forEach( (cls,vf) -> {
                    for( Map.Entry<Class,Object> en : veditor.entrySet() ){
                        Class cls = en.getKey();
                        Object vf = en.getValue();
                        if( cls!=null && vf!=null && vf instanceof Fn2 ){
                            Fn2 fn = (Fn2)vf;
                            ttnvc.addValueEditor(cls, fn);
                        }else if( cls!=null && vf!=null && vf instanceof Fn1 ){
                            Fn1 fn = (Fn1)vf;
                            ttnvc.addValueEditor(cls, fn);
                        }
                    }
                }
            }

            return dataColumnsConf;
        }
    }

    public class DataColumnsConf {
        protected Map<String,DataColumnConf> columns = new LinkedHashMap<String,DataColumnConf>();

        public Map<String, DataColumnConf> getColumns() {
            if( columns==null )columns = new LinkedHashMap<String,DataColumnConf>();
            return columns;
        }

        public void setColumns(Map<String, DataColumnConf> columns) {
            this.columns = columns;
        }

        public DataColumnConf column(String name){
            if (name== null) {
                throw new IllegalArgumentException("name==null");
            }

            DataColumnConf dcc = columns.get(name);
            if( dcc==null ){
                dcc = new DataColumnConf();
                dcc.setName(name);
                dcc.setDataColumnsConf(this);
                columns.put(name, dcc);
            }

            return dcc;
        }

        public TreeTableHelper apply(){
            //getColumns().forEach( (name,dcc) -> {
            for( Map.Entry<String,DataColumnConf> en : getColumns().entrySet() ){
                String name = en.getKey();
                DataColumnConf dcc = en.getValue();
                if( name==null || dcc==null )continue;
                dcc.setName(name);
                dcc.setDataColumnsConf(this);
                dcc.apply();
            }// );

            return TreeTableHelper.this;
        }
    }

    protected DataColumnsConf columns = new DataColumnsConf();

    public DataColumnsConf columns(){
        if( columns==null )columns = new DataColumnsConf();
        return columns;
    }

    public DataColumnConf column( String name ){
        return columns().column(name);
    }

    public TreeTableHelper apply(){
        nodes().apply();
        columns().apply();
        return this;
    }
}
