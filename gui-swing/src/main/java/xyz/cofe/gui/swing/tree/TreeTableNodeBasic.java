package xyz.cofe.gui.swing.tree;

import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.ecolls.Fn1;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreeTableNodeBasic
    implements TreeTableNode<TreeTableNodeBasic>, TreeTableNodeGetText
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableNodeBasic.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(TreeTableNodeBasic.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(TreeTableNodeBasic.class.getName(), method, result);
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TreeTableNodeBasic()">
    /**
     * Конструктор по умолчанию
     */
    public TreeTableNodeBasic(){
    }

    /**
     * Конструктор
     * @param data данные узла для отображения/редактирования
     */
    public TreeTableNodeBasic(Object data){
        setData(data);
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     * @param withChildren Копировать с дочерними узлами
     * @param preferred Копировать предпочтительные функции/данные (getPreffered...())
     */
    public TreeTableNodeBasic(TreeTableNodeBasic sample, boolean withChildren, boolean preferred){
        if( sample!=null ){
            setData( sample.getData() );

            setCacheLifeTime( preferred ? sample.getPreferredCacheLifeTime() : sample.getCacheLifeTime() );
            setDataFollowable( preferred ? sample.getPreferredDataFollowable() : sample.getDataFollowable() );
            setDataFollower( preferred ? sample.getPreferredDataFollower() : sample.getDataFollower() );
            setDataTextReader( sample.getDataTextReader() );
            setDataFormatter( preferred ? sample.getPreferredDataFormatter() : sample.getDataFormatter() );

            Map<TreeTableNode,Date> sampleCached = new LinkedHashMap<TreeTableNode,Date>();
            sampleCached.putAll(sample.getCachedNodes());

            setFollowStarted(sample.getFollowStarted());
            setFollowStarted(sample.getFollowFinished());

            if( withChildren ){
                var sampleChildren = sample.nodes();
                //List<TreeTableNodeBasic> children = getChildrenList();

                if( sampleChildren!=null ){
                    Map<TreeTableNode,TreeTableNode> clones = new LinkedHashMap<TreeTableNode,TreeTableNode>();

                    for( TreeTableNodeBasic schild : sampleChildren ){
                        if( schild==null )continue;

                        TreeTableNodeBasic child = schild.clone(withChildren, preferred);
                        clones.put(schild, child);
                        append(child);
                    }

                    for( Map.Entry<TreeTableNode,Date> en : sampleCached.entrySet() ){
                        TreeTableNode schild = en.getKey();
                        Date d = en.getValue();
                        if( schild==null )continue;

                        TreeTableNode child = clones.get(schild);
                        if( child==null )continue;

                        getCachedNodes().put(child, d);
                    }
                }
            }

            setExpanded(sample.isExpanded());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="clone()">
    /**
     * Создание клона узла с дочерними узлами.
     * @return клон
     */
    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public TreeTableNodeBasic clone(){
        TreeTableNodeBasic cloned = new TreeTableNodeBasic(
            TreeTableNodeBasic.this, true, false);
        return cloned;
    }

    /**
     * Создание клона
     * @param withChildren клонировать так-же дочерние узлы
     * @param preferred Копировать предпочтительные функции/данные (getPreffered...())
     * @return Клон
     */
    public TreeTableNodeBasic clone( final boolean withChildren, final boolean preferred ){
        TreeTableNodeBasic cloned = new TreeTableNodeBasic(
            TreeTableNodeBasic.this, withChildren, preferred);
        return cloned;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Форматирование">
    /**
     * Форматирование вывода
     */
    protected TreeTableNodeGetFormatOf dataFormatter;

    /**
     * Возвращает форматтер данных
     * @return форматтер
     */
    public TreeTableNodeGetFormatOf getDataFormatter() {
        return dataFormatter;
    }

    /**
     * Указывает форматтер данных
     * @param dataFormatter форматтер
     */
    public void setDataFormatter(TreeTableNodeGetFormatOf dataFormatter) {
        this.dataFormatter = dataFormatter;
    }

    /**
     * Возвращает предпочтительное форматирование данных (ближайщее функция форматирования вверх по дереву)
     * @return предпочтительный форматтер
     * @see #getDataFormatter()
     * @see #setDataFormatter(xyz.cofe.gui.swing.tree.TreeTableNodeGetFormatOf)
     */
    public TreeTableNodeGetFormatOf getPreferredDataFormatter(){
        TreeTableNodeGetFormatOf formatter = dataFormatter;
        if( formatter==null ){
            List<TreeTableNodeBasic> path = path();
            if( path!=null && path.size()>0 )path.remove(path.size()-1);
            for( int i=path.size()-1; i>=0; i-- ){
                TreeTableNodeBasic n = path.get(i);
                formatter = n.getDataFormatter();
                if( formatter!=null )break;
            }
        }
        return formatter;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dataTextReader">
    protected Fn1<Object,String> dataTextReader;

    /**
     * Возвращает функцию преобразоавния данных узла в текстовое представление
     * @return функция (node.data):String
     */
    public Fn1<Object,String> getDataTextReader() {
        return dataTextReader;
    }

    /**
     * Указывает функцию преобразоавния данных узла в текстовое представление
     * @param dataTextReader функция (node.data):String
     */
    public void setDataTextReader(Fn1<Object,String> dataTextReader) {
        this.dataTextReader = dataTextReader;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getDataText()">
    /**
     * Возвращает отображаемый текст для данных узла,
     * использует ближащую (по дереву вверх к корню) функцию dataTextReader
     * @return Отображаемый текст
     * @see #getDataTextReader()
     */
    public String getDataText(){
        var dataTextResolver = dataTextReader;
        if( dataTextResolver==null ){
            List<TreeTableNodeBasic> path = path();
            if( path!=null && path.size()>0 )path.remove(path.size()-1);
            for( int i=path.size()-1; i>=0; i-- ){
                TreeTableNodeBasic n = path.get(i);
                dataTextResolver = n.getDataTextReader();
                if( dataTextResolver!=null )break;
            }
        }

        if( dataTextResolver!=null ){
            Object data = getData();
            String txt = dataTextResolver.apply(data);
            if( txt==null && data!=null )return data.toString();
            return txt;
        }

        Object data = getData();
        return data==null ? data.toString() : null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="treeTableNodeGetText() : String">
    /**
     * Возвращает отображаемый текст для данных узла,
     * использует ближащую (по дереву вверх к корню) функцию dataTextReader
     * @return Отображаемый текст
     * @see #getDataTextReader()
     */
    @Override
    public String treeTableNodeGetText() {
        return getDataText();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="data : Object">
    protected Object data = null;

    @Override
    public Object getData(){
        return data;
    }

    @Override
    public void setData( Object v ){
        Object old = data;
        this.data = v;

        TreeTableDataChanged ev = new TreeTableDataChanged(this,old,v);
        treeNotify(ev);
    }
    //</editor-fold>

    //<editor-fold desc="expanded : boolean">
    private boolean expanded = false;

    @Override
    public boolean isExpanded(){
        return expanded;
    }

    @Override
    public void setExpanded( boolean v ){
        this.expanded = v;
    }
    //</editor-fold>

    //<editor-fold desc="expand()">
    @Override
    public void expand(){
        TreeTableNodeExpanding ev1 = null;

        // Генерация события Expanding
        Boolean old1 = isExpanded();
        if( !Objects.equals(old1, (Boolean)true) ){
            ev1 = new TreeTableNodeExpanding(this);
            treeNotify( ev1 );
        }

        // Раскрытие род. узла
        TreeTableNodeBasic prnt = (TreeTableNodeBasic) getParent();
        if( prnt!=null ){
            prnt.expand();
        }

        // Тестирование 1
        if( followStarted==null ){
            follow();
        }

        // Генерация события Expanded
        boolean exp = true;
        Boolean old2 = isExpanded();
        setExpanded(exp);
        if( !Objects.equals(old2, (Boolean)exp) ){
            TreeTableNodeExpanded ev2 = new TreeTableNodeExpanded(this,ev1);
            treeNotify( ev2 );
        }
    }
    //</editor-fold>

    //<editor-fold desc="collapse()">
    @Override
    public void collapse(){
        TreeTableNodeCollapsing ev1 = null;

        Boolean old1 = isExpanded();

        if( !Objects.equals(old1, (Boolean)false) ){
            ev1 = new TreeTableNodeCollapsing(this);
            treeNotify( ev1 );
        }

        boolean exp = false;
        Boolean old2 = isExpanded();
        setExpanded(false);

        if(!Objects.equals(old2, (Boolean)exp)){
            TreeTableNodeCollapsed ev2 =new TreeTableNodeCollapsed(this,ev1);
            treeNotify( ev2 );
        }

        // cache drop
        if( followFinished!=null ){
            Long lifet = getPreferredCacheLifeTime();
            if( lifet!=null && lifet>0 ){
                long tdiff = Math.abs(followFinished.getTime() - System.currentTimeMillis() );
                if( tdiff > lifet ){
                    dropCache();
                    followFinished = null;
                    followStarted = null;
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="follow()">
    /**
     * Извлекает дочерние объекты и добавляет их в дерево и кэш.
     * <p>
     *
     * Для извлечения использует:
     *
     * <ul>
     * <li>getPreferredDataFollowable() - Для проверки возможности извлечения данных
     * <li>getFollowChildrenIterable() - Для получения извлекающего итератора
     * </ul>
     *
     * Извлеченные объекты кэшируются, в дальшейшем кэш самостоятельно
     * (в зависимости от cacheLifTime) очищается. <p>
     *
     * Принудительно можно очистить вызвав dropCache() <p>
     *
     * Время начала и завершения извлечени содержаться в свойствах:
     * followStarted, followFinished.
     *
     * @see #getPreferredDataFollowable()
     * @see #readFollowChildrenTo
     * @see TreeNodeFollowing
     * @see TreeNodeFollowed
     */
    public void follow(){
        followStarted = new Date();

        Fn1<Object,Boolean> extrSuppTester = getPreferredDataFollowable();

        if( extrSuppTester!=null ){
            Object data = getData();
            if( extrSuppTester.apply(data) ){
                treeNotify(new TreeNodeFollowing(this));
                readFollowChildrenTo( consumeChildData() );
                treeNotify(new TreeNodeFollowed(this));
            }
        }else{
            treeNotify(new TreeNodeFollowing(this));
            readFollowChildrenTo( consumeChildData() );
            treeNotify(new TreeNodeFollowed(this));
        }

        followFinished = new Date();
    }
    //</editor-fold>

    //<editor-fold desc="followStarted : Date - Указывает время начала follow функции">
    /**
     * Указывает время начала follow функции
     * @see #follow()
     * @see #getPreferredDataFollower()
     */
    protected Date followStarted;

    /**
     * Указывает время начала follow функции
     * @return время начала или null, если еще не вызвана
     * @see #follow()
     * @see #getPreferredDataFollower()
     */
    public Date getFollowStarted() {
        return followStarted;
    }

    /**
     * Указывает время начала follow функции
     * @param followStarted время начала или null, если еще не вызвана
     * @see #follow()
     * @see #getPreferredDataFollower()
     */
    public void setFollowStarted(Date followStarted) {
        this.followStarted = followStarted;
    }
    //</editor-fold>

    //<editor-fold desc="followFinished : Date - время завершения follow функции">
    /**
     * Указывает время завершения follow функции
     * @see #follow()
     * @see #getPreferredDataFollower()
     */
    protected Date followFinished;

    /**
     * Указывает время завершения follow функции
     * @return время завершения или null, если еще не завершена
     * @see #follow()
     * @see #getPreferredDataFollower()
     */
    public Date getFollowFinished() {
        return followFinished;
    }

    /**
     * Указывает время завершения follow функции
     * @param followFinished время завершения или null, если еще не завершена
     * @see #follow()
     * @see #getPreferredDataFollower()
     */
    public void setFollowFinished(Date followFinished) {
        this.followFinished = followFinished;
    }
    //</editor-fold>

    //<editor-fold desc="cacheLifeTime : Long - время жизни в кэше или null">
    protected Long cacheLifeTime = null;

    /**
     * Указывает время жизни извлеченных (follow) объектов в кэше
     * @return время жизни в кэше или null
     * @see #follow()
     */
    public Long getCacheLifeTime() {
        return cacheLifeTime;
    }

    /**
     * Указывает время жизни извлеченных (follow) объектов в кэше
     * @param cacheLifeTime время жизни в кэше или null
     * @see #follow()
     */
    public void setCacheLifeTime(Long cacheLifeTime) {
        this.cacheLifeTime = cacheLifeTime;
    }

    /**
     * Указывает предпочтительно время жизни извлеченных (follow) объектов в кэше. <p>
     * Если значение не установлено, то (рекурсивно) читается значение у родительского узла.
     * @return время жизни в кэше или null
     * @see #follow()
     */
    public Long getPreferredCacheLifeTime(){
        Long lifetime = -1L;
        var path = path();
        for( int i=path.size()-1; i>=0; i-- ){
            TreeTableNodeBasic ttnb = path.get(i);
            if( ttnb.cacheLifeTime!=null ){
                lifetime = ttnb.cacheLifeTime;
                break;
            }
        }
        return lifetime;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dataFollower">
    protected Fn1<Object,Boolean> dataFollowable;

    /**
     * Возвращает функцию проверки наличия дочерних улов для их дальшего извлечени.
     * @return функция fn( node.data ) : boolean
     * @see #getDataFollower()
     * @see #getPreferredDataFollower()
     */
    public Fn1<Object,Boolean> getDataFollowable() {
        return dataFollowable;
    }

    /**
     * Указывает функцию проверки наличия дочерних улов для их дальшего извлечени
     * @param dataFollowable функция fn( node.data ) : boolean
     * @see #getDataFollower()
     * @see #getPreferredDataFollower()
     */
    public void setDataFollowable(Fn1<Object,Boolean> dataFollowable) {
        this.dataFollowable = dataFollowable;
    }

    /**
     * Возвращает ближайшую (вверх по дереву) функцию проверки наличия дочерних улов
     * @return функция fn( node.data ) : boolean
     * @see #getDataFollowable()
     * @see #getDataFollower()
     * @see #getPreferredDataFollower()
     */
    public Fn1<Object,Boolean> getPreferredDataFollowable(){
        var extractable = dataFollowable;
        if( extractable==null ){
            List<TreeTableNodeBasic> path = path();
            if( path!=null && path.size()>0 )path.remove(path.size()-1);
            for( int i=path.size()-1; i>=0; i-- ){
                TreeTableNodeBasic n = path.get(i);
                extractable = n.getDataFollowable();
                if( extractable!=null )break;
            }
        }
        // if( extracter==null )return null;
        return extractable;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dataFollower">
    protected NodesExtracter<Object,Object> dataFollower;

    /**
     * Возвращает функцию извлечения дочерних узлов поддерева
     * @return функция fn( node ) : [node]
     */
    public NodesExtracter<Object, Object> getDataFollower() {
        return dataFollower;
    }

    /**
     * Указывает функцию извлечения дочерних узлов поддерева
     * @param dataFollower функция fn( node ) : [node]
     */
    public void setDataFollower(NodesExtracter<Object, Object> dataFollower) {
        this.dataFollower = dataFollower;
    }

    /**
     * Возвращает ближайщую (вверх по дереву) функцию получения дочерних узлов
     * @return функция получения дочерних узлов или null
     */
    public NodesExtracter<Object,Object> getPreferredDataFollower(){
        NodesExtracter<Object,Object> extracter = dataFollower;
        if( extracter==null ){
            List<TreeTableNodeBasic> path = path();
            if( path!=null && path.size()>0 )path.remove(path.size()-1);
            for( int i=path.size()-1; i>=0; i-- ){
                TreeTableNodeBasic n = path.get(i);
                extracter = n.getDataFollower();
                if( extracter!=null )break;
            }
        }
        // if( extracter==null )return null;
        return extracter;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cachedNodes">
    protected WeakHashMap<TreeTableNode,Date> cachedNodes = new WeakHashMap<TreeTableNode,Date>();

    /**
     * Возвращает кэш извлеченных дочерних узлов
     * @return кеш
     * @see #follow()
     * @see #getCacheLifeTime()
     */
    public Map<TreeTableNode,Date> getCachedNodes(){
        return cachedNodes;
    }
    //</editor-fold>

    //<editor-fold desc="conf_useExpanderThresholdTimeout">
    private static final String KEY_PREFIX="xyz.cofe.gui.swing.tree.TreeTableNodeBasic.";
    private static final String USE_EXPANDER_THRESHOLD_TIMEOUT=KEY_PREFIX+"useExpanderThresholdTimeout";
    protected volatile static Integer useExpanderThresholdTimeout;
    private int conf_useExpanderThresholdTimeout(){
        if( useExpanderThresholdTimeout!=null )return useExpanderThresholdTimeout;

        String val =
            System.getProperties().getProperty(
                USE_EXPANDER_THRESHOLD_TIMEOUT, "500");

        if( val==null || !val.matches("-?\\d+") ){
            useExpanderThresholdTimeout = 500;
        }else{
            useExpanderThresholdTimeout = Integer.parseInt(val);
        }

        return useExpanderThresholdTimeout;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="followChildrenIterable">
    /**
     * Получение дочерних узлов/данных для данного узла
     * @return дочерние узлы/данные
     */
    public Iterable getFollowChildrenIterable(){
        NodesExtracter ne = getPreferredDataFollower();
        Object data = getData();

        if( ne==null || data==null )return null;
        if( ne instanceof TreeNodesExtracter ){
            return ((TreeNodesExtracter)ne).extract(this);
        }

        return ne.extract(data);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readFollowChildrenTo()">
    /**
     * Извлекает дочерние узлы и передает их в функцию приемник
     * @param childDataConsumer функция применик
     * @see #follow()
     * @see #getPreferredDataFollower()
     */
    protected void readFollowChildrenTo( final Consumer<Object> childDataConsumer ){
        Iterable children = getFollowChildrenIterable();
        if( children!=null ){
            int timeoutThreshold = conf_useExpanderThresholdTimeout();
            long tStart = System.currentTimeMillis();

            long tReadTotal = 0;
            long tConsumeTotal = 0;
            int readed = 0;

            Iterator iter = children.iterator();
            boolean createExpander = false;
            while( true ){
                long tRead0 = System.currentTimeMillis();
                boolean hnext = iter.hasNext();
                if( !hnext )break;

                long tCurrent = System.currentTimeMillis();
                long tDiff = Math.abs(tCurrent - tStart);
                if( timeoutThreshold>0 && tDiff>=timeoutThreshold ){
                    createExpander = true;
                    logFine( "terminate readFollowChildrenTo by timeoutThreshold={0} timeout={1}", timeoutThreshold, tDiff );
                    break;
                }

                Object childData = iter.next();
                readed++;
                long tRead1 = System.currentTimeMillis();

                long tConsume0 = System.currentTimeMillis();
                childDataConsumer.accept(childData);
                long tConsume1 = System.currentTimeMillis();

                tReadTotal += Math.abs(tRead0 - tRead1);
                tConsumeTotal += Math.abs(tConsume1-tConsume0);

                logFiner("node readed, total={0}, t.consume={1} {4} t.read1={2} {5}, t.*1={3} {6}",
                    readed, //0
                    Math.abs(tConsume1-tConsume0), //1
                    Math.abs(tRead0 - tRead1),  //2
                    Math.abs(tConsume1-tConsume0)+Math.abs(tRead0 - tRead1), //3
                    tConsumeTotal, //4
                    tReadTotal, //5
                    tConsumeTotal + tReadTotal
                );
            }
            if( createExpander ){
                logFine("create expander by timeoutThreshold={0}", timeoutThreshold);

                TreeTableNodeExpander expander = new TreeTableNodeExpander(iter);
                childDataConsumer.accept(expander);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="consumeChildData()">
    /**
     * Создает функцию применик для ивлеченных дочерних узлов,
     * кеширует принятые узлы
     * @return функция применик
     * @see #follow()
     * @see #getCachedNodes()
     * @see #getCacheLifeTime()
     */
    protected Consumer<Object> consumeChildData(){
        //return (Object childData) -> {
        return new Consumer<Object>() {
            @Override
            public void accept(Object childData) {

                TreeTableNodeBasic ttnb = null;

                if( childData instanceof TreeTableNodeBasic ){
                    ttnb = (TreeTableNodeBasic)childData;
                }else{
                    ttnb = new TreeTableNodeBasic(childData);
                }

                append(ttnb);

                cachedNodes.put(ttnb, new Date());
            }};
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dropCache()">
    /**
     * Очистка кэша и удаление уешированных узлов (только прямые потомки данного узла)
     * @see #getCacheLifeTime()
     * @see #getCachedNodes()
     * @see #getPreferredDataFollower()
     * @see #follow()
     */
    public void dropCache(){
        TreeNodeCacheDropped ev = new TreeNodeCacheDropped(this);

        AtomicInteger cnt = new AtomicInteger(0);

        for( TreeTableNode node : cachedNodes.keySet() ){
            //cachedNodes.keySet().forEach( node -> {
            if( node!=null ){
                if( node instanceof TreeTableNodeBasic ){
                    delete((TreeTableNodeBasic)node);
                    cnt.addAndGet(((TreeTableNodeBasic) node).getNodesCount());
                    ev.getDropped().add(node);
                }
            }
        }// );

        cachedNodes.clear();

        followFinished = null;
        followStarted = null;

        treeNotify(ev);

        logFine("dropped {0} child nodes", cnt.get());
    }
    //</editor-fold>
}
