package xyz.cofe.fn;

import java.io.Serializable;

/**
 * 22ка значений
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 * @param <C> тип третьего элемента
 * @param <D> тип 4го элемента
 * @param <E> тип 5го элемента
 * @param <F> тип 6го элемента
 * @param <G> тип 7го элемента
 * @param <H> тип 8го элемента
 * @param <I> тип 9го элемента
 * @param <J> тип 10го элемента
 * @param <K> тип 11го элемента
 * @param <L> тип 12го элемента
 * @param <M> тип 13го элемента
 * @param <N> тип 14го элемента
 * @param <O> тип 15го элемента
 * @param <P> тип 16го элемента
 * @param <Q> тип 17го элемента
 * @param <R> тип 18го элемента
 * @param <S> тип 19го элемента
 * @param <T> тип 20го элемента
 * @param <U> тип 21го элемента
 * @param <V> тип 22го элемента
 */
public interface Tuple22<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V> {
    /**
     * Возвращает первый элемент
     * @return первый элемент
     */
    A a();

    /**
     * Возвращает второй элемент
     * @return второй элемент
     */
    B b();

    /**
     * Возвращает третий элемент
     * @return третий элемент
     */
    C c();

    /**
     * Возвращает 4ий элемент
     * @return 4ий элемент
     */
    D d();

    /**
     * Возвращает 5ий элемент
     * @return 5ий элемент
     */
    E e();

    /**
     * Возвращает 6ий элемент
     * @return 6ий элемент
     */
    F f();

    /**
     * Возвращает 7ий элемент
     * @return 7ий элемент
     */
    G g();

    /**
     * Возвращает 8ий элемент
     * @return 8ий элемент
     */
    H h();

    /**
     * Возвращает 9ий элемент
     * @return 9ий элемент
     */
    I i();

    /**
     * Возвращает 10ий элемент
     * @return 10ий элемент
     */
    J j();

    /**
     * Возвращает 11ий элемент
     * @return 11ий элемент
     */
    K k();

    /**
     * Возвращает 12ий элемент
     * @return 12ий элемент
     */
    L l();

    /**
     * Возвращает 13ий элемент
     * @return 13ий элемент
     */
    M m();

    /**
     * Возвращает 14ий элемент
     * @return 14ий элемент
     */
    N n();

    /**
     * Возвращает 15ий элемент
     * @return 15ий элемент
     */
    O o();

    /**
     * Возвращает 16ий элемент
     * @return 16ий элемент
     */
    P p();

    /**
     * Возвращает 17ий элемент
     * @return 17ий элемент
     */
    Q q();

    /**
     * Возвращает 18ий элемент
     * @return 18ий элемент
     */
    R r();

    /**
     * Возвращает 19ий элемент
     * @return 19ий элемент
     */
    S s();

    /**
     * Возвращает 20ий элемент
     * @return 20ий элемент
     */
    T t();

    /**
     * Возвращает 21ий элемент
     * @return 21ий элемент
     */
    U u();

    /**
     * Возвращает 22ий элемент
     * @return 22ий элемент
     */
    V v();

    /**
     * Реализация кортежа
     * @param <A> тип значения
     */
    public static class Tuple22Impl<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V> implements Tuple22<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V>, Serializable {
        private final A a;
        private final B b;
        private final C c;
        private final D d;
        private final E e;
        private final F f;
        private final G g;
        private final H h;
        private final I i;
        private final J j;
        private final K k;
        private final L l;
        private final M m;
        private final N n;
        private final O o;
        private final P p;
        private final Q q;
        private final R r;
        private final S s;
        private final T t;
        private final U u;
        private final V v;

        public Tuple22Impl(
            A a, B b, C c, D d, E e,
            F f, G g, H h, I i, J j,
            K k, L l, M m, N n, O o,
            P p, Q q, R r, S s, T t,
            U u, V v
        ){
            this.a = a;this.b = b;this.c = c;this.d = d;this.e = e;
            this.f = f;this.g = g;this.h = h;this.i = i;this.j = j;
            this.k = k;this.l = l;this.m = m;this.n = n;this.o = o;
            this.p = p;this.q = q;this.r = r;this.s = s;this.t = t;
            this.u = u;this.v = v;
        }

