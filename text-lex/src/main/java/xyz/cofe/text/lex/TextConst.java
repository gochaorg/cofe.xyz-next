package xyz.cofe.text.lex;

import xyz.cofe.text.Text;

/**
 * Текстовая константа
 * <code>"lalala \escape"</code>
 * @author gocha
 */
public class TextConst extends Token
{
    public TextConst(){
        this.id = "textConst";
    }

    @Override
    public String getId() {
        if( id==null )id = "textConst";
        return super.getId();
    }

    protected String decodedText = null;

    public String getDecodedText() {
        return decodedText;
    }

    public void setDecodedText(String decodedText) {
        this.decodedText = decodedText;
    }

    @Override
    public String toString() {
        return this.getId()+" "+
            (decodedText==null ? "null" : Text.encodeStringConstant(decodedText))
            ;
    }
}
