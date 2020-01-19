package xyz.cofe.iter;

import xyz.cofe.fn.Pair;

import java.util.Iterator;

/**
 * Итератор - декартовое произведение
 * @param <A> Тип первого списка
 * @param <B> Тип второго списка
 */
@SuppressWarnings("WeakerAccess")
public class BiProductIterator<A,B> implements Iterator<Pair<A,B>> {
    /**
     * Конструктор
     * @param _1 Первый список
     * @param _2 Второй список
     */
    public BiProductIterator(Iterator<A> _1, Iterable<B> _2){
        if( _1==null )throw new IllegalArgumentException("_1 == null");
        if( _2==null )throw new IllegalArgumentException("_2 == null");
        this._1it = _1;
        this._2src = _2;
        fetched = fetch();
    }

    /**
     * Конструктор
     * @param _1 Первый список
     * @param _2 Второй список
     */
    public BiProductIterator(Iterable<A> _1, Iterable<B> _2){
        if( _1==null )throw new IllegalArgumentException("_1 == null");
        if( _2==null )throw new IllegalArgumentException("_2 == null");
        this._1it = _1.iterator();
        this._2src = _2;
        fetched = fetch();
    }

    protected Iterator<A> _1it;
    protected Iterable<B> _2src;

    protected void finish(){
        _1it = null;
        _2src = null;
        _2it = null;
    }

    protected A _1cur;
    protected boolean switchNextA(){
        if( _1it==null )return false;
        if( !_1it.hasNext() )return false;
        _1cur = _1it.next();
        return true;
    }

    protected Iterator<B> _2it;
    protected Pair<B,Boolean> nextB(){
        while (true){
            if( _1it==null || _2src==null ){
                finish();
                return null;
            }
            if( _2it!=null ) {
                if (_2it.hasNext()) {
                    return Pair.of(_2it.next(), true);
                }
            }

            if( !switchNextA() )return null;

            _2it = _2src.iterator();
            if( !_2it.hasNext() ){
                finish();
                return null;
            }
        }
    }

    protected Pair<A, B> fetched;

    @Override
    public boolean hasNext() {
        if( _1it==null )return false;
        if( _2src==null )return false;
        return fetched!=null;
    }

    protected Pair<A, B> fetch() {
        if( _1it==null )return null;
        if( _2src==null )return null;

        Pair<B,Boolean> pB = nextB();
        if( pB==null || !pB.b() )return null;

        return Pair.of(_1cur, pB.a());
    }

    @Override
    public Pair<A, B> next() {
        if( _1it==null )return null;
        if( _2src==null )return null;

        Pair<A,B> f = fetched;
        fetched = fetch();
        return f;
    }
}
