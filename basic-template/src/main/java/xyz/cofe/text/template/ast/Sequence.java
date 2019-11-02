package xyz.cofe.text.template.ast;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AST контейнер - последовательность AST узлов
 * @author nt.gocha@gmail.com
 */
public class Sequence extends AstNode {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Sequence.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Sequence.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(Sequence.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Sequence.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Sequence.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Sequence.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Sequence.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public final AstNode previous;
    public final AstNode next;

    public Sequence(AstNode previous, AstNode next){
        if( previous==null )throw new IllegalArgumentException("previous==null");
        if( next==null )throw new IllegalArgumentException("next==null");

        this.next = next;
        this.next.setParent(this);

        this.previous = previous;
        this.previous.setParent(this);
    }
}
