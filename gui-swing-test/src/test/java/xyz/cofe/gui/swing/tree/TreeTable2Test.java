package xyz.cofe.gui.swing.tree;

import xyz.cofe.fn.Tuple2;
import xyz.cofe.gui.swing.tmodel.WrapTM;
import xyz.cofe.text.Align;
import xyz.cofe.text.Text;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreeTable2Test extends JFrame {
    public static class GNode {
        public static final AtomicInteger idSeq = new AtomicInteger(0);
        public final int id = idSeq.incrementAndGet();
        public int getId(){ return id; }

        private List<GNode> children;
        public List<GNode> getChildren(){
            if( children!=null )return children;
            children = new ArrayList<>();
            int cnt =
                //ThreadLocalRandom.current().nextInt(100) + 10;
                20;
            for( int i=0; i<cnt; i++ ){
                children.add(new GNode());
            }
            return children;
        }

        @Override
        public String toString(){
            return "GNode#"+id;
        }
    }

    public TreeTable2Test(){
        setTitle("TreeTable2Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,400);
        setLocationRelativeTo(null);

        treeTable = new TreeTable();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add( new JScrollPane(treeTable) );

        TreeTableNodeBasic root = new TreeTableNodeBasic(new GNode());
        treeTable.setRoot(root);
        treeTable.setRootVisible(true);
        root.setCacheLifeTime(1000L);
        treeTable.setRowHeight(23);

        root.addTreeListener(ev -> {
            System.out.println(ev);
            TreeTableModel ttm = treeTable.getTreeTableModel();
            int rc = ttm.getRowCount();

            List<Tuple2<Integer,TreeTableNode>> nodes = IntStream.range(0,rc).mapToObj(i-> Tuple2.of(i,ttm.getNodeOf(i))).collect(Collectors.toList());
            long nullCnt = nodes.stream().filter(p->p.b()==null).count();
            if( nullCnt>0 ){
                System.out.println("!!! has null tree nodes, count="+nullCnt);
                System.out.println(
                    nodes.stream().filter(p->p.b()==null).map(p->p.a()).map(x->x.toString()).reduce("",(a,b)->a+","+b
                ));
            }
        });

        //region follow
        root.setDataFollowable((n)->{
            if( n instanceof GNode )return true;
            return false;
        });
        root.setDataFollower(n -> {
            if( n instanceof GNode ){
                Iterable itr = ((GNode)n).getChildren();
                return itr;
            }
            return null;
        });
        //endregion
    }

    private TreeTable treeTable;

    private static String messageFormat(String tmpl,Object[] params){
        if( tmpl==null )return "";
        String txt = tmpl;
        if( params!=null ){
            for( int i=0; i<params.length; i++ ){
                txt = txt.replace("{"+i+"}", params[i]!=null ? params[i].toString() : "null");
            }
        }
        return txt;
    }

    static {
        Formatter fmt = new Formatter() {
            @Override
            public String format( LogRecord record ){
                if( record==null )return "";

                StringBuilder sb = new StringBuilder();

                if( record.getLevel()!=null ){
                    sb.append(Text.align(record.getLevel().toString(), Align.Begin, " ", 8));
                    sb.append("|");
                }

                if( record.getLoggerName()!=null ){
                    String lgr = record.getLoggerName();
                    String[] lgr1 = lgr.split("\\.");
                    sb.append(Text.align(lgr, Align.Begin," ",45,true));
                    sb.append("|");
                }

                sb.append( messageFormat(record.getMessage(), record.getParameters()) );

                return sb.toString()+System.lineSeparator();
            }
        };

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFilter( rec ->
            rec.getLoggerName().matches("(?is).*(TreeTableFilterModel|WrapTM).*")
        );
        ch.setFormatter(fmt);

        Logger.getLogger("").addHandler(ch);
        Logger.getLogger(TreeTableFilterModel.class.getName()).setLevel(Level.ALL);
        Logger.getLogger(WrapTM.class.getName()).setLevel(Level.ALL);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->{
            TreeTable2Test f = new TreeTable2Test();
            f.setVisible(true);
        });
    }
}
