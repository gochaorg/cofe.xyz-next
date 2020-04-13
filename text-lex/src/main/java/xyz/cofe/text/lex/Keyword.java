package xyz.cofe.text.lex;

import xyz.cofe.text.Text;
import xyz.cofe.text.lex.Token;

/**
 * Лексема - ключевое слово
 * @author gocha
 */
public class Keyword extends Token
{
    public Keyword(){
        this.id = "keyWord";
    }

    @Override
    public String getId() {
        if( id==null )id = "keyWord";
        return super.getId();
    }

    protected String keyword = null;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return this.getId()+" "+Text.encodeStringConstant(getMatchedText());
    }
}
