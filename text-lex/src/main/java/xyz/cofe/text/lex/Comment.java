package xyz.cofe.text.lex;

/**
 * Лексема - коментарий
 * @author gocha
 */
public class Comment extends Token
{
    public Comment(){
        this.id = "comment";
    }

    @Override
    public String getId() {
        if( this.id==null )this.id = "comment";
        return super.getId();
    }
}

