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

package xyz.cofe.typeconv;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Взешенный caster, с возможностью установки веса
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public abstract class MutableWeightedCaster
    extends WeightedCaster
    implements
    SetWeight,
    WeightChangeSender
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(MutableWeightedCaster.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(MutableWeightedCaster.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(MutableWeightedCaster.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(MutableWeightedCaster.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(MutableWeightedCaster.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(MutableWeightedCaster.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(MutableWeightedCaster.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public MutableWeightedCaster(){
    }

    public MutableWeightedCaster(double weight){
        super(weight);
    }

    protected List<WeakReference<WeightChangeListener>> weakListeners =
        new ArrayList<WeakReference<WeightChangeListener>>();

    protected List<WeightChangeListener> listeners =
        new ArrayList<WeightChangeListener>();

    protected void fireEventWeakListeners( WeightChangeEvent ev ){
        Set<WeakReference> removeSet = new HashSet<WeakReference>();
//        weakListeners.stream().forEach((wref) -> {
//            WeightChangeListener l = wref.get();
//            if( l==null ){
//                removeSet.add( wref );
//            }else{
//                l.weightChanged(ev);
//            }
//        });
        for( WeakReference<WeightChangeListener> wref : weakListeners ){
            WeightChangeListener l = wref.get();
            if( l==null ){
                removeSet.add( wref );
            }else{
                l.weightChanged(ev);
            }
        }
        weakListeners.removeAll(removeSet);
    }

    protected void fireEventHardListeners( WeightChangeEvent ev ){
//        listeners.stream().filter((l) -> ( l!=null )).forEach((l) -> {
//            l.weightChanged(ev);
//        });

        for( WeightChangeListener l : listeners ){
            if( l!=null )l.weightChanged(ev);
        }
    }

    protected void fireEvent( WeightChangeEvent ev ){
        fireEventHardListeners(ev);
        fireEventWeakListeners(ev);
    }

    protected void fireEvent( Double old, Double newv ){
        fireEvent(new WeightChangeEvent(this, old, newv));
    }

    @Override
    public void setWeight( Double w ){
        Double oldw = this.weight;
        this.weight = w;
        if( oldw==null && w!=null ){
            fireEvent(oldw, w);
        }else if( oldw!=null && w==null ){
            fireEvent(oldw, w);
        }else if( oldw!=null && w!=null && !oldw.equals(w) ){
            fireEvent(oldw, w);
        }
//        if( !Objects.equals(w, oldw) )fireEvent(oldw, w);
    }

    @Override
    public AutoCloseable addWeightChangeListener(WeightChangeListener listener) {
        if( listener==null )return new AutoCloseable() {
            @Override
            public void close() {
            }
        };
        final WeightChangeListener fl = listener;
        final MutableWeightedCaster self = this;
        AutoCloseable ch = new AutoCloseable() {
            WeightChangeListener l = fl;
            MutableWeightedCaster slf = self;
            @Override
            public void close() {
                if( l!=null && slf!=null ){
                    slf.removeWeightChangeListener(l);
                }
                if( l!=null )l = null;
                if( slf!=null )slf = null;
            }
        };
        listeners.add(fl);
        return ch;
    }

    @Override
    public AutoCloseable addWeightChangeListener(WeightChangeListener listener, boolean softLink) {
        if( listener==null )return new AutoCloseable() {
            @Override
            public void close() {
            }
        };
        final WeightChangeListener fl = listener;
        final MutableWeightedCaster self = this;
        AutoCloseable ch = new AutoCloseable() {
            WeightChangeListener l = fl;
            MutableWeightedCaster slf = self;
            @Override
            public void close() {
                if( l!=null && slf!=null ){
                    slf.removeWeightChangeListener(l);
                }
                if( l!=null )l = null;
                if( slf!=null )slf = null;
            }
        };
        if( softLink ){
            this.weakListeners.add( new WeakReference<WeightChangeListener>(listener) );
        }else{
            this.listeners.add(listener);
        }
        return ch;
    }

    @Override
    public void removeWeightChangeListener(WeightChangeListener listener) {
        Set<WeakReference> removeSet = new HashSet<WeakReference>();

//        weakListeners.stream().forEach((wref) -> {
//            WeightChangeListener l = wref.get();
//            if( l==null ){
//                removeSet.add( wref );
//            }
//            if( l==listener ){
//                removeSet.add( wref );
//            }
//        });
//        weakListeners.removeAll(removeSet);

        for( WeakReference<WeightChangeListener> wref : weakListeners ){
            WeightChangeListener l = wref.get();
            if( l==null ){
                removeSet.add( wref );
            }else{
                if( l==listener ){
                    removeSet.add(wref);
                    wref.clear();
                }
            }
        }
        weakListeners.removeAll(removeSet);


        listeners.remove( listener );
    }
}
