package xyz.cofe.collection;

/**
 * Интерфейс для реализации дерева с подсчетом улозв
 */
public interface IndexTree<A extends IndexTree<A>> extends UpTree<A> {
    @Override
    default void treeNotify(TreeEvent<A> event) {
        TreeNotifyImpl.treeNotify(this,event);
        IndexTreeImpl.setNodesCount(this,null);
    }

    /**
     * Возвращает кол-во узлов включая себя и всех вложенных
     * @return кол-во узлов (всегда больше 0)
     */
    default int getNodesCount(){
        return IndexTreeImpl.getNodesCount(this);
    }

    /**
     * Возвращает смещение узла относительно корня древа
     * @return смещение (0 для корня).
     */
    default int getRootOffset(){
        return IndexTreeImpl.getRootOffsetOf(this);
    }

    /**
     * Возвращает узел относительно текущего в древе, движение согласно порядку обхода узлов вглубь.
     * @param offset смещение,<br>
     * для 0 - вернет текущий. <br>
     * @return Узел в древе относительно указанного или null
     */
    default A deepOffset(int offset){
        return (A)IndexTreeImpl.deepOffset(this,offset);
    }
}
