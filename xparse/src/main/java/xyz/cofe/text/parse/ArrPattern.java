package xyz.cofe.text.parse;

import xyz.cofe.fn.Fn0;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Fn2;

import java.util.Optional;

public class ArrPattern<Item> {
    private final Arr<Item> arr;
    public ArrPattern( Arr<Item> arr ){
        if( arr==null ) throw new IllegalArgumentException("arr==null");
        this.arr = arr;
    }

    public <A> Pattern1<Item,A> like( int pos, Class<A> cls ){
        if( cls==null ) throw new IllegalArgumentException("cls==null");
        return new Pattern1<Item, A>(arr, ()->{
            if( pos<0 || pos>=arr.size() )return Optional.empty();

            Item it = arr.get(pos);
            if( it==null )return Optional.empty();

            if( !cls.isAssignableFrom(it.getClass()) )return Optional.empty();

            return Optional.of( (A)it );
        });
    }

    public static class Pattern1<Item,A> {
        private final Arr<Item> arr;
        private final Fn0<Optional<A>> get;
        public Pattern1( Arr<Item> arr, Fn0<Optional<A>> get ){
            this.arr = arr;
            this.get = get;
        }

        public <Z> Optional<Z> match( Fn1<A,Z> call ){
            if( call==null ) throw new IllegalArgumentException("call==null");

            Optional<A> oA = get.apply();
            if( oA.isPresent() ){
                Z res = call.apply( oA.get() );
                return Optional.of(res);
            }

            return Optional.empty();
        }

        public <B> Pattern2<Item,A,B> like( int pos, Class<B> cls ){
            if( cls==null ) throw new IllegalArgumentException("cls==null");
            return new Pattern2<Item, A, B>(arr, get, ()->{
                if( pos<0 || pos>=arr.size() )return Optional.empty();

                Item it = arr.get(pos);
                if( it==null )return Optional.empty();

                if( !cls.isAssignableFrom(it.getClass()) )return Optional.empty();

                return Optional.of( (B)it );
            });
        }
    }

    public static class Pattern2<Item,A,B> {
        private final Arr<Item> arr;
        private final Fn0<Optional<A>> get0;
        private final Fn0<Optional<B>> get1;

        public Pattern2( Arr<Item> arr, Fn0<Optional<A>> get0, Fn0<Optional<B>> get1 ){
            this.arr = arr;
            this.get0 = get0;
            this.get1 = get1;
        }

        public <Z> Optional<Z> match( Fn2<A,B, Z> call ){
            if( call==null ) throw new IllegalArgumentException("call==null");

            Optional<A> oA = get0.apply();
            if( !oA.isPresent() ){
                return Optional.empty();
            }

            Optional<B> oB = get1.apply();
            if( !oB.isPresent() ){
                return Optional.empty();
            }

            return Optional.of( call.apply(oA.get(), oB.get()) );
        }
    }
}
