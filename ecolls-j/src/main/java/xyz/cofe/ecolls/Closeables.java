package xyz.cofe.ecolls;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Хранит ссылки на освобождение сылок/реусрсов одим скопом
 */
public class Closeables implements AutoCloseable {
    private final HashSet<Object> links = new LinkedHashSet<>();
    private final WeakHashMap<Object,Object> weaklinks = new WeakHashMap<>();

    //<editor-fold defaultstate="collapsed" desc="getCloseables()">
    /**
     * Получение массива объектов для закрытия
     * @return массив объектов
     */
    public synchronized Object[] getCloseables(){
        ArrayList<Object> cl = new ArrayList<Object>();
        cl.addAll(links);
        cl.addAll(weaklinks.keySet());
        return cl.toArray();
    }
    //</editor-fold>

    //<editor-fold desc="close()">
    @Override
    public void close() {
        closeAll(true);
    }

    /**
     * Вызвать для всех объектов close
     * @param removeAll удалять из списка обрабатываемых
     */
    public synchronized void closeAll(boolean removeAll){
        closeAll0(removeAll);
    }

    /**
     * Вызвать для всех объектов close
     * @param removeAll удалять из списка обрабатываемых
     */
    private synchronized void closeAll0(boolean removeAll){
        for( Object c : getCloseables() ){
            try{
                if( c instanceof Runnable ) ((Runnable)c).run();
                else if( c instanceof AutoCloseable ) ((AutoCloseable)c).close();
                else if( c instanceof Supplier) ((Supplier)c).get();
            } catch( Throwable ex ){
                Logger.getLogger(Closeables.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="add()">
    /**
     * Добавляет объект в список обрабатываемых
     * @param closeable объект
     * @return интерфейс удяления из списка обрабатываемых
     */
    public synchronized AutoCloseable add( Runnable closeable ){
        return add0( closeable, false );
    }

    /**
     * Добавляет объект в список обрабатываемых
     * @param closeable объект
     * @return интерфейс удяления из списка обрабатываемых
     */
    public synchronized AutoCloseable add( AutoCloseable closeable ){
        return add0( closeable, false );
    }

    /**
     * Добавляет объекты в список обрабатываемых
     * @param closeables объекты
     * @return интерфейс удяления из списка обрабатываемых
     */
    public synchronized AutoCloseable addAll( Iterable<AutoCloseable> closeables ){
        WeakHashMap<Object,Integer> removeSet = new WeakHashMap<>();
        if( closeables!=null ){
            Integer i = 0;
            for( AutoCloseable c : closeables ){
                if( c!=null ){
                    AutoCloseable cl = add(c);
                    if( cl!=null ){
                        removeSet.put(cl, i);
                        i++;
                    }
                }
            }
        }
        return ()->{
            for(Object ref : removeSet.keySet()){
                if( ref!=null ){
                    remove(ref);
                }
            }
        };
    }

    private static final AutoCloseable dummy = () -> {
    };

    /**
     * Добавляет объект в список обрабатываемых
     * @param closeable объект
     * @param weak true - добавить как weak ссылку / false - как hard
     * @return интерфейс удяления из списка обрабатываемых
     */
    protected synchronized AutoCloseable add0( Object closeable, boolean weak ){
        if( closeable==null ){ return dummy; }

        if( weak ){
            weaklinks.put(closeable, true);
        }else{
            links.add(closeable);
        }

        final WeakReference<Object> cl = new WeakReference<>(closeable);
        return () -> {
            Object c = cl.get();
            if (c != null) {
                Closeables.this.remove(c);
                cl.clear();
            }
        };
    }

    public Closeables append( Runnable ... run ){
        if( run==null )return this;
        for( Runnable r : run ) {
            if( r!=null )add(r);
        }
        return this;
    }

    public Closeables append( AutoCloseable ... run ){
        if( run==null )return this;
        for( AutoCloseable r : run ) {
            if( r!=null )add(r);
        }
        return this;
    }

    public Closeables appendRuns( Iterable<Runnable> run ){
        if( run==null )return this;
        for( Runnable r : run ){
            add(r);
        }
        return this;
    }

    public Closeables appendClose( Iterable<AutoCloseable> run ){
        if( run==null )return this;
        for( AutoCloseable r : run ){
            add(r);
        }
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="remove()">
    /**
     * Удалить объект из списка обрабатываемых
     * @param closeable объект
     */
    public synchronized void remove( Object closeable ){
        remove0(closeable);
    }

    /**
     * Удалить объект из списка обрабатываемых
     * @param closeable объект
     */
    protected synchronized void remove0( Object closeable ){
        if (closeable != null) {
            links.remove(closeable);
            weaklinks.remove(closeable);
        }
    }
    //</editor-fold>
}
