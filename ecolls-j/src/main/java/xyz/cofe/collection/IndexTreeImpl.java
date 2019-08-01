package xyz.cofe.collection;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Реализация IndexTree
 */
public class IndexTreeImpl {
    private static final WeakHashMap<Object,Integer> nodesCount = new WeakHashMap<>();
    public static int getNodesCount(IndexTree<?> node){
        if( node == null ) throw new IllegalArgumentException("node==null");

        Integer cachedCnt = nodesCount.get(node);
        if( cachedCnt!=null )return cachedCnt;

        AtomicInteger sum = new AtomicInteger(0);
        node.nodes().forEach( n -> sum.addAndGet(n.getNodesCount()) );

        sum.incrementAndGet();
        nodesCount.put(node,sum.get());

        return sum.get();
    }
    public static void setNodesCount(IndexTree node, Integer count){
        if( node == null ) throw new IllegalArgumentException("node==null");
        if( count==null ){
            nodesCount.remove(node);
        }else{
            nodesCount.put(node,count);
        }
    }

    public static int getRootOffsetOf( IndexTree node ){
        return getRootOffsetOf( node, null );
    }

    private static boolean confRootOffsetCheckCycle(){ return true; }

    private static int getRootOffsetOf( IndexTree node, Set visited ){
        if( node==null )throw new IllegalArgumentException("node==null");
        if( confRootOffsetCheckCycle() && visited==null )visited = new LinkedHashSet();

        int off = 0;
        while( true ){
            if( confRootOffsetCheckCycle() && visited.contains(node) ){
                throw new Error( "cycle detect" );
            }
            if( confRootOffsetCheckCycle() )visited.add(node);

            IndexTree parent = (IndexTree)node.getParent();
            if( parent==null )return off;

            IndexTree sib = (IndexTree)node.getPreviousSibling();
            if( sib==null ){
                off++;
                node = parent;
                continue;
            }

            int sibncnt = sib.getNodesCount();
            off += sibncnt;
            node = sib;
        }
    }

    public static IndexTree deepOffset( IndexTree node, int offset ) {
        return deepOffset(node, offset, null);
    }

    private static boolean conf_deepOffset_CheckCycle(){ return true; }

    private static IndexTree deepOffset( IndexTree node, int offset, Set visited ) {
        if( node==null )throw new IllegalArgumentException("node==null");

        if( conf_deepOffset_CheckCycle() && visited==null )visited = new LinkedHashSet();
        if( conf_deepOffset_CheckCycle() && visited.contains(node) )throw new Error( "cycle detect" );
        if( conf_deepOffset_CheckCycle() )visited.add(node);

        if( offset == 0 )return node;

        if( offset>0 ){
            return offsetRight(node, offset, visited);
        }

        return offsetLeft(node, offset, visited);
    }

    private static IndexTree moveNext( IndexTree node, Set visited ){
        if( node==null )throw new IllegalArgumentException("node==null");
        if( visited==null )visited = new LinkedHashSet();
        //if( visited.contains(node) )throw new Error( "cycle detect" );

//        IndexTree[] children = node.getChildren();
//        if( children!=null && children.length>0 ){
//            Node n = (Node)children[0];
//            return n;
//        }

        IndexTree sib = (IndexTree)node.getNextSibling();
        if( sib!=null )return (IndexTree)sib;

        // move up to sib
        IndexTree p = (IndexTree)node.getParent();
        while ( true ){
            if( p==null )break;

            sib = (IndexTree)p.getNextSibling();
            if( sib!=null )return (IndexTree)sib;

            p = (IndexTree)p.getParent();
        }

        return null;
    }

    private static IndexTree movePrevious( IndexTree node, Set visited ){
        if( node==null )throw new IllegalArgumentException("node==null");
        if( visited==null )visited = new LinkedHashSet();
        // if( visited.contains(node) )throw new Error( "cycle detect" );

        IndexTree sib = (IndexTree)node.getPreviousSibling();
        if( sib!=null ){
            int ncount = sib.getNodesCount();
            if( ncount<1 ) return null; // Error
            if( ncount==1 )return (IndexTree)sib;
            return offsetRight((IndexTree)sib, ncount-1, visited);
        }

        return (IndexTree)node.getParent();
    }

