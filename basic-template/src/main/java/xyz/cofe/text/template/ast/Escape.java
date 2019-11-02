package xyz.cofe.text.template.ast;

import xyz.cofe.text.lex.Token;

import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AST - Escape последовательность
 * @author nt.gocha@gmail.com
 */
public class Escape extends AstNode {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Escape.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Escape.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(Escape.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Escape.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Escape.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Escape.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Escape.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public final Token token;
    public final LinkedHashMap<String,String> rewriteMap;

    public Escape( Token token, LinkedHashMap<String,String> rewriteMap) {
        this.token = token;
        this.rewriteMap = rewriteMap;
    }
}
