/*
 * The MIT License
 *
 * Copyright 2018 user.
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

package xyz.cofe.gui.swing.properties.editor;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import xyz.cofe.gui.swing.SwingListener;
import xyz.cofe.gui.swing.bean.UiBean;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;
import xyz.cofe.gui.swing.properties.SetPropertyEditorOpts;
import xyz.cofe.text.lex.LexerUtil;
import xyz.cofe.text.lex.ListLexer;
import xyz.cofe.text.lex.Token;
import xyz.cofe.text.lex.Identifier;
import xyz.cofe.text.lex.IdentifierParser;
import xyz.cofe.text.lex.Keyword;
import xyz.cofe.text.lex.KeywordsParser;
import xyz.cofe.text.lex.NumberConst;
import xyz.cofe.text.lex.NumberConstParser;
import xyz.cofe.text.lex.TextConst;
import xyz.cofe.text.lex.TextConstParser;
import xyz.cofe.text.lex.WhiteSpace;
import xyz.cofe.text.lex.WhiteSpaceParser;

/**
 * Редактор для свойства типа File.
 *
 * <p>
 * Поддерживаются опции редактирования, заданные через строку,
 * пример: <br>
 * <code>type=save;mode=files;title=\"dialog title\"</code>
 * Опции:
 * <ul>
 * <li>type = save | open - диалог сохранения или открытия
 * <li>mode = files | dirs | files+dirs - открывать только файлы / каталоги / файлы и каталоги
 * <li>title = "заголовок" - заголовок диалога.
 * </ul>
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @see UiBean#editorOpts()
 */
