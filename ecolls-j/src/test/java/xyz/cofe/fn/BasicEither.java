package xyz.cofe.fn;

import java.util.function.Function;

public class BasicEither<A,B> implements Either<A,B> {
    public BasicEither(A a, boolean aDefined, B b, boolean bDefined){
        if( aDefined && bDefined )throw new IllegalArgumentException( "both defined" );
        if( (!aDefined) && (!bDefined) )throw new IllegalArgumentException( "both undefined" );

        this.a = a;
        this.b = b;
        this.aDefined = aDefined;
        this.bDefined = bDefined;
    }

    protected final A a;
    protected final boolean aDefined;

    protected final B b;
    protected final boolean bDefined;

    @Override
    public boolean isLeft(){
        return aDefined;
    }

    @Override
    public boolean isRight(){
        return bDefined;
    }

    @Override
    public LeftProjection<A, B> left(){
        return new LeftProjection<>( this, a, aDefined );
    }

    @Override
    public RightProjection<A, B> right(){
        return new RightProjection<>( this, b, bDefined );
    }

    @Override
    public Either<B, A> swap(){
        return new BasicEither<>(b, bDefined, a, aDefined);
    }

    public <C> C fold(Function<A,C> l, Function<B,C> r){
        if( l==null )throw new IllegalArgumentException( "l==null" );
        if( r==null )throw new IllegalArgumentException( "r==null" );
        if( isLeft() )return l.apply(a);
        return r.apply(b);
    }
}
