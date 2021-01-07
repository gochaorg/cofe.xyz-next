package xyz.cofe.fn;

import java.util.Optional;

public class LeftProjection<A,B> {
    public LeftProjection( Either<A,B> either, A value, boolean exists ){
        if( either==null )throw new IllegalArgumentException( "either==null" );
        this.either = either;
        this.value = value;
        this.exists = exists;
    }

    protected final boolean exists;

    protected final Either<A,B> either;
    public Either<A,B> e(){ return either; }

    protected final A value;
    public A get(){
        if( !exists )throw new IllegalStateException("value not exists");
        return value;
    }

    public Optional<A> toOptional(){
        return exists ? Optional.of(value) : Optional.empty();
    }
}
