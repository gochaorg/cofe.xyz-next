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


import xyz.cofe.collection.EventList;
import xyz.cofe.collection.EventSet;
import xyz.cofe.ecolls.Closeables;

import java.io.Closeable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Кэшируемые данные
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @param <E> ТИп элемента в списке
 */
public class CachedTM<E>
    extends ListTM<E>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(CachedTM.class.getName());
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
    //</editor-fold>

    public CachedTM() {
    }

    //<editor-fold defaultstate="collapsed" desc="source">
    protected Iterable<E> source = null;
    protected final Closeables sourceCloseableSet = new Closeables();

    public Iterable<E> getSource() {
        try {
            this.lock.lock();
            return source;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void setSource(Iterable<E> source) {
        Object old = this.source;

        try {
            this.lock.lock();

            old  = this.source;

            sourceCloseableSet.close();

            this.source = source;
            if( this.source!=null ){
                if( this.source instanceof EventList ){
                    AutoCloseable c = addEventListener((EventList)this.source);
                    if( c!=null ){
                        sourceCloseableSet.add(c);
                    }
                }else if( this.source instanceof EventSet ){
                    AutoCloseable c = addEventListener((EventSet)this.source);
                    if( c!=null ){
                        sourceCloseableSet.add(c);
                    }
                }
            }
        }
        finally {
            this.lock.unlock();
        }

        fetch();

        firePropertyChange("source", old, source);
    }
    //</editor-fold>

    protected void removeFromCache(final E item){
        List l = getList();
        l.remove(item);
    }

    protected void addToCache(final E item){
        List l = getList();
        l.add(item);
    }

    protected AutoCloseable addEventListener( EventSet<E> es ){
        return Closeables.of(
            es.onAdded( this::removeFromCache ),
            es.onRemoved( this::removeFromCache ) );
    }

    protected AutoCloseable addEventListener( EventList<E> el ){
        return el.onChanged( (idx,oldv,curv) -> {
            if( oldv!=null )removeFromCache( oldv );
            if( curv!=null )addToCache( curv );
        } );
    }

    protected boolean contains( Collection<E> col, E obj ){
        if( col==null )throw new IllegalArgumentException( "col==null" );
        return col.contains(obj);
    }

    /**
     * Удаляет содержимое кеша, и читает данные из источника
     */
    public void refresh(){
        Iterable<E> src = getSource();

        List<E> cache = getList();
        cache.clear();

        if( src==null ){
            return;
        }

        boolean skipNulls = true;
        for( E itm : src ){
            if( itm==null ){
                if( skipNulls )continue;
                addToCache(itm);
            }else{
                addToCache(itm);
            }
        }
    }

    /**
     * Обновление кеша,
     * вытягивает данные из источника новые добавляет,
     * отсуствующие удаляет
     */
    public void fetch(){
        Iterable<E> src = getSource();
        List<E> l = getList();

        if( src==null ){
            l.clear();
            return;
        }

        Set<E> srcSet = new LinkedHashSet();
        Set<E> removeSet = new LinkedHashSet();
        Set<E> addSet = new LinkedHashSet();

        for( E i : src ){
            if( i==null )continue;
            srcSet.add(i);
        }

        for( E s : srcSet ){
//            if( !l.contains(s) ){
            if( ! contains(l,s) ){
                addSet.add(s);
            }
        }

        for( E e : l ){
//            if( !srcSet.contains(e) ){
            if( ! contains( srcSet, e ) ){
                removeSet.add(e);
            }
        }

        for( E o : removeSet ){
            removeFromCache(o);
        }

        for( E o : addSet ){
            addToCache(o);
        }
    }
}
