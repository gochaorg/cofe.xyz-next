package xyz.cofe.text.tparse;

import java.util.List;
import java.util.Optional;

/**
 * Токен соответствующий последовательности символов
 */
public class CToken implements Tok<CharPointer> {
    private CharPointer begin;
    private CharPointer end;

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public CToken(CToken sample){
        if( sample==null )throw new IllegalArgumentException("sample==null");
        this.begin = sample.begin;
        this.end = sample.end;
    }

    /**
     * Конструктор
     * @param begin начальный символ
     * @param end конечный символ
     */
    public CToken(CharPointer begin, CharPointer end){
        if( begin==null )throw new IllegalArgumentException("begin == null");
        if( end==null )throw new IllegalArgumentException("end == null");
        if( begin.compareTo(end)>0 )throw new IllegalArgumentException("begin > end");
        this.begin = begin;
        this.end = end;
    }

    /**
     * Конструктор
     * @param begin начальный символ
     * @param end конечный символ
     */
    public CToken(CToken begin, CToken end){
        if( begin==null )throw new IllegalArgumentException("begin == null");
        if( end==null )throw new IllegalArgumentException("end == null");

        this.begin = begin.begin();
        this.end = end.end();

        if( this.begin.compareTo(this.end)>0 )throw new IllegalArgumentException("begin > end");
    }

    /**
     * Конструктор
     * @param tokens последовательность символов
     */
    public CToken(List<CToken> tokens){
        if( tokens==null )throw new IllegalArgumentException("tokens == null");
        if( tokens.isEmpty() )throw new IllegalArgumentException("tokens is empty");
        CharPointer b = null;
        CharPointer e = null;
        for( CToken t : tokens ){
            if( t==null )throw new IllegalArgumentException("tokens contains null element");
            if( b==null || e==null ){
                b = t.begin();
                e = t.end();
                continue;
            }
            if( b.compareTo(t.begin())>0 ) b = t.begin();
            if( b.compareTo(t.end())>0 ) b = t.end();
            if( e.compareTo(t.begin())<0 ) e = t.begin();
            if( e.compareTo(t.end())<0 ) e = t.end();
        }
        this.begin = b;
        this.end = e;
        if( this.begin.compareTo(this.end)==0 )throw new IllegalStateException("bug!");
    }

    /**
     * Клонирование
     * @return клон
     */
    public CToken clone(){
        return new CToken(this);
    }

    /**
     * Возвращает начало токена
     * @return указатель на начало (включительно)
     */
    @Override
    public CharPointer begin() {
        return begin;
    }

    /**
     * Клонирует токен с указанием нового начала/конца токена
     * @return указатель на начало (включительно)
     */
    public CToken location(CharPointer begin,CharPointer end) {
        if( begin==null )throw new IllegalArgumentException("begin == null");
        if( end==null )throw new IllegalArgumentException("end == null");
        if( begin.compareTo(end)>0 )throw new IllegalArgumentException("begin > end");
        CToken t = clone();
        t.begin = begin;
        t.end = end;
        return t;
    }

    /**
     * Возвращает указатель на конец токена
     * @return Указатель на конец токена (включительно)
     */
    @Override
    public CharPointer end() {
        return end;
    }

    /**
     * Возвращает текст размещенный между начальным и конечный указателями
     * @return текст
     */
    public String text(){
        int len = end.position() - begin.position();
        if( len>0 ){
            StringBuilder sb = new StringBuilder();
            for( int i=0; i<len; i++ ){
                Optional<Character> c = begin.lookup(i);
                if( c==null || !c.isPresent() )throw new IllegalStateException("bug");
                sb.append( c.get() );
            }
            return sb.toString();
        }else{
            return "";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()+" \"");
        String str = text();
        for( int i=0;i<str.length(); i++ ){
            char c = str.charAt(i);
            int ci = (int)c;
            if( c=='\n' ) {
                sb.append("\\n");
            }else if( c=='\r' ){
                sb.append("\\n");
            }else if( c=='\t' ){
                sb.append("\\t");
            }else if( c=='"' ){
                sb.append("\\\"");
            }else if( ci<32 ){
                sb.append("\\h");
                String h = Integer.toString(ci,16);
                if( h.length()==1 ) sb.append("0");
                sb.append(h);
            }else{
                sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