        @Override public A a(){ return a; }
        @Override public B b(){ return b; }
        @Override public C c(){ return c; }
        @Override public D d(){ return d; }
        @Override public E e(){ return e; }
        @Override public F f(){ return f; }
        @Override public G g(){ return g; }
        @Override public H h(){ return h; }
        @Override public I i(){ return i; }
        @Override public J j(){ return j; }
        @Override public K k(){ return k; }
        @Override public L l(){ return l; }
        @Override public M m(){ return m; }
        @Override public N n(){ return n; }
        @Override public O o(){ return o; }
        @Override public P p(){ return p; }
        @Override public Q q(){ return q; }
        @Override public R r(){ return r; }
        @Override public S s(){ return s; }
        @Override public T t(){ return t; }
        @Override public U u(){ return u; }
        @Override public V v(){ return v; }

        public String toString(){
            return "(a="+a+",b="+b+",c="+c+",d="+d+",e="+e+
                ",f="+f+",g="+g+",h="+h+",i="+i+",j="+j+
                ",k="+k+",l="+l+",m="+m+",n="+n+",o="+o+
                ",p="+p+",q="+q+",r="+r+",s="+s+",t="+t+
                ",u="+u+",v="+v+
                ")";
        }
    }

    /**
     * Возвращает 22ку значений
     * @param a первый элемент
     * @param b второй элемент
     * @param c 3й элемент
     * @param d 4й элемент
     * @param e 5й элемент
     * @param f 6й элемент
     * @param g 7й элемент
     * @param h 8й элемент
     * @param i 9й элемент
     * @param j 10й элемент
     * @param k 11й элемент
     * @param l 12й элемент
     * @param m 13й элемент
     * @param n 14й элемент
     * @param o 15й элемент
     * @param p 16й элемент
     * @param q 17й элемент
     * @param r 18й элемент
     * @param s 19й элемент
     * @param t 20й элемент
     * @param u 21й элемент
     * @param v 22й элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @param <C> тип 3го элемента
     * @param <D> тип 4го элемента
     * @param <E> тип 5го элемента
     * @param <F> тип 6го элемента
     * @param <G> тип 7го элемента
     * @param <H> тип 8го элемента
     * @param <I> тип 9го элемента
     * @param <J> тип 10го элемента
     * @param <K> тип 11го элемента
     * @param <L> тип 12го элемента
     * @param <M> тип 13го элемента
     * @param <N> тип 14го элемента
     * @param <O> тип 15го элемента
     * @param <P> тип 16го элемента
     * @param <Q> тип 17го элемента
     * @param <R> тип 18го элемента
     * @param <S> тип 19го элемента
     * @param <T> тип 20го элемента
     * @param <U> тип 21го элемента
     * @param <V> тип 22го элемента
     * @return 22ка значений
     */
    static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V> Tuple22<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V>
    of(
        A a, B b, C c, D d, E e,
        F f, G g, H h, I i, J j,
        K k, L l, M m, N n, O o,
        P p, Q q, R r, S s, T t,
        U u, V v
    ){
        return new Tuple22Impl<>(
            a,b,c,d,e,
            f,g,h,i,j,
            k,l,m,n,o,
            p,q,r,s,t,
            u,v
        );
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Tuple22<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V> apply(
        Consumer22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>
            consumer
    ){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k(),l(),m(),n(),o(),
            p(),q(),r(),s(),t(),
            u(),v()
        );
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param <Z> тип результата
     * @param fn функция приемник
     * @return результат вызова функции
     */
    default <Z> Z apply( Fn22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, Z> fn ){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(
            a(),b(),c(),d(),e(),
            f(),g(),h(),i(),j(),
            k(),l(),m(),n(),o(),
            p(),q(),r(),s(),t(),
            u(),v()
        );
    }

    /**
     * Создает новый котреж добавляя текущее и указанное значение
     * @param w значение
     * @param <W> тип значения
     * @return Кортэж
     */
    default <W> Tuple23<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W> add(W w){
        return Tuple.of(a(),b(),c(),d(),e(),f(),g(),h(),i(),j(),k(),l(),m(),n(),o(),p(),q(),r(),s(),t(),u(),v(),w);
    }
}
