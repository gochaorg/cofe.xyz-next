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

package xyz.cofe.gui.swing.tree;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import xyz.cofe.collection.ClassMap;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn3;
import xyz.cofe.gui.swing.table.Column;
import xyz.cofe.gui.swing.table.IsRowEditable;

/**
 * Колонка с данными узла TreeTableNode, возвращает данные TreeTableNodeValue
 * @author nt.gocha@gmail.com
 */
public class TreeTableNodeValueColumn extends Column implements IsRowEditable
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableNodeValueColumn.class.getName());

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
        logger.entering(TreeTableNodeValueColumn.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(TreeTableNodeValueColumn.class.getName(), method, result);
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

    /**
     * Конструктор
     */
    public TreeTableNodeValueColumn() {
        setName("value");
        setType( TreeTableNodeValue.class );
        setReader( createReader() );
        setWriter( createWriter() );
    }

    /**
     * Конструктор
     * @param sync объект для синхронизации
     */
    public TreeTableNodeValueColumn( ReadWriteLock sync) {
        super(sync);
        setName("value");
        setType( TreeTableNodeValue.class );
        setReader( createReader() );
    }

    /**
     * Конструктор копирования
     * @param src образец для копирования
     */
    public TreeTableNodeValueColumn(Column src) {
        super(src);
//        setType( TreeTableNodeValue.class );
//        setReader( createReader() );
    }

    /**
     * Конструктор копирования
     * @param sync объект для синхронизации
     * @param src образец для копирования
     */
    public TreeTableNodeValueColumn(ReadWriteLock sync, Column src) {
        super(sync, src);
//        setName("value");
//        setType( TreeTableNodeValue.class );
//        setReader( createReader() );
    }

    /**
     * Указывает имя колонки
     * @param name имя колонки
     * @return self ссылка
     */
    @Override
    public TreeTableNodeValueColumn name(String name) {
        super.name(name);
        return this;
    }

    //<editor-fold defaultstate="collapsed" desc="customPainter - Специализированый рендер">
    /**
     * customPainter : ClassMap&lt;Рендер-функция, Объект, Узел&gt; . <br>
     * Рендер-функция: <br>
     * fn ( Объект, Узел ) =&gt; Рендер функция.
     */
    protected ClassMap<Fn2<Object, TreeTableNode, Fn2<Graphics,Rectangle,Object>>> customPainter
        = new ClassMap<>();

    /**
     * Спец рендер функции ClassMap&lt;Рендер_функция, Объект, Узел&gt; . <br>
     * Значение карты: <br>
     * fn ( Объект, Узел ) =&gt; Рендер функция. <br>
     * @return карта специализированных ренедер функций
     */
    public ClassMap<Fn2<Object, TreeTableNode,Fn2<Graphics,Rectangle,Object>>> getCustomPainter(){
        if( customPainter!=null )return customPainter;
        customPainter = new ClassMap<>();
        return customPainter;
    }

    /**
     * Указывает спец рендер для указанных типов
     * @param <T> тип данных узла
     * @param cls тип данных узла
     * @param reader рендер функция
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addCustomPainter(
        Class<T> cls,
        Fn2<T,TreeTableNode,Fn2<Graphics,Rectangle,Object>> reader
    ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (reader== null) {
            throw new IllegalArgumentException("reader==null");
        }
        getCustomPainter().put(
            cls,
            (Fn2<Object, TreeTableNode,Fn2<Graphics,Rectangle,Object>>)reader);
        return this;
    }

    /**
     * Указывает спец рендер для указанных типов
     * @param <T> тип данных узла
     * @param cls тип данных узла
     * @param reader рендер функция
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addCustomPainter(
        final Class<T> cls,
        final Fn1<T,Fn2<Graphics,Rectangle,Object>> reader )
    {
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (reader== null) {
            throw new IllegalArgumentException("reader==null");
        }

        Fn2<Object, TreeTableNode, Fn2<Graphics,Rectangle,Object>> freader =
            (dataOfNode, node) -> {
                    if( dataOfNode!=null && cls.isAssignableFrom(dataOfNode.getClass()) ){
                        return reader.apply((T)dataOfNode);
                    }
                    return null;
                };

        getCustomPainter().put(cls, freader);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valueFormat - форматирование данных">
    /**
     * valueFormat : ClassMap&lt;Форматирование, Объект, Узел&gt; - функция: <br>
     * fn ( Объект, Узел ) =&gt; Форматирование объекта.
     */
    protected ClassMap<Fn2<Object, TreeTableNode,TreeTableNodeFormat>> valueFormat
        = new ClassMap<>();

    /**
     * Форматирование ClassMap&lt;Форматирование, Объект, Узел&gt; - функция: <br>
     * fn ( Объект, Узел ) =&gt; Форматирование объекта.
     * @return картка форматирования различных типов объектов
     */
    public ClassMap<Fn2<Object, TreeTableNode,TreeTableNodeFormat>> getValueFormat(){
        if( valueFormat!=null )return valueFormat;
        valueFormat = new ClassMap<>();
        return valueFormat;
    }

    /**
     * Указывает форматирование для указанных типов
     * @param <T> тип данных узла
     * @param cls тип данных узла
     * @param reader функция форматирования
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addValueFormat( Class<T> cls, Fn2<T,TreeTableNode,TreeTableNodeFormat> reader ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (reader== null) {
            throw new IllegalArgumentException("reader==null");
        }
        getValueFormat().put(
            cls,
            (Fn2)reader);
        return this;
    }

    /**
     * Указывает форматирование для указанных типов
     * @param <T> тип данных узла
     * @param cls тип данных узла
     * @param reader функция форматирования
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addValueFormat(
        final Class<T> cls,
        final Fn1<T,TreeTableNodeFormat> reader
    ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (reader== null) {
            throw new IllegalArgumentException("reader==null");
        }

        Fn2<Object, TreeTableNode,TreeTableNodeFormat> freader =
            //( Object dataOfNode, TreeTableNode node ) -> {
            (Object dataOfNode, TreeTableNode node) -> {
                    if( dataOfNode!=null && cls.isAssignableFrom(dataOfNode.getClass()) ){
                        return reader.apply((T)dataOfNode);
                    }
                    return null;
                };

        getValueFormat().put(cls, freader);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="value readers - функция чтения">
    /**
     * valueReader : ClassMap&lt;Возвращаемое, Объект, Узел&gt; - функция: <br>
     * fn ( Объект, Узел ) =&gt; Данные объекта.
     */
    protected ClassMap<Fn2<Object, TreeTableNode, Object>> valueReader
        = new ClassMap<>();

    /**
     * Функция чтение данных узла. <br>
     * Карта: тип данных =&gt; fn ( данные_узла, узел ) : отображаемые данные
     * @return карта
     */
    public ClassMap<Fn2<Object, TreeTableNode, Object>> getValueReaders() {
        if( valueReader==null )valueReader = new ClassMap<>();
        return valueReader;
    }

    /**
     * Указывает функцию чтения узла
     * @param <T> тип данных
     * @param cls тип данных
     * @param reader fn ( данные_узла, узел ) : отображаемые данные
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addValueReader( Class<T> cls, Fn2<T,TreeTableNode,Object> reader ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (reader== null) {
            throw new IllegalArgumentException("reader==null");
        }
        getValueReaders().put(
            cls,
            (Fn2)reader);
        return this;
    }

    /**
     * Указывает функцию чтения узла
     * @param <T> тип данных
     * @param cls тип данных
     * @param reader fn ( данные_узла, узел ) : отображаемые данные
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addValueReader(
        final Class<T> cls,
        final Fn1<T,Object> reader ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (reader== null) {
            throw new IllegalArgumentException("reader==null");
        }

        Fn2<Object, TreeTableNode,Object> freader =
            ( Object dataOfNode, TreeTableNode node ) -> {
                    if( dataOfNode!=null && cls.isAssignableFrom(dataOfNode.getClass()) ){
                        return reader.apply((T)dataOfNode);
                    }
                    return null;
                };

        getValueReaders().put(cls, freader);
        return this;
    }

    /**
     * Создает функцию чтения
     * @return функция чтения, возвращает TreeTableNodeValue
     */
    public Function createReader(){
        return (Object node) -> {
                if( node instanceof TreeTableNode ){
                    Object dataOfNode = ((TreeTableNode)node).getData();
                    if( dataOfNode!=null ){
                        Fn2 fnReader = getValueReaders().fetch(dataOfNode.getClass());
                        if( fnReader!=null ){
                            Object value = fnReader.apply(dataOfNode, (TreeTableNode)node);

                            TreeTableNodeValue ttnv = new TreeTableNodeValue(value,dataOfNode,(TreeTableNode)node);

                            ttnv.valueReader = fnReader;
                            ttnv.valueWriter = getValueWriters().fetch(dataOfNode.getClass());

                            Fn2<Object,TreeTableNode,Class> fnType = getValueType().fetch(dataOfNode.getClass());
                            if( fnType!=null ){
                                ttnv.valueType = fnType.apply(dataOfNode, (TreeTableNode)node);
                            }else{
                                ttnv.valueType = null;
                            }

                            Fn2<Object,TreeTableNode,TreeTableNodeFormat> fnFormat =
                                getValueFormat().fetch(dataOfNode.getClass());

                            if( fnFormat!=null ){
                                TreeTableNodeFormat fmt = fnFormat.apply(dataOfNode, (TreeTableNode)node);
                                ttnv.setFormat(fmt);
                            }

                            Fn2<Object,TreeTableNode,TreeTableNodeValueEditor.Editor> fnEditor
                                = getValueEditor().fetch(dataOfNode.getClass());

                            if( fnEditor!=null ){
                                ttnv.setEditor(fnEditor.apply(dataOfNode, (TreeTableNode)node));
                            }

                            Fn2<Object, TreeTableNode,Fn2<Graphics,Rectangle,Object>> fnPainter
                                = getCustomPainter().fetch(dataOfNode.getClass());

                            if( fnPainter!=null ){
                                ttnv.setCustomPainter(fnPainter.apply(dataOfNode, (TreeTableNode)node));
                            }

                            return ttnv;
                        }
                    }
                }
                return new TreeTableNodeValue();
            };
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="value writers - функция записи">
    /**
     * valueWriter
     * fn( Узел, Объект, Измененные данные ) =&gt; Сохраненные данные
     */
    protected ClassMap<Fn3<TreeTableNode, Object, Object, Object>> valueWriter
        = new ClassMap<>();

    /**
     * Возвращает карту функций записи. <br>
     * Карта тип_данных =&gt; fn( узел, данные_узла, записываемые_данные )
     * @return карта
     */
    public ClassMap<Fn3<TreeTableNode, Object, Object, Object>> getValueWriters(){
        if( valueWriter==null )valueWriter = new ClassMap<>();
        return valueWriter;
    }

    /**
     * Указывает функцию записи данных
     * @param <T> тип_данных
     * @param cls тип_данных
     * @param writer fn( узел, данные_узла, записываемые_данные )
     * @return self-ссылка
     */
    public <T> TreeTableNodeValueColumn addValueWriter( Class<T> cls, Fn3<TreeTableNode, T, Object,Object> writer ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (writer== null) {
            throw new IllegalArgumentException("writer==null");
        }

        getValueWriters().put(cls, (Fn3)writer);

        return this;
    }

    /**
     * Указывает функцию записи данных
     * @param <T> тип_данных
     * @param cls тип_данных
     * @param writer fn( узел, данные_узла, записываемые_данные )
     * @return self-ссылка
     */
    public <T> TreeTableNodeValueColumn addValueWriter(
        final Class<T> cls,
        final Fn2<T, Object, Object> writer ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (writer== null) {
            throw new IllegalArgumentException("writer==null");
        }

        Fn3<TreeTableNode, T, Object, Object> wr =
            ( TreeTableNode node, T t, Object v ) -> {
                    Object r = writer.apply(t, v);
                    return r;
                };

        getValueWriters().put(cls, (Fn3)wr);

        return this;
    }

    /**
     * Создает функцию записи
     * @return функция записи
     */
    public Function<Cell,Boolean> createWriter(){
        return (Column.Cell cell) -> {
                if( cell.object instanceof TreeTableNode ){
                    TreeTableNode ttnode = (TreeTableNode)cell.object;
                    Object dataOfNode = ttnode.getData();
                    if( dataOfNode!=null ){
                        Fn3 writer
                            = getValueWriters().fetch(dataOfNode.getClass());

                        if( writer==null )return false;

                        Object writed = writer.apply(ttnode, dataOfNode, cell.newValue);
                        // ttnode.setData(writed);
                        return true;
                    }
                }
                return false;
            };
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="value type - возвращает тип данных используемый при чтении / записи">
    protected ClassMap<Fn2<Object,TreeTableNode,Class>> valueType
        = new ClassMap<>();

    /**
     * Возвращает карту функций вычисления типа данных
     * @return карта функций
     */
    public ClassMap<Fn2<Object,TreeTableNode,Class>> getValueType(){
        if( valueType==null )valueType = new ClassMap<>();
        return valueType;
    }

    /**
     * Добавляет функцию для вычисления типа отображаемого значения
     * @param <T> Тип данных
     * @param cls Тип данных
     * @param t Функция высиляющая тип отображаемых данных
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addValueType( Class<T> cls, Fn2<Object,TreeTableNode,Class> t ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (t== null) {
            throw new IllegalArgumentException("t==null");
        }
        getValueType().put(cls, t);
        return this;
    }

    /**
     * Добавляет функцию для вычисления типа отображаемого значения
     * @param <T> Тип данных
     * @param cls Тип данных
     * @param typeReader Функция высиляющая тип отображаемых данных
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addValueType(
        final Class<T> cls,
        final Fn1<T,Class> typeReader ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (typeReader== null) {
            throw new IllegalArgumentException("typeReader==null");
        }

        Fn2<Object, TreeTableNode, Class> freader =
            ( Object dataOfNode, TreeTableNode node ) -> {
                    if( dataOfNode!=null && cls.isAssignableFrom(dataOfNode.getClass()) ){
                        return typeReader.apply((T)dataOfNode);
                    }
                    return null;
                };

        getValueType().put(cls, freader);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valueEditor">
    protected ClassMap<Fn2<Object, TreeTableNode,TreeTableNodeValueEditor.Editor>> valueEditor
        = new ClassMap<>();

    /**
     * Возвращает карту функция для получения редкатора значения ячейки
     * @return карта функций
     */
    public ClassMap<Fn2<Object, TreeTableNode, TreeTableNodeValueEditor.Editor>> getValueEditor(){
        if( valueEditor!=null )return valueEditor;
        valueEditor = new ClassMap<>();
        return valueEditor;
    }

    /**
     * Добавляет функция возращающую редактор значения
     * @param <T> Тип данных узла
     * @param cls Тип данных узла
     * @param editorFun Функция редактирования значения
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addValueEditor( Class<T> cls, Fn2<T,TreeTableNode,TreeTableNodeValueEditor.Editor> editorFun ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (editorFun== null) {
            throw new IllegalArgumentException("editorFun==null");
        }
        getValueEditor().put(
            cls,
            (Fn2)editorFun);
        return this;
    }

    /**
     * Добавляет функция возращающую редактор значения
     * @param <T> Тип данных узла
     * @param cls Тип данных узла
     * @param editorFun Функция редактирования значения
     * @return self ссылка
     */
    public <T> TreeTableNodeValueColumn addValueEditor(
        final Class<T> cls,
        final Fn1<T,TreeTableNodeValueEditor.Editor> editorFun ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (editorFun== null) {
            throw new IllegalArgumentException("editorFun==null");
        }

        Fn2<Object, TreeTableNode, TreeTableNodeValueEditor.Editor> freader =
            ( Object dataOfNode, TreeTableNode node ) -> {
                    if( dataOfNode!=null && cls.isAssignableFrom(dataOfNode.getClass()) ){
                        return editorFun.apply((T)dataOfNode);
                    }
                    return null;
                };

        getValueEditor().put(cls, freader);
        return this;
    }
    //</editor-fold>

    /**
     * Проверяет есть ли возмодность редактирования занчения для указанного значения.
     *
     * <p>
     * Чтоб можно было редактировать данные, необходимо добавить соответствующий редактор и функцию записи
     * @param node Узел дерева (TreeTableNode)
     * @return true - есть возможность редактирования
     * @see TreeTableNode
     * @see #addValueEditor
     * @see #addValueWriter
     */
    @Override
    public boolean isRowEditable(Object node) {
        if( node == null )return false;
        if( !(node instanceof TreeTableNode) )return false;

        TreeTableNode ttnode = (TreeTableNode)node;
        Object dataOfNode = ttnode.getData();
        if( dataOfNode!=null ){
            Object writer
                = getValueWriters().fetch(dataOfNode.getClass());

            Object editor
                = getValueEditor().fetch(dataOfNode.getClass());

            if( writer!=null || editor!=null )return true;
        }

        return false;
    }
}
