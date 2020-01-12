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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import xyz.cofe.gui.swing.SwingListener;
import xyz.cofe.gui.swing.cell.CellFormat;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;
import xyz.cofe.gui.swing.text.ValidatedTextField;

/**
 * Редактор pattern-ов для бордюров
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @see CellFormat
 */
public class PatternEditor extends TextFieldEditor implements PropertyDBService
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PatternEditor.class.getName());
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
        logger.entering(PatternEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(PatternEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(PatternEditor.class.getName(), method, result);
    }
    //</editor-fold>

    public PatternEditor(){
        super(true);
    }

    public PatternEditor(boolean allowNull) {
        super(allowNull);
    }

    public PatternEditor(TextFieldEditor sample) {
        super(sample);
    }

    @Override
    public PatternEditor clone(){
        return new PatternEditor(this);
    }

    @Override
    public void register(PropertyDB pdb) {
        if( pdb==null )return;

        PatternEditor pe = new PatternEditor();
        pe.setExternalEditor(getPatternExternalEditor());
        pdb.registerTypeEditor(Pattern.class, pe);
    }

    @Override
    protected JTextComponent getTextField() {
        if( textField!=null )return textField;

        final ValidatedTextField tf = new ValidatedTextField();

        tf.setFilter((String txt) -> {
            try{
                if( isAllowNull() && (txt==null || txt.length()==0) )return true;

                Pattern ptrn = Pattern.compile(txt);
                return true;
            }catch(PatternSyntaxException ex){
                String msg = ex.getDescription();
                msg = msg != null ? msg : ex.getLocalizedMessage();
                msg = msg != null ? msg : ex.getMessage();
                msg = msg != null ? msg : ex.getClass().getName();
                tf.setBalloonText("format exception: "+ex.getLocalizedMessage());
            }
            return false;
        });
        tf.setPlaceholder("Enter regex");
        tf.setBalloonText("");
        tf.setBorder(new EmptyBorder(0, 0, 0, 0));

        textField = tf;
        return textField;
    }

    @Override
    protected Object getTextFieldValue() {
        Object otxt = super.getTextFieldValue();
        if( otxt!=null ){
            String txt = otxt.toString();
            try{
                Pattern ptrn = Pattern.compile(txt);
                return ptrn;
            }catch( Throwable err ){
                logException(err);
                return null;
            }
        }

        return super.getTextFieldValue();
    }

    @Override
    protected void setTextFieldValue(Object value) {
        if( value instanceof Pattern ){
            Pattern ptrn = (Pattern)value;
            super.setTextFieldValue(ptrn.pattern());
            return;
        }
        super.setTextFieldValue(value);
    }

    public static class PatternExternalDlg extends JDialog {
        public PatternExternalDlg() {
            initUI();
        }

        protected JTextArea regexTextArea;
        protected JTextArea inputTextArea;
        protected JTextArea matchTextArea;
        protected JButton testMatchButton;
        protected JButton okButton;
        protected ExternalEditorConsumer consumer;
        protected ExternalEditor externalEditor;

        protected void initUI(){
            getContentPane().setLayout(new BorderLayout());

            regexTextArea = new JTextArea();
            regexTextArea.setText("regex");

            inputTextArea = new JTextArea();
            inputTextArea.setText("input text");

            matchTextArea = new JTextArea();
            matchTextArea.setText("match result");

            JPanel matchPln = new JPanel();

            JPanel matchBtns = new JPanel();
            matchBtns.setLayout(new FlowLayout());

            testMatchButton = new JButton("match");
            matchBtns.add(testMatchButton);

            okButton = new JButton("ok");
            matchBtns.add(okButton);

            matchPln.setLayout(new BorderLayout());
            matchPln.add( matchBtns, BorderLayout.SOUTH );

            matchPln.add( new JScrollPane(matchTextArea), BorderLayout.CENTER );

            JSplitPane split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(regexTextArea),
                new JScrollPane(inputTextArea));

            JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, split1, matchPln);
            getContentPane().add(split2, BorderLayout.CENTER);

            setMinimumSize(new Dimension(400, 200));
            pack();

            split1.setDividerLocation(0.5);
            split1.setResizeWeight(0.5);

            split2.setDividerLocation(0.5);
            split2.setResizeWeight(0.5);

            SwingListener.onActionPerformed(okButton, e -> okClicked());
            SwingListener.onActionPerformed(testMatchButton, e -> matchClicked());

            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setTitle("Regex pattern editor");
        }

        protected void okClicked(){
            if( consumer!=null && externalEditor!=null ){
                consumer.updated(externalEditor, externalEditor.getValue());
            }

            setVisible(false);

            if( consumer!=null && externalEditor!=null ){
                consumer.closed(externalEditor);
            }
        }
        protected void matchClicked(){
            Pattern ptrn=null;
            try{
                ptrn = Pattern.compile(getPatternText());
                Matcher m = ptrn.matcher( inputTextArea.getText() );
                if( m.matches() ){
                    StringBuilder sb = new StringBuilder();
                    sb.append("matches() = true\n");

                    int gc = m.groupCount();
                    for( int gi=1; gi<=gc; gi++ ){
                        sb.append("group ").append(gi).append(": ").append(m.group(gi)).append("\n");
                    }

                    matchTextArea.setText(sb.toString());
                }else{
                    matchTextArea.setText("matches() = false");
                }
            }catch( Throwable err ){
                matchTextArea.setText(err.toString());
            }
        }

        public void setPatternText(String pattern){
            regexTextArea.setText(pattern!=null ? pattern : "");
        }
        public String getPatternText(){
            return regexTextArea.getText();
        }

        public ExternalEditorConsumer getConsumer() { return consumer; }
        public void setConsumer(ExternalEditorConsumer consumer) { this.consumer = consumer; }

        public ExternalEditor getExternalEditor() { return externalEditor; }
        public void setExternalEditor(ExternalEditor externalEditor) { this.externalEditor = externalEditor; }
    }

    protected ExternalEditor patternExternalEditor;
    public ExternalEditor getPatternExternalEditor(){
        if( patternExternalEditor!=null )return patternExternalEditor;
        patternExternalEditor = new ExternalEditor() {
            private Component cmpt;

            @Override
            public void setContextComponent(Component contextComponent) {
                this.cmpt = contextComponent;
            }

            @Override
            public Component getContextComponent() {
                return this.cmpt;
            }

            private ExternalEditorConsumer consum;

            @Override
            public void setConsumer(ExternalEditorConsumer consumer) {
                consum = consumer;
            }

            @Override
            public ExternalEditorConsumer getConsumer() {
                return consum;
            }

            private WeakReference<PatternExternalDlg> dlg;

            @Override
            public void open(Object value) {
                close();

                PatternExternalDlg pdlg = new PatternExternalDlg();
                dlg = new WeakReference<>( pdlg );

                if( value instanceof Pattern ){
                    pdlg.setPatternText(((Pattern)value).pattern());
                }else{
                    pdlg.setPatternText("");
                }

                pdlg.setConsumer(consum);
                pdlg.setExternalEditor(this);

                if( cmpt!=null ){
                    pdlg.setLocationRelativeTo(cmpt);
                }

                pdlg.setVisible(true);
            }

            @Override
            public boolean isOpen() {
                if( dlg==null )return false;

                PatternExternalDlg d = dlg.get();
                if( d==null )return false;

                return d.isVisible();
            }

            @Override
            public void close() {
                if( dlg==null )return;

                PatternExternalDlg d = dlg.get();
                if( d==null )return;

                d.setVisible(false);
            }

            @Override
            public void setValue(Object value) {
                if( dlg==null )return;

                PatternExternalDlg d = dlg.get();
                if( d==null )return;

                if( value instanceof Pattern ){
                    Pattern ptn = (Pattern)value;
                    d.setPatternText(ptn.pattern());
                }else{
                    d.setPatternText("");
                }
            }

            @Override
            public Object getValue() {
                if( dlg==null )return null;

                PatternExternalDlg d = dlg.get();
                if( d==null )return null;

                String ptrnText = d.getPatternText();
                if( ptrnText==null )return null;

                try{
                    Pattern ptrn = Pattern.compile(ptrnText);
                    return ptrn;
                }catch( Throwable err ){
                    return null;
                }
            }
        };
        return patternExternalEditor;
    }
}
