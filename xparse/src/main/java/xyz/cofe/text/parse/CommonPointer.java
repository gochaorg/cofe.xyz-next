package xyz.cofe.text.parse;

import java.util.function.Function;

/**
 * Указатель на некую последовательность токенов/лексем
 * @param <SELF> Возвращаемый тип
 * @param <Item> Тип элемента
 * @param <Pos> Тип позиции
 */
public interface CommonPointer<SELF extends CommonPointer<SELF,Item,Pos>,Item,Pos> extends Pointer {
    /**
     * Возвращает текущий указатель в тексте
     * @return указатель
     */
    Pos pointer();

    /**
     * Просмотр одного символа относительно указателя
     * @return символ
     */
    Item lookup();

    default <T> T lookup( Function<Item,T> map ){
        if( map==null ) throw new IllegalArgumentException("map==null");
        if( eof() )return null;

        Item itm = lookup();
        if( itm==null )return null;

        return map.apply(itm);
    }

    /**
     * Просмотр одного символа с указанным смещением, относительно указателя
     * @param offset смещение относительно которого происходит просомтр
     * @return символ
     */
    default Item lookup(int offset){
        CommonPointer<SELF,Item,Pos> self = this;
        SELF trgt = self.move(offset);
        if( trgt.eof() )return null;
        return trgt.lookup();
    };

    default <T> T lookup( int offset, Function<Item,T> map ){
        if( map==null ) throw new IllegalArgumentException("map==null");
        if( eof() )return null;

        Item itm = lookup(offset);
        if( itm==null )return null;

        return map.apply(itm);
    }

    /**
     * Перемещение указателя на заданное кол-во символов вперед
     * @param offset смещение указателя
     * @return новый указатель
     */
    SELF move( int offset );
}
