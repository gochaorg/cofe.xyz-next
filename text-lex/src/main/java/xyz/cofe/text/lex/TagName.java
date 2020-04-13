package xyz.cofe.text.lex;

/**
 * Лексема имени XML тэга
 * @author gocha
 */
public class TagName extends Token
{
    public TagName(){
        setId("tag");
    }

    public TagName(String id, String source,int b,int l,String prefix,String suffix){
        super(id==null ? "tag" : id, source, b, l);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public TagName(String id, String source,int b,int l){
        super(id==null ? "tag" : id, source, b, l);
    }

    protected String prefix = null;
    protected String suffix = null;

    public String getPrefix() { return prefix; }

    public String getSuffix() { return suffix; }

    public String getLocalName(){ return this.getMatchedText(); }
}
