package xyz.cofe.text.lex;

import xyz.cofe.text.Text;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Лексема - доступ к полю: identifier { '.' identifier }
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class FieldAccess
    extends Token
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(FieldAccess.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(FieldAccess.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(FieldAccess.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(FieldAccess.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(FieldAccess.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(FieldAccess.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(FieldAccess.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    private final Token beginToken;
    private final Token endToken;
    private final Token[] ids;

    public FieldAccess( String id, String source, Token beginToken, Token endToken, Token[] ids) {
        super(id, source,
            beginToken.getBegin(),
            (endToken.getBegin()+endToken.getLength()) - beginToken.getBegin()
        );
        this.beginToken = beginToken;
        this.endToken = endToken;
        this.ids = ids;
    }

    public Token getBeginToken() {
        return beginToken;
    }

    public Token getEndToken() {
        return endToken;
    }

    public Token[] getIdTokens() {
        return ids;
    }

    public String[] getAccessPath(){
        Token[] tids = getIdTokens();
        String[] res = new String[tids.length];
        for( int i=0; i<res.length; i++ ){
            res[i] = tids[i].getMatchedText();
        }
        return res;
    }

    @Override
    public String toString() {
        return getId() + " "+ Text.join(getAccessPath(), ".");
    }
}
