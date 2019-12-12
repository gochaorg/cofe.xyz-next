package xyz.cofe.text.parse;

/**
 * Лексема
 */
public class Token implements Tok<CharPointer> {
    /**
     * Конструктор
     * @param begin начало лексемы в тексте
     * @param end конец лексемы в тексте
     */
    public Token( CharPointer begin, CharPointer end){
        if( begin == null ) throw new IllegalArgumentException("begin==null");
        if( end == null ) throw new IllegalArgumentException("end==null");
        if( begin.compareTo(end)>0 ){
            this.begin = end;
            this.end = begin;
        }else{
            this.begin = begin;
            this.end = end;
        }
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public Token(Token sample){
        if( sample == null ) throw new IllegalArgumentException("sample==null");
        this.begin = sample.getBegin();
        this.end = sample.getEnd();
    }

    protected final CharPointer begin;

    /**
     * Возвращает начало лексемы
     * @return начало лексемы
     */
    public CharPointer getBegin(){
        return begin;
    }

    protected final CharPointer end;

    /**
     * Возвращает конец лексемы
     * @return конец лексемы
     */
    public CharPointer getEnd(){
        return end;
    }

    /**
     * Возвращает текст лексемы
     * @return текст лексемы
     */
    public String getText(){
        return begin.text(Math.abs(end.pointer() - begin.pointer()));
    }

    @Override
    public String toString(){
        return "<"+this.getClass().getSimpleName()+" begin='"+begin.pointer()+"' text='"+getText()+"' >";
    }
}
