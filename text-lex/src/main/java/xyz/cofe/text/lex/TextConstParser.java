package xyz.cofe.text.lex;

/**
 * Парсер текстовой константы
 * <pre>
 * text ::= doubleQuote { escapeText | anyChar } doubleQuote
 *        | singleQuote { escapeText | anyChar } singleQuote
 *
 * doubleQuote ::= <font style='background-color:#bbbbbb; color:#000000'>"</font>
 * singleQuote ::= <font style='background-color:#bbbbbb; color:#000000'>'</font>
 * escapeChar  ::= <font style='background-color:#bbbbbb; color:#000000'>\</font>
 * escapeText  ::= escapeChar
 *                 ( escapeChar
 *                 | singleQuote
 *                 | doubleQuote
 *                 | <font style='background-color:#bbbbbb; color:#000000'>n</font>
 *                 | <font style='background-color:#bbbbbb; color:#000000'>r</font>
 *                 | <font style='background-color:#bbbbbb; color:#000000'>t</font>
 *                 | <font style='background-color:#bbbbbb; color:#000000'>b</font>
 *                 | <font style='background-color:#bbbbbb; color:#000000'>f</font>
 *                 | <font style='background-color:#bbbbbb; color:#000000'>u</font> hexDigit
 *                 )
 * hexDigit ::= <font style='background-color:#bbbbbb; color:#000000'>0..9</font>
 *            | <font style='background-color:#bbbbbb; color:#000000'>a..f</font>
 *            | <font style='background-color:#bbbbbb; color:#000000'>A..F</font>
 * </pre>
 * @author gocha
 */
public class TextConstParser implements TokenParser
{
    protected String id = null;

    public TextConstParser(){
    }

    public TextConstParser(String id){
        this.id = id;
    }

    @Override
    public TextConst parse(String source, int offset) {
        int i = offset-1;
        int state = 0;
        char c = 0;
        int slen = source.length();
        StringBuilder txt = new StringBuilder();
        StringBuilder hex = new StringBuilder();
        int retstate = 1;

        while(true){
            i++;
            if( !(i < slen) )break;

            c = source.charAt(i);
            switch(state){
                case 0: // start
                    switch(c){
                        case '"':
                            state = 1;
                            retstate = 1;
                            break;
                        case '\'':
                            state = 2;
                            retstate = 2;
                            break;
                        default:
                            state = -1;
                    }
                    break;
                case 1: // escape | end | char
                    switch(c){
                        case '\\':
                            state = 100;
                            break;
                        case '"':
                            state = 99;
                            break;
                        default:
                            txt.append(c);
                    }
                    break;
                case 2: // escape | end | char
                    switch(c){
                        case '\\':
                            state = 100;
                            break;
                        case '\'':
                            state = 99;
                            break;
                        default:
                            txt.append(c);
                    }
                    break;
                case 100: // escape convert | unicode
                    switch(c){
                        case '\\':
                            state = retstate;
                            txt.append("\\");
                            break;
                        case '"':
                            state = retstate;
                            txt.append('"');
                            break;
                        case '\'':
                            state = retstate;
                            txt.append('\'');
                            break;
                        case 'n':
                            state = retstate;
                            txt.append("\n");
                            break;
                        case 'r':
                            state = retstate;
                            txt.append("\r");
                            break;
                        case 'b':
                            state = retstate;
                            txt.append("\b");
                            break;
                        case 'f':
                            state = retstate;
                            txt.append("\f");
                            break;
                        case 't':
                            state = retstate;
                            txt.append("\t");
                            break;
                        case 'u':
                            state = 103;
                            break;
                        default:
                            state = -1;
                            break;
                    }
                    break;
                case 103: // unicode - 1 digit
                    switch( c ){
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                        case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                        case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                            hex.setLength(0);
                            hex.append(c);
                            state = 104;
                            break;
                        default:
                            state = -1;
                            break;
                    }
                    break;
                case 104: // unicode - 2 digit
                    switch( c ){
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                        case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                        case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                            hex.append(c);
                            state = 105;
                            break;
                        default:
                            state = -1;
                            break;
                    }
                    break;
                case 105: // unicode - 3 digit
                    switch( c ){
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                        case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                        case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                            hex.append(c);
                            state = 106;
                            break;
                        default:
                            state = -1;
                            break;
                    }
                    break;
                case 106: // unicode - 4 digit
                    switch( c ){
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                        case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                        case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                            hex.append(c);
                            int hexVal = Integer.parseInt(hex.toString(), 16);
                            char[] hexChars = Character.toChars(hexVal);
                            for(char hc : hexChars )txt.append(hc);
                            state = retstate;
                            break;
                        default:
                            state = -1;
                            break;
                    }
                    break;
            }
            if( state == -1 )return null;
            if( state == 99 )break;
        }

        if( state!=99 )return null;

        int len = (i - offset) + 1;
        TextConst tct = new TextConst();
        tct.setSource(source);
        tct.setBegin(offset);
        tct.setLength(len);
        tct.setDecodedText(txt.toString());
        if( id!=null )tct.setId(id);

        return tct;
    }
}
