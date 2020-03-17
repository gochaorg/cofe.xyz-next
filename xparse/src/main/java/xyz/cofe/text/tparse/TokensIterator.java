package xyz.cofe.text.tparse;

import xyz.cofe.fn.Tuple2;
import xyz.cofe.iter.Eterable;

import java.util.*;

/**
 * Итератор по распарсеным токенам
 * @param <P> Тип указателя
 * @param <T> Тип токена
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TokensIterator<P extends Pointer<?,?,P>, T extends Tok<P>> implements Iterator<T> {
    private final List<GR> rules;
    private P pointer;

    /**
     * Конструктор
     * @param initial начальный указатель
     * @param rules грамматические правила
     */
    public TokensIterator(P initial, List<GR<P,T>> rules ){
        if( rules==null )throw new IllegalArgumentException("rules==null");
        if( initial==null )throw new IllegalArgumentException("initial==null");
        List<GR> arules = new ArrayList<>();
        for( GR r : rules ){
            if( r==null )throw new IllegalArgumentException("rules contains empty element");
            arules.add(r);
        }
        this.rules = (List)Collections.unmodifiableList(arules);

        this.pointer = initial;

        fetched = fetch(pointer);
        if( fetched!=null ){
            this.pointer = fetched.end();
        }
    }

    /**
     * Получение текущего указателя
     * @return текущий указатель
     */
    public P pointer(){ return pointer; }

    private T fetched;

    @SuppressWarnings({"OptionalAssignedToNull", "ConstantConditions"})
    private T fetch(P pointer){
        if( pointer==null )return null;
        if( pointer.eof() )return null;
        for( GR<P,? extends T> r :rules ){
            Optional<? extends T> ot = r.apply(pointer);
            if( ot==null )throw new IllegalStateException("bug");

            if( !ot.isPresent() )continue;
            if( ot.get()==null )throw new IllegalStateException("bug");

            return ot.get();
        }
        return null;
    }

    /**
     * Проверка наличия следующего токена
     * @return true - есть токен
     */
    @Override
    public boolean hasNext() {
        return fetched!=null;
    }

    /**
     * Получение очередного токена и перемещение указателя
     * @return Токен или null
     */
    @Override
    public T next() {
        T r = fetched;
        fetched = fetch(pointer);
        if( fetched!=null ){
            pointer = fetched.end();
        }
        return r;
    }
}
