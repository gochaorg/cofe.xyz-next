package xyz.cofe.text.template.ast;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AST тело блока кода
 * @author nt.gocha@gmail.com
 */
public class BlockBody extends AstNode {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(BlockBody.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(BlockBody.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(BlockBody.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(BlockBody.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(BlockBody.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(BlockBody.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(BlockBody.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public final AstNode body;

    public BlockBody(AstNode body) {
        if( body==null )throw new IllegalArgumentException( "body==null" );
        this.body = body;
        body.setParent(this);
    }
}
