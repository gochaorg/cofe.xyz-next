package xyz.cofe.text.parse;

import java.util.function.Function;

public interface CommonPointer<PointerType extends CommonPointer<PointerType,Item,Pos>,Item,Pos> extends Pointer {
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
     * @return символ
     */
    default Item lookup(int offset){
        var self = this;
        var trgt = self.move(offset);
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
    PointerType move( int offset );
}
