package xyz.cofe.text.tparse;

import java.util.List;
import java.util.Optional;

/**
 * Указатель на список токенов/лексем
 * @param <T> Тип токена/лексемы
 */
public class LPointer<T> implements Pointer<T,Integer,LPointer<T>> {
    /**
     * Конструктор
     * @param tokens список токенов/лексем
     * @param pos налальное смещение (индекс) в списке
     */
    public LPointer(List<T> tokens, int pos){
        if( tokens==null )throw new IllegalArgumentException("tokens==null");
        this.position = pos;
        this.tokens = tokens;
    }

    /**
     * Конструктор
     * @param tokens список токенов/лексем
     */
    public LPointer(List<T> tokens){
        if( tokens==null )throw new IllegalArgumentException("tokens==null");
        this.position = 0;
        this.tokens = tokens;
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирвоания
     */
    protected LPointer(LPointer<T> sample){
        if( sample==null )throw new IllegalArgumentException("sample == null");
        this.position = sample.position;
        this.tokens = sample.tokens;
    }

    /**
     * Клонирование
     * @return клон
     */
    public LPointer<T> clone(){
        return new LPointer<>(this);
    }

    private final List<T> tokens;

    /**
     * Возвращает список токенов
     * @return список токенов/лексем
     */
    public List<T> tokens(){ return tokens; }

    @Override
    public boolean eof() {
        if( position<0 )return true;
        if( position>=tokens.size() )return true;
        return false;
    }

    private int position;

    /**
     * Получение значения текущего указателя
     * @return указатель (смещение)
     */
    @Override
    public Integer position() {
        return position;
    }

    /**
     * Перемещение указателя n позиций вперед/назад
     * @param offset кол-во позиций
     * @return Новый указатель
     */
    @Override
    public LPointer<T> move(Integer offset) {
        if( offset==null )throw new IllegalArgumentException("offset==null");
        LPointer<T> c = clone();
        c.position = c.position + offset;
        return c;
    }

    /**
     * Предпросмотр n-ой лексемы относительно текущего указателя
     * @param offset Номер лексемы/символа
     * @return Лексема
     */
    @Override
    public Optional<T> lookup(Integer offset) {
        if( offset==null )throw new IllegalArgumentException("offset==null");
        int t = position + offset;
        if( t<0 || t>=tokens.size() )return Optional.empty();
        return Optional.of( tokens.get(t) );
    }

    @Override
    public int compareTo(LPointer<T> o) {
        if( o==null )return 0;
        if( o==this )return 0;
        if( o.tokens!=tokens )return 0;
        return Integer.compare(position, o.position);
    }

    @Override
    public String toString() {
        return LPointer.class.getSimpleName() + " " + position();
    }
}
