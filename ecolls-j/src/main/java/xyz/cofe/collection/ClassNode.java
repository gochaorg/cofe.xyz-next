package xyz.cofe.collection;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.iter.EmptyIterable;
import xyz.cofe.iter.Eterable;
import xyz.cofe.scn.LongScn;

/*
 * The MIT License
 *
 * Copyright 2016 Kamnev Georgiy (nt.gocha@gmail.com).
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

/**
 * Функция "следования" (извлечения) дочерних/связынных объектов из объектов определенного типа. <p>
 * Использование: <br>
 * <pre>
 * ClassNode cn = new ClassNode();
 *
 * // Определяем функцию извелечения дочерних файлов из каталога
 * cn.add( File.class, new NodesExtracter(){ ... } );
 *
 * // Определяем функцию 1 извелечения элементов из списка
 * cn.add( List.class, new NodesExtracter(){ ... } );
 *
 * // Определяем функцию 2 извелечения элементов из списка в обратном порядке
 * cn.add( List.class, new NodesExtracter(){ ... } );
 *
 * // вернет итератор по элементам,
 * // при чем элементы будут возвращены по два раза
 * // в прямом и в обратном порядке т.к. определены две функции
 * for( Object it : cn.fetch( list ) )
 * </pre>
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ClassNode
    implements NodesExtracter, LongScn<ClassNode,Object>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ClassNode.class.getName());
    private static final Level logLevel = logger.getLevel();

    private static final boolean isLogSevere =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.SEVERE.intValue();

    private static final boolean isLogWarning =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.WARNING.intValue();

    private static final boolean isLogInfo =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.INFO.intValue();

    private static final boolean isLogFine =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINE.intValue();

    private static final boolean isLogFiner =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINER.intValue();

    private static final boolean isLogFinest =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINEST.intValue();

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

    protected final Map<Class,Set<NodesExtracter>> typedExtracter;
    protected final ClassSet types;

    /**
     * Конструктор по умочанию
     */
    public ClassNode(){
        Map<Class, Set<NodesExtracter>>
            typedExtracter = new LinkedHashMap<Class, Set<NodesExtracter>>();

        types = new ClassSet(true);

        BasicEventMap<Class,Set<NodesExtracter>> ev_typedExtracter
            = new BasicEventMap<>(typedExtracter);

        typedExtracter = ev_typedExtracter;

        this.typedExtracter = typedExtracter;
        syncTypes(types, ev_typedExtracter);
    }

    /**
     * Конструктор копирования с блокиовкой
     * @param src образец для копирования
     */
    public ClassNode(ClassNode src){
        Map<Class, Set<NodesExtracter>>
            typedExtracter = new LinkedHashMap<Class, Set<NodesExtracter>>();
        types = new ClassSet(true);

        BasicEventMap<Class,Set<NodesExtracter>> ev_typedExtracter
            = new BasicEventMap<>(typedExtracter);
        typedExtracter = ev_typedExtracter;

        this.typedExtracter = typedExtracter;
        syncTypes(types, ev_typedExtracter);

        if( src!=null ){
            this.typedExtracter.putAll(src.typedExtracter);
        }
    }

    /**
     * Клонирование
     * @return клон
     */
    @Override
    public ClassNode clone(){
        return new ClassNode(this);
    }

    private AutoCloseable syncTypes( final ClassSet types, EventMap<Class,Set<NodesExtracter>>  map ){
        return map.onChanged( (cls,oldset,newset)->{
            if( cls!=null ){
                if( oldset!=null ){
                    types.remove(cls);
                }
                if( newset!=null ){
                    types.add(cls);
                }
            }
        });
    }

    /**
     * Добавляет функции следования для указанного типа
     * @param <T> тип
     * @param cls тип
     * @param ne функции
     * @return self ссылка
     */
    public <T> ClassNode adds( final Class<T> cls, final NodesExtracter<T,?> ... ne ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        if( ne==null )throw new IllegalArgumentException( "ne==null" );
        scn( () -> {
            for( NodesExtracter n : ne ){
                if( n!=null ){
                    add( cls, n );
                }
            }
        });
        return this;
    }

    /**
     * Добавляет функцию следования
     * @param cls тип
     * @param ne функция следования
     * @return интерфейс для удаления ассоциации
     */
    public Closeable add( Class cls, NodesExtracter ne ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        if( ne==null )throw new IllegalArgumentException( "ne==null" );

        Set<NodesExtracter> setne = typedExtracter.get(cls);
        if( setne==null ){
            setne = new LinkedHashSet<NodesExtracter>();
            typedExtracter.put(cls, setne);
        }

        setne.add(ne);

        nextscn();

        final WeakReference<Class> fcls = new WeakReference<>(cls);
        final WeakReference<NodesExtracter> fne = new WeakReference<>(ne);

        return () -> {
                Class xcls = fcls.get();
                NodesExtracter xne = fne.get();
                if( xcls==null || xne==null )return;
                remove(xcls, ne);
            };
    }

    /**
     * Удаляет функцию следования
     * @param cls тип
     * @param ne функция
     */
    public void remove( Class cls, NodesExtracter ne ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        if( ne==null )throw new IllegalArgumentException( "ne==null" );

        Set<NodesExtracter> setne = typedExtracter.get(cls);
        if( setne==null ) return;

        setne.remove(ne);
        if( setne.isEmpty() ){
            typedExtracter.remove(cls);
        }

        nextscn();
    }

    /**
     * Очищает все функции следования для указанного типа
     * @param cls тип
     */
    public void clear( Class cls ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );

        Set<NodesExtracter> setne = typedExtracter.get(cls);
        if( setne==null ) return;

        setne.clear();
        if( setne.isEmpty() ){
            typedExtracter.remove(cls);
        }

        nextscn();
    }

    /**
     * Очищает все функции следования для указанного типа
     */
    public void clear(){
        typedExtracter.clear();
        nextscn();
    }

    /**
     * Проверяет наличие функций у указанного типа
     * @param cls тип
     * @return true - есть функции
     */
    public boolean hasClass( Class cls ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        return typedExtracter.containsKey(cls);
    }

    /**
     * Возвращает зарегистрированные типы
     * @return типы
     */
    public Class[] getClasses(){
        return types.toArray(new Class[]{});
    }

    /**
     * Возвращает функции следования для указанного типа
     * @param cls тип
     * @return функции
     * @see Map#get(java.lang.Object)
     */
    public NodesExtracter[] getNodeExtracters( Class cls ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        Set<NodesExtracter> childrenExtr = typedExtracter.get(cls);
        if( childrenExtr==null )return null;
        return childrenExtr.toArray(new NodesExtracter[]{});
    }

    /**
     * Возвращает набор функций подходящих для извлечения данных
     * @param cls тип данных
     * @return набор подходящих функций
     */
    public NodesExtracter[] extractersOf( Class cls ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );

        Set<NodesExtracter> childrenExtr = typedExtracter.get(cls);
        if( childrenExtr!=null ){
            return childrenExtr.toArray(new NodesExtracter[]{});
        }

        Collection<Class> matchParents = types.getAssignableFrom(cls, true, false);
        Set<Class> matchedCls = new LinkedHashSet<Class>();
        if( matchParents!=null ){
            for( Class c : matchParents ){
                if( c!=null ){
                    matchedCls.add(c);
                    if( c.isInterface() ){
                        continue;
                    }
                    break;
                }
            }
        }

        childrenExtr = new LinkedHashSet<NodesExtracter>();
        for( Class c : matchedCls ){
            Set<NodesExtracter> snet = typedExtracter.get(c);
            childrenExtr.addAll(snet);
        }

        if( childrenExtr!=null && childrenExtr.size()>0 ){
            return childrenExtr.toArray(new NodesExtracter[]{});
        }

        return new NodesExtracter[]{};
    }

    /**
     * Возвращает набор функций подходящих для извлечения данных
     * @param node объект из которого происходит извлечение данных
     * @return Объекты которые следуют согласно указанным функциям
     */
    public Eterable fetch( Object node ){
        if( node==null )return Eterable.empty();

        Class cls = node.getClass();

        NodesExtracter[] extrs = extractersOf(cls);
        return fetch(node, extrs);
    }

    private Eterable fetch( Object node, NodesExtracter ... set ){
        Eterable itr = null;
        for( NodesExtracter ne : set ){
            if( ne!=null ){
                Iterable i = ne.extract(node);
                if( i!=null && !(i instanceof EmptyIterable) ){
                    if( itr==null ){
                        itr = Eterable.of(i);//Eterable.single(i);
                    }else{
                        itr = itr.union(i);
                    }
                }
            }
        }
        return itr==null ? Eterable.empty() : itr;
    }

    /**
     * Возвращает набор функций подходящих для извлечения данных (синоним fetch).
     * @param from объект из которого происходит извлечение данных
     * @return Объекты которые следуют согласно указанным функциям
     * @see #fetch(java.lang.Object)
     */
    @Override
    public Eterable extract(Object from) {
        return fetch(from);
    }
}
