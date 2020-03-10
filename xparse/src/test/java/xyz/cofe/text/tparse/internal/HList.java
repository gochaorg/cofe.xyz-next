package xyz.cofe.text.tparse.internal;

public interface HList<H,T> {
    H left();
    boolean lexists();

    T right();
    boolean rexists();

    default <A> HList<HList<H,T>,A> plus(A a) {
        HList<H,T> self = this;
        return new HList<HList<H, T>, A>() {
            @Override
            public boolean lexists() {
                return true;
            }

            @Override
            public boolean rexists() {
                return true;
            }

            @Override
            public HList<H, T> left() {
                return self;
            }

            @Override
            public A right() {
                return a;
            }
        };
    }

    static <A>  HList<Void,A> of(A a){
        return new HList<Void, A>() {
            @Override
            public boolean lexists() {
                return false;
            }

            @Override
            public boolean rexists() {
                return true;
            }

            @Override
            public Void left() {
                return null;
            }

            @Override
            public A right() {
                return a;
            }
        };
    }

    static <A,B,C> int count( HList<HList<A,B>,C> hl ){
        if( hl==null )throw new IllegalArgumentException("hl==null");
        int cright = hl.rexists() ? 1 : 0;
        int cleft =  hl.lexists() ? 1 : 0;
        return cright + cleft;
    }
}
