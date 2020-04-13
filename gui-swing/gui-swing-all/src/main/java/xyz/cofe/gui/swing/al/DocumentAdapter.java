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
package xyz.cofe.gui.swing.al;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import xyz.cofe.gui.swing.SwingListener;

/**
 * Прослушивание изменений текстового документа (JTextComponent...)
 * @author gocha
 * @see SwingListener
 */
public class DocumentAdapter implements DocumentListener
{
    //<editor-fold defaultstate="collapsed" desc="DocumentListener impl">
    @Override
    public void insertUpdate(DocumentEvent e)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
        onTextChanged();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e)
    {
//        e.
//        throw new UnsupportedOperationException("Not supported yet.");
        onTextChanged();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
        onTextChanged();
    }
//</editor-fold>

    /**
     * Вызывается при изменении текста
     */
    protected void onTextChanged(){
    }
    
    //<editor-fold defaultstate="collapsed" desc="listenChanged()">
    public static Closeable listenChanged( JTextComponent cmpt, Supplier<Object> fun ){
        if( cmpt==null )throw new IllegalArgumentException( "cmpt==null" );
        if( fun==null )throw new IllegalArgumentException( "fun==null" );
        return listenChanged(cmpt.getDocument(), fun);
    }
    
    public static Closeable listenChanged( JTextComponent cmpt, final Runnable fun ){
        if( cmpt==null )throw new IllegalArgumentException( "cmpt==null" );
        if( fun==null )throw new IllegalArgumentException( "fun==null" );
        return listenChanged(cmpt.getDocument(), new Supplier<Object>() {
            @Override
            public Object get() {
                fun.run();
                return null;
            }
        });
    }
    
    public static Closeable listenChanged( final Document doc, final Supplier<Object> fun ){
        if( doc==null )throw new IllegalArgumentException( "doc==null" );
        if( fun==null )throw new IllegalArgumentException( "fun==null" );
        
        final DocumentAdapter da = new DocumentAdapter(){
            @Override
            protected void onTextChanged() {
                fun.get();
            }
        };
        
        doc.addDocumentListener(da);
        
        Closeable cl = new Closeable(){
            private DocumentAdapter listener = da;
            private Document document = doc;
            
            @Override
            public void close() throws IOException {
                if( document==null )return;
                if( listener==null )return;
                
                document.removeDocumentListener(listener);
                document = null;
                listener = null;
            }
        };
        
        return cl;
    }
//</editor-fold>
}
