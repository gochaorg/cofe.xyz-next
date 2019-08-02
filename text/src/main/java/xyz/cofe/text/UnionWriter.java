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
package xyz.cofe.text;

//import java.beans.ExceptionListener;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Делает прозрачную запись сразу во внесколько потоков
 * @author gocha
 */
public class UnionWriter extends java.io.Writer
{
    /**
     * Куда записывать
     */
    protected Set<java.io.Writer> writers = new LinkedHashSet<Writer>();

//    /**
//     * Приемник сообщений о ошибке
//     */
//    protected ExceptionListener exListener = null;

    /**
     * Конструктор по умолчанию
     */
    public UnionWriter()
    {
    }

    /**
     * Конструктор
     * @param writers куда записывать
     */
    public UnionWriter(Writer ... writers)
    {
        if( writers==null )return;

        Iterable<Writer> ww = getWriters();
        if( ww==null )return;
        if( !(ww instanceof Collection) )return;
        Collection<Writer> _w = (Collection<Writer>)ww;

        for( Writer w : writers )
        {
            if( w==null )continue;
            _w.add(w);
        }
    }

    /**
     * Конструктор
     * @param writers куда записывать
     */
    public UnionWriter(Iterable<Writer> writers)
    {
        if( writers==null )return;

        Iterable<Writer> ww = getWriters();
        if( ww==null )return;
        if( !(ww instanceof Collection) )return;
        Collection<Writer> _w = (Collection<Writer>)ww;

        for( Writer w : writers )
        {
            if( w==null )continue;
            _w.add(w);
        }
    }

//    /**
//     * Конструктор
//     * @param exceptionListener Приемник ошибок
//     * @param writers куда записывать
//     */
//    public UnionWriter(ExceptionListener exceptionListener, Writer ... writers)
//    {
//        setExceptionListener(exceptionListener);
//        if( writers==null )return;
//
//        Iterable<Writer> ww = getWriters();
//        if( ww==null )return;
//        if( !(ww instanceof Collection) )return;
//        Collection<Writer> _w = (Collection<Writer>)ww;
//
//        for( Writer w : writers )
//        {
//            if( w==null )continue;
//            _w.add(w);
//        }
//    }
//
//    /**
//     * Конструктор
//     * @param exceptionListener Приемник ошибок
//     * @param writers куда записывать
//     */
//    public UnionWriter(ExceptionListener exceptionListener, Iterable<Writer> writers)
//    {
////        setExceptionListener(exceptionListener);
//        if( writers==null )return;
//
//        Iterable<Writer> ww = getWriters();
//        if( ww==null )return;
//        if( !(ww instanceof Collection) )return;
//        Collection<Writer> _w = (Collection<Writer>)ww;
//
//        for( Writer w : writers )
//        {
//            if( w==null )continue;
//            _w.add(w);
//        }
//    }

    /**
     * Указывает куда производиться запись
     * @return куда производиться запись
     */
    public Set<Writer> getWriters() {
        if( writers==null )writers = new LinkedHashSet<Writer>();
        return writers;
    }

    /**
     * Указывает куда производиться запись
     * @param writers куда производиться запись
     */
    public void setWriters(Iterable<Writer> writers) {
        if( this.writers!=null ){
            this.writers.clear();
        }else{
            this.writers = new LinkedHashSet<Writer>();
        }
        if( writers!=null ){
            for( Writer w : writers ){
                if( w!=null )this.writers.add( w );
            }
        }
    }

//    /**
//     * Указывает приемник ошибок
//     * @return приемник ошибок
//     */
//    public ExceptionListener getExceptionListener() {
//        return exListener;
//    }
//
//    /**
//     * Указывает приемник ошибок
//     * @param exListener приемник ошибок
//     */
//    public void setExceptionListener(ExceptionListener exListener) {
//        this.exListener = exListener;
//    }

    /**
     * Перехватывать ошибки и перенаправлять в приемник.
     * По умолчанию true
     * @return true - перехватывать
     */
    protected boolean isCatchException()
    {
        return true;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        Iterable<Writer> _writers = getWriters();
        if( _writers==null )return;

        for( Writer w : _writers )
        {
            if( w==null )continue;
            if( isCatchException() )
            {
                try
                {
                    w.write(cbuf, off, len);
                }
                catch(Exception e)
                {
                    Logger.getLogger(UnionWriter.class.getName()).log(Level.SEVERE,"",e);
                }
            }else{
                w.write(cbuf, off, len);
            }
        }
    }

    @Override
    public void flush() throws IOException {
        Iterable<Writer> _writers = getWriters();
        if( _writers==null )return;

        for( Writer w : _writers )
        {
            if( w==null )continue;
            if( isCatchException() )
            {
                try
                {
                    w.flush();
                }
                catch(Exception e)
                {
                    Logger.getLogger(UnionWriter.class.getName()).log(Level.SEVERE,"",e);
                }
            }else{
                w.flush();
            }
        }
    }

    @Override
    public void close() throws IOException {
        Iterable<Writer> _writers = getWriters();
        if( _writers==null )return;

        for( Writer w : _writers )
        {
            if( w==null )continue;
            if( isCatchException() )
            {
                try
                {
                    w.close();
                }
                catch(Exception e)
                {
                    Logger.getLogger(UnionWriter.class.getName()).log(Level.SEVERE,"",e);
                }
            }else{
                w.close();
            }
        }
    }
}
