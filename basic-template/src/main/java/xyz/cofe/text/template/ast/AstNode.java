package xyz.cofe.text.template.ast;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.collection.BasicVisitor;
import xyz.cofe.collection.IndexTree;
import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.collection.Visitor;
import xyz.cofe.iter.Eterable;

/**
 * Узел AST дерева
 * @author nt.gocha@gmail.com
 */
public abstract class AstNode
    implements IndexTree<AstNode>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(AstNode.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(AstNode.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(AstNode.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(AstNode.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(AstNode.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(AstNode.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(AstNode.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected AstNode[] children( AstNode ... nodes ){
        return nodes;
    }

    public static final NodesExtracter<AstNode, AstNode> astNodeExtracter
        = new NodesExtracter<AstNode, AstNode>() {
        @Override
        public Eterable<AstNode> extract( AstNode from) {
            if( from!=null ){
                return from.nodes().filter(Objects::nonNull);
            }
            return null;
        }
    };

    public static void visit( AstNode tree, Visitor<AstNode> visitor){
        if( visitor==null )throw new IllegalArgumentException( "visitor==null" );
        if( tree==null )throw new IllegalArgumentException( "tree==null" );
        BasicVisitor.visit(visitor, tree, astNodeExtracter);
    }
}
