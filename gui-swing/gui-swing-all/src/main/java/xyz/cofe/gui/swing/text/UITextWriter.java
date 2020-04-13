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

package xyz.cofe.gui.swing.text;

import xyz.cofe.fn.Pair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * Пожжержка записи текст в компонент SWING из любого потока/трэда
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class UITextWriter {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(UITextWriter.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(UITextWriter.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(UITextWriter.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(UITextWriter.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(UITextWriter.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    //<editor-fold desc="create / writer">
    /**
     * Создает Writer для записи текста в компонент
     * @param component компонент
     * @param lazyStyle стиль текста
     * @return поток (writer) для записи текста
     */
    public static Writer writer( JTextComponent component, Supplier<AttributeSet> lazyStyle ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        return new UITextWriter().create(component, lazyStyle);
    }

    /**
     * Создает Writer для записи текста в компонент
     * @param component компонент
     * @return поток (writer) для записи текста
     */
    public static Writer writer( JTextComponent component ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        return new UITextWriter().create(component, null);
    }

    /**
     * Создает Writer для записи текста в компонент
     * @param component компонент
     * @param lazyStyle стиль текста
     * @return поток (writer) для записи текста
     */
    public Writer create(
        final JTextComponent component,
        final Supplier<AttributeSet> lazyStyle
    ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
//        if( lazyStyle==null )throw new IllegalArgumentException( "lazyStyle==null" );

        Writer wr = new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                String txt = new String(cbuf, off, len);
                UITextWriter.write(component, txt, lazyStyle);
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
            }
        };

        BufferedWriter bwr = new BufferedWriter(wr, 80*4){
            @Override
            public void newLine() throws IOException {
                synchronized (lock) {
                    super.newLine();
                    super.flush();
                }
            }

            @Override
            public void write(String s, int off, int len) throws IOException {
                synchronized (lock) {
                    super.write(s, off, len);
                    if( s!=null && (s.contains("\n") || s.contains("\r")) ){
                        super.flush();
                    }
                }
            }

            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                synchronized (lock) {
                    super.write(cbuf, off, len);
                    if( cbuf!=null ){
                        for( char c : cbuf ){
                            if( c=='\n' || c=='\r' ){
                                super.flush();
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void write(int c) throws IOException {
                synchronized (lock) {
                    super.write(c);
                    if( c=='\n' || c=='\r' ){
                        super.flush();
                        return;
                    }
                }
            }
        };

        return bwr;
    }
    //</editor-fold>
    //<editor-fold desc="write()">
    /**
     * Пишет текст в текстовый компонент
     * @param out текстовый компонент
     * @param lazyMessage сообщение/текст
     */
    public static void write(
        final JTextComponent out,
        final Supplier<Iterable<Pair<String,AttributeSet>>> lazyMessage
    ) {
        // append text
        Runnable rIns = new Runnable() {
            @Override
            public void run() {
                javax.swing.text.Document doc = out.getDocument();
                int docLen = doc.getLength();
                try {
                    int off = 0;
                    Iterable<Pair<String,AttributeSet>>
                        message = lazyMessage.get();
                    for( Pair<String,AttributeSet> m : message ){
                        String txt = m.a();
                        AttributeSet attr = m.b();

                        if( txt!=null && txt.length()>0 ){
                            doc.insertString(docLen + off, txt, attr);
                            off += txt.length();
                        }
                    }
                } catch( BadLocationException ex ) {
                    Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        if( SwingUtilities.isEventDispatchThread() ){
            rIns.run();
        }else{
            try {
                SwingUtilities.invokeAndWait(rIns);
            } catch (InterruptedException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Пишет текст в текстовый компонент
     * @param out текстовый компонент
     * @param message сообщение/текст
     */
    public static void write(
        final JTextComponent out,
        final Iterable<Pair<String,AttributeSet>> message
    ) {
        // append text
        Runnable rIns = new Runnable() {
            @Override
            public void run() {
                javax.swing.text.Document doc = out.getDocument();
                int docLen = doc.getLength();
                try {
                    int off = 0;
                    for( Pair<String,AttributeSet> m : message ){
                        String txt = m.a();
                        AttributeSet attr = m.b();

                        if( txt!=null && txt.length()>0 ){
                            doc.insertString(docLen + off, txt, attr);
                            off += txt.length();
                        }
                    }
                } catch( BadLocationException ex ) {
                    Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        if( SwingUtilities.isEventDispatchThread() ){
            rIns.run();
        }else{
            try {
                SwingUtilities.invokeAndWait(rIns);
            } catch (InterruptedException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Пишет текст в текстовый компонент
     * @param out текстовый компонент
     * @param message сообщение/текст
     * @param textAttributes стиль текста
     */
    public static void write(
        final JTextComponent out,
        final String message,
        final AttributeSet textAttributes
    ) {
        // append text
        Runnable rIns = new Runnable() {
            @Override
            public void run() {
                javax.swing.text.Document doc = out.getDocument();
                int docLen = doc.getLength();
                try {
                    doc.insertString(docLen, message, textAttributes);
                } catch( BadLocationException ex ) {
                    Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        if( SwingUtilities.isEventDispatchThread() ){
            rIns.run();
        }else{
            try {
                SwingUtilities.invokeAndWait(rIns);
            } catch (InterruptedException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Пишет текст в текстовый компонент
     * @param out текстовый компонент
     * @param message сообщение/текст
     * @param lazyTextAttributes стиль текста
     */
    public static void write(
        final JTextComponent out,
        final String message,
        final Supplier<AttributeSet> lazyTextAttributes
    ){
        Runnable rIns = new Runnable() {
            @Override
            public void run() {
                write(
                    out,
                    message,
                    lazyTextAttributes==null
                        ? null
                        : lazyTextAttributes.get()
                );
            }
        };
        if( SwingUtilities.isEventDispatchThread() ){
            rIns.run();
        }else{
            try {
                SwingUtilities.invokeAndWait(rIns);
            } catch (InterruptedException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Пишет текст в текстовый компонент
     * @param out текстовый компонент
     * @param message сообщение/текст
     */
    public static void write(
        final JTextComponent out,
        final String message
    ){
        Runnable rIns = new Runnable() {
            @Override
            public void run() {
                write(
                    out,
                    message,
                    (Supplier<AttributeSet>)null
                );
            }
        };
        if( SwingUtilities.isEventDispatchThread() ){
            rIns.run();
        }else{
            try {
                SwingUtilities.invokeAndWait(rIns);
            } catch (InterruptedException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(UITextWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    //</editor-fold>
}
