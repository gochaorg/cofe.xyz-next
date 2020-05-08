package xyz.cofe.text.tparse;

import java.util.List;

/**
 * Указаетль на лексемы
 */
public class TPointer extends LPointer<CToken,TPointer> {
    /**
     * Конструктор
     * @param tokens лексемы
     * @param pos индекс (значение указателя)
     */
    public TPointer( List<? extends CToken> tokens, int pos ){
        super(tokens, pos);
    }

    /**
     * Конструктор
     * @param tokens лексемы
     */
    public TPointer( List<? extends CToken> tokens ){
        super(tokens);
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    protected TPointer( LPointer<CToken, TPointer> sample ){
        super(sample);
    }

    /**
     * Клонирование указателя
     * @return клон
     */
    @Override
    public TPointer clone(){
        return new TPointer(this);
    }

    @Override
    public String toString(){
        return "t.ptr "+position+" => "+lookup(0)+", "+lookup(1);
    }
}
