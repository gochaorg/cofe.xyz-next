package xyz.cofe.text.parse;

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

    /**
     * Перемещение указателя на заданное кол-во символов вперед
     * @param offset смещение указателя
     * @return новый указатель
     */
    PointerType move( int offset );
}
