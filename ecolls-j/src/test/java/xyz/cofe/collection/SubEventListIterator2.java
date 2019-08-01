package xyz.cofe.collection;

import java.util.ListIterator;

public class SubEventListIterator2<E> implements ListIterator<E>, AutoCloseable
{
    protected EventList<E> elist = null;
    protected int position = -1;
    protected boolean closed = false;

    public SubEventListIterator2(EventList<E> elist){
        if( elist==null )throw new IllegalArgumentException( "elist==null" );
        this.elist = elist;
        this.position = getBeginPosition();
    }

    public SubEventListIterator2(EventList<E> elist, int initialIndex){
        if( elist==null )throw new IllegalArgumentException( "elist==null" );
        this.elist = elist;
        this.position = initialIndex;
        checkPositionBounds();
    }

    @Override
    public boolean hasNext() {
        if( closed )return false;
        int nidx = nextIndex();
        return indexInList(nidx);
    }

    @Override
    public E next() {
        if( closed )return null;
        this.position = getNextIndex();
        if( indexInList(this.position) ){
            return elist.get(position);
        }
        return null;
    }

    @Override
    public boolean hasPrevious() {
        if( closed )return false;
        int nidx = previousIndex();
        return indexInList(nidx);
    }

    @Override
    public E previous() {
        if( closed )return null;
        this.position = previousIndex();
        if( indexInList(this.position) ){
            return elist.get(position);
        }
        return null;
    }

    protected void checkPositionBounds(){
        if( closed )return;
        int nmin = getBeginPosition();
        int nmax = getEndPosition();
        if( position>nmax && isLimitNextPosition() )position = nmax;
        if( position<nmin && isLimitPreviousPosition() )position = nmin;
    }

    protected boolean positionInBounds(int idx){
        if( closed )return false;
        if( idx<=getBeginPosition() )return false;
        if( idx>=getEndPosition() )return false;
        return true;
    }

    protected boolean indexInList(int idx){
        if( closed )return false;
        if( idx<0 )return false;
        if( idx>=elist.size() )return false;
        return true;
    }

    protected int getEndPosition(){
        if( closed )return -100;
        return elist.size();
    }

    protected boolean isLimitNextPosition(){ return true; }

    private int getNextIndex() {
        if( closed )return -100;
        int npos = position+1;
        int endidx = getEndPosition();
        if( npos>endidx && isLimitNextPosition() )npos = endidx;
        return npos;
    }

    @Override
    public int nextIndex() {
        if( closed )return -100;
        return position;
    }

    protected int getBeginPosition(){
        if( closed )return -100;
        return -1;
    }

    protected boolean isLimitPreviousPosition(){ return true; }

    @Override
    public int previousIndex() {
        if( closed )return -100;
        int npos = position-1;
        int bidx = getBeginPosition();
        if( npos<bidx && isLimitPreviousPosition() )npos = bidx;
        return npos;
    }

    @Override
    public void remove() {
        if( closed )return;
        if( indexInList(position) ){
            elist.remove(position);
            position = previousIndex();
        }
        checkPositionBounds();
    }

    @Override
    public void set(E e) {
        if( closed )return;
        if( indexInList(position) ){
            elist.set(position, e);
        }
    }

    protected int getFirstIndex(){ return 0; }

    @Override
    public void add(E e) {
        if( closed )return;
        if( indexInList(position) ){
            elist.add(position,e);
        }else if( position<=getBeginPosition() ){
            int fidx = getFirstIndex();
            elist.add(fidx,e);
            position = fidx;
        }else if( position>=getEndPosition() ){
            elist.add(e);
            position = getEndPosition();
        }
    }

    @Override
    public void close() {
        if( closed )return;
        this.closed = true;
        if( this.elist!=null ){
            this.elist = null;
        }
    }
}
