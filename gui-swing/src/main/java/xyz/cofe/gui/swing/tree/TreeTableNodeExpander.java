package xyz.cofe.gui.swing.tree;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Узел осуществляющий чтение итератора и вставку прочитанных на свое место
 * @author nt.gocha@gmail.com
 */
public class TreeTableNodeExpander extends TreeTableNodeBasic
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableNodeExpander.class.getName());
    private static Level logLevel(){ return logger.getLevel(); }

    private static boolean isLogSevere(){
        Level logLevel = logger.getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }

    private static boolean isLogWarning(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }

    private static boolean isLogInfo(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }

    private static boolean isLogFine(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }

    private static boolean isLogFiner(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }

    private static boolean isLogFinest(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
    }

    private static void logFine(String message,Object ... args){
        logger.log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        logger.log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        logger.log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        logger.log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }

    private static void logEntering(String method,Object ... params){
        logger.entering(TreeTableNodeExpander.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeTableNodeExpander.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeTableNodeExpander.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param iterator итератор по дочерним узлам
     */
    public TreeTableNodeExpander( Iterator iterator ){
        if( iterator==null )throw new IllegalArgumentException("iterator == null");
        this.iterable = null;
        this.iterator = iterator;

        this.totalReaded = 0;
        this.totalReadTime = 0;
        this.lastReaded = 0;
        this.lastReadTime = 0;

        logFine("created TreeTableNodeExpander with iterator");
    }

    /**
     * Конструктор
     * @param iterable итератор по дочерним узлам
     */
    public TreeTableNodeExpander( Iterable iterable ){
        if( iterable==null )throw new IllegalArgumentException("iterable == null");
        this.iterable = iterable;
        this.iterator = iterable.iterator();

        this.totalReaded = 0;
        this.totalReadTime = 0;
        this.lastReaded = 0;
        this.lastReadTime = 0;

        logFine("created TreeTableNodeExpander with iterable");
    }

    protected Iterable iterable;

    /**
     * Возвращает интератор по дочерним узлам
     * @return итератор по извлекаемым узлам
     */
    public Iterable getIterable(){ return iterable; }

    protected Iterator iterator;

    /**
     * Возвращает интератор по дочерним узлам
     * @return итератор по извлекаемым узлам
     */
    public Iterator getIterator(){ return iterator; }

    protected int totalReaded;

    /**
     * Возвращает кол-во извлеченных узлов
     * @return кол-во извлеченных узлов
     */
    public int getTotalReaded(){ return totalReaded; }

    protected long totalReadTime;

    /**
     * Возвращает суммарное время извлечения узлов
     * @return суммарное время (мс)
     */
    public long getTotalReadTime(){ return totalReadTime; }

    protected int lastReaded;

    /**
     * Возвращает кол-во извлеченных узлов за последний вызов
     * @return кол-во извлеченных узлов за последний вызов
     */
    public int getLastReaded(){ return lastReaded; }

    protected long lastReadTime;

    /**
     * Возвращает время последнего извлечения дочерних узлов
     * @return время (мс)
     */
    public long getLastReadTime(){ return lastReadTime; }

    protected long lastInsertTime = 0;

    /**
     * Возвращает кол-во эл последней вставки
     * @return кол-во элементов
     */
    public long getLastInsertTime(){ return lastInsertTime; }

    protected long totalInsertTime = 0;

    /**
     * Возвращает время последней вставки извлеченных улов
     * @return время вставки (мс)
     */
    public long getTotalInsertTime(){ return totalInsertTime; }

    /**
     * Возвращает сумарное время работы
     * @return суммарное время (мс)
     */
    public long getTotalTime(){
        return totalReadTime + totalInsertTime;
    }

    protected boolean removeOnFinish = TreeTableNodeExpanderImpl.isRemoveOnFinish();

    /**
     * Возвращает удалять текущий объект из дерева по заверению извлечения
     * @return true - удалять
     */
    public boolean isRemoveOnFinish() { return removeOnFinish; }

    /**
     * Указывает удалять текущий объект из дерева по заверению извлечения
     * @param removeOnFinish true - удалять
     */
    public void setRemoveOnFinish(boolean removeOnFinish) { this.removeOnFinish = removeOnFinish; }

    /**
     * Возвращает есть-ли дочерние не извлеченные узлы
     * @return true - есть не извлеченные узлы
     */
    public boolean hasNext(){
        if( iterator==null )return false;
        return iterator.hasNext();
    }

    /**
     * Указывает узел приемник дочерних узлов
     * @return узел дерева принимающий дочерние узлы
     */
    public TreeTableNodeBasic getTargetRecipient(){
        TreeTableNodeBasic prnt = (TreeTableNodeBasic)getParent();
        return prnt;
    }

    /**
     * Возвращает индекс куда производить вставку извлеченных узлов
     * @return индекс, значение может быть Integer.MAX_VALUE, если не получилось определить елевой индекс
     */
    public int getTargetIndex(){
        int idx = getSibIndex();
        if( idx<0 ) return Integer.MAX_VALUE;
        return idx;
    }

    //protected boolean insertAtRead = true;
    //public boolean isInsertAtRead() { return insertEachCount > 0; }
    //public void setInsertAtRead(boolean insertAtRead) { this.insertAtRead = insertAtRead; }

    protected int insertEachCount = TreeTableNodeExpanderImpl.getInsertEachCount();

    /**
     * Возвращает кол-во вставляемых элементов за один шаг
     * @return кол-во вставляемых дочерних узлов за раз
     */
    public int getInsertEachCount() { return insertEachCount; }

    /**
     * Указывает кол-во вставляемых элементов за один шаг
     * @param insertEachCount кол-во вставляемых дочерних узлов за раз
     */
    public void setInsertEachCount(int insertEachCount) { this.insertEachCount = insertEachCount; }

    protected long timeout = TreeTableNodeExpanderImpl.getTimeout();

    /**
     * Возвращает время за которое должена быть произведено извлечение и вставка узлов
     * @return ограничение времени (мс) извлечения и вставки
     */
    public long getTimeout() { return timeout; }

    /**
     * Указывает время за которое должена быть произведено извлечение и вставка узлов
     * @param timeout ограничение времени (мс) извлечения и вставки
     */
    public void setTimeout(long timeout) { this.timeout = timeout; }

    protected boolean cacheFetched = TreeTableNodeExpanderImpl.isCacheFetched();

    /**
     * Указывает что извлеченные узлы должны добавляться в кеш дочерних улов
     * @return кешировать извлеченные узлы
     */
    public boolean isCacheFetched() { return cacheFetched; }

    /**
     * Указывает что извлеченные узлы должны добавляться в кеш дочерних улов
     * @param cacheFetched кешировать извлеченные узлы
     */
    public void setCacheFetched(boolean cacheFetched) { this.cacheFetched = cacheFetched; }

    /**
     * Извлечение очередной порции дочерних улов
     */
    public void fetch(){
        logFine("fetch begin");

        fetch0();

        logFine("fetch end, readed={0} t.read={1}ms t.insert={2}", lastReaded, lastReadTime, lastInsertTime);
    }

    private void fetch0(){
        lastReaded = 0;
        lastReadTime = 0;
        lastInsertTime = 0;

        Iterator itr = getIterator();
        if( itr==null )return;

        TreeTableNodeBasic recipient = getTargetRecipient();
        if( recipient==null )return;

        int trgtIdx = getTargetIndex();
        if( itr.hasNext()==false ){
            readFinished();
            return;
        }

        boolean insertAtEnd = false;

        //List<TreeTableNodeBasic> recipientList = recipient.getChildrenList();
        if( trgtIdx>=recipient.count() ){
            // insert at end
            insertAtEnd = true;
        }else{
            // insert at idx
            insertAtEnd = false;
        }

        List<TreeTableNodeBasic> nodeList = createNodeList();

        long readStart = System.currentTimeMillis();
        long readFinish = System.currentTimeMillis();

        boolean tInterrupt = false;
        boolean insAtRead = insertEachCount > 0;

        long timeoutStart = System.currentTimeMillis();

        while( true ){
            if( Thread.interrupted() ){
                tInterrupt = true;
                logFine("interrupt thread");
                break;
            }

            long timeoutCurrent = System.currentTimeMillis();
            long timeoutDiff = Math.abs(timeoutCurrent - timeoutStart);
            long timeoutVal = getTimeout();
            if( timeoutVal>0 && timeoutDiff >= timeoutVal){
                logFine("interrupt by timeout={0}ms - now={1}ms", timeoutVal, timeoutDiff);
                break;
            }

            if( itr.hasNext() ){
                Object fetched = itr.next();
                nodeList.add(createNode(fetched));

                if( insAtRead ){
                    int insEachN = getInsertEachCount();
                    if( !nodeList.isEmpty() && (insEachN==1 || nodeList.size() >= insEachN) ){
                        readFinish = System.currentTimeMillis();
                        lastReadTime += Math.abs( readStart - readFinish );
                        readStart = readFinish;

//                        if( recipientList instanceof BulkInsert ){
//                            if( insertAtEnd ){
//                                int rsize = recipientList.size();
//                                ((BulkInsert) recipientList).bulkInsert(rsize,nodeList);
//                            }else{
//                                ((BulkInsert) recipientList).bulkInsert(trgtIdx,nodeList);
//                            }
//                        }else{
                            if( insertAtEnd ){
                                //recipientList.addAll(nodeList);
                                recipient.appends(nodeList);
                            }else{
                                //recipientList.addAll(trgtIdx, nodeList);
                                recipient.inserts(trgtIdx, nodeList);
                            }
//                        }

                        trgtIdx += nodeList.size();

                        if( cacheFetched ){
                            for( TreeTableNodeBasic nb : nodeList ){
                                recipient.getCachedNodes().put(nb, new Date());
                            }
                        }

                        long tInserted = System.currentTimeMillis();
                        lastInsertTime += Math.abs( readFinish - tInserted );

                        lastReaded += nodeList.size();

                        nodeList.clear();
                    }
                }
            }else{
                readFinished();
                break;
            }
        }

        readFinish = System.currentTimeMillis();

        if( !insAtRead ){
            if( !nodeList.isEmpty() ){
//                if( recipientList instanceof BulkInsert ){
//                    if( insertAtEnd ){
//                        int rsize = recipientList.size();
//                        ((BulkInsert) recipientList).bulkInsert(rsize,nodeList);
//                    }else{
//                        ((BulkInsert) recipientList).bulkInsert(trgtIdx,nodeList);
//                    }
//                }else{
                    if( insertAtEnd ){
                        recipient.appends(nodeList);
                    }else{
                        recipient.inserts(trgtIdx, nodeList);
                    }
//                }
            }

            if( cacheFetched ){
                for( TreeTableNodeBasic nb : nodeList ){
                    recipient.getCachedNodes().put(nb, new Date());
                }
            }

            long insertFinished = System.currentTimeMillis();

            lastReaded = nodeList.size();
            lastReadTime = Math.abs(readFinish - readStart);
            lastInsertTime = Math.abs(readFinish - insertFinished);
        }else{
            readFinish = System.currentTimeMillis();
            lastReadTime += Math.abs( readStart - readFinish );
            readStart = readFinish;

            if( insertAtEnd ){
                recipient.appends(nodeList);
            }else{
                recipient.inserts(trgtIdx, nodeList);
            }

            trgtIdx += nodeList.size();

            long tInserted = System.currentTimeMillis();
            lastInsertTime += Math.abs( readFinish - tInserted );

            lastReaded += nodeList.size();

            if( cacheFetched ){
                for( TreeTableNodeBasic nb : nodeList ){
                    recipient.getCachedNodes().put(nb, new Date());
                }
            }

            nodeList.clear();
        }

        totalReaded += lastReaded;
        totalReadTime += lastReadTime;
        totalInsertTime += lastInsertTime;
    }

    protected List<TreeTableNodeBasic> createNodeList(){
        return new ArrayList<>();
    }

    protected TreeTableNodeBasic createNode( Object data ){
        return new TreeTableNodeBasic(data);
    }

    protected void readFinished(){
        removeSelf();
    }

    protected void removeSelf(){
        TreeTableNodeBasic parent = (TreeTableNodeBasic)getParent();

        if( parent!=null ){
            logFine("removeSelf {0}",this);

            treeNotify(
                new TreeTableExpanderFinish(this)
            );

            parent.delete(this);
            setParent(null);
        }
    }

    @Override
    public void follow() {
        //super.follow(); //To change body of generated methods, choose Tools | Templates.
        fetch();
    }

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public void expand() {
        fetch();
    }
}
