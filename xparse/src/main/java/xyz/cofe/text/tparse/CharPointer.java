package xyz.cofe.text.tparse;

import java.util.MissingFormatArgumentException;
import java.util.Optional;

/**
 * Указатель на потом симвлов строки
 */
public class CharPointer implements Pointer<Character,Integer,CharPointer> {
    /**
     * Конструктор указателя
     * @param source исходный текст
     * @param offset смещение в тексте
     */
    public CharPointer(String source, int offset){
        this.source = source;
        this.offset = offset;
    }

    /**
     * Конструктор указателя
     * @param source исходный текст
     */
    public CharPointer(String source){
        this.source = source;
        this.offset = 0;
    }

    private final String source;

    /**
     * Возвращает исходный текст
     * @return исходный текст
     */
    public String source(){ return source; }

    /**
     * Проверка что указаетль находиться за границей списка
     * @return true - за границей списка
     */
    @Override public boolean eof() {
        if( source==null )return true;
        if( offset<0 )return true;
        return offset >= source.length();
    }

    private final int offset;
    /**
     * Получение значения текущего указателя
     * @return указатель (смещение)
     */
    @Override public Integer position() { return offset; }

    /**
     * Перемещение указателя n позиций вперед/назад
     * @param offset кол-во позиций
     * @return Новый указатель
     */
    @Override
    public CharPointer move(Integer offset) {
        if( offset==null )throw new IllegalArgumentException("offset == null");
        return new CharPointer(source, this.offset + offset);
    }

    /**
     * Предпросмотр n-ой лексемы относительно текущего указателя
     * @param offset Номер лексемы/символа
     * @return Лексема или символ
     */
    @Override
    public Optional<Character> lookup(Integer offset) {
        if( offset==null )throw new IllegalArgumentException("offset == null");
        if( eof() )return Optional.empty();
        int t = (position()+offset);
        if( t<0 )return Optional.empty();
        if( t>=source.length() )return Optional.empty();
        return Optional.of( source.charAt(t) );
    }

    @Override
    public int compareTo(CharPointer o) {
        if( o==null )return 0;
        // if( o.getClass()!=CharPointer.class )return 0;
        // noinspection StringEquality
        if( source!=o.source ) {
            if (!o.source.equals(this.source)) return 0;
        }
        int x = o.position();
        return Integer.compare(offset, x);
    }

    @Override
    public String toString() {
        return "CharPointer "+position();
    }
}