public class FileEditor
    extends CustomEditor
    implements
    PropertyDBService
    , SetPropertyEditorOpts
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(FileEditor.class.getName());
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
        logger.entering(FileEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(FileEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(FileEditor.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public FileEditor() {
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public FileEditor(FileEditor sample) {
    }

    @Override
    public FileEditor clone() {
        return new FileEditor(this);
    }

    private JPanel panel;
    private JTextComponent fileTextField;
    private JLabel nullButton;
    private JLabel dlgButton;

    //<editor-fold defaultstate="collapsed" desc="nullSelected : boolean">
    public void setNullSelected( boolean selected ){
        boolean old = isNullSelected();
        if( nullButton!=null ){
            Icon icn1 = getNullSelectedIcon();
            Icon icn2 = getNullUnSelectedIcon();
            nullButton.setIcon(selected ? icn1 : icn2);
        }
        boolean cur = isNullSelected();
        firePropertyChanged("nullSelected", old, cur);
    }
    public boolean isNullSelected(){
        if( nullButton==null )return false;
        Icon icn = nullButton.getIcon();
        return icn == getNullSelectedIcon();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="nullable : boolean">
    public void setNullable(boolean nullable){
        if( nullButton==null )return;

        boolean old = isNullable();

        nullButton.setVisible(nullable);
        if( panel!=null ){
            panel.revalidate();
            panel.invalidate();
            panel.repaint();
        }

        boolean cur = isNullable();

        if( Objects.equals(old, cur) )firePropertyChanged("nullable", old, cur);
    }
    public boolean isNullable(){
        if( nullButton==null )return false;
        return nullButton.isVisible();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="defaultFile : Object">
    protected Object defaultFile;

    public Object getDefaultFile() {
        return defaultFile;
    }

    public void setDefaultFile(Object defaultFile) {
        this.defaultFile = defaultFile;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createComponent()">
    @Override
    protected JComponent createComponent() {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = null;

        fileTextField = new JTextField();
        fileTextField.setBorder(new EmptyBorder(0, 0, 0, 0));

        SwingListener.onTextChanged(fileTextField, new Runnable() {
            @Override
            public void run() {
                setNullSelected(false);
            }
        });

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add( fileTextField, gbc );

        nullButton = new JLabel(getNullIcon());
        nullButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        nullButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        SwingListener.onMouseClicked(nullButton, me -> {
            if( me.getButton()==MouseEvent.BUTTON1 ){
                setNullSelected(!isNullSelected());
            }
        });

        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 3, 0, 0);
        panel.add( nullButton, gbc );

        dlgButton = new JLabel(getEditIcon());
        dlgButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        dlgButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        SwingListener.onMouseClicked(dlgButton, obj -> {
            JTextComponent txtcmpt = fileTextField;
            if( txtcmpt==null )return;

            String newtxt = openFileDialog(txtcmpt.getText());
            if( newtxt!=null ){
                txtcmpt.setText(newtxt);
                setNullSelected(false);
                fireEditingStopped(FileEditor.this);
            }
        });

        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 3, 0, 0);
        panel.add( dlgButton, gbc );

        panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        return panel;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fileSelectionMode">
    public static enum FileSelectionMode {
        FilesOnly,
        DirsOnly,
        FilesAndDirs;

        public int value(){
            switch(this){
                case DirsOnly: return JFileChooser.DIRECTORIES_ONLY;
                case FilesOnly: return JFileChooser.FILES_ONLY;
                case FilesAndDirs: return JFileChooser.FILES_AND_DIRECTORIES;
            }
            return JFileChooser.FILES_AND_DIRECTORIES;
        }
    }

    protected FileSelectionMode fileSelectionMode = FileSelectionMode.FilesAndDirs;

    public FileSelectionMode getFileSelectionMode() {
        if( fileSelectionMode==null ){
            fileSelectionMode = FileSelectionMode.FilesAndDirs;
        }
        return fileSelectionMode;
    }

    public void setFileSelectionMode(FileSelectionMode fileSelectionMode) {
        this.fileSelectionMode = fileSelectionMode;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="dialogType">
    public static enum DialogType {
        Open,
        Save;
        public int value(){
            switch(this){
                case Open: return JFileChooser.OPEN_DIALOG;
                case Save: return JFileChooser.SAVE_DIALOG;
            }
            return JFileChooser.SAVE_DIALOG;
        }
    }

    protected DialogType dialogType = DialogType.Open;

    public DialogType getDialogType() {
        if( dialogType==null ){
            dialogType = DialogType.Open;
        }
        return dialogType;
    }

    public void setDialogType(DialogType dialogType) {
        this.dialogType = dialogType;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="saveLabel : String">
    private String saveLabel;

    public String getSaveLabel() {
        if( saveLabel==null )return "Save";
        return saveLabel;
    }

    public void setSaveLabel(String saveLabel) {
        this.saveLabel = saveLabel;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="openLabel : String">
    private String openLabel;

    public String getOpenLabel() {
        if( openLabel==null )return "Open";
        return openLabel;
    }

    public void setOpenLabel(String openLabel) {
        this.openLabel = openLabel;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="title : String">
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String str) {
        this.title = str;
    }
    //</editor-fold>

    /**
     * Парсинг параметров редактирования
     * @param opts страка опций
     * @return опции
     */
    protected Map<String,Object> parseOptions(String opts){
        LinkedHashMap<String,Object> hm = new LinkedHashMap<>();
        if( opts!=null ){
            ListLexer llexer = new ListLexer();
            llexer.getParsers().add(new KeywordsParser(
                true,
                "=",";",
                "save","open",
                "files", "dirs", "files+dirs", "dirs+files"
            ));
            llexer.getParsers().add(new IdentifierParser());
            llexer.getParsers().add(new TextConstParser());
            llexer.getParsers().add(new NumberConstParser());
            llexer.getParsers().add(new WhiteSpaceParser("ws"));
            List<Token> toks = llexer.parse(opts);
            toks = LexerUtil.filter(toks, WhiteSpace.class);

            Identifier prop = null;
            int state = 0;
            for( Token tok : toks ){
                switch( state ){
                    case 0:
                        if( tok instanceof Identifier ){
                            Identifier id = (Identifier)tok;
                            prop = id;
                            state = 1;
                        }
                        break;
                    case 1:
                        if( tok instanceof Keyword && ((Keyword)tok).getKeyword().equals("=") ){
                            state = 2;
                        }else if( tok instanceof Keyword && ((Keyword)tok).getKeyword().equals(";") ){
                            state = 0;
                        }else{
                            state = -1;
                        }
                        break;
                    case 2:
                        if( tok instanceof Keyword ){
                            Keyword kw = (Keyword)tok;
                            if( kw.getKeyword().equals(";") ){
                                state = 0;
                            }else if( prop!=null ){
                                hm.put(prop.getMatchedText(), kw.getKeyword());
                                state = 5;
                            }
                        }else if( tok instanceof NumberConst ){
                            if( prop!=null ){
                                hm.put(prop.getMatchedText(), ((NumberConst)tok).getNumber() );
                                state = 5;
                            }
                        }else if( tok instanceof TextConst ){
                            if( prop!=null ){
                                hm.put(prop.getMatchedText(), ((TextConst)tok).getDecodedText());
                                state = 5;
                            }
                        }
                        break;
                    case 5:
                        if( tok instanceof Keyword && ((Keyword)tok).getKeyword().equals(";") ){
                            state = 0;
                        }else{
                            state = -1;
                        }
                        break;
                    case -1:
                        if( tok instanceof Keyword && ((Keyword)tok).getKeyword().equals(";") ){
                            state = 0;
                        }
                        break;
                }
            }
        }

        return hm;
    }

    @Override
    public void setPropertyEditorOpts( String opts ){
        parseEditOptions(opts);
    }

    //@Override
    @Override
    protected void parseEditOptions(String opts) {
        fileSelectionMode = FileSelectionMode.FilesAndDirs;
        dialogType = DialogType.Open;
        title = null;

        if( opts!=null ){
            Map hm = parseOptions(opts);

            if( hm.containsKey("type") ){
                setDialogType( "save".equalsIgnoreCase(hm.get("type").toString()) ?
                    DialogType.Save : DialogType.Open
                );
            }

            if( hm.containsKey("mode") ){
                String mode = hm.get("mode").toString().toLowerCase();
                switch(mode){
                    case "files": setFileSelectionMode(FileSelectionMode.FilesOnly); break;
                    case "dirs": setFileSelectionMode(FileSelectionMode.DirsOnly); break;
                    case "files+dirs": setFileSelectionMode(FileSelectionMode.FilesAndDirs); break;
                    case "dirs+files": setFileSelectionMode(FileSelectionMode.FilesAndDirs); break;
                }
            }

            if( hm.containsKey("title") ){
                setTitle(hm.get("title").toString());
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="openFileDialog(String):String">
    protected String openFileDialog( String currentFilename ){
        JFileChooser fc = new JFileChooser();

        java.io.File currentFile = currentFilename!=null
            ? new java.io.File(currentFilename)
            : null;

        java.io.File curDir = currentFile!=null
            ? currentFile.getParentFile()
            : new java.io.File(".").getAbsoluteFile();

        fc.setCurrentDirectory(curDir);

        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(getFileSelectionMode().value());
        fc.setDialogType(getDialogType().value());

        if( title!=null )fc.setDialogTitle(title);

        int dlgres = -1;
        switch(getDialogType()){
            case Open:
                dlgres = fc.showDialog(panel, getOpenLabel());
                break;
            case Save:
                dlgres = fc.showDialog(panel, getSaveLabel());
                break;
        }

        if( dlgres==JFileChooser.APPROVE_OPTION ){
            java.io.File selfile = fc.getSelectedFile();
            return selfile!=null ? selfile.toString() : null;
        }

        return null;
    }
    //</editor-fold>

    protected Object createFileFromString(String text){
        return new java.io.File(text);
    }

    //<editor-fold defaultstate="collapsed" desc="value : Object">
    @Override
    public void setValue(Object value) {
        if( fileTextField==null )return;

        if( value instanceof java.io.File ){
            fileTextField.setText(value.toString());
            setNullSelected(false);
        }else if( value instanceof xyz.cofe.io.fs.File ){
            fileTextField.setText(value.toString());
            setNullSelected(false);
        }else if( value instanceof java.nio.file.Path ){
            fileTextField.setText(value.toString());
            setNullSelected(false);
        }else{
            fileTextField.setText("");
            setNullSelected(true);
        }
    }

    @Override
    public Object getValue() {
        if( isNullable() && isNullSelected() )return null;
        if( !isNullable() &&
            (isNullSelected() || (fileTextField!=null && fileTextField.getText().length()==0))
        ){
            Object deffile = getDefaultFile();
            if( deffile instanceof String ){
                return createFileFromString(deffile.toString());
            }else if(
                deffile instanceof java.io.File ||
                    deffile instanceof java.nio.file.Path ||
                    deffile instanceof xyz.cofe.io.fs.File
            ){
                return deffile;
            }
        }

        JTextComponent txtcmpt = fileTextField;
        if( txtcmpt!=null ){
            String txt = txtcmpt.getText();
            return createFileFromString(txt);
        }

        return null;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="getJavaInitializationString()">
    @Override
    public String getJavaInitializationString() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return null;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="asText : String">
    @Override
    public String getAsText() {
        JTextComponent txt = fileTextField;
        return txt!=null ? txt.getText() : "";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        JTextComponent txt = fileTextField;
        if( txt!=null )txt.setText(text!=null ? text : "");
    }
    //</editor-fold>

    @Override
    public void register(PropertyDB pdb) {
        if( pdb==null )return;

        FileEditor fed1 = new FileEditor();
        pdb.registerTypeEditor(java.io.File.class, fed1);

        FileEditor fed2 = new FileEditor(){
            @Override
            protected Object createFileFromString(String text) {
                return java.nio.file.Paths.get(text);
            }
        };
        pdb.registerTypeEditor(java.nio.file.Path.class,fed2);

        FileEditor fed3 = new FileEditor(){
            @Override
            protected Object createFileFromString(String text) {
                return new xyz.cofe.io.fs.File(text);
            }
        };
        pdb.registerTypeEditor(xyz.cofe.io.fs.File.class,fed3);
    }
}
