package xyz.cofe.text.tparse;

import xyz.cofe.iter.Eterable;

import java.util.*;

/**
 * Итератор - преобразователь входной цепочки символов в токены
 * @param <P> Тип указателя
 * @param <T> Тип токенов
 */
public class Tokenizer<P extends Pointer<?,?,P>, T extends Tok<P>> implements Eterable<T> {
    private final List<GR<P,? super T>> rules;
    private final P pointer;

    /**
     * Конструктор
     * @param initial начальный указатель
     * @param rules Грамматический правила
     */
    public Tokenizer(P initial, Iterable<GR<P,T>> rules ){
        if( rules==null )throw new IllegalArgumentException("rules==null");
        if( initial==null )throw new IllegalArgumentException("initial==null");
        List<GR<P,? super T>> arules = new ArrayList<>();
        for( GR<P,? super T> r : rules ){
            if( r==null )throw new IllegalArgumentException("rules contains empty element");
            arules.add(r);
        }
        this.rules = arules;
        this.pointer = initial;
    }

    /**
     * Возвращает итератор по токенам
     * @return итератор по токенам
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public TokensIterator<P,T> iterator() {
        return new TokensIterator(pointer, rules);
    }

    /**
     * Создает лексический анализатор
     * @param source исходный текст
     * @param rules грамматические правила
     * @return Итератор по токенам
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Tokenizer<CharPointer, CToken> lexer(String source, Iterable<GR<CharPointer,? extends CToken>> rules ){
        if( source==null )throw new IllegalArgumentException("source == null");
        if( rules==null )throw new IllegalArgumentException("rules == null");

        CharPointer cptr = new CharPointer(source);
        return new Tokenizer(cptr, rules);
    }

    /**
     * Создает лексический анализатор
     * @param source исходный текст
     * @param from с какой позиции (от 0 и больше) в исходном тексте начать анализ
     * @param rules грамматические правила
     * @return Итератор по токенам
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Tokenizer<CharPointer, CToken> lexer(String source, int from, Iterable<GR<CharPointer,? extends CToken>> rules ){
        if( source==null )throw new IllegalArgumentException("source == null");
        if( rules==null )throw new IllegalArgumentException("rules == null");
        if( from<0 )throw new IllegalArgumentException( "from<0" );

        CharPointer cptr = new CharPointer(source, from);
        return new Tokenizer(cptr, rules);
    }

    /**
     * Создает лексический анализатор
     * @param source исходный текст
     * @param rules грамматические правила
     * @return Итератор по токенам
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Tokenizer<CharPointer, ? extends CToken> lexer( String source, GR<CharPointer,? extends CToken> ... rules ){
        if( source==null )throw new IllegalArgumentException("source == null");
        if( rules==null )throw new IllegalArgumentException("rules == null");

        CharPointer cptr = new CharPointer(source);
        return new Tokenizer(cptr,  Arrays.asList(rules));
    }

    /**
     * Создает лексический анализатор
     * @param source исходный текст
     * @param from с какой позиции (от 0 и больше) в исходном тексте начать анализ
     * @param rules грамматические правила
     * @return Итератор по токенам
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Tokenizer<CharPointer,? extends CToken> lexer( String source, int from, GR<CharPointer,? extends CToken> ... rules ){
        if( source==null )throw new IllegalArgumentException("source == null");
        if( rules==null )throw new IllegalArgumentException("rules == null");
        if( from<0 )throw new IllegalArgumentException( "from<0" );

        CharPointer cptr = new CharPointer(source, from);
        return new Tokenizer(cptr,  Arrays.asList(rules));
    }
}
