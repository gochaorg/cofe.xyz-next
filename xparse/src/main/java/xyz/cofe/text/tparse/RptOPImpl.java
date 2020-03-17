package xyz.cofe.text.tparse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Правило повтора конструкции - т.е. соответ фигурным скобкам черте в грамматике BNF
 * {@link GR}
 * @param <P> Указатель
 * @param <T> Лексема/Токен
 */
public class RptOPImpl<P extends Pointer<?,?,P>, T extends Tok<P>> implements RptOP<P,T> {
    private final GR<P,T> gr;

    /**
     * Конструктор
     * @param gr правило - шаблон
     * @param min минимальное кол-во повтора шаблона
     * @param max максимальное кол-во повтора шаблона
     * @param greedly жадный или нет алгоритм захвата
     */
    public RptOPImpl(GR<P,T> gr, int min, int max, boolean greedly){
        if( gr==null )throw new IllegalArgumentException("gr==null");
        if( min>max )throw new IllegalArgumentException("min>max");
        this.gr = gr;
        this.min = min;
        this.max = max;
        this.greedly = greedly;
    }

    public GR<P,T> expression(){ return gr; }

    private final int min;

    @Override
    public int min() {
        return min;
    }

    @Override
    public RptOP<P, T> min(int n) {
        return new RptOPImpl<>(gr,n,max,greedly);
    }

    private final int max;

    @Override
    public int max() {
        return max;
    }

    @Override
    public RptOP<P, T> max(int n) {
        return new RptOPImpl<>(gr,min,n,greedly);
    }

    private final boolean greedly;

    @Override
    public boolean greedly() {
        return greedly;
    }

    @Override
    public RptOP<P, T> greedly(boolean b) {
        return new RptOPImpl<>(gr,min,max,b);
    }

    @Override
    public <U extends Tok<P>> GR<P, U> map(Function<List<T>, U> map) {
        if( map==null )throw new IllegalArgumentException("map==null");
        return new GR<P, U>() {
            @Override
            public Optional<U> apply(P ptr) {
                if( ptr==null )throw new IllegalArgumentException("ptr==null");
                if( ptr.eof() )return Optional.empty();

                List<T> matched = new ArrayList<>();

                while (true) {
                    if(ptr.eof())break;

                    Optional<T> tokOpt = gr.apply(ptr);
                    if (tokOpt == null) throw new IllegalStateException("bug!");
                    if (!tokOpt.isPresent()) break;

                    T tok = tokOpt.get();
                    if (tok == null) throw new IllegalStateException("bug");
                    if (tok.end() == null) throw new IllegalStateException("bug");

                    P next = tok.end();
                    if (next == null) throw new IllegalStateException("bug");
                    if (ptr.compareTo(next) >= 0) throw new IllegalStateException("bug");

                    matched.add(tok);

                    if (!greedly && matched.size() >= min) break;
                    if( max>0 && matched.size() >= max )break;

                    ptr = next;
                }

                if( max>0 && matched.size()>max ){
                    int dropSize = matched.size() - max;
                    for( int i=0; i<dropSize; i++ )matched.remove( matched.size()-1 );
                }

                if( matched.isEmpty() )return Optional.empty();

                if( min>0 && matched.size()<min )return Optional.empty();

                U result = map.apply( matched );
                if( result==null )throw new IllegalStateException("bug");

                return Optional.of(result);
            }
        };
    }
}
