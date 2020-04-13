package xyz.cofe.text.lex;

/**
 * Лексемма - пробелный символ.<br>
 * Пробелами считаются символы: <span style="font-family:monospaced"><b>'\r' '\n' '\t' ' '</b><i>(пробел)</i></span>
 * @author gocha
 */
public class WhiteSpaceParser implements TokenParser
{
    protected String id = null;

    public WhiteSpaceParser(){
    }
    public WhiteSpaceParser(String id){
        this.id = id;
    }

    @Override
    public Token parse(String source, int offset) {
        int i = -1;
        while((offset+i+1)<source.length()){
            i++;
            char c = source.charAt(offset+i);
            if( Character.isWhitespace(c) || c=='\r' || c=='\n' || c=='\t' || c==' ' )
            {
                continue;
            }else{
                if( i==0 )return null;
                break;
            }
        }

        WhiteSpace ws = new WhiteSpace();
        if( id!=null )ws.setId(id);

        if( i==0 ){
            ws.setBegin(offset);
            ws.setLength(1);
            ws.setSource(source);
            return ws;
        }

        ws.setBegin(offset);
        ws.setLength(i);
        ws.setSource(source);
        return ws;
    }
}
