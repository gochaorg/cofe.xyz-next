/*
 * The MIT License
 *
 * Copyright 2017 user.
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
package xyz.cofe.collection;

import xyz.cofe.ecolls.Pair;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Карта биективного отображения.
 * <p>
 * Каждый элемент x из X сопоставляется только один элемент y из Y, <br>
 * и соответсвенно есть обратное отображение:
 * Каждый элемент x из X сопоставляется только один элемент y из Y.
 * <table border='1' summary='Пример биекции'>
 * <tr><td>X множество</td> <td>&larr; &rarr;</td> <td>Y множество</td></tr>
 * <tr><td>x<sub>1</sub></td> <td>&larr; &rarr;</td> <td>y<sub>a</sub></td></tr>
 * <tr><td>x<sub>2</sub></td> <td>&larr; &rarr;</td> <td>y<sub>b</sub></td></tr>
 * <tr><td>x<sub>3</sub></td> <td>&larr; &rarr;</td> <td>y<sub>c</sub></td></tr>
 * <tr><td>x<sub>4</sub></td> <td>&larr; &rarr;</td> <td>y<sub>d</sub></td></tr>
 * </table>
 * @param <X> Тип значений x
 * @param <Y> Тип значений y
 */
public class Bijection<X extends Comparable,Y extends Comparable>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Bijection.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Bijection.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(Bijection.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Bijection.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Bijection.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Bijection.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Bijection.class.getName()).log(Level.SEVERE, null, ex);
    }

    private static Level logLevel(){
        Level level = Logger.getLogger(Bijection.class.getName()).getLevel();
        return level;
    }
    //</editor-fold>

    protected final Object sync;

    public Bijection(){
        sync = this;
        xToYmap = new TreeMap<>();
        yToXmap = new TreeMap<>();
    }
    public Bijection(Object sync){
        this.sync = sync == null ? this : sync;
        xToYmap = new TreeMap<>();
        yToXmap = new TreeMap<>();
    }

    public Bijection(Bijection sample, Object sync){
        this.sync = sync == null ? this : sync;
        xToYmap = new TreeMap<>();
        yToXmap = new TreeMap<>();
        if( sample!=null ){
            assign(sample);
        }
    }

    protected final TreeMap<X,Y> xToYmap;
    protected final TreeMap<Y,X> yToXmap;

    //<editor-fold defaultstate="collapsed" desc="getEntries() : Set<Pair<X,Y>>">
    public Set<Pair<X,Y>> getEntries(){
        synchronized(sync){
            Set<Pair<X,Y>> set = new LinkedHashSet<>();
            for( Map.Entry<X,Y> en : xToYmap.entrySet() ){
                set.add(Pair.of(en.getKey(), en.getValue()));
            }
            return set;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="eachXY()">
    public void eachXY( BiConsumer<X,Y> reciver ){
        if( reciver==null )throw new IllegalArgumentException("reciver == null");
        synchronized(sync){
            for( Map.Entry<X,Y> en : xToYmap.entrySet() ){
                reciver.accept(en.getKey(), en.getValue());
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setXY()">
    public LinkedHashSet<Pair<X,Y>> setXY( X x, Y y ){
        if( x==null )throw new IllegalArgumentException("x == null");
        if( y==null )throw new IllegalArgumentException("y == null");
        synchronized(sync){
            LinkedHashSet<Pair<X,Y>> res = new LinkedHashSet();

            if( xToYmap.containsKey(x) ){
                Y ey = xToYmap.get(x);
                if( ey==null ){
                    throw new IllegalStateException(
                            "internal error: return y=null for xToYmap(x="+x+")"
                    );
                }

                X ex = yToXmap.get(ey);
                if( ex==null ){
                    throw new IllegalStateException(
                            "internal error: return x=null for yToXmap(x="+ey+")"
                    );
                }

                int cmp_x_ex = x.compareTo(ex);
                if( cmp_x_ex!=0 ){
                    throw new IllegalStateException(
                            "internal error: x!=x x("+x+")->y("+ey+"),y->x("+ex+")"
                    );
                }

                Pair<X,Y> ep = Pair.of( x, ey );
                res.add(ep);

                xToYmap.remove(x);
                yToXmap.remove(ey);
            }

            if( yToXmap.containsKey(y) ){
                X ex = yToXmap.get(y);
                if( ex==null ){
                    throw new IllegalStateException(
                            "internal error: return x=null for yToXmap(y="+y+")"
                    );
                }

                Y ey = xToYmap.get(ex);
                if( ey==null ){
                    throw new IllegalStateException(
                            "internal error: return y=null for xToYmap(x="+ex+")"
                    );
                }

                int cmp_y_ey = y.compareTo(ey);
                if( cmp_y_ey!=0 ){
                    throw new IllegalStateException(
                            "internal error: y!=y x("+ex+")->y("+y+"),y("+ey+")->x("+ex+")"
                    );
                }

                Pair<X,Y> ep = Pair.of( ex, y );
                res.add(ep);

                xToYmap.remove(ex);
                yToXmap.remove(y);
            }

            xToYmap.put(x, y);
            yToXmap.put(y, x);
            return res;
        }
    }

    public LinkedHashSet<Pair<X,Y>> setXY( Iterable<Pair<X,Y>> itr ){
        if( itr==null )throw new IllegalArgumentException("itr == null");
        synchronized(sync){
            LinkedHashSet<Pair<X,Y>> res = new LinkedHashSet<>();
            for( Pair<X,Y> p : itr ){
                if( p==null )continue;
                X x = p.a();
                Y y = p.b();
                if( x!=null && y!=null ){
                    Collection<Pair<X,Y>> r = setXY(x, y);
                    if( r!=null ){
                        res.addAll(r);
                    }
                }
            }
            return res;
        }
    }

    public LinkedHashSet<Pair<X,Y>> setXYMapEntries( Iterable<Map.Entry<X,Y>> itr ){
        if( itr==null )throw new IllegalArgumentException("itr == null");
        synchronized(sync){
            LinkedHashSet<Pair<X,Y>> res = new LinkedHashSet<>();
            for( Map.Entry<X,Y> p : itr ){
                if( p==null )continue;
                X x = p.getKey();
                Y y = p.getValue();
                if( x!=null && y!=null ){
                    Collection<Pair<X,Y>> r = setXY(x, y);
                    if( r!=null ){
                        res.addAll(r);
                    }
                }
            }
            return res;
        }
    }

    public LinkedHashSet<Pair<X,Y>> setXYMap( Map<X,Y> itr ){
        if( itr==null )throw new IllegalArgumentException("itr == null");
        return setXYMapEntries(itr.entrySet());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeX()">
    public LinkedHashSet<Pair<X,Y>> removeX( X x ){
        if( x==null )return null;
        synchronized(sync){
            LinkedHashSet<Pair<X,Y>> res = new LinkedHashSet<>();
            if( !xToYmap.containsKey(x) )return res;

            Y y = xToYmap.get(x);
            res.add( Pair.of(x,y) );

            xToYmap.remove(x);
            yToXmap.remove(y);
            return res;
        }
    }

    public LinkedHashSet<Pair<X,Y>> removeX( Iterable<X> itr ){
        if( itr==null )return new LinkedHashSet<>();
        synchronized(sync){
            LinkedHashSet<Pair<X,Y>> set = new LinkedHashSet<>();
            for( X x : itr ){
                Collection<Pair<X,Y>> p = removeX(x);
                if( p!=null )set.addAll( p );
            }
            return set;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeY()">
    public LinkedHashSet<Pair<X,Y>> removeY( Y y ){
        if( y==null )return null;
        synchronized(sync){
            LinkedHashSet<Pair<X,Y>> set = new LinkedHashSet<>();
            if( !yToXmap.containsKey(y) )return set;

            X x = yToXmap.get(y);
            set.add(Pair.of(x,y));

            xToYmap.remove(x);
            yToXmap.remove(y);

            return set;
        }
    }

    public LinkedHashSet<Pair<X,Y>> removeY( Iterable<Y> itr ){
        if( itr==null )return new LinkedHashSet<>();
        synchronized(sync){
            LinkedHashSet<Pair<X,Y>> set = new LinkedHashSet<>();
            for( Y y : itr ){
                Collection<Pair<X,Y>> p = removeY(y);
                if( p!=null )set.addAll( p );
            }
            return set;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="size() : int">
    public int size(){
        synchronized(sync){
            return xToYmap.size();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="clear()">
    public void clear(){
        synchronized(sync){
            xToYmap.clear();
            yToXmap.clear();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hasX() / hasY()">
    //<editor-fold defaultstate="collapsed" desc="hasX()">
    public boolean hasX( X x ){
        synchronized( sync ){
            return xToYmap.containsKey(x);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hasY()">
    public boolean hasY( Y y ){
        synchronized( sync ){
            return yToXmap.containsKey(y);
        }
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getXSet() / getYSet()">
    //<editor-fold defaultstate="collapsed" desc="getXSet()">
    public NavigableSet<X> getXSet(){
        synchronized( sync ){
            NavigableSet<X> s = new TreeSet<>(xToYmap.keySet());
            return s;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getYSet()">
    public NavigableSet<Y> getYSet(){
        synchronized( sync ){
            NavigableSet<Y> s = new TreeSet<>(yToXmap.keySet());
            return s;
        }
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getXMin() / getXMax()">
    public X getXMin(){
        synchronized(sync){
            if( xToYmap.isEmpty() )return null;
            return xToYmap.firstKey();
        }
    }

    public X getXMax(){
        synchronized(sync){
            if( xToYmap.isEmpty() )return null;
            return xToYmap.lastKey();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getYMin() / getYMax()">
    public Y getYMin(){
        synchronized(sync){
            if( yToXmap.isEmpty() )return null;
            return yToXmap.firstKey();
        }
    }

    public Y getYMax(){
        synchronized(sync){
            if( yToXmap.isEmpty() )return null;
            return yToXmap.lastKey();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getXbyY() getYbyX()">
    public Y getYbyX( X x ){
        synchronized(sync){
            return xToYmap.get(x);
        }
    }
    public Y x( X x ){
        return getYbyX(x);
    }

    public X getXbyY( Y y ){
        synchronized(sync){
            return yToXmap.get(y);
        }
    }
    public X y(Y y){
        return getXbyY(y);
    }
    //</editor-fold>

    public static enum ResolveDuplicate {
        Merge, Error
    }

    public static enum ResolveNull {
        Remove, Skip, Error
    }

    public class Update implements Runnable {
        //<editor-fold defaultstate="collapsed" desc="fn(x,y):Pair<X,Y>">
        protected BiFunction<X,Y,Pair<X,Y>> fnXY = null;

        public synchronized BiFunction<X, Y,Pair<X,Y>> getFnXY() {
            return fnXY;
        }

        public synchronized void setFnXY(BiFunction<X, Y,Pair<X,Y>> fnXY) {
            this.fnXY = fnXY;
        }

        public Update fnXY( BiFunction<X, Y,Pair<X,Y>> fn ){
            setFnXY(fn);
            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="xDuplicate">
        protected ResolveDuplicate xDuplicate = ResolveDuplicate.Error;

        public synchronized ResolveDuplicate getXDuplicate() {
            if( xDuplicate==null )return ResolveDuplicate.Error;
            return xDuplicate;
        }

        public synchronized void setXDuplicate(ResolveDuplicate xDuplicate) {
            this.xDuplicate = xDuplicate;
        }

        public Update xDuplicate( ResolveDuplicate resolv ){
            setXDuplicate(resolv);
            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="xNull">
        protected ResolveNull xNull = ResolveNull.Error;

        public synchronized ResolveNull getXNull() {
            if( xNull==null )return ResolveNull.Error;
            return xNull;
        }

        public synchronized void setXNull(ResolveNull xNull) {
            this.xNull = xNull;
        }

        public Update xNull( ResolveNull resolv ){
            setXNull(resolv);
            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="yDuplicate">
        protected ResolveDuplicate yDuplicate = ResolveDuplicate.Error;

        public synchronized ResolveDuplicate getYDuplicate() {
            if( yDuplicate==null )return ResolveDuplicate.Error;
            return yDuplicate;
        }

        public synchronized void setYDuplicate(ResolveDuplicate xDuplicate) {
            this.yDuplicate = xDuplicate;
        }

        public Update yDuplicate( ResolveDuplicate resolv ){
            setYDuplicate(resolv);
            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="yNull">
        protected ResolveNull yNull = ResolveNull.Error;

        public synchronized ResolveNull getYNull() {
            if( yNull==null )return ResolveNull.Error;
            return yNull;
        }

        public synchronized void setYNull(ResolveNull xNull) {
            this.yNull = xNull;
        }

        public Update yNull( ResolveNull resolv ){
            setYNull(resolv);
            return this;
        }
        //</editor-fold>

        public synchronized Update strong(){
            setXDuplicate(ResolveDuplicate.Error);
            setYDuplicate(ResolveDuplicate.Error);
            setXNull(ResolveNull.Error);
            setYNull(ResolveNull.Error);
            return this;
        }

        public synchronized Update simple(){
            setXDuplicate(ResolveDuplicate.Merge);
            setYDuplicate(ResolveDuplicate.Merge);
            setXNull(ResolveNull.Remove);
            setYNull(ResolveNull.Remove);
            return this;
        }

        protected synchronized Error check(TreeMap<X,Y> newXtoY){
            synchronized(sync){
                if( newXtoY==null )return null;
                Map<Y,Integer> yCnt = new TreeMap<>();

                for( Map.Entry<X,Y> en : newXtoY.entrySet() ){
                    X x = en.getKey();
                    Y y = en.getValue();
                    if( x==null ){ return new Error("internal error: new pair(x,y) x = null"); }
                    if( y==null ){ return new Error("internal error: new pair(x,y) y = null"); }

                    Integer yc = yCnt.get(y);
                    if( yc==null ){
                        yc = 1;
                        yCnt.put(y, yc);
                    }else if( yc>=1 ){
                        return new Error("internal error: duplicate y="+y+" duplicate on x="+x+",y="+y);
                    }
                }
                return null;
            }
        }

        private boolean isLogDuplicates(){
            Level l = logLevel();
            int v = l.intValue();

            return v <= Level.FINER.intValue();
        }

        @Override
        public synchronized void run() {
            if( fnXY!=null ){
                updateXY();
            }
        }

        protected boolean trackXInsert = false;
        protected boolean trackXDelete = false;

        public synchronized void updateXY(){
            if( fnXY==null )return;
            synchronized(sync){
                final TreeMap<X,Y> newXtoY = new TreeMap<>();

                //<editor-fold defaultstate="collapsed" desc="each xy">
                eachXY(new BiConsumer<X, Y>() {
                    @Override
                    public void accept(X x, Y y) {
                        Pair<X,Y> newXY = fnXY.apply(x, y);
                        if( newXY==null ){
                            if( getXNull()==ResolveNull.Error || getYNull()==ResolveNull.Error ){
                                throw new Error( "error on null x for update(x="+x+", y="+y+")" );
                            }
                            return;
                        }

                        X newX = newXY.a();
                        Y newY = newXY.b();

                        //<editor-fold defaultstate="collapsed" desc="resolve null for new x,y">
                        if( newX==null ){
                            if( getXNull()==ResolveNull.Error ){
                                throw new Error( "error on null x for update(x="+x+", y="+y+")" );
                            }else if( getXNull()==ResolveNull.Skip ){
                                return;
                            }else if( getXNull()==ResolveNull.Remove && newY!=null ){
                                LinkedHashSet<X> removeX = new LinkedHashSet<>();
                                for( Map.Entry<X,Y> en : newXtoY.entrySet() ){
                                    if( newY.compareTo( en.getValue() )==0 ){
                                        removeX.add(en.getKey());
                                    }
                                }
                                for( X rx : removeX ){
                                    newXtoY.remove(rx);
                                }
                                return;
                            }else{
                                throw new Error("internal error: unsupported getXNull()="+getXNull());
                            }
                        }

                        if( newY==null ){
                            if( getYNull()==ResolveNull.Error ){
                                throw new Error( "error on null y for update(x="+x+", y="+y+")" );
                            }else if( getYNull()==ResolveNull.Skip ){
                                return;
                            }else if( getYNull()==ResolveNull.Remove && newX!=null ){
                                newXtoY.remove(newX);
                                return;
                            }else{
                                throw new Error("internal error: unsupported getYNull()="+getYNull());
                            }
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="resolve duplicates">
                        if( newXtoY.containsKey(newX) ){
                            logFine("error on duplicate x="+newX+" for update(x="+x+", y="+y+")");
                            if( isLogDuplicates() ){
                                int ni = -1;
                                int nc = newXtoY.size();
                                for( X nx : newXtoY.keySet() ){
                                    ni++;
                                    logFiner("{0}/{1}. exists x={2}", ni+1, nc, nx);
                                }
                            }
                            if( getXDuplicate()==ResolveDuplicate.Error ){
                                throw new Error( "error on duplicate x="+newX+" for update(x="+x+", y="+y+")" );
                            }else if( getXDuplicate()==ResolveDuplicate.Merge ){
                                newXtoY.put(newX, newY);
                                return;
                            }else{
                                throw new Error("internal error: unsupported getXDuplicate()="+getXDuplicate());
                            }
                        }

                        if( newXtoY.containsValue(newY) ){
                            logFine("error on duplicate x="+newX+" for update(x="+x+", y="+y+")");
                            if( isLogDuplicates() ){
                                int ni = -1;
                                int nc = newXtoY.size();
                                for( Y ny : newXtoY.values() ){
                                    ni++;
                                    logFiner("{0}/{1}. exists y={2}", ni+1, nc, ny);
                                }
                            }
                            if( getYDuplicate()==ResolveDuplicate.Error ){
                                throw new Error( "error on duplicate y="+newY+" for update(x="+x+", y="+y+")" );
                            }else if( getYDuplicate()==ResolveDuplicate.Merge ){
                                LinkedHashSet<X> resetX = new LinkedHashSet<>();
                                for( Map.Entry<X,Y> en : newXtoY.entrySet() ){
                                    if( newY.compareTo(en.getValue())==0 ){
                                        resetX.add(en.getKey());
                                    }
                                }
                                if( resetX.size()>1 ){
                                    throw new Error("internal error: resetX.size()>1");
                                }
                                for( X ex : resetX ){
                                    newXtoY.put(ex, newY);
                                }
                                return;
                            }else{
                                throw new Error("internal error: unsupported getYDuplicate()="+getYDuplicate());
                            }
                        }
                        //</editor-fold>

                        newXtoY.put(newX, newY);
                        return;
                    }
                });
                //</editor-fold>

                Error err = check(newXtoY);
                if( err!=null ) throw err;

                xToYmap.clear();
                yToXmap.clear();

                xToYmap.putAll(newXtoY);
                for( Map.Entry<X,Y> en : xToYmap.entrySet() ){
                    yToXmap.put(en.getValue(), en.getKey());
                }
            }
        }
    }

    public Update update(){
        return new Update();
    }

    public Update update(BiFunction<X,Y,Pair<X,Y>> fn, boolean strong){
        if( fn==null )throw new IllegalArgumentException("fn == null");
        Update up = update().fnXY(fn);
        if( strong ){ up.strong(); } else { up.simple(); }
        up.run();
        return up;
    }

    public Update update(BiFunction<X,Y,Pair<X,Y>> fn){
        return update(fn, true);
    }

    public void assign( Bijection<X,Y> sample ){
        if( sample==null )throw new IllegalArgumentException("sample == null");
        synchronized(sync){
            clear();
            sample.eachXY(this::setXY);
        }
    }

    @Override
    public Bijection<X,Y> clone(){
        synchronized(sync){
            return new Bijection<>(this, null);
        }
    }
}
