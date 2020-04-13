package xyz.cofe.text.lex;

/**
 * Лексема - идентификатор
 * @author gocha
 */
public class Identifier extends Token
{
    public Identifier(){
        this.id = "id";
    }

    @Override
    public String getId() {
        if( id==null )id = "id";
        return super.getId();
    }

    @Override
    public String toString() {
        return getMatchedText();
    }
}

