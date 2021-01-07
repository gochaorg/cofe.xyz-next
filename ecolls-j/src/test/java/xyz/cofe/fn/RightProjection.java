package xyz.cofe.fn;

import java.util.Optional;

public class RightProjection<A,B> {
    public RightProjection( Either<A,B> either, B value, boolean exists ){
        if( either==null )throw new IllegalArgumentException( "either==null" );
        this.either = either;
        this.value = value;
        this.exists = exists;
    }

    protected final boolean exists;

    protected final Either<A,B> either;
    public Either<A,B> e(){ return either; }

    protected final B value;
    public B get(){
        if( !exists )throw new IllegalStateException("value not exists");
        return value;
    }

    public Optional<B> toOptional(){
        return exists ? Optional.of(value) : Optional.empty();
    }
}
