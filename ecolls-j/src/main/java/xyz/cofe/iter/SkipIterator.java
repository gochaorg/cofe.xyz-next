package xyz.cofe.iter;

import java.util.Iterator;

public class SkipIterator<A> implements Iterator<A> {
    private Iterator<A> iter;

    public SkipIterator( Iterator<A> iterator, long count ){
        if( iterator==null )throw new IllegalArgumentException( "iterator==null" );
        this.iter = iterator;
        if( count>0 ){
            for( long i = 0; i<count; i++ ){
                if( iter.hasNext() ){
                    iter.next();
                }
            }
        }
    }

    @Override
    public boolean hasNext(){
        return iter.hasNext();
    }

    @Override
    public A next(){
        return iter.next();
    }
}
