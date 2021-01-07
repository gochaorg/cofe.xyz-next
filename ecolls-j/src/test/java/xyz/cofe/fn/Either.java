package xyz.cofe.fn;

import java.util.function.Function;

public interface Either<A,B> {
    public static <A,B> Either<A,B> left( A a ){
        if( a==null )throw new IllegalArgumentException( "a==null" );
        return new BasicEither<A, B>( a, true, null, false );
    }

    public static <A,B> Either<A,B> right( B b ){
        if( b==null )throw new IllegalArgumentException( "b==null" );
        return new BasicEither<A, B>( null, false, b, true);
    }

    boolean isLeft();

    boolean isRight();

    LeftProjection<A,B> left();

    RightProjection<A,B> right();

    Either<B,A> swap();

    public <C> C fold(Function<A,C> l, Function<B,C> r);
}
