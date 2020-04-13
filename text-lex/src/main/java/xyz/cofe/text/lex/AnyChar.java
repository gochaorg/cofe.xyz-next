package xyz.cofe.text.lex;

import xyz.cofe.text.Text;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Лексема представляющая любой текстовый символ
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class AnyChar extends Token {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(AnyChar.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(AnyChar.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(AnyChar.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(AnyChar.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(AnyChar.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(AnyChar.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(AnyChar.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public AnyChar() {
        setId("anyChar");
    }

    public AnyChar(String id, String source, int begin, int len) {
        super(id, source, begin, len);
    }

    public AnyChar(Token src) {
        super(src);
    }

    @Override
    public String toString(){
        return this.getId()+" "+Text.encodeStringConstant(getMatchedText());
    }
}
