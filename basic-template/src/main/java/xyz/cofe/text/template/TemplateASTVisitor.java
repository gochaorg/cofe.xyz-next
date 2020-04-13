package xyz.cofe.text.template;

import xyz.cofe.collection.BasicVisitor;
import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.iter.Eterable;
import xyz.cofe.text.template.ast.*;

import java.util.Objects;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Посетитель дерева AST
 * @author nt.gocha@gmail.com
 */
public class TemplateASTVisitor extends BasicVisitor<AstNode> {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(TemplateASTVisitor.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(TemplateASTVisitor.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(TemplateASTVisitor.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(TemplateASTVisitor.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(TemplateASTVisitor.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(TemplateASTVisitor.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(TemplateASTVisitor.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected Stack<AstNode> path = new Stack<AstNode>();

    @Override
    public void exit(AstNode obj) {
        if( obj instanceof Code ){
            exit((Code)obj);
        }else if( obj instanceof Block ){
            exit((Block)obj);
        }else if( obj instanceof BlockBody ){
            exit((BlockBody)obj);
        }else if( obj instanceof Sequence ){
            exit((Sequence)obj);
        }else if( obj instanceof Escape ){
            exit((Escape)obj);
        }else if( obj instanceof xyz.cofe.text.template.ast.Text ){
            exit((xyz.cofe.text.template.ast.Text)obj);
        }else if( obj instanceof AstNode ){
//                visit((AstNode)obj);
        }

        path.pop();
    }

    @Override
    public boolean enter(AstNode obj) {
        path.push(obj);

        if( obj instanceof Code ){
            enter((Code)obj);
            visit((Code)obj);
        }else if( obj instanceof Block ){
            enter((Block)obj);
            visit((Block)obj);
        }else if( obj instanceof BlockBody ){
            enter((BlockBody)obj);
            visit((BlockBody)obj);
        }else if( obj instanceof Sequence ){
            enter((Sequence)obj);
            visit((Sequence)obj);
        }else if( obj instanceof Escape ){
            enter((Escape)obj);
            visit((Escape)obj);
        }else if( obj instanceof xyz.cofe.text.template.ast.Text ){
            enter((xyz.cofe.text.template.ast.Text)obj);
            visit((xyz.cofe.text.template.ast.Text)obj);
        }else if( obj instanceof AstNode ){
//                enter((AstNode)obj);
            visit((AstNode)obj);
        }
        return true;
    }

    public void visit( Code code ){
    }

    public void enter( Code code ){
    }

    public void exit( Code code ){
    }

    public void visit( Block block ){
    }

    public void enter( Block block ){
    }

    public void exit( Block block ){
    }

    public void visit( BlockBody blockBody ){
    }

    public void enter( BlockBody blockBody ){
    }

    public void exit( BlockBody blockBody ){
    }

    public void visit( Sequence seq ){
    }

    public void enter( Sequence seq ){
    }

    public void exit( Sequence seq ){
    }

    public void visit( Escape escape ){
    }

    public void enter( Escape escape ){
    }

    public void exit( Escape escape ){
    }

    public void visit( xyz.cofe.text.template.ast.Text text ){
    }

    public void enter( xyz.cofe.text.template.ast.Text text ){
    }

    public void exit( xyz.cofe.text.template.ast.Text text ){
    }

    public void visit( AstNode val ){
    }

    private static final NodesExtracter<AstNode, AstNode> nodeExt
        = new NodesExtracter<AstNode, AstNode>() {
        @Override
        public Eterable<AstNode> extract( AstNode from) {
//            if( from instanceof BlockBody ){
//                return Iterators.array(
//                    ((BlockBody)from).body
//                );
//            }
//            if( from instanceof Code ){
//                return Iterators.array((AstNode)((Code)from).body
//                );
//            }
//            if( from instanceof Block ){
//                return Iterators.array(
//                    ((Block)from).body
//                );
//            }
//            if( from instanceof Sequence ){
//                return Iterators.array(
//                    ((Sequence)from).previous,
//                    ((Sequence)from).next
//                );
//            }
            if( from!=null ){
                return from.nodes().filter(Objects::nonNull);
            }
            return null;
        }
    };

    public static void visit( AstNode tree, TemplateASTVisitor visitor){
        if( visitor==null )throw new IllegalArgumentException( "visitor==null" );
        if( tree==null )throw new IllegalArgumentException( "tree==null" );
        TemplateASTVisitor.visit(visitor, tree, nodeExt);
    }
}
