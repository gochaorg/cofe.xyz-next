package xyz.cofe.text.lex;

/**
 * Анализатор для лексемы идентификатора.<br>
 * Идентификатор состоит из двух частей,
 * первого символа обязательного и последующих не обязательных:
 * <pre>
 * Identifier ::= ( '_' | letter ) { '_' | letter | digit }
 * </pre>
 * @author gocha
 */
public class IdentifierParser implements TokenParser
{
    protected String id = null;

    public IdentifierParser(){
    }

    public IdentifierParser(String id){
        this.id = id;
    }

    @Override
    public Token parse(String source, int offset) {
        int i = offset-1;
        int state = 0;
        char c = 0;
        int slen = source.length();

        while(true){
            i++;
            if( i >= slen )break;

            c = source.charAt(i);
            switch( state ){
                case 0:
                    if( !( Character.isLetter(c) ||
                        c=='_'
                    ) )
                    {
                        state = -1;
                    }else{
                        state = 1;
                    }
                    break;
                case 1:
                    if( !( Character.isLetter(c) ||
                        c=='_' ||
                        Character.isDigit(c)
                    ) )
                    {
                        state = 99;
                    }else{
                        state = 1;
                    }
                    break;
            }

            if( state == -1 )return null;
            if( state == 99 )break;
        }

        if( state==99 || state==1 ){
            int len = (i - offset);
            Identifier idToken = new Identifier();
            idToken.setSource(source);
            idToken.setBegin(offset);
            idToken.setLength(len);
            if( id!=null )idToken.setId(id);
            return idToken;
        }

        return null;
    }
}
