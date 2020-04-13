package xyz.cofe.text.lex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Парсер "Тег"
 * <pre>
 * Тег ::= ( Буква | Цифра | '-' | '_' )+ [ ':' ( Буква | Цифра | '-' | '_' )+ ]
 * </pre>
 * @see TagName
 * @author gocha
 */
public class TagNameParser implements TokenParser
{
    public TagNameParser(){
    }

    public TagNameParser(String id){
        this.id = id;
    }

    protected String id = "tag";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private static final Pattern ptr = Pattern.compile("^([\\w|\\d|\\-|_]+)(:([\\w\\d\\-_]+))?");

    @Override
    public TagName parse(String source, int offset) {
        if( source==null )return null;
        if( offset<0 )return null;
        if( offset>=source.length() )return null;

        Matcher m = ptr.matcher(source);
        m.region(offset, source.length());
        if( m.find() ){
            return createToken(source, offset, m.end()-m.start(), m.group(1), m.group(3));
        }

        return null;
    }

    protected TagName createToken(String source,int offset,int len,String prefix,String suffix){
        return new TagName(getId(),source,offset,len,prefix,suffix);
    }
}