    private static IndexTree offsetLeft( IndexTree node, int offset, Set visited ){
        if( node==null )throw new IllegalArgumentException("node==null");
        if( visited==null )visited = new LinkedHashSet();
        // if( visited.contains(node) )throw new Error( "cycle detect" );
        visited.add(node);

        if( offset == 0 )return node;
        if( offset > 0 )return offsetRight(node, offset, visited);

        IndexTree sib = (IndexTree)node.getPreviousSibling();
        if( sib==null ){
            IndexTree prev = movePrevious(node, visited);
            if( prev==null )return null;

            //if( visited.contains(prev) )throw new Error( "cycle detect" );
            return offsetLeft(prev, offset+1, visited);
        }

        int sibncount = sib.getNodesCount();
        int toff = offset + sibncount;

        if( (toff)==0 )return sib;
        if( (toff)>0 ){
            // if( visited.contains(sib) )throw new Error( "cycle detect" );
            // visited.add(sib);
            return offsetRight(sib, toff, visited);
        }

        //if( visited.contains(sib) )throw new Error( "cycle detect" );
        return offsetLeft( sib, toff, visited );
    }

    // Движение в право
    // offset < 0 - результат null
    // offset = 0 - результат node аргумент
    // offset > 0 && offset < node.getNodesCount() - результат (суб)дочерний узел
    // offset >= node.getNodesCount() - результат cоседние или суб-соседние узлы
    private static IndexTree offsetRight( IndexTree node, int offset, Set visited ){
        if( node==null )throw new IllegalArgumentException("node==null");
        if( visited==null )visited = new LinkedHashSet();
        //if( visited.contains(node) )throw new Error( "cycle detect" );

        while( true ){
            if( offset == 0 )return node;
            if( offset < 0 )return null;

            int nCounts = node.getNodesCount();

            List<IndexTree> children = node.nodes().toList();

//            children =
//                node instanceof TreeNodeChildrenList
//                    ? ((TreeNodeChildrenList)node).getChildrenList()
//                    : Arrays.asList(node.getChildren());

            // По сути тоже не возможная ситуация
            if( children.size() <= 0 && nCounts > 1 ){
//                logWarning("bag!!! cnt="+nCounts+" children.length="+children.size());
                Logger.getLogger(IndexTreeImpl.class.getName()).severe("bag!!! cnt=" + nCounts + " children.length=" + children.size());

                setNodesCount(node, null);
                nCounts = node.getNodesCount();
            }

            if( nCounts<1 )return null;

            //offset--;

            int minoff = 0;
            int maxoff = nCounts - 1;

            if( offset < minoff ){
                // движение влево - не возможная ситуация
                return null;
            }

            if( offset > maxoff ){
                // Движение за пределы поддеррева

                if( offset < nCounts ){ // не возможная ситуация
                    throw new Error( "bag!!" );
                }

                // Переходим к соседу с права
                IndexTree nextSib = (IndexTree)node.getNextSibling();
                if( nextSib!=null ){
                    offset -= nCounts;
                    node = nextSib;
                    continue;
                }

                // Не перешли, тогда обычным путем
                // движение к след по ходу узлу
                IndexTree nextNode = moveNext(node, visited);
                if( nextNode==null )return null;

                //return (Node)deepOffset( (Node)nextNode, offset-1, visited);
                node = nextNode;
                offset--;
                continue;
            }

            // движение вниз
            if( children==null )throw new Error( "bag!!!" );
            if( children.size()<0 )throw new Error( "bag!!!!" );
            if( children.size()<1 ){
                Logger.getLogger(IndexTreeImpl.class.getName()).severe("!! offsetRight, children.length="+children.size());
            }

            int offsum = 0;

            IndexTree child = null;
            for( int ci=0; ci<children.size(); ci++ ){
                child = children.get(ci);
                if( child==null )continue;

                //if( !(child instanceof TreeNodeDeepOffset) )throw new Error( "bag!!!!!" );

                IndexTree tndo = (IndexTree)child;
                int ncount = tndo.getNodesCount();
                if( ncount<=0 )throw new Error( "bag!!!!!" );

                minoff = offsum+1;
                int mmxoff = minoff+ncount;

                if( !(offset >= minoff && offset < mmxoff) ){
                    // вне диапазона мин/макс доч.смещения
                    offsum += ncount;
                    continue;
                }

                // return (Node)tndo.deepOffset(offset - offsum - 1);
                return (IndexTree)deepOffset( (IndexTree)tndo,offset - offsum - 1, visited );
            }

            return null;
        }
    }
}
