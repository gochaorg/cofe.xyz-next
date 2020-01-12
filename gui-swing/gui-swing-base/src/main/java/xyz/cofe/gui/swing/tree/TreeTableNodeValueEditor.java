package xyz.cofe.gui.swing.tree;

import xyz.cofe.collection.ClassMap;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.gui.swing.bean.UiBean;
import xyz.cofe.gui.swing.properties.Property;
import xyz.cofe.gui.swing.properties.PropertyValue;
import xyz.cofe.gui.swing.properties.SetPropertyEditorOpts;
import xyz.cofe.gui.swing.properties.editor.EnumEditor;
import xyz.cofe.gui.swing.properties.editor.TreeTableWrapEditor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.beans.PropertyEditor;
import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreeTableNodeValueEditor
    implements TableCellEditor
{
    //<editor-fold defaultstate="collapsed" desc="interface Editor">
    /**
     * Интерфейс редактора значения ячейки
     */
    public interface Editor {
        public JComponent getComponent();

        public void startEditing( Object value, Object context );

        public boolean stopCellEditing();
        public void cancelCellEditing();

        public Object getCellEditorValue();
        public boolean isCellEditable();
        public boolean isShouldSelectCell();

        public void clearAllListeners();
        public void addCellEditorListener( CellEditorListener l ) ;
        public void removeCellEditorListener( CellEditorListener l ) ;

        public void fireEditingCanceled( Object src );
        public void fireEditingStopped( Object src );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="event listeners">
    /**
     * Событие отмены редактирования
     */
    public static class EditingCanceledEvent extends ChangeEvent {
        public EditingCanceledEvent(Object source) {
            super(source);
        }
    }

    /**
     * Событие завершения редактирования
     */
    public static class EditingStoppedEvent extends ChangeEvent {
        public EditingStoppedEvent(Object source) {
            super(source);
        }
    }

    /**
     * Поддержка событий редактирования
     */
    public static class CellEditorListenerSupport {
        protected ListenersHelper<CellEditorListener,ChangeEvent> listeners
            = new ListenersHelper<CellEditorListener,ChangeEvent>(
            (CellEditorListener lstn, ChangeEvent ev) -> {
                    if( lstn!=null ){
                        if( ev instanceof EditingCanceledEvent ){
                            lstn.editingCanceled(ev);
                        }else if( ev instanceof EditingStoppedEvent ){
                            lstn.editingStopped(ev);
                        }
                    }
                }
        );

        public ListenersHelper<CellEditorListener, ChangeEvent> getListenersHelper(){
            return listeners;
        }

        protected Closeables listenersCs = new Closeables();

        public void clearAllListeners() {
            listenersCs.close();
        }

        public void addCellEditorListener(CellEditorListener l) {
            if (l== null) {
                throw new IllegalArgumentException("l==null");
            }
            listenersCs.add( listeners.addListener(l) );
        }

        public void removeCellEditorListener(CellEditorListener l) {
            if (l== null) {
                throw new IllegalArgumentException("l==null");
            }
            listeners.removeListener(l);
        }

        public void fireEditingCanceled(Object src){
            listeners.fireEvent(new EditingCanceledEvent(src) );
        }

        public void fireEditingStopped(Object src){
            listeners.fireEvent(new EditingStoppedEvent(src) );
        }
    }
    //</editor-fold>

    protected final CellEditorListenerSupport listenersSupport = new CellEditorListenerSupport();

    //<editor-fold defaultstate="collapsed" desc="class BaseEditor">
    public static class BaseEditor implements Editor {
        //<editor-fold defaultstate="collapsed" desc="component">
        protected JComponent component;

        @Override
        public JComponent getComponent() {
            return component;
        }

        public void setComponent(JComponent component) {
            this.component = component;
        }
        //</editor-fold>

        @Override
        public void startEditing(Object value, Object context) {
            JComponent cmpt = getComponent();
            //if( cmpt!=null )cmpt.setVisible(true);
            /*if( cmpt!=null ){
                SwingUtilities.invokeLater(() -> {

                    cmpt.revalidate();
                    cmpt.repaint();
                });
            }*/
        }

        @Override
        public boolean stopCellEditing() {
            JComponent cmpt = getComponent();
            //if( cmpt!=null )cmpt.setVisible(false);
            return true;
        }

        @Override
        public void cancelCellEditing() {
            JComponent cmpt = getComponent();
            //if( cmpt!=null )cmpt.setVisible(false);
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        //<editor-fold defaultstate="collapsed" desc="cellEditable">
        protected boolean cellEditable = true;

        public void setCellEditable( boolean v ){
            cellEditable = true;
        }

        @Override
        public boolean isCellEditable() {
            return cellEditable;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="shouldSelectCell">
        protected boolean shouldSelectCell = true;

        @Override
        public boolean isShouldSelectCell() {
            return shouldSelectCell;
        }

        public void setShouldSelectCell(boolean shouldSelectCell) {
            this.shouldSelectCell = shouldSelectCell;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="listeners">
        protected final CellEditorListenerSupport listeners = new CellEditorListenerSupport();

        @Override
        public void clearAllListeners() {
            listeners.clearAllListeners();
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listeners.addCellEditorListener(l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listeners.removeCellEditorListener(l);
        }

        @Override
        public void fireEditingCanceled(Object src) {
            listeners.fireEditingCanceled(src);
        }

        @Override
        public void fireEditingStopped(Object src) {
            listeners.fireEditingStopped(src);
        }
        //</editor-fold>

        private static Icon editIcon;
        private static Icon nullIcon;
        private static Icon nullSelectedIcon;
        private static Icon nullUnSelectedIcon;

        static {
            URL u = TreeTableNodeValueEditor.class.getResource(
                "/xyz/cofe/gui/swing/properties/editor/gtk-edit_16.png");

            if( u!=null ){ editIcon = new ImageIcon(u); }

            u = TreeTableNodeValueEditor.class.getResource(
                "/xyz/cofe/gui/swing/properties/editor/null.png");

            if( u!=null ){ nullIcon = new ImageIcon(u); }

            u = TreeTableNodeValueEditor.class.getResource(
                "/xyz/cofe/gui/swing/properties/editor/null-a-001.png");

            if( u!=null ){ nullSelectedIcon = new ImageIcon(u); }

            u = TreeTableNodeValueEditor.class.getResource(
                "/xyz/cofe/gui/swing/properties/editor/null-b-002.png");

            if( u!=null ){ nullUnSelectedIcon = new ImageIcon(u); }
        }

        /**
         * Возвращает иконку edit
         * @return иконка edit
         */
        public static Icon getEditIcon(){
            return editIcon;
        }

        /**
         * Возвращает иконку null
         * @return иконка null
         */
        public static Icon getNullIcon(){
            return nullIcon;
        }

        /**
         * Возвращает иконку null selected
         * @return иконка null-selected
         */
        public static Icon getNullSelectedIcon(){
            return nullSelectedIcon;
        }

        /**
         * Возвращает иконку null unselected
         * @return иконка null-unselected
         */
        public static Icon getNullUnSelectedIcon(){
            return nullUnSelectedIcon;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="class WrappedEditor">
    public static class WrappedEditor implements Editor, Closeable {
        protected Editor source;

        public WrappedEditor(Editor source){
            if( source==null )throw new IllegalArgumentException( "source==null" );
            this.source = source;
            this.source.addCellEditorListener(sourceListener);
        }

        protected CellEditorListener sourceListener = new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                ChangeEvent ce = new ChangeEvent(this);
                fireEditingStopped(ce);
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                ChangeEvent ce = new ChangeEvent(this);
                fireEditingCanceled(ce);
            }
        };

        @Override
        public void close() {
            if( source!=null ){
                source.removeCellEditorListener(sourceListener);
                source = null;
            }
        }

        @Override
        public JComponent getComponent() {
            if( source==null )return null;
            return source.getComponent();
        }

        @Override
        public void startEditing(Object value, Object context) {
            if( source==null )return;
            source.startEditing(value, context);
        }

        @Override
        public boolean stopCellEditing() {
            if( source==null )return true;
            return source.stopCellEditing();
        }

        @Override
        public void cancelCellEditing() {
            if( source==null )return;
            source.cancelCellEditing();
        }

        @Override
        public Object getCellEditorValue() {
            if( source==null )return null;
            return source.getCellEditorValue();
        }

        @Override
        public boolean isCellEditable() {
            if( source==null )return false;
            return source.isCellEditable();
        }

        @Override
        public boolean isShouldSelectCell() {
            if( source==null )return false;
            return source.isShouldSelectCell();
        }

        //<editor-fold defaultstate="collapsed" desc="listeners">
        protected final CellEditorListenerSupport listeners = new CellEditorListenerSupport();

        @Override
        public void clearAllListeners() {
            listeners.clearAllListeners();
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listeners.addCellEditorListener(l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listeners.removeCellEditorListener(l);
        }

        @Override
        public void fireEditingCanceled(Object src) {
            listeners.fireEditingCanceled(src);
        }

        @Override
        public void fireEditingStopped(Object src) {
            listeners.fireEditingStopped(src);
        }
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="currentEditor">
    protected Editor currentEditor = null;

    public Editor getCurrentEditor() {
        if( currentEditor!=null )return currentEditor;
        currentEditor = getUnsupportedEditor();
        return currentEditor;
    }

    public void setCurrentEditor(Editor currentEditor) {
        if( this.currentEditor!=null ){
            this.currentEditor.cancelCellEditing();
            this.currentEditor.clearAllListeners();
        }
        this.currentEditor = currentEditor;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="unsupportedEditor">
    protected volatile Editor unsupportedEditor = null;
    public Editor getUnsupportedEditor(){
        if( unsupportedEditor!=null )return unsupportedEditor;
        synchronized(this){
            if( unsupportedEditor!=null )return unsupportedEditor;
            unsupportedEditor = createUnsupportedEditor(null);
            return unsupportedEditor;
        }
    }

    public static class UnsupportedEditor extends BaseEditor {
        protected JLabel label;
        protected WeakReference startValue = null;

        public UnsupportedEditor(String message){
            label = new JLabel(message==null ? "not supported" : message);
            setComponent(label);
            setCellEditable(false);
        }

        @Override
        public Object getCellEditorValue() {
            if( startValue!=null ){
                Object v = startValue.get();
                return v;
            }
            return super.getCellEditorValue();
        }

        @Override
        public void startEditing(Object value, Object context) {
            startValue = new WeakReference(value);
            super.startEditing(value, context);
        }

        @Override
        public JComponent getComponent() {
            return label;
        }

    }

    public UnsupportedEditor createUnsupportedEditor(String message){
        UnsupportedEditor ed = new UnsupportedEditor(message);
        return ed;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createTextFieldEditor">
    public Editor createTextFieldEditor(TreeTableNodeValue ttnv){
        BaseEditor ed = new BaseEditor();

        JTextField tf = new JTextField();
        ed.setComponent(tf);

        return ed;
    }

    public static class TextFieldEditor extends BaseEditor
    {
        //        protected String startValue = null;
        protected JTextField textField = null;

        public TextFieldEditor(){
            textField = new JTextField();
            textField.setBorder(new EmptyBorder(0, 0, 0, 0));

            setComponent(textField);
            setCellEditable(true);
            setShouldSelectCell(true);
        }

        @Override
        public Object getCellEditorValue() {
            return textField.getText();
        }

        @Override
        public void startEditing(Object value, Object context) {
            if( value instanceof String ){
                textField.setText((String)value);
            }else{
                textField.setText("");
            }

            super.startEditing(value, context);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="typeEditors">
    protected ClassMap<Editor> typeEditors;
    public ClassMap<Editor> getTypeEditors(){
        if( typeEditors!=null )return typeEditors;
        typeEditors = createTypeEditors();
        return typeEditors;
    }
    public void setTypeEditors(ClassMap<Editor> editors){
        typeEditors = editors;
    }
    //</editor-fold>

    protected ClassMap<Editor> createTypeEditors(){
        ClassMap<Editor> editors = new ClassMap<Editor>();
        editors.put(String.class, getTextFieldEditor());
        return editors;
    }

    protected TextFieldEditor textFieldEditor;
    protected TextFieldEditor getTextFieldEditor(){
        if( textFieldEditor!=null )return textFieldEditor;
        textFieldEditor = new TextFieldEditor();
        return textFieldEditor;
    }

    protected WeakReference<JTable> editTable = null;
    protected WeakReference<Object> editValue = null;
    protected int editRow = -1;
    protected int editColumn = -1;

    /**
     * Производит поиск подходящего редактора
     */
    public interface EditorFinder {
        /**
         * Поиск подходящего редактора
         * @param table Оригинальная таблица
         * @param value Редактируемое значение
         * @param isSelected Значение выделенно
         * @param row Строка
         * @param column Колонка
         * @return Походящий редактор или null
         */
        Editor findEditor( JTable table, Object value, boolean isSelected, int row, int column );
    }

    protected EditorFinder editorFinder;
    public EditorFinder getEditorFinder() { return editorFinder; }
    public void setEditorFinder(EditorFinder editorFinder) { this.editorFinder = editorFinder; }

    @Override
    public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column) {
        editTable = new WeakReference<>(table);
        editValue = new WeakReference<>(value);
        editRow = row;
        editColumn = column;
        //convertResult = null;

        EditorFinder edFinder = editorFinder;

        if( value instanceof TreeTableNodeValue ){
            TreeTableNodeValue ttnv = (TreeTableNodeValue)value;
            Object val = ttnv.getValue();

            Editor customEditor = ttnv.getEditor();
            if( customEditor!=null ){
                customEditor.startEditing(val, ttnv);
                setCurrentEditor(customEditor);
                return customEditor.getComponent();
            }

            Class valType = ttnv.getValueType();
            if( valType!=null ){
                Editor ed = getTypeEditors().fetch(valType);
                if( ed!=null ){
                    ed.startEditing(val, ttnv);
                    setCurrentEditor(ed);
                    return ed.getComponent();
                }
            }

            if( val instanceof String ){
                Editor ed = getTextFieldEditor();
                setCurrentEditor(ed);
                ed.startEditing(val, ttnv);
                return ed.getComponent();
            }
        }else if( value instanceof PropertyValue ){
            final PropertyValue propertyValue = (PropertyValue)value;
            final Property prop = propertyValue.getProperty();
            final Class propType = prop!=null ? prop.getPropertyType() : null;
            final Object val = propertyValue.getValue();
            final Throwable err = propertyValue.getError();

            if( err==null && prop!=null && propType!=null ){
                Class propEditorClass = prop.getPropertyEditorClass();

                Editor ed = propertyValue.getEditor();
                if( ed!=null ){
                    // TODO duplicate
                    UiBean uib = prop.getUiBean();
                    if(ed instanceof SetPropertyEditorOpts ){
                        if( uib!=null ){
                            String opts = uib.editorOpts();
                            if( opts!=null ){
                                ((SetPropertyEditorOpts)ed).setPropertyEditorOpts(opts);
                            }else{
                                ((SetPropertyEditorOpts)ed).setPropertyEditorOpts("");
                            }
                        }else{
                            ((SetPropertyEditorOpts)ed).setPropertyEditorOpts("");
                        }
                    }

                    WrappedEditor we = new WrappedEditor(ed){
                        @Override
                        public Object getCellEditorValue() {
                            Object eval = super.getCellEditorValue();
                            propertyValue.setValue(eval);
                            propertyValue.setError(null);
                            return propertyValue;
                        }
                    };
                    we.startEditing(val, propertyValue);
                    setCurrentEditor(we);
                    return we.getComponent();
                }

                //<editor-fold defaultstate="collapsed" desc="editor through pdb">
                ed = getTypeEditors().fetch(propType);
                if( ed!=null ){
                    // TODO duplicate
                    UiBean uib = prop.getUiBean();
                    if(ed instanceof SetPropertyEditorOpts){
                        if( uib!=null ){
                            String opts = uib.editorOpts();
                            if( opts!=null ){
                                ((SetPropertyEditorOpts)ed).setPropertyEditorOpts(opts);
                            }else{
                                ((SetPropertyEditorOpts)ed).setPropertyEditorOpts("");
                            }
                        }else{
                            ((SetPropertyEditorOpts)ed).setPropertyEditorOpts("");
                        }
                    }

                    WrappedEditor we = new WrappedEditor(ed){
                        @Override
                        public Object getCellEditorValue() {
                            Object eval = super.getCellEditorValue();
                            propertyValue.setValue(eval);
                            propertyValue.setError(null);
                            return propertyValue;
                        }
                    };
                    we.startEditing(val, propertyValue);
                    setCurrentEditor(we);
                    return we.getComponent();
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="editor through propEditorClass">
                if( propEditorClass!=null ){
                    try {
                        Object edInst = propEditorClass.newInstance();
                        if( edInst instanceof PropertyEditor ){
                            // TODO duplicate
                            UiBean uib = prop.getUiBean();
                            if(edInst instanceof SetPropertyEditorOpts){
                                if( uib!=null ){
                                    String opts = uib.editorOpts();
                                    if( opts!=null ){
                                        ((SetPropertyEditorOpts)edInst).setPropertyEditorOpts(opts);
                                    }else{
                                        ((SetPropertyEditorOpts)edInst).setPropertyEditorOpts("");
                                    }
                                }else{
                                    ((SetPropertyEditorOpts)edInst).setPropertyEditorOpts("");
                                }
                            }

                            TreeTableWrapEditor ttwe = new TreeTableWrapEditor((PropertyEditor)edInst);
                            WrappedEditor we = new WrappedEditor(ttwe){
                                @Override
                                public Object getCellEditorValue() {
                                    Object eval = super.getCellEditorValue();
                                    propertyValue.setValue(eval);
                                    propertyValue.setError(null);
                                    return propertyValue;
                                }
                            };
                            we.startEditing(val, propertyValue);
                            setCurrentEditor(we);
                            return we.getComponent();
                        }
                    } catch (InstantiationException ex) {
                        Logger.getLogger(TreeTableNodeValueEditor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(TreeTableNodeValueEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //</editor-fold>

                if( propType.isEnum() ){
                    boolean allowNull = true;

                    UiBean ub = prop.getUiBean();
                    if( ub!=null ){
                        if( ub.forceNotNull() ){
                            allowNull = false;
                        }
                    }

                    EnumEditor enEd = new EnumEditor(propType, allowNull);
                    ed = enEd;

                    WrappedEditor we = new WrappedEditor(ed){
                        @Override
                        public Object getCellEditorValue() {
                            Object eval = super.getCellEditorValue();
                            propertyValue.setValue(eval);
                            propertyValue.setError(null);
                            return propertyValue;
                        }
                    };
                    we.startEditing(val, propertyValue);
                    setCurrentEditor(we);
                    return we.getComponent();
                }
            }
        }else if( edFinder!=null ){
            final Editor ed = edFinder.findEditor(table, value, isSelected, row, column);
            if( ed!=null ){
                WrappedEditor we = new WrappedEditor(ed);
                we.startEditing(value, null);
                setCurrentEditor(we);
                return we.getComponent();
            }
        }

        Editor ed = getUnsupportedEditor();
        setCurrentEditor(ed);
        ed.startEditing(null, value);

        return ed.getComponent();
    }

    @Override
    public Object getCellEditorValue() {
        // System.out.println("getCellEditorValue");
        return getCurrentEditor().getCellEditorValue();
    }

    @Override
    public boolean isCellEditable( EventObject anEvent) {
        return getCurrentEditor().isCellEditable();
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        // System.out.println("shouldSelectCell");
        return getCurrentEditor().isShouldSelectCell();
    }

    @Override
    public boolean stopCellEditing() {
        // System.out.println("stopCellEditing");
        boolean res = getCurrentEditor().stopCellEditing();
        fireEditingStopped(this);
        return res;
    }

    @Override
    public void cancelCellEditing() {
        // System.out.println("cancelCellEditing");
        getCurrentEditor().cancelCellEditing();
        fireEditingCanceled(this);
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        if (l== null) {
            throw new IllegalArgumentException("l==null");
        }
        getCurrentEditor().addCellEditorListener(l);
        listenersSupport.addCellEditorListener(l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        if (l== null) {
            throw new IllegalArgumentException("l==null");
        }
        getCurrentEditor().removeCellEditorListener(l);
        listenersSupport.removeCellEditorListener(l);
    }

    public void fireEditingCanceled(Object src){
        //getCurrentEditor().fireEditingCanceled(src);
        listenersSupport.fireEditingCanceled(src);
    }

    public void fireEditingStopped(Object src){
        //getCurrentEditor().fireEditingStopped(src);
        listenersSupport.fireEditingStopped(src);
    }
}
