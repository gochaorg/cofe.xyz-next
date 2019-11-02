package xyz.cofe.text.lex;

import static xyz.cofe.text.lex.LexerUtil.*;

/**
 * Лексема - однострочный комментарий в стиле языка C.<br>
 * Пример: <br>
 * <font style="font-family:monospaced"><b>// и до конца строки</b></font>
 * @author gocha
 */
public class SLCommentParser implements TokenParser
{
    protected String id = null;

    public SLCommentParser(){
    }

    public SLCommentParser(String id){
        this.id = id;
    }

    @Override
    public Token parse(String source, int offset) {
        int i = -1;
        int state = 0;
        int slen = source.length();
        while((i+offset)<slen){
            i++;
            switch(state){
                case 0:
                    if( match(source, offset+i,false, "//") ){
                        state = 1;
                    }else{
                        state = -1;
                    }
                    break;
                case 1:
                    state = 2;
                    break;
                case 2:
                    if( match(source, offset+i,false, "\r\n") )state = 3;
                    else if( match(source, offset+i,false, "\n") )state = 4;
                    break;
                case 3:
                    state = 4;
                    break;
                case 4:
                    state = 99;
                    break;
            }
            if( state==-1 )return null;
            if( state==99 )break;
        }
        if( state==99 ){
            Comment cmnt = new Comment();
            cmnt.setBegin(offset);
            cmnt.setSource(source);
            cmnt.setLength(i);
            if( id!=null )cmnt.setId(id);
            return cmnt;
        }
        return null;
    }
}
