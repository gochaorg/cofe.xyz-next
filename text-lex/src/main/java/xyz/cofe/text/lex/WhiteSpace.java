package xyz.cofe.text.lex;

/**
 * Лексема - пробельный символ
 * @author gocha
 */
public class WhiteSpace extends Token
{
    public WhiteSpace(){
        id="whiteSpace";
    }

    @Override
    public String getId() {
        if( id==null )id="whiteSpace";
        return super.getId();
    }
}