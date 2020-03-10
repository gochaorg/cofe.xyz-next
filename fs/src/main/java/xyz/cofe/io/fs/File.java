/*
 * The MIT License
 *
 * Copyright 2017 user.
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

package xyz.cofe.io.fs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import xyz.cofe.collection.BasicVisitor;
import xyz.cofe.collection.Visitor;
import xyz.cofe.iter.TreeStep;

/**
 * Файл. Обвертка над файловыми операциями java.nio.file.*.
 * @author Kamnev Georgiy
 */
public class File implements Comparable<File>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(File.class.getName());

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
        logger.entering(File.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(File.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(File.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Путь к файлу
     */
    public final Path path;

    /**
     * Конструктор
     * @param path путь к файлу
     */
    public File(Path path){
        if( path==null )throw new IllegalArgumentException("path == null");
        this.path = path;
    }

    /**
     * Конструктор
     * @param path путь к файлу
     */
    public File(File path){
        if( path==null )throw new IllegalArgumentException("path == null");
        this.path = path.path;
    }

    /**
     * Конструктор
     * @param filePath путь к файлу
     * @param more  путь к файлу
     */
    public File(String filePath, String ... more){
        if( filePath==null )throw new IllegalArgumentException("filePath == null");
        FileSystem fs = FileSystems.getDefault();
        this.path = fs.getPath(filePath, more);
        //this.path = Paths.get(filePath, more);
    }

    //<editor-fold defaultstate="collapsed" desc="fileSystem : FileSystem">
    /**
     * Возвращает файловую систему, создавшую этот объект
     * @return файловая система, создавшая этот объект
     */
    //@Override
    public FileSystem getFileSystem() {
        return path.getFileSystem();
    }
    //</editor-fold>

    /*
    private static Path original( Path p ){
        if( p==null )return null;
        long t0 = System.currentTimeMillis();
        while( true ){
            long tdiff = System.currentTimeMillis() - t0;
            if( tdiff>200 )break;
            if( p instanceof File ){
                p = ((File)p).path;
                continue;
            }
            break;
        }
        return p;
    }
    */

    //<editor-fold defaultstate="collapsed" desc="absolute : boolean">
    /**
     * Указывает, является ли этот путь абсолютным.
     * Абсолютный путь завершен в том смысле, что его не нужно комбинировать с другой информацией о пути, чтобы найти файл.
     * @return true, если и только если этот путь является абсолютным
     */
    //@Override
    public boolean isAbsolute() {
        return path.isAbsolute();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="root : File">
    /**
     * Возвращает корневой компонент этого пути как объект Path или null, если этот путь не имеет корневого компонента.
     * @return путь, представляющий корневой компонент этого пути, или null
     */
    //@Override
    public File getRoot() {
        Path p = path.getRoot();
        return p!=null ? new File(p) : null;
    }
    //</editor-fold>

    /**
     * Возвращает корневые файловые директории для локальной файловой системы
     * @return Корневые директории
     */
    public static List<File> getRootDirectories(){
        ArrayList<File> rootDirs = new ArrayList<>();
        for( Path rootDir : FileSystems.getDefault().getRootDirectories() ){
            if( rootDir!=null ){
                File f = new File(rootDir);
                rootDirs.add(f);
            }
        }
        return rootDirs;
    }

    //<editor-fold defaultstate="collapsed" desc="fileName : File">
    /**
     * Возвращает имя файла или каталога, обозначенного этим путем как объект Path.
     * Имя файла является самым дальним элементом из корня в иерархии каталогов.
     * @return путь, представляющий имя файла или каталога, или null, если этот путь имеет нулевые элементы
     */
    //@Override
    public File getFileName() {
        Path p = path.getFileName();
        return p!=null ? new File(p) : null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="parent : File">
    /**
     * Возвращает родительский путь или null, если этот путь не имеет родителя. <p>
     *
     * Родитель этого объекта пути состоит из корневого компонента этого пути,
     * если таковой имеется, и каждого элемента пути,
     * кроме самого удаленного от корня в иерархии каталогов.
     * Этот метод не имеет доступа к файловой системе; путь или его родительский
     * элемент могут не существовать. Кроме того, этот метод не исключает
     * особых имен, таких как «.». и "..", которые могут использоваться в некоторых реализациях. <p>
     *
     * Например, в UNIX родителем «/ a / b / c» является «/ a / b»,
     * а родительский элемент «x / y /». «х / у». Этот метод может использоваться с методом нормализации,
     * чтобы исключить избыточные имена, для случаев, когда требуется командная оболочка. <p>
     *
     * Если этот путь имеет один или несколько элементов и не имеет корневого компонента,
     * то этот метод эквивалентен оценке выражения: <p>
     *
     *  subpath(0, getNameCount()-1);;
     * @return путь, представляющий родительский путь
     */
    //@Override
    public File getParent() {
        Path p = path.getParent();
        return p!=null ? new File(p) : null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nameCount : int">
    /**
     * Возвращает количество элементов name в пути.
     * @return количество элементов в пути, или 0, если этот путь представляет только корневой компонент
     */
    //@Override
    public int getNameCount() {
        return path.getNameCount();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="name : File">
    /**
     * Возвращает элемент name этого пути как объект Path. <p>
     *
     * Параметр index - это индекс возвращаемого элемента name.
     * Элемент, ближайший к корню в иерархии каталогов, имеет индекс 0.
     * Элемент, который находится дальше всего от корня, имеет индекс count-1.
     *
     * @param index индекс элемента
     * @return элемент имени
     */
    //@Override
    public File getName(int index) {
        Path p = path.getName(index);
        return p!=null ? new File(p) : null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="subpath(beginIndex,endIndex)">
    /**
     * Возвращает относительный путь, который является подпоследовательностью элементов name этого пути. <p>
     *
     * Параметры beginIndex и endIndex определяют подпоследовательность элементов name.
     * Имя, которое ближе всего к корню в иерархии каталогов, имеет индекс 0.
     * Имя, которое находится дальше всего от корня, имеет индекс count-1.
     * Возвращаемый объект Path имеет элементы name, которые начинаются с beginIndex и распространяются
     * на элемент с индексом endIndex-1.
     *
     * @param beginIndex индекс первого элемента, включительно
     * @param endIndex индекс последнего элемента, исключительно
     * @return новый объект File, который является подпоследовательностью элементов name на этом пути
     */
    //@Override
    public File subpath(int beginIndex, int endIndex) {
        Path p = path.subpath(beginIndex, endIndex);
        return p!=null ? new File(p) : null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="startsWith(other)">
    /**
     * Тест, если этот путь начинается с заданного пути. <p>
     *
     * Этот путь начинается с заданного пути, если корневой компонент этого
     * пути начинается с корневого компонента данного пути, и этот путь начинается с
     * тех же элементов названия, что и указанный путь. <p>
     *
     * Если данный путь имеет больше элементов имени, чем этот путь, возвращается false.
     * Независимо от того, начинается ли корневой компонент этого пути с корневым компонентом
     * данного пути, зависит конкретная файловая система. <p>
     *
     * Если этот путь не имеет корневого компонента, и данный путь имеет корневой компонент,
     * то этот путь не начинается с данного пути. <p>
     *
     * Если данный путь связан с другой файловой системой с этим путем, возвращается false.
     * @param other данный путь
     * @return true, если этот путь начинается с данного пути; в противном случае
     */
    //@Override
    public boolean startsWith(Path other) {
        if( other==null )throw new IllegalArgumentException( "other==null" );
        //return path.startsWith(original(other));
        return path.startsWith(other);
    }

    /**
     * Тест, если этот путь начинается с заданного пути. <p>
     *
     * Этот путь начинается с заданного пути, если корневой компонент этого
     * пути начинается с корневого компонента данного пути, и этот путь начинается с
     * тех же элементов названия, что и указанный путь. <p>
     *
     * Если данный путь имеет больше элементов имени, чем этот путь, возвращается false.
     * Независимо от того, начинается ли корневой компонент этого пути с корневым компонентом
     * данного пути, зависит конкретная файловая система. <p>
     *
     * Если этот путь не имеет корневого компонента, и данный путь имеет корневой компонент,
     * то этот путь не начинается с данного пути. <p>
     *
     * Если данный путь связан с другой файловой системой с этим путем, возвращается false.
     * @param other данный путь
     * @return true, если этот путь начинается с данного пути; в противном случае
     */
    public boolean startsWith(File other) {
        if( other==null )throw new IllegalArgumentException( "other==null" );
        return path.startsWith(other.path);
    }

    /**
     * Тест, если этот путь начинается с заданного пути. <p>
     *
     * Этот путь начинается с заданного пути, если корневой компонент этого
     * пути начинается с корневого компонента данного пути, и этот путь начинается с
     * тех же элементов названия, что и указанный путь. <p>
     *
     * Если данный путь имеет больше элементов имени, чем этот путь, возвращается false.
     * Независимо от того, начинается ли корневой компонент этого пути с корневым компонентом
     * данного пути, зависит конкретная файловая система. <p>
     *
     * Если этот путь не имеет корневого компонента, и данный путь имеет корневой компонент,
     * то этот путь не начинается с данного пути. <p>
     *
     * Если данный путь связан с другой файловой системой с этим путем, возвращается false.
     * @param other данный путь
     * @return true, если этот путь начинается с данного пути; в противном случае
     */
    //@Override
    public boolean startsWith(String other) {
        return path.startsWith(other);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="endsWith(other)">
    /**
     * Проверяет, заканчивается ли этот путь указанным путем. <p>
     *
     * Если данный путь содержит N элементов и не имеет корневого компонента,
     * и этот путь имеет N или более элементов, то этот путь заканчивается указанным путем,
     * если последние N элементов каждого пути, начиная с элемента, наиболее удаленного от корня, равны, <p>
     *
     * Если данный путь имеет корневой компонент, то этот путь заканчивается указанным путем,
     * если корневой компонент этого пути заканчивается корневой составляющей данного пути,
     * а соответствующие элементы обоих путей равны. Независимо от того, заканчивается ли корневой
     * компонент этого пути с корневым компонентом данного пути, зависит конкретная файловая система. <p>
     *
     * Если этот путь не имеет корневого компонента, и данный путь имеет корневой компонент, то этот путь
     * не заканчивается заданным путем. <p>
     *
     * Если данный путь связан с другой файловой системой с этим путем, возвращается false.
     * @param other данный путь
     * @return true, если этот путь заканчивается заданным путем; в противном случае
     */
    //@Override
    public boolean endsWith(Path other) {
        //return path.endsWith(original(other));
        return path.endsWith(other);
    }

    /**
     * Проверяет, заканчивается ли этот путь указанным путем. <p>
     *
     * Если данный путь содержит N элементов и не имеет корневого компонента,
     * и этот путь имеет N или более элементов, то этот путь заканчивается указанным путем,
     * если последние N элементов каждого пути, начиная с элемента, наиболее удаленного от корня, равны, <p>
     *
     * Если данный путь имеет корневой компонент, то этот путь заканчивается указанным путем,
     * если корневой компонент этого пути заканчивается корневой составляющей данного пути,
     * а соответствующие элементы обоих путей равны. Независимо от того, заканчивается ли корневой
     * компонент этого пути с корневым компонентом данного пути, зависит конкретная файловая система. <p>
     *
     * Если этот путь не имеет корневого компонента, и данный путь имеет корневой компонент, то этот путь
     * не заканчивается заданным путем. <p>
     *
     * Если данный путь связан с другой файловой системой с этим путем, возвращается false.
     * @param other данный путь
     * @return true, если этот путь заканчивается заданным путем; в противном случае
     */
    //@Override
    public boolean endsWith(File other) {
        //return path.endsWith(original(other));
        return path.endsWith(other.path);
    }

    /**
     * Проверяет, заканчивается ли этот путь указанным путем. <p>
     *
     * Если данный путь содержит N элементов и не имеет корневого компонента,
     * и этот путь имеет N или более элементов, то этот путь заканчивается указанным путем,
     * если последние N элементов каждого пути, начиная с элемента, наиболее удаленного от корня, равны, <p>
     *
     * Если данный путь имеет корневой компонент, то этот путь заканчивается указанным путем,
     * если корневой компонент этого пути заканчивается корневой составляющей данного пути,
     * а соответствующие элементы обоих путей равны. Независимо от того, заканчивается ли корневой
     * компонент этого пути с корневым компонентом данного пути, зависит конкретная файловая система. <p>
     *
     * Если этот путь не имеет корневого компонента, и данный путь имеет корневой компонент, то этот путь
     * не заканчивается заданным путем. <p>
     *
     * Если данный путь связан с другой файловой системой с этим путем, возвращается false.
     * @param other данный путь
     * @return true, если этот путь заканчивается заданным путем; в противном случае
     */
    //@Override
    public boolean endsWith(String other) {
        return path.endsWith(other);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="normalize()">
    /**
     * Возвращает путь, который является этим путем, при этом исключаются элементы избыточного имени. <p>
     *
     * Точное определение этого метода зависит от реализации, но в целом оно вытекает из этого
     * пути, который не содержит избыточных элементов имени. <p>
     *
     * Во многих файловых системах «.» и ".." - это специальные имена, используемые для указания
     * текущего каталога и родительского каталога.
     * В таких файловых системах все вхождения «.» считаются избыточными.
     * Если «..» предшествует имя «..», то оба имени считаются избыточными
     * (процесс идентификации таких имен повторяется до тех пор, пока он больше не применим). <p>
     *
     * Этот метод не имеет доступа к файловой системе; <br>
     * путь может не найти файл, который существует.
     *
     * Исключение «..» и предыдущее имя из пути могут привести к тому, что
     * путь, который находит другой файл, чем исходный путь. Это может возникнуть,
     * когда предыдущее имя является символической ссылкой.
     *
     * @return результирующий путь или этот путь,
     * если он не содержит избыточных элементов имени;
     * пустой путь возвращается, если этот путь имеет корневой компонент,
     * а все элементы имени являются избыточными
     */
    public File normalize() {
        Path p = path.normalize();
        return p!=null ? new File(p) : null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="resolve(other)">
    /**
     * Разрешение пути от текущего (this). <p>
     *
     * Если параметр other является абсолютным путем, этот метод тривиально возвращает другое. <br>
     *
     * Если другой - пустой путь, этот метод тривиально возвращает этот путь.  <p>
     *
     * В противном случае этот метод считает этот путь каталогом и разрешает данный путь по этому пути. <p>
     *
     * В простейшем случае данный путь не имеет корневого компонента,
     * и в этом случае этот метод соединяет данный путь с
     * этим путем и возвращает результирующий путь, который заканчивается данным путем. <p>
     *
     * Если данный путь имеет корневой компонент, то разрешение сильно зависит от реализации и поэтому не определено.
     * @param other Путь который следует разрешить
     * @return результирующий путь
     */
    public File resolve(Path other) {
        if( other==null )throw new IllegalArgumentException("other == null");
        //Path p = path.resolve(original(other));
        Path p = path.resolve(other);
        return p!=null ? new File(p) : null;
    }

    /**
     * Разрешение пути от текущего (this). <p>
     *
     * Если параметр other является абсолютным путем, этот метод тривиально возвращает другое. <br>
     *
     * Если другой - пустой путь, этот метод тривиально возвращает этот путь.  <p>
     *
     * В противном случае этот метод считает этот путь каталогом и разрешает данный путь по этому пути. <p>
     *
     * В простейшем случае данный путь не имеет корневого компонента,
     * и в этом случае этот метод соединяет данный путь с
     * этим путем и возвращает результирующий путь, который заканчивается данным путем. <p>
     *
     * Если данный путь имеет корневой компонент, то разрешение сильно зависит от реализации и поэтому не определено.
     * @param other Путь который следует разрешить
     * @return результирующий путь
     */
    public File resolve(File other) {
        if( other==null )throw new IllegalArgumentException("other == null");
        //Path p = path.resolve(original(other));
        Path p = path.resolve(other.path);
        return p!=null ? new File(p) : null;
    }

    /**
     * Разрешение пути от текущего (this). <p>
     *
     * Если параметр other является абсолютным путем, этот метод тривиально возвращает другое. <br>
     *
     * Если другой - пустой путь, этот метод тривиально возвращает этот путь.  <p>
     *
     * В противном случае этот метод считает этот путь каталогом и разрешает данный путь по этому пути. <p>
     *
     * В простейшем случае данный путь не имеет корневого компонента,
     * и в этом случае этот метод соединяет данный путь с
     * этим путем и возвращает результирующий путь, который заканчивается данным путем. <p>
     *
     * Если данный путь имеет корневой компонент, то разрешение сильно зависит от реализации и поэтому не определено.
     * @param other Путь который следует разрешить
     * @return результирующий путь
     */
    public File resolve(String other) {
        Path p = path.resolve(other);
        return p!=null ? new File(p) : null;
    }
    //</editor-fold>

    //@Override
    public File resolveSibling(Path other) {
        if( other==null )throw new IllegalArgumentException("other == null");
        //Path p = path.resolveSibling(original(other));
        Path p = path.resolveSibling(other);
        return p!=null ? new File(p) : null;
    }

    public File resolveSibling(File other) {
        if( other==null )throw new IllegalArgumentException("other == null");
        //Path p = path.resolveSibling(original(other));
        Path p = path.resolveSibling(other.path);
        return p!=null ? new File(p) : null;
    }

    //@Override
    public File resolveSibling(String other) {
        Path p = path.resolveSibling(other);
        return p!=null ? new File(p) : null;
    }

    //<editor-fold defaultstate="collapsed" desc="relativize()">
    /**
     * Создает относительный путь между этим путем и заданным путем. <p>
     *
     * Релятивизация является обратной функцией для разрешения. <p>
     *
     * Этот метод пытается построить относительный путь,
     * который при разрешении по этому пути дает путь, который находит тот же файл, что и указанный путь. <p>
     *
     * Например, в UNIX, если этот путь является «/ a / b», и данный путь «/ a / b / c / d»,
     * то полученный относительный путь будет «c / d». <br>
     *
     * Если этот путь и данный путь не имеют корневого компонента,
     * то можно построить относительный путь. Относительный путь не может быть построен,
     * если только один из путей имеет корневой компонент. <br>
     *
     * Если оба пути имеют корневой компонент, то он зависит от реализации,
     * если можно построить относительный путь. <br>
     *
     * Если этот путь и заданный путь равны, возвращается пустой путь. <p>
     *
     * Для любых двух нормализованных путей p и q, где q не имеет корневой компоненты,
     * p.relativize(p.resolve(q)).equals(q) <p>
     *
     * Когда поддерживаются символические ссылки, то возникает ли результирующий путь,
     * когда он разрешен по этому пути, путь, который может
     * использоваться для поиска того же файла, что и другой, зависит от реализации. <p>
     *
     * Например, если этот путь «/ a / b», и данный путь «/ a / x»,
     * то полученный относительный путь может быть «../x». <br>
     *
     * Если «b» является символической ссылкой, то зависит от реализации, если «a / b /../ x»
     * найдет тот же файл, что и «/ a / x».
     * @param other путь к релятивизации против этого пути
     * @return результирующий относительный путь или пустой путь, если оба пути равны
     */
    public File relativize(Path other) {
        if( other==null )throw new IllegalArgumentException("other == null");
        //Path p = path.relativize(original(other));
        Path p = path.relativize(other);
        return p!=null ? new File(p) : null;
    }

    /**
     * Создает относительный путь между этим путем и заданным путем. <p>
     *
     * Релятивизация является обратной функцией для разрешения. <p>
     *
     * Этот метод пытается построить относительный путь,
     * который при разрешении по этому пути дает путь, который находит тот же файл, что и указанный путь. <p>
     *
     * Например, в UNIX, если этот путь является «/ a / b», и данный путь «/ a / b / c / d»,
     * то полученный относительный путь будет «c / d». <br>
     *
     * Если этот путь и данный путь не имеют корневого компонента,
     * то можно построить относительный путь. Относительный путь не может быть построен,
     * если только один из путей имеет корневой компонент. <br>
     *
     * Если оба пути имеют корневой компонент, то он зависит от реализации,
     * если можно построить относительный путь. <br>
     *
     * Если этот путь и заданный путь равны, возвращается пустой путь. <p>
     *
     * Для любых двух нормализованных путей p и q, где q не имеет корневой компоненты,
     * p.relativize(p.resolve(q)).equals(q) <p>
     *
     * Когда поддерживаются символические ссылки, то возникает ли результирующий путь,
     * когда он разрешен по этому пути, путь, который может
     * использоваться для поиска того же файла, что и другой, зависит от реализации. <p>
     *
     * Например, если этот путь «/ a / b», и данный путь «/ a / x»,
     * то полученный относительный путь может быть «../x». <br>
     *
     * Если «b» является символической ссылкой, то зависит от реализации, если «a / b /../ x»
     * найдет тот же файл, что и «/ a / x».
     * @param other путь к релятивизации против этого пути
     * @return результирующий относительный путь или пустой путь, если оба пути равны
     */
    public File relativize(File other) {
        if( other==null )throw new IllegalArgumentException("other == null");
        //Path p = path.relativize(original(other));
        Path p = path.relativize(other.path);
        return p!=null ? new File(p) : null;
    }
    //</editor-fold>

    //@Override
    public URI toUri() {
        return path.toUri();
    }

    public URL toUrl() {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            String msg = ex.getMessage();
            throw ( msg!=null ? new IllegalStateException(msg, ex) : new IllegalStateException(ex) );
        }
    }

    //@Override
    public File toAbsolute() {
        Path p = path.toAbsolutePath();
        return p!=null ? new File(p) : null;
    }

    //@Override
    public File toReal(LinkOption... options) throws IOException {
        Path p = path.toRealPath(options);
        return p!=null ? new File(p) : null;
    }

    //@Override
    public java.io.File toFile() {
        return path.toFile();
    }

    //@Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return path.register(watcher, events, modifiers);
    }

    //@Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        return path.register(watcher, events);
    }

    //<editor-fold defaultstate="collapsed" desc="compareTo()">
    /**
     * Сравнивает два абстрактных пути лексикографически. <p>
     *
     * Порядок, определенный этим методом, специфичен для провайдера,
     * а в случае поставщика по умолчанию - для конкретной платформы. <p>
     *
     * Этот метод не имеет доступа к файловой системе, и ни один файл не требуется для существования.
     * Этот метод не может использоваться для сравнения путей, связанных с различными поставщиками файловых систем.
     * @param other путь по сравнению с этим путем.
     * @return ноль, если аргумент равен этому пути, значение меньше нуля, если этот путь лексикографически меньше
     * аргумента или значение больше нуля, если этот путь лексикографически больше аргумента
     */
    @Override
    public int compareTo(File other) {
        if( other instanceof File ){
            return path.compareTo(((File) other).path );
        }
        return 0;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="equals()">
    /**
     * Проверяет этот путь для равенства с данным объектом. <p>
     *
     * Если данный объект не является Путем или является Путем,
     * связанным с другой файловой системой, то этот метод возвращает false.<p>
     *
     * Независимо от того, равен или нет два пути, зависит от реализации файловой системы.
     * В некоторых случаях пути сравниваются без учета случая, а другие чувствительны к регистру. <p>
     *
     * Этот метод не имеет доступа к файловой системе, и файл не требуется для существования.
     * При необходимости метод isSameFile может использоваться для проверки наличия двух путей в том же файле. <p>
     *
     * Этот метод удовлетворяет общему договору метода Object.equals.
     * @param other объект, которому должен сравниваться этот объект
     * @return true, если и только если данный объект является Путем, который идентичен этому Пути
     */
    @Override
    public boolean equals(Object other) {
        if( other == null )return false;
        if( other instanceof File ){
            return path.equals(((File)other).path );
        }
        return path.equals(other);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hashCode()">
    /**
     * Вычисляет хэш-код для этого пути.
     * Хэш-код основан на компонентах пути и удовлетворяет общему контракту метода Object.hashCode.
     * @return значение хэш-кода для этого пути
     */
    @Override
    public int hashCode() {
        return path.hashCode();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="toString()">
    /**
     * Возвращает строковое представление этого пути. <p>
     *
     * Если этот путь был создан путем преобразования строки пути с
     * использованием метода getPath, то строка пути,
     * возвращаемая этим методом, может отличаться от исходной строки, используемой для создания пути. <p>
     *
     * В возвращаемой строке пути используется разделитель имен по умолчанию для разделения имен в пути. <p>
     *
     * @return строковое представление этого пути
     */
    @Override
    public String toString() {
        return path.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="name : String - Имя файла (без каталога)">
    /**
     * Имя файла
     * @return Имя файла
     */
    public String getName(){
        return path.getFileName().toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="extension : String - Раширение имени файла">
    /**
     * Раширение имени файла.
     * @return расширение имени файла без точки
     */
    public String getExtension(){
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf(".");
        if( dot<0 )return "";
        if( dot>=(name.length()-1) )return "";
        return name.substring(dot+1);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="basename : String - Базовое имя файла без расширения">
    /**
     * Базовое имя файла без расширения
     * @return базовое имя файла
     */
    public String getBasename(){
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf(".");
        if( dot<0 )return name;
        if( dot==0 )return "";
        return name.substring(0, dot);
    }
    //</editor-fold>

    /**
     * Возвращает итератор по каталогу
     * @return итератор
     */
    public DirIterator dirIterator(){
        try {
            return new DirIterator(path);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Возвращает список файлов
     * @return список файлов
     */
    public List<File> dirList(){
        try{
            DirIterator dirItr = dirIterator();
            ArrayList<File> list = new ArrayList<>();
            while( dirItr.hasNext() ){
                File ch = dirItr.next();
                if( ch==null )continue;
                list.add(ch);
            }
            try {
                dirItr.close();
            } catch (IOException ex) {
                //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
            return list;
        }
        catch( IOError err ){
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, err);
            throw err;
            //return new ArrayList<>();
        }
    }

    /**
     * Возвращает список дочерних файлов
     * @return список дочерних файлов
     */
//    @UiBean(forceHidden = true)
    public List<File> getFiles(){
        return dirList();
    }

    /**
     * Осуществляет обход каталога
     * @param visitor функция визитер
     */
    public void visit( Visitor<File> visitor ){
        if( visitor==null )throw new IllegalArgumentException("visitor == null");
        BasicVisitor.<File>visit(visitor, this,
            new DirFollowBuilder().
                checkCycle(true).
                followLinks(true).
                build()
        );
    }

    /**
     * Обход дерева каталогов
     * @return создание алгоритма обхода
     */
    public FileTreeBuilder walk(){
        return new FileTreeBuilder(this);
    }

    //<editor-fold defaultstate="collapsed" desc="isDirectory(opts):boolean - Проверяет, является ли файл каталогом">
    /**
     * Проверяет, является ли файл каталогом. <p>
     *
     * Массив опций может использоваться для указания того,
     * как обрабатываются символические ссылки для случая,
     * когда файл является символической ссылкой.  <p>
     *
     * По умолчанию используются символические ссылки и считывается атрибут файла конечной цели ссылки.  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS, то символьные ссылки не соблюдаются. <p>
     *
     * Где требуется отличать исключение ввода-вывода от случая, когда файл не является каталогом,
     * атрибуты файла могут быть прочитаны с помощью метода readAttributes и
     * типа файла, проверенного с помощью метода BasicFileAttributes.isDirectory. <p>
     *
     * @param options параметры, указывающие, как обрабатываются символические ссылки
     * @return true, если файл является каталогом;
     * false, если файл не существует, не является каталогом
     * или не может быть определено, является ли файл каталогом или нет.
     */
    public boolean isDirectory(LinkOption ... options){
        return Files.isDirectory(path, options);
    }
    //</editor-fold>

    /**
     * Проверяет, является ли файл обычным файлом с непрозрачным контентом. <p>
     *
     * Массив опций может использоваться для указания того, как обрабатываются символические ссылки для случая,
     * когда файл является символической ссылкой.  <p>
     *
     * По умолчанию используются символические ссылки
     * и считывается атрибут файла конечной цели ссылки.  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS, то символьные ссылки не соблюдаются. <p>
     *
     * Где требуется отличать исключение ввода-вывода от случая,
     * когда файл не является обычным файлом, тогда атрибуты файла могут быть
     * прочитаны с помощью метода readAttributes и типа файла, проверенного
     * с помощью метода BasicFileAttributes.isRegularFile.
     * @param options параметры, указывающие, как обрабатываются символические ссылки
     * @return true, если файл является обычным файлом; false, если файл не существует,
     * не является обычным файлом или его невозможно определить, является ли файл обычным файлом или нет.
     */
    public boolean isRegularFile(LinkOption ... options){
        return Files.isRegularFile(path, options);
    }

    /**
     * Проверяет, является ли файл обычным файлом с непрозрачным контентом.
     * @return true, если файл является обычным файлом; false, если файл не существует,
     * не является обычным файлом или его невозможно определить, является ли файл обычным файлом или нет.
     */
    public boolean isFile(){
        return Files.isRegularFile(path);
    }

    /**
     * Проверяет, является ли файл символической ссылкой. <p>
     *
     * Где требуется различать исключение ввода-вывода из случая,
     * когда файл не является символической ссылкой,
     * атрибуты файла могут быть прочитаны с помощью метода readAttributes и типа файла,
     * проверенного методом BasicFileAttributes.isSymbolicLink.
     * @return true, если файл является символической ссылкой; false, если файл не существует,
     * не является символической ссылкой или не может быть определено, является ли файл символической ссылкой или нет.
     */
    public boolean isSymbolicLink(){
        return Files.isSymbolicLink(path);
    }

    /**
     * Проверяет, доступен ли файл для записи.  <p>
     *
     * Этот метод проверяет, существует ли файл и что эта
     * виртуальная машина Java имеет соответствующие привилегии,
     * которые позволили бы ему открыть файл для записи.  <p>
     *
     * В зависимости от реализации этот метод может потребовать проверки прав доступа к файлам,
     * списков управления доступом или других атрибутов файла,
     * чтобы проверить эффективный доступ к файлу.  <p>
     *
     * Следовательно, этот метод может быть не атомарным по отношению к другим операциям файловой системы.
     *
     * Обратите внимание, что результат этого метода сразу устарел,
     * нет гарантии, что последующая попытка открыть файл для записи будет успешной
     * (или даже если она получит доступ к тому же файлу).  <p>
     *
     * Следует соблюдать осторожность при использовании этого метода в приложениях, чувствительных к безопасности.
     * @return true, если файл существует и доступен для записи; false, если файл не существует, доступ на
     * запись будет отклонен, поскольку виртуальная машина Java
     * имеет недостаточно привилегий или доступ не может быть определен
     */
    public boolean isWritable(){
        return Files.isWritable(path);
    }

    /**
     * Проверяет, если два пути обнаруживают один и тот же файл. <p>
     *
     * Если оба объекта Path равны, то этот метод возвращает true, не проверяя, существует ли файл.
     * Если два объекта Path связаны с разными провайдерами, этот метод возвращает false.  <p>
     *
     * В противном случае этот метод проверяет, могут ли оба объекта Path найти один и тот же файл
     * и в зависимости от реализации могут потребовать открытия или доступа к обеим файлам. <p>
     *
     * Если файловая система и файлы остаются статическими,
     * то этот метод реализует отношение эквивалентности для ненулевых путей. <p>
     *
     * Он рефлексивен: для пути f isSameFile (f, f) должен возвращать true. <br>
     * Он симметричен: для двух путей f и g isSameFile (f, g) будет равно isSameFile (g, f). <br>
     * Это транзитивно: для трех путей f, g и h, если isSameFile (f, g) возвращает true и isSameFile (g, h)
     * возвращает true, то isSameFile (g, h) вернет значение true.
     * @param path2 другой путь
     * @return true, если и только если два пути обнаруживают один и тот же файл
     */
    public boolean isSameFile(Path path2){
        if( path2==null )throw new IllegalArgumentException("path2 == null");
        try {
            return Files.isSameFile(path, path2);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Проверяет, если два пути обнаруживают один и тот же файл. <p>
     *
     * Если оба объекта Path равны, то этот метод возвращает true, не проверяя, существует ли файл.
     * Если два объекта Path связаны с разными провайдерами, этот метод возвращает false.  <p>
     *
     * В противном случае этот метод проверяет, могут ли оба объекта Path найти один и тот же файл
     * и в зависимости от реализации могут потребовать открытия или доступа к обеим файлам. <p>
     *
     * Если файловая система и файлы остаются статическими,
     * то этот метод реализует отношение эквивалентности для ненулевых путей. <p>
     *
     * Он рефлексивен: для пути f isSameFile (f, f) должен возвращать true. <br>
     * Он симметричен: для двух путей f и g isSameFile (f, g) будет равно isSameFile (g, f). <br>
     * Это транзитивно: для трех путей f, g и h, если isSameFile (f, g) возвращает true и isSameFile (g, h)
     * возвращает true, то isSameFile (g, h) вернет значение true.
     * @param path2 другой путь
     * @return true, если и только если два пути обнаруживают один и тот же файл
     */
    public boolean isSameFile(File path2){
        if( path2==null )throw new IllegalArgumentException("path2 == null");
        try {
            return Files.isSameFile(path, path2.path);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Проверяет, является ли файл каталогом. <p>
     *
     * По умолчанию используются символические ссылки и считывается атрибут файла конечной цели ссылки.
     *
     * @return true, если файл является каталогом;
     * false, если файл не существует, не является каталогом
     * или не может быть определено, является ли файл каталогом или нет.
     */
    public boolean isDir(){
        return Files.isDirectory(path);
    }

    /**
     * Проверяет, является ли файл исполняемым. <p>
     *
     * Этот метод проверяет, существует ли файл и что эта
     * виртуальная машина Java имеет соответствующие права на файл Runtime.exec.  <p>
     *
     * При проверке доступа к каталогу семантика может отличаться.  <p>
     *
     * Например, в системах UNIX проверка наличия доступа проверяет,
     * имеет ли виртуальная машина Java разрешение на поиск в каталоге для доступа к файлу или подкаталогам. <p>
     *
     * В зависимости от реализации этот метод может потребовать проверки прав доступа к файлам,
     * списков управления доступом или других атрибутов файла, чтобы проверить эффективный доступ к файлу.  <p>
     *
     * Следовательно, этот метод может быть не атомарным по отношению к другим операциям файловой системы. <p>
     *
     * Обратите внимание, что результат этого метода сразу устарел,
     * нет гарантии, что последующая попытка выполнить файл будет успешной
     * (или даже будет иметь доступ к тому же файлу).  <p>
     *
     * Следует соблюдать осторожность при использовании этого метода в приложениях, чувствительных к безопасности.
     * @return true, если файл существует и является исполняемым; false, если файл не существует, выполнить
     * доступ будет отказано, поскольку виртуальная машина Java
     * имеет недостаточно привилегий или доступ не может быть определен
     */
    public boolean isExecutable(){
        return Files.isExecutable(path);
    }

    /**
     * Указывает, считается ли файл скрытым. <p>
     *
     * Точное определение скрытых зависит от платформы или поставщика.  <p>
     *
     * Например, в UNIX файл считается скрытым, если его имя начинается с символа периода ('.').  <p>
     *
     * В Windows файл считается скрытым, если он не является каталогом
     * и установлен атрибут DOS DosFileAttributes.isHidden. <p>
     *
     * В зависимости от реализации этот метод может потребовать доступа к
     * файловой системе, чтобы определить, считается ли файл скрытым.
     * @return true, если файл считается скрытым
     */
    public boolean isHidden(){
        try {
            return Files.isHidden(path);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Проверяет, читается ли файл. <p>
     *
     * Этот метод проверяет, существует ли файл и что эта виртуальная машина
     * Java имеет соответствующие привилегии, которые позволят ему открыть файл для чтения.  <p>
     *
     * В зависимости от реализации этот метод может потребовать проверки прав доступа к файлам,
     * списков управления доступом или других атрибутов файла, чтобы проверить эффективный доступ к файлу.  <p>
     *
     * Следовательно, этот метод может быть не атомарным по отношению к другим операциям файловой системы. <p>
     *
     * Обратите внимание, что результат этого метода сразу устарел, нет гарантии,
     * что последующая попытка открыть файл для чтения будет успешной
     * (или даже если она будет обращаться к одному и тому же файлу).  <p>
     *
     * Следует соблюдать осторожность при использовании этого метода в приложениях, чувствительных к безопасности.
     * @return true, если файл существует и доступен для чтения; false, если файл не существует, доступ на чтение
     * будет отклонен, поскольку виртуальная машина Java имеет недостаточные права или доступ не может быть определен
     */
    public boolean isReadable(){
        return Files.isReadable(path);
    }

    /**
     * Проверяет, не существует ли файл, расположенный по этому пути. <p>
     *
     * Этот метод предназначен для случаев, когда требуется принять меры,
     * когда можно подтвердить, что файл не существует. <p>
     *
     * Параметр options может использоваться для указания того,
     * как обрабатываются символические ссылки для случая,
     * когда файл является символической ссылкой.  <p>
     *
     * По умолчанию используются символические ссылки.  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS, то символьные ссылки не соблюдаются. <p>
     *
     * Обратите внимание, что этот метод не является дополнением к методу exist.
     * Если невозможно определить, существует ли файл или нет, оба метода возвращают false.
     * Как и в методе exist, результат этого метода сразу устарел.  <p>
     *
     * Если этот метод указывает, что файл существует,
     * то нет гарантии, что попытка создания последовательности для последовательности будет успешной.  <p>
     *
     * Следует соблюдать осторожность при использовании этого метода в чувствительном к безопасности
     * @param opts параметры, указывающие, как обрабатываются символические ссылки
     * @return true, если файл не существует; false,
     * если файл существует или его существование не может быть определено
     */
    public boolean notExists(LinkOption ... opts){
        return Files.notExists(path, opts);
    }

    /**
     * Проверяет, не существует ли файл, расположенный по этому пути. <p>
     *
     * Этот метод предназначен для случаев, когда требуется принять меры,
     * когда можно подтвердить, что файл не существует. <p>
     *
     * По умолчанию используются символические ссылки.  <p>
     *
     * Обратите внимание, что этот метод не является дополнением к методу exist.
     * Если невозможно определить, существует ли файл или нет, оба метода возвращают false.
     * Как и в методе exist, результат этого метода сразу устарел.  <p>
     *
     * Если этот метод указывает, что файл существует,
     * то нет гарантии, что попытка создания последовательности для последовательности будет успешной.  <p>
     *
     * Следует соблюдать осторожность при использовании этого метода в чувствительном к безопасности
     * @return true, если файл не существует; false,
     * если файл существует или его существование не может быть определено
     */
    public boolean notExists(){
        return Files.notExists(path);
    }

    /**
     * Проверяет, существует ли файл. <p>
     *
     * Параметр options может использоваться для указания того,
     * как обрабатываются символические ссылки для случая,
     * когда файл является символической ссылкой.  <p>
     *
     * По умолчанию используются символические ссылки.  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS, то символьные ссылки не соблюдаются. <p>
     *
     * Обратите внимание, что результат этого метода сразу устарел.
     * Если этот метод указывает, что файл существует, то нет гарантии,
     * что доступ подпоследовательности будет успешным.  <p>
     *
     * Следует соблюдать осторожность при использовании этого метода в приложениях, чувствительных к безопасности.
     * @param options options - опции, указывающие, как обрабатываются символические ссылки.
     * @return true, если файл существует; false, если файл не существует или его существование не может быть определено.
     */
    public boolean exists(LinkOption... options){
        return Files.exists(path, options);
    }

    /**
     * Проверяет, существует ли файл. <p>
     *
     * По умолчанию используются символические ссылки.  <p>
     *
     * Обратите внимание, что результат этого метода сразу устарел.
     * Если этот метод указывает, что файл существует, то нет гарантии,
     * что доступ подпоследовательности будет успешным.  <p>
     *
     * Следует соблюдать осторожность при использовании этого метода в приложениях, чувствительных к безопасности.
     * @return true, если файл существует;
     * false, если файл не существует или его существование не может быть определено.
     */
    public boolean isExists(){
        return exists();
    }

    /**
     * Возвращает размер файла (в байтах). <p>
     *
     * Размер может отличаться от фактического размера файловой системы из-за сжатия,
     * поддержки разреженных файлов или по другим причинам.  <p>
     *
     * Размер файлов, которые не являются обычными файлами,
     * специфичен для реализации и поэтому не указан.
     * @return размер файла, в байтах
     */
    public long getSize(){
        try {
            return Files.size(path);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Открывает файл, возвращая поток ввода для чтения из файла. <p>
     *
     * Поток не буферизуется и не требуется поддерживать методы InputStream.mark или InputStream.reset.  <p>
     *
     * Поток будет безопасным для доступа несколькими параллельными потоками.  <p>
     *
     * Чтение начинается в начале файла. Независимо от того, является ли возвращенный
     * поток асинхронно закрытым и / или прерывается, сильно зависит от поставщика
     * файловой системы и поэтому не указывается. <p>
     *
     * Параметр options определяет способ открытия файла.  <p>
     *
     * Если параметров нет, это эквивалентно открытию файла с помощью опции READ.  <p>
     *
     * В дополнение к опции READ, реализация может также поддерживать дополнительные варианты реализации.
     * @param opts options - опции, определяющие способ открытия файла
     * @return новый входной поток
     */
    public InputStream readStream( OpenOption ... opts ){
        try {
            return Files.newInputStream(path, opts);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Открывает или создает файл, возвращая выходной поток, который может использоваться для записи байтов в файл. <p>
     *
     * Результирующий поток не будет буферизирован.  <p>
     *
     * Поток будет безопасным для доступа несколькими параллельными потоками.
     * Независимо от того, является ли возвращенный поток асинхронно закрытым и / или прерывается,
     * сильно зависит от поставщика файловой системы и поэтому не указывается. <p>
     *
     * Этот метод открывает или создает файл точно таким же образом, как метод newByteChannel,
     * за исключением того, что опция READ может отсутствовать в массиве параметров.  <p>
     *
     * Если параметров нет, этот метод работает так, как если
     * бы присутствовали опции CREATE, TRUNCATE_EXISTING и WRITE.  <p>
     *
     * Другими словами, он открывает файл для записи, создавая файл, если он не существует,
     * или изначально обрезает существующий обычный файл до размера 0, если он существует. <p>
     *
     * File file = ... <br>
     * // обрезаем и перезаписываем существующий файл или создаем файл if <br>
     * // он изначально не существует <br>
     * OutputStream out = file.writeStream(); <br> <br>
     *
     * // добавление к существующему файлу, завершение неудачи, если файл не существует <br>
     * out = file.writeStream (APPEND); <br> <br>
     *
     * // добавляем к существующему файлу, создаем файл, если он изначально не существует <br>
     * out = file.writeStream (CREATE, APPEND); <br> <br>
     *
     * // всегда создаем новый файл, если он уже существует <br>
     * out = file.writeStream (CREATE_NEW); <br>
     * @param opts options - опции, определяющие способ открытия файла
     * @return новый выходной поток
     */
    public OutputStream writeStream( OpenOption ... opts ){
        try {
            return Files.newOutputStream(path, opts);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Удаляет файл. <p>
     * Для реализации может потребоваться проверка файла, чтобы определить,
     * является ли файл каталогом.  <p>
     *
     * Следовательно, этот метод может быть не атомарным по отношению к другим операциям файловой системы.  <p>
     *
     * Если файл является символической ссылкой, то сама символическая ссылка, а не конечная цель ссылки, удаляется. <br>
     * Если файл является каталогом, каталог должен быть пустым.  <p>
     *
     * В некоторых реализациях каталог имеет записи для специальных файлов или ссылок,
     * которые создаются при создании каталога.  <p>
     *
     * В таких реализациях каталог считается пустым, когда существуют только специальные записи.  <p>
     *
     * Этот метод можно использовать с методом tree для удаления каталога
     * и всех записей в каталоге или всего необходимого дерева файлов. <p>
     *
     * некоторых операционных системах может быть невозможно удалить файл,
     * когда он открыт и используется этой виртуальной машиной Java или другими программами.
     */
    public void delete(){
        try {
            Files.delete(path);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Удаляет файл, если он существует. <p>
     * Как и в методе delete (Path), реализации, возможно, потребуется изучить файл, чтобы определить,
     * является ли файл каталогом.  <p>
     *
     * Следовательно, этот метод может быть не атомарным по
     * отношению к другим операциям файловой системы.  <p>
     *
     * Если файл является символической ссылкой, то сама символическая ссылка, а не конечная цель ссылки, удаляется. <br>
     *
     * Если файл является каталогом, каталог должен быть пустым.  <p>
     *
     * В некоторых реализациях каталог имеет записи для специальных файлов или ссылок,
     * которые создаются при создании каталога.  <p>
     *
     * В таких реализациях каталог считается пустым, когда существуют только специальные записи. <p>
     *
     * В некоторых операционных системах может быть невозможно удалить файл,
     * когда он открыт и используется этой виртуальной машиной Java или другими программами.
     * @return true, если этот файл был удален этим методом;
     * false, если файл не может быть удален, поскольку он не существует
     */
    public boolean deleteIfExists(){
        try {
            return Files.deleteIfExists(path);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает новый пустой файл в указанном каталоге, используя префикс
     * и суффиксные строки, чтобы сгенерировать его имя. <p>
     *
     * Полученный Путь связан с той же FileSystem, что и указанный каталог. <p>
     *
     * Информация о том, как сконструировано имя файла, зависит от реализации и поэтому не указана.  <p>
     *
     * По возможности префикс и суффикс используются для создания имен кандидатов таким же образом,
     * как метод File.createTempFile (String, String, File). <p>
     *
     * Как и в методах File.createTempFile, этот метод является лишь частью средства временного файла.  <p>
     *
     * В случае использования в качестве рабочих файлов результирующий файл может быть открыт с
     * помощью параметра DELETE_ON_CLOSE, чтобы файл удалялся при вызове соответствующего метода close.  <p>
     *
     * Кроме того, для автоматического удаления файла может использоваться
     * Runtime.addShutdownHook или механизм File.deleteOnExit. <p>
     *
     * Параметр attrs является необязательным атрибутом FileAttribute для атомарного набора при
     * создании файла. Каждый атрибут идентифицируется его FileAttribute.name.  <p>
     *
     * Если в массив включено более одного атрибута с тем же именем, то все,
     * кроме последнего вхождения, игнорируются. <br>
     *
     * Если атрибуты файлов не указаны,
     * то результирующий файл может иметь более ограничительные права доступа к файлам,
     * созданным методом File.createTempFile (String, String, File).
     * @param prefix префиксную строку, которая будет использоваться при создании имени файла; может быть нулевым
     * @param suffix строка суффикса, которая будет использоваться при создании имени файла;
     * может быть нулевым, и в этом случае используется «.tmp»
     * @param attrs необязательный список атрибутов файла для атомарного набора при создании файла
     * @return путь к вновь созданному файлу, который не существовал до этого метода, был вызван
     */
    public File createTempFile( String prefix, String suffix, FileAttribute<?> ... attrs ){
        try {
            return new File( Files.createTempFile(path, prefix, suffix, attrs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает пустой файл в каталоге временных файлов по умолчанию,
     * используя данный префикс и суффикс, чтобы сгенерировать его имя. <p>
     *
     * Полученный Путь связан с файловой системой по умолчанию. <p>
     * Этот метод работает точно так же, как метод Files.createTempFile
     * (Path, String, String, FileAttribute []) для случая,
     * когда параметр dir является каталогом временных файлов.
     * @param prefix префиксную строку, которая будет использоваться при создании имени файла; может быть нулевым
     * @param suffix строка суффикса, которая будет использоваться при создании имени файла;
     * может быть нулевым, и в этом случае используется «.tmp»
     * @param attrs необязательный список атрибутов файла для атомарного набора при создании файла
     * @return  путь к вновь созданному файлу, который не существовал до этого метода, был вызван
     */
    public static File tempFile( String prefix, String suffix, FileAttribute<?> ... attrs ){
        try {
            return new File( Files.createTempFile(prefix, suffix, attrs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает новый каталог в указанном каталоге, используя префикс для генерации его имени. <p>
     *
     * Полученный Путь связан с той же FileSystem, что и указанный каталог. <p>
     *
     * Информация о том, как создается имя каталога, зависит от реализации и поэтому не указана.
     * По возможности префикс используется для создания имен кандидатов. <p>
     *
     * Как и в методах createTempFile, этот метод является лишь частью средства временного файла. <br>
     * Для автоматического удаления каталога может использоваться Runtime.addShutdownHook или механизм File.deleteOnExit. <p>
     *
     * Параметр attrs является необязательным атрибутом FileAttribute для атомарного набора при создании каталога.  <p>
     * Каждый атрибут идентифицируется его FileAttribute.name.  <p>
     *
     * Если в массив включено более одного атрибута с тем же именем, то все, кроме последнего вхождения, игнорируются.
     * @param prefix префиксную строку, которая будет использоваться при создании имени каталога; может быть нулевым
     * @param attrs необязательный список атрибутов файла для атомарного набора при создании файла
     * @return путь к вновь созданному каталогу, который не существовал до этого метода
     */
    public File createTempDirectory( String prefix, FileAttribute<?> ... attrs ){
        try {
            return new File( Files.createTempDirectory(path, prefix, attrs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает новый каталог в каталоге временных файлов по умолчанию, используя префикс для генерации его имени. <p>
     *
     * Полученный Путь связан с файловой системой по умолчанию. <p>
     *
     * Этот метод работает точно так же,
     * как метод Files.createTempDirectory (Path, String, FileAttribute []) для случая,
     * когда параметр dir является файлом временного файла.
     * @param prefix префиксную строку, которая будет использоваться при создании имени каталога; может быть нулевым
     * @param attrs необязательный список атрибутов файла для атомарного набора при создании файла
     * @return  путь к вновь созданному каталогу, который не существовал до этого метода
     */
    public static File tempDirectory( String prefix, FileAttribute<?> ... attrs ){
        try {
            return new File( Files.createTempDirectory(prefix, attrs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает каталог, создавая сначала все несуществующие родительские каталоги. <p>
     *
     * В отличие от метода createDirectory исключение не создается, если каталог
     * не может быть создан, потому что он уже существует. <p>
     *
     * Параметр attrs является необязательным атрибутом FileAttribute для атомарного набора при создании
     * несуществующих каталогов.  <p>
     *
     * Каждый атрибут файла определяется его FileAttribute.name.  <p>
     *
     * Если в массив включено более одного атрибута с тем же именем,
     * то все, кроме последнего вхождения, игнорируются. <p>
     *
     * Если этот метод выходит из строя, он может сделать это после создания некоторых,
     * но не всех, родительских каталогов. <p>
     * @param attrs необязательный список атрибутов файла для атомарного набора при создании каталога
     * @return каталог
     */
    public File createDirectories( FileAttribute<?> ... attrs ){
        try {
            return new File( Files.createDirectories(path, attrs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает новый каталог. <p>
     *
     * Проверка наличия файла и создания каталога,
     * если он не существует, - это одна операция,
     * которая является атомарной относительно всех других действий файловой системы,
     * которые могут повлиять на каталог.  <p>
     *
     * Метод createDirectories
     * должен использоваться там, где требуется сначала создать все несуществующие родительские каталоги. <p>
     *
     * Параметр attrs является необязательным атрибутом FileAttribute для атомарного набора при
     * создании каталога. Каждый атрибут идентифицируется его FileAttribute.name.  <p>
     *
     * Если в массив включено более одного атрибута с тем же именем, то все, кроме последнего вхождения, игнорируются.
     * @param attrs необязательный список атрибутов файла для атомарного набора при создании каталога
     * @return каталог
     */
    public File createDirectory( FileAttribute<?> ... attrs ){
        try {
            return new File( Files.createDirectory(path, attrs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает новый и пустой файл, если файл уже существует. <p>
     *
     * Проверка наличия файла и создание нового файла, если он не существует,
     * - это одна операция, которая является атомарной по отношению ко всем другим действиям
     * файловой системы, которые могут повлиять на каталог. <p>
     *
     * Параметр attrs является необязательным атрибутом
     * FileAttribute для атомарного набора при создании файла.  <p>
     *
     * Каждый атрибут идентифицируется его FileAttribute.name.  <p>
     *
     * Если в массив включено более одного атрибута с тем же именем, то все, кроме последнего вхождения, игнорируются.
     * @param attrs необязательный список атрибутов файла для атомарного набора при создании
     * @return файл
     */
    public File createFile( FileAttribute<?> ... attrs ){
        try {
            return new File( Files.createFile(path, attrs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает новую ссылку (запись в каталоге) для существующего файла (дополнительная операция). <p>
     *
     * Параметр ссылки находит запись каталога для создания.  <p>
     *
     * Существующий параметр - это путь к существующему файлу.  <p>
     *
     * Этот метод создает новую запись каталога для файла, так что к ней можно получить доступ,
     * используя ссылку в качестве пути.  <p>
     *
     * В некоторых файловых системах это называется созданием «жесткой ссылки».  <p>
     *
     * Поддерживаются ли атрибуты файла для файла или для каждой записи каталога, зависит от
     * конкретной файловой системы и поэтому не указывается. Как правило, для файловой системы
     * требуется, чтобы все ссылки (записи в каталоге) для файла находились в
     * одной и той же файловой системе.  <p>
     *
     * Кроме того, на некоторых платформах виртуальная машина
     * Java может потребоваться начать с особых прав реализации для создания жестких ссылок
     * или для создания ссылок на каталоги.
     * @param target цель куда ссылается
     * @return путь к ссылке (запись в каталоге)
     */
    public File createLink( Path target ){
        if( path==null )throw new IllegalArgumentException("path == null");
        try {
            //return new File( Files.createLink(path,original(target)) );
            return new File( Files.createLink(path,(target)) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает новую ссылку (запись в каталоге) для существующего файла (дополнительная операция). <p>
     *
     * Параметр ссылки находит запись каталога для создания.  <p>
     *
     * Существующий параметр - это путь к существующему файлу.
     * Этот метод создает новую запись каталога для файла, так что к ней можно получить доступ,
     * используя ссылку в качестве пути.  <p>
     *
     * В некоторых файловых системах это называется созданием «жесткой ссылки».  <p>
     *
     * Поддерживаются ли атрибуты файла для файла или для каждой записи каталога, зависит от
     * конкретной файловой системы и поэтому не указывается.  <p>
     *
     * Как правило, для файловой системы
     * требуется, чтобы все ссылки (записи в каталоге) для файла находились в
     * одной и той же файловой системе.  <p>
     *
     * Кроме того, на некоторых платформах виртуальная машина
     * Java может потребоваться начать с особых прав реализации для создания жестких ссылок
     * или для создания ссылок на каталоги.
     * @param target цель куда ссылается
     * @return путь к ссылке (запись в каталоге)
     */
    public File createLink( File target ){
        if( path==null )throw new IllegalArgumentException("path == null");
        try {
            //return new File( Files.createLink(path,original(target)) );
            return new File( Files.createLink(path,(target.path)) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает символическую ссылку на цель (дополнительная операция). <p>
     *
     * Целевой параметр является целью ссылки.  <p>
     * Это может быть абсолютный или относительный путь и может не существовать.  <p>
     * Когда целью является относительный путь,
     * то операции файловой системы по результирующей ссылке относятся к пути к ссылке. <p>
     *
     * Параметр attrs является необязательным атрибутом FileAttribute для атомарного набора
     * при создании ссылки. Каждый атрибут идентифицируется его FileAttribute.name.  <p>
     *
     * Если в массив включено более одного атрибута с тем же именем, то все, кроме последнего вхождения, игнорируются. <p>
     *
     * Там, где поддерживаются символические ссылки, но базовый FileStore не поддерживает
     * символические ссылки, это может завершиться с ошибкой IOException.  <p>
     *
     * Кроме того, некоторым операционным системам может потребоваться запуск виртуальной машины Java
     * с конкретными правами реализации для создания символических ссылок,
     *
     * и в этом случае этот метод может вызывать исключение IOException.
     * @param target цель символической ссылки
     * @param attrs массив атрибутов, который устанавливается атомарно при создании символической ссылки
     * @return путь к символической ссылке
     */
    public File createSymbolicLink(Path target, FileAttribute<?> ... attrs){
        try {
            //return new File( Files.createSymbolicLink(path,original(target),attrs) );
            return new File( Files.createSymbolicLink(path,(target),attrs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Создает символическую ссылку на цель (дополнительная операция). <p>
     *
     * Целевой параметр является целью ссылки.  <p>
     * Это может быть абсолютный или относительный путь и может не существовать.  <p>
     * Когда целью является относительный путь,
     * то операции файловой системы по результирующей ссылке относятся к пути к ссылке. <p>
     *
     * Параметр attrs является необязательным атрибутом FileAttribute для атомарного набора
     * при создании ссылки. Каждый атрибут идентифицируется его FileAttribute.name.  <p>
     *
     * Если в массив включено более одного атрибута с тем же именем, то все, кроме последнего вхождения, игнорируются. <p>
     *
     * Там, где поддерживаются символические ссылки, но базовый FileStore не поддерживает
     * символические ссылки, это может завершиться с ошибкой IOException.  <p>
     *
     * Кроме того, некоторым операционным системам может потребоваться запуск виртуальной машины Java
     * с конкретными правами реализации для создания символических ссылок,
     *
     * и в этом случае этот метод может вызывать исключение IOException.
     * @param target цель символической ссылки
     * @param attrs массив атрибутов, который устанавливается атомарно при создании символической ссылки
     * @return путь к символической ссылке
     */
    public File createSymbolicLink(File target, FileAttribute<?> ... attrs){
        try {
            //return new File( Files.createSymbolicLink(path,original(target),attrs) );
            return new File( Files.createSymbolicLink(path,(target.path),attrs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Читает значение атрибута файла. <p>
     *
     * Параметр атрибута идентифицирует атрибут для чтения и принимает форму: <p>
     * [Вид имя:] атрибут-имя <p>
     *
     * где квадратные скобки [...] определяют необязательный компонент, а символ «:» обозначает себя. <p>
     *
     * view-name - это FileAttributeView.name файла FileAttributeView, который идентифицирует набор атрибутов файла.  <p>
     *
     * Если не указано, то по умолчанию используется «basic», имя представления атрибута файла,
     * которое идентифицирует базовый набор атрибутов файлов, общих для многих файловых систем.  <p>
     *
     * attribute-name - это имя атрибута.   <p>
     *
     * Массив опций может использоваться для указания того, как обрабатываются символические ссылки для случая,
     * когда файл является символической ссылкой.  <p>
     *
     * По умолчанию используются символические ссылки
     * и считывается атрибут файла конечной цели ссылки.  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS,
     * то символьные ссылки не соблюдаются. <p>
     *
     * Пример использования.  <p>
     * Предположим, нам нужен идентификатор пользователя владельца файла в системе,
     * которая поддерживает представление «unix»:
     * File file = ...
     * int uid = (Integer) file.getAttribute ("unix: uid");
     * @param attrib атрибут для чтения
     * @param lopts параметры, указывающие, как обрабатываются символические ссылки
     * @return значение атрибута
     */
    public Object getAttribute(String attrib,LinkOption ... lopts){
        try {
            return Files.getAttribute(path, attrib, lopts);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Устанавливает значение атрибута файла. <p>
     *
     * Параметр атрибута определяет атрибут, который должен быть установлен, и принимает форму: <p>
     *
     * [Вид имя:] атрибут-имя <p>
     *
     * где квадратные скобки [...] определяют необязательный компонент, а символ «:» обозначает себя. <p>
     *
     * view-name - это FileAttributeView.name файла FileAttributeView,
     * который идентифицирует набор атрибутов файла.  <p>
     *
     * Если не указано, то по умолчанию используется «basic», имя представления атрибута файла,
     * которое идентифицирует базовый набор атрибутов файлов, общих для многих файловых систем.  <p>
     *
     * attribute-name - это имя атрибута в наборе. <p>
     *
     * Массив опций может использоваться для указания того,
     * как обрабатываются символические ссылки для случая, когда файл является символической ссылкой.  <p>
     *
     * По умолчанию используются символические ссылки и задан атрибут файла конечной цели ссылки.  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS, то символьные ссылки не соблюдаются. <p>
     *
     * Пример использования: предположим, что мы хотим установить DOS «скрытый» атрибут: <p>
     *
     * File file = ... <br>
     * files.setAttribute ("dos: hidden", true); <br>
     * @param attrib атрибут для установки
     * @param value значение атрибута
     * @param lopts параметры, указывающие, как обрабатываются символические ссылки
     * @return сам файл
     */
    public File setAttribute(String attrib,Object value,LinkOption ... lopts){
        try {
            return new File( Files.setAttribute(path, attrib, value, lopts) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Возвращает представление атрибута файла для заданного типа. <p>
     *
     * Представление атрибута файла предоставляет доступное только для чтения или обновляемое представление набора
     * атрибутов файла.  <p>
     *
     * Этот метод предназначен для использования, когда
     * представление атрибута файла определяет методы безопасного типа для
     * чтения или обновления атрибутов файла.  <p>
     *
     * Параметр type - это тип требуемого вида атрибута,
     * и метод возвращает экземпляр этого типа, если он поддерживается.  <p>
     *
     * Тип BasicFileAttributeView поддерживает доступ к основным атрибутам файла.  <p>
     *
     * Вызов этого метода для выбора представления атрибута файла этого типа всегда
     * будет возвращать экземпляр этого класса. <p>
     *
     * Массив параметров может использоваться, чтобы указать,
     * как обрабатываются символические ссылки в результате представления атрибута файла для
     * случая, когда файл является символической ссылкой.  <p>
     *
     * По умолчанию используются символические ссылки.  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS, то символьные ссылки не соблюдаются.  <p>
     *
     * Этот вариант игнорируется реализациями, которые не поддерживают символические ссылки. <p>
     *
     * Пример использования:  <p>
     * предположим, что мы хотим прочитать или установить ACL файла, если он поддерживается:
     *
     * <pre>
     * File file = ...
     * AclFileAttributeView view = Files.getFileAttributeView (путь, AclFileAttributeView.class);
     * if (view! = null) {
     *    List&lt;AclEntry&gt; acl = view.getAcl ();
     *    ...
     * }
     * </pre>
     *
     * @param <V> Тип атрибутов
     * @param type объект класса, соответствующий представлению атрибута файла
     * @param lopts параметры, указывающие, как обрабатываются символические ссылки
     * @return представление атрибута файла указанного типа или null, если тип представления атрибута недоступен
     */
    public <V extends FileAttributeView> V getFileAttributeView(Class<V> type, LinkOption ... lopts){
        return Files.getFileAttributeView(path, type, lopts);
    }

    /**
     * Возвращает FileStore, представляющий хранилище файлов, в котором находится файл. <p>
     *
     * Когда полученная ссылка на FileStore получена,
     * она специфична для реализации, если операции с возвращаемыми объектами FileStore или FileStoreAttributeView,
     * полученными из нее, продолжают зависеть от существования файла.  <p>
     *
     * В частности, поведение не определено для случая, когда файл удаляется или перемещается в другой хранилище файлов.
     * @return хранилище файлов, в котором хранится файл
     */
    public FileStore getFileStore(){
        try {
            return Files.getFileStore(path);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Возвращает последнее измененное время файла. <p>
     *
     * Массив опций может использоваться для указания того, как обрабатываются символические ссылки
     * для случая, когда файл является символической ссылкой.  <p>
     *
     * По умолчанию используются символические
     * ссылки и считывается атрибут файла конечной цели ссылки.
     *  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS,
     * то символьные ссылки не соблюдаются.
     * @param lopts параметры, указывающие, как обрабатываются символические ссылки
     * @return FileTime, представляющий время последнего изменения файла,
     * или определенный по умолчанию по умолчанию, когда временная метка,
     * указывающая время последней модификации, не поддерживается файловой системой
     */
    public FileTime getLastModifiedTime(LinkOption ... lopts){
        try {
            return Files.getLastModifiedTime(path, lopts);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Возвращает последнее измененное время файла. <p>
     *
     * Массив опций может использоваться для указания того, как обрабатываются символические ссылки
     * для случая, когда файл является символической ссылкой.  <p>
     *
     * По умолчанию используются символические
     * ссылки и считывается атрибут файла конечной цели ссылки.  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS,
     * то символьные ссылки не соблюдаются.
     * @param lopts параметры, указывающие, как обрабатываются символические ссылки
     * @return время последнего изменения
     */
    public Date getLastModifiedDate(LinkOption ... lopts){
        FileTime ftime = getLastModifiedTime(lopts);
        return new Date(ftime.toMillis());
    }

    /**
     * Обновляет атрибут последнего измененного времени файла.  <p>
     *
     * Время файла преобразуется в эпоху и точность,
     * поддерживаемую файловой системой.  <p>
     *
     * Преобразование от более тонких к более крупным деталям приводит к прецизионным потерям.  <p>
     *
     * Поведение этого метода при попытке установить последнее измененное время, когда оно не поддерживается
     * файловой системой или находится за пределами диапазона, поддерживаемого базовым хранилищем файлов, не определено.
     *  <p>
     *
     * Это может произойти с ошибкой IOException.
     * @param ftime новое значение последнего измененнения
     * @return файл
     */
    public File setLastModifiedTime(FileTime ftime){
        try {
            return new File( Files.setLastModifiedTime(path, ftime) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Обновляет атрибут последнего измененного времени файла.  <p>
     *
     * Время файла преобразуется в эпоху и точность, поддерживаемую файловой системой.  <p>
     *
     * Преобразование от более тонких к более крупным деталям приводит к прецизионным потерям.  <p>
     *
     * Поведение этого метода при попытке установить последнее измененное время, когда
     * оно не поддерживается файловой системой или находится за пределами диапазона,
     * поддерживаемого базовым хранилищем файлов, не определено.  <p>
     *
     * Это может произойти с ошибкой IOException.
     * @param date новое значение последнего измененнения
     * @return файл
     */
    public File setLastModifiedDate( Date date ){
        FileTime ftime = FileTime.fromMillis(date.getTime());
        return new File( setLastModifiedTime(ftime) );
    }

    /**
     * Возвращает владельца файла. <p>
     *
     * Параметр path связан с файловой системой, которая поддерживает FileOwnerAttributeView.
     * Это представление атрибута файла предоставляет доступ к атрибуту
     * файла, который является владельцем файла.
     * @param lopts параметры, указывающие, как обрабатываются символические ссылки
     * @return Пользователь-пользователь, представляющий владельца файла
     */
    public UserPrincipal getOwner(LinkOption ... lopts){
        try {
            return Files.getOwner(path, lopts);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Обновляет владельца файла. <p>
     *
     * Параметр path связан с файловой системой, которая поддерживает FileOwnerAttributeView.  <p>
     *
     * Это представление атрибута файла
     * предоставляет доступ к атрибуту файла, который является владельцем файла. <p>
     *
     * Пример использования: предположим, что мы хотим сделать «joe» владельцем файла: <p>
     * Путь пути = ...
     * UserPrincipalLookupService lookupService =
     * поставщик (путь) .getUserPrincipalLookupService ();
     * UserPrincipal joe = lookupService.lookupPrincipalByName ("joe");
     * Files.setOwner (путь, joe);
     * @param user Новый владелец файла
     * @return файл
     */
    public File setOwner(UserPrincipal user){
        try {
            return new File( Files.setOwner(path, user) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Возвращает права доступа к файлам POSIX. <p>
     *
     * Параметр path связан с файловой системой, которая поддерживает PosixFileAttributeView.  <p>
     *
     * Это представление атрибутов обеспечивает доступ к атрибутам файлов,
     * обычно связанным с файлами файловых систем, используемых операционными системами,
     * которые используют семейство стандартов Portable Operating System Interface (POSIX). <p>
     *
     * Массив опций может использоваться для указания того,
     * как обрабатываются символические ссылки для случая, когда файл является символической ссылкой.
     *  <p>
     *
     * По умолчанию используются символические ссылки и считывается атрибут файла конечной цели ссылки.
     *  <p>
     *
     * Если присутствует опция NOFOLLOW_LINKS, то символьные ссылки не соблюдаются.
     * @param lopts параметры, указывающие, как обрабатываются символические ссылки
     * @return права доступа к файлам
     */
    public Set<PosixFilePermission> getPosixFilePermissions(LinkOption ... lopts){
        try {
            return Files.getPosixFilePermissions(path, lopts);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Устанавливает разрешения POSIX файла. <p>
     *
     * Параметр path связан с файловой системой, которая поддерживает PosixFileAttributeView.
     *  <p>
     *
     * Это представление атрибутов обеспечивает доступ к атрибутам файлов,
     * обычно связанным с файлами файловых систем, используемых операционными системами,
     * которые используют семейство стандартов Portable Operating System Interface (POSIX).
     *
     * @param perms Новый набор разрешений
     * @return файл
     */
    public File setPosixFilePermissions( Set<PosixFilePermission> perms ){
        try {
            return new File( Files.setPosixFilePermissions(path, perms) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Переместите или переименуйте файл в целевой файл. <p>
     *
     * По умолчанию этот метод пытается переместить файл в целевой файл,
     * если он не существует, если целевой файл существует, за исключением случаев,
     * когда источник и цель являются одним и тем же файлом, и в этом случае этот метод не влияет.  <p>
     *
     * Если файл является символической ссылкой, перемещается сама символическая ссылка, а не цель ссылки.
     * Этот метод можно вызвать, чтобы переместить пустой каталог.  <p>
     *
     * В некоторых реализациях каталог имеет записи для специальных файлов или ссылок,
     * которые создаются при создании каталога. В таких реализациях каталог считается пустым,
     * когда существуют только специальные записи. <p>
     *
     * При вызове для перемещения каталога,
     * который не является пустым, каталог перемещается, если он не требует перемещения
     * записей в каталоге.  <p>
     *
     * Например, переименование каталога в том же FileStore, как правило,
     * не требует перемещения записей в каталоге.  <p>
     *
     * При перемещении каталога требуется, чтобы его записи были перемещены,
     * тогда этот метод завершился неудачно (путем исключения исключения IOException).  <p>
     *
     * Для перемещения дерева файлов может потребоваться копирование, а не перемещение каталогов,
     * и это можно сделать с помощью метода копирования в сочетании с утилитой Files.walkFileTree. <p>
     *
     * Параметр параметров может включать в себя любое из следующего:
     * <ul>
     * <li>
     * REPLACE_EXISTING
     * Если целевой файл существует, то целевой файл заменяется,
     * если он не является непустым каталогом.
     * Если целевой файл существует и является символической ссылкой,
     * вместо него заменяется сама символическая ссылка, а не цель ссылки.
     *
     * <li>ATOMIC_MOVE
     * Перемещение выполняется как операция с файловой системой атома,
     * и все остальные параметры игнорируются.  <p>
     *
     * Если целевой файл существует, то он специфичен для реализации, если существующий
     * файл заменен или этот метод терпит неудачу, выбрасывая исключение IOException.
     *
     * Если перемещение не может выполняться как операция с использованием атомной файловой системы,
     * то вызывается AtomicMoveNotSupportedException.  <p>
     *
     * Это может возникнуть, например, когда целевое местоположение находится в другом файловом
     * хранилище и требует, чтобы файл был скопирован, или целевое местоположение
     * связано с другим поставщиком этого объекта.
     * </ul>
     *
     * Реализация этого интерфейса может поддерживать дополнительные варианты реализации. <p>
     *
     * Если для перемещения требуется скопировать файл,
     * то файл BasicFileAttributes.lastModifiedTime будет скопирован в новый файл.  <p>
     *
     * Реализация также может попытаться скопировать другие атрибуты файла,
     * но не требуется сбой, если атрибуты файла не могут быть скопированы.  <p>
     *
     * Когда перемещение выполняется как неатомная операция, и генерируется исключение IOException,
     * состояние файлов не определяется.  <p>
     *
     * Исходный файл и целевой файл могут существовать, целевой файл может быть неполным или некоторые
     * его атрибуты файла не могут быть скопированы из исходного файла. <p>
     *
     * Примеры использования.  <p>
     * Предположим, мы хотим переименовать файл в «новое имя», сохранив файл в том же каталоге:
     *
     * Источник пути = ...
     * Files.move (source, source.resolveSibling ("newname"));
     *
     * В качестве альтернативы предположим,
     * что мы хотим переместить файл в новый каталог,
     * сохранив одно и то же имя файла и заменив любой существующий файл этого имени в каталоге:
     *
     * Источник пути = ...
     * Путь newdir = ...
     * Files.move (source, newdir.resolve (source.getFileName ()), REPLACE_EXISTING);
     * @param target путь к целевому файлу (может быть связан с другим провайдером с исходным путем)
     * @param copts варианты, указывающие, как следует переименовывать
     * @return путь к целевому файлу
     */
    public File move( Path target, CopyOption ... copts ){
        try {
            //return new File( Files.move(path, original(target), copts) );
            return new File( Files.move(path, (target), copts) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Переместите или переименуйте файл в целевой файл. <p>
     *
     * По умолчанию этот метод пытается переместить файл в целевой файл,
     * если он не существует, если целевой файл существует, за исключением случаев,
     * когда источник и цель являются одним и тем же файлом, и в этом случае этот метод не влияет.
     *
     * Если файл является символической ссылкой, перемещается сама символическая ссылка, а не цель ссылки.
     * Этот метод можно вызвать, чтобы переместить пустой каталог.
     * В некоторых реализациях каталог имеет записи для специальных файлов или ссылок,
     * которые создаются при создании каталога. В таких реализациях каталог считается пустым,
     * когда существуют только специальные записи. При вызове для перемещения каталога,
     * который не является пустым, каталог перемещается, если он не требует перемещения
     * записей в каталоге. Например, переименование каталога в том же FileStore, как правило,
     * не требует перемещения записей в каталоге.
     *
     * При перемещении каталога требуется, чтобы его записи были перемещены,
     * тогда этот метод завершился неудачно (путем исключения исключения IOException).
     *
     * Для перемещения дерева файлов может потребоваться копирование, а не перемещение каталогов,
     * и это можно сделать с помощью метода копирования в сочетании с утилитой Files.walkFileTree.
     *
     * Параметр параметров может включать в себя любое из следующего:
     * REPLACE_EXISTING
     * Если целевой файл существует, то целевой файл заменяется,
     * если он не является непустым каталогом.
     * Если целевой файл существует и является символической ссылкой,
     * вместо него заменяется сама символическая ссылка, а не цель ссылки.
     *
     * ATOMIC_MOVE
     * Перемещение выполняется как операция с файловой системой атома,
     * и все остальные параметры игнорируются.
     * Если целевой файл существует, то он специфичен для реализации, если существующий
     * файл заменен или этот метод терпит неудачу, выбрасывая исключение IOException.
     *
     * Если перемещение не может выполняться как операция с использованием атомной файловой системы,
     * то вызывается AtomicMoveNotSupportedException.
     *
     * Это может возникнуть, например, когда целевое местоположение находится в другом файловом
     * хранилище и требует, чтобы файл был скопирован, или целевое местоположение
     * связано с другим поставщиком этого объекта.
     *
     * Реализация этого интерфейса может поддерживать дополнительные варианты реализации.
     *
     * Если для перемещения требуется скопировать файл,
     * то файл BasicFileAttributes.lastModifiedTime будет скопирован в новый файл.
     *
     * Реализация также может попытаться скопировать другие атрибуты файла,
     * но не требуется сбой, если атрибуты файла не могут быть скопированы.
     * Когда перемещение выполняется как неатомная операция, и генерируется исключение IOException,
     * состояние файлов не определяется.
     *
     * Исходный файл и целевой файл могут существовать, целевой файл может быть неполным или некоторые
     * его атрибуты файла не могут быть скопированы из исходного файла.
     *
     * Примеры использования.
     * Предположим, мы хотим переименовать файл в «новое имя», сохранив файл в том же каталоге:
     *
     * Источник пути = ...
     * Files.move (source, source.resolveSibling ("newname"));
     *
     * В качестве альтернативы предположим,
     * что мы хотим переместить файл в новый каталог,
     * сохранив одно и то же имя файла и заменив любой существующий файл этого имени в каталоге:
     *
     * Источник пути = ...
     * Путь newdir = ...
     * Files.move (source, newdir.resolve (source.getFileName ()), REPLACE_EXISTING);
     * @param target путь к целевому файлу (может быть связан с другим провайдером с исходным путем)
     * @param copts варианты, указывающие, как следует переименовывать
     * @return путь к целевому файлу
     */
    public File move( File target, CopyOption ... copts ){
        try {
            //return new File( Files.move(path, original(target), copts) );
            return new File( Files.move(path, (target.path), copts) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Копирует все байты из входного потока в файл. <p>
     *
     * По возвращении входной поток будет в конце потока. <p>
     *
     * По умолчанию копия завершается с ошибкой, если целевой файл уже существует или является символической ссылкой.
     * Если указан параметр REPLACE_EXISTING, и целевой файл уже существует, то он заменяется,
     * если он не является непустым каталогом. Если целевой файл существует и является символической ссылкой,
     * то символическая ссылка заменяется.  <p>
     *
     * В этом выпуске опция REPLACE_EXISTING является единственным вариантом,
     * который должен поддерживаться этим методом.  <p>
     *
     * Дополнительные опции могут поддерживаться в будущих выпусках. <p>
     *
     * Если возникает ошибка ввода-вывода из входного потока или запись в файл,
     * он может сделать это после создания целевого файла и после того, как некоторые байты будут прочитаны или записаны.
     *  <p>
     *
     * Следовательно, входной поток может не находиться в конце потока и может находиться
     * в несогласованном состоянии. Настоятельно рекомендуется, чтобы входной поток был быстро закрыт,
     * если возникла ошибка ввода-вывода. <p>
     *
     * Этот метод может блокировать бесконечное чтение из входного потока (или записи в файл).
     * Поведение для случая, когда входной поток асинхронно закрыт, или поток,
     * прерванный во время копирования, является сильно входным потоком и поставщиком файловой системы,
     * и поэтому не указан. <p>
     *
     * Пример использования: предположим, что мы хотим захватить веб-страницу и сохранить ее в файле:
     *
     * <pre>
     * File file = ...
     * URI u = URI.create ("http://java.sun.com/");
     * try (InputStream input = u.toURL().openStream ()) {
     *    file.copy (input);
     * }
     * </pre>
     * @param input входной поток для чтения
     * @param copts параметры, указывающие, как должна выполняться копия
     * @return количество прочитанных или записанных байтов
     */
    public long copyFrom(InputStream input, CopyOption ... copts){
        try {
            return Files.copy(input, path, copts);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Копирует все байты из файла в выходной поток. <p>
     *
     * Если ошибка ввода-вывода происходит из-за чтения из файла или записи в выходной поток,
     * он может это сделать после того, как некоторые байты были прочитаны или записаны.  <p>
     *
     * Следовательно, выходной поток может находиться в несогласованном состоянии.
     * Настоятельно рекомендуется, чтобы выходной поток был быстро закрыт, если произошла ошибка ввода-вывода.
     *  <p>
     *
     * Этот метод может блокировать бесконечную запись в выходной поток (или чтение из файла).
     * Поведение для случая, когда выходной поток асинхронно закрыт, или поток,
     * прерванный во время копирования, является высокопроизводительным потоком и поставщиком файловой системы,
     * и поэтому не указан. <p>
     *
     * Обратите внимание, что если данный выходной поток Flushable, то его метод flush
     * может потребоваться вызвать после завершения этого метода, чтобы сбросить любой буферный вывод.
     * @param out выходной поток для записи
     * @return количество прочитанных или записанных байтов
     */
    public long copyTo(OutputStream out){
        try {
            return Files.copy(path, out);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Скопируйте файл в целевой файл. <p>
     *
     * Этот метод копирует файл в целевой файл с параметром options, определяющим, как выполняется копирование.
     * По умолчанию копия завершается с ошибкой, если целевой файл уже существует или является символической ссылкой,
     * за исключением случаев, когда источник и цель являются одним и тем же файлом, и в этом случае метод
     * завершается без копирования файла. Атрибуты файлов не требуются для копирования в целевой файл. <p>
     *
     * Если символические ссылки поддерживаются, а файл является символической ссылкой, то конечная цель ссылки копируется.
     * Если файл является каталогом, он создает пустой каталог в целевом местоположении
     * (записи в каталоге не копируются). <p>
     *
     * Этот метод можно использовать с методом walkFileTree,
     * чтобы скопировать каталог и все записи в каталоге или полное дерево файлов, где это необходимо. <p>
     *
     * Параметр параметров может включать в себя любое из следующего:
     * <ul>
     * <li>REPLACE_EXISTING
     * Если целевой файл существует, то целевой файл заменяется, если он не является непустым каталогом.
     * Если целевой файл существует и является символической ссылкой, вместо него заменяется сама символическая ссылка,
     * а не цель ссылки.
     *
     * <li>COPY_ATTRIBUTES
     * Попытка скопировать атрибуты файла, связанные с этим файлом, в целевой файл.
     * Точные атрибуты файлов, которые копируются, зависят от платформы и файловой системы и поэтому не указаны.
     * В минимальном случае значение BasicFileAttributes.lastModifiedTime копируется в целевой файл,
     * если поддерживается как хранилищем источника, так и целевым файлом. Копирование временных меток файла может
     * привести к потере точности.
     *
     * <li>NOFOLLOW_LINKS
     * Символические ссылки не соблюдаются.
     * Если файл является символической ссылкой, копируется сама символическая ссылка, а не цель ссылки.
     * Это специфично для реализации, если атрибуты файлов можно скопировать в новую ссылку.
     * Другими словами, параметр COPY_ATTRIBUTES может игнорироваться при копировании символической ссылки.
     * </ul>
     *
     * Реализация этого интерфейса может поддерживать дополнительные варианты реализации. <p>
     *
     * Копирование файла не является атомной операцией.
     * Если выбрано исключение IOException, возможно, что целевой файл является неполным
     * или некоторые его атрибуты файла не были скопированы из исходного файла.  <p>
     *
     * Когда задан параметр REPLACE_EXISTING и существует целевой файл, то целевой файл заменяется.
     * Проверка наличия файла и создание нового файла может быть не атомарной в отношении других действий
     * файловой системы. <p>
     *
     * Пример использования: <br>
     * предположим, что мы хотим скопировать файл в каталог, указав ему то же имя файла, что и исходный файл:
     *
     * File source = ...
     * File newdir = ...
     * source.copy(newdir.resolve(source.getFileName());
     * @param target путь к файлу для копирования
     * @param copts параметры, указывающие, как должна выполняться копия
     * @return путь к целевому файлу
     */
    public File copyTo(Path target, CopyOption ... copts){
        try {
            //return new File( Files.copy(path, original(target), copts) );
            return new File( Files.copy(path, (target), copts) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Скопируйте файл в целевой файл. <p>
     *
     * Этот метод копирует файл в целевой файл с параметром options, определяющим, как выполняется копирование.
     * По умолчанию копия завершается с ошибкой, если целевой файл уже существует или является символической ссылкой,
     * за исключением случаев, когда источник и цель являются одним и тем же файлом, и в этом случае метод
     * завершается без копирования файла. Атрибуты файлов не требуются для копирования в целевой файл. <p>
     *
     * Если символические ссылки поддерживаются, а файл является символической ссылкой, то конечная цель ссылки копируется.
     * Если файл является каталогом, он создает пустой каталог в целевом местоположении
     * (записи в каталоге не копируются). <p>
     *
     * Этот метод можно использовать с методом walkFileTree,
     * чтобы скопировать каталог и все записи в каталоге или полное дерево файлов, где это необходимо. <p>
     *
     * Параметр параметров может включать в себя любое из следующего:
     * <ul>
     * <li>REPLACE_EXISTING
     * Если целевой файл существует, то целевой файл заменяется, если он не является непустым каталогом.
     * Если целевой файл существует и является символической ссылкой, вместо него заменяется сама символическая ссылка,
     * а не цель ссылки.
     *
     * <li>COPY_ATTRIBUTES
     * Попытка скопировать атрибуты файла, связанные с этим файлом, в целевой файл.
     * Точные атрибуты файлов, которые копируются, зависят от платформы и файловой системы и поэтому не указаны.
     * В минимальном случае значение BasicFileAttributes.lastModifiedTime копируется в целевой файл,
     * если поддерживается как хранилищем источника, так и целевым файлом. Копирование временных меток файла может
     * привести к потере точности.
     *
     * <li>NOFOLLOW_LINKS
     * Символические ссылки не соблюдаются.
     * Если файл является символической ссылкой, копируется сама символическая ссылка, а не цель ссылки.
     * Это специфично для реализации, если атрибуты файлов можно скопировать в новую ссылку.
     * Другими словами, параметр COPY_ATTRIBUTES может игнорироваться при копировании символической ссылки.
     * </ul>
     *
     * Реализация этого интерфейса может поддерживать дополнительные варианты реализации. <p>
     *
     * Копирование файла не является атомной операцией.
     * Если выбрано исключение IOException, возможно, что целевой файл является неполным
     * или некоторые его атрибуты файла не были скопированы из исходного файла.  <p>
     *
     * Когда задан параметр REPLACE_EXISTING и существует целевой файл, то целевой файл заменяется.
     * Проверка наличия файла и создание нового файла может быть не атомарной в отношении других действий
     * файловой системы. <p>
     *
     * Пример использования: <br>
     * предположим, что мы хотим скопировать файл в каталог, указав ему то же имя файла, что и исходный файл:
     *
     * File source = ...
     * File newdir = ...
     * source.copy(newdir.resolve(source.getFileName());
     * @param target путь к файлу для копирования
     * @param copts параметры, указывающие, как должна выполняться копия
     * @return путь к целевому файлу
     */
    public File copyTo(File target, CopyOption ... copts){
        try {
            //return new File( Files.copy(path, original(target), copts) );
            return new File( Files.copy(path, (target.path), copts) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Проверяет тип содержимого файла. <p>
     *
     * Этот метод использует установленные реализации FileTypeDetector,
     * чтобы исследовать данный файл, чтобы определить его тип содержимого. <p>
     *
     * Каждый тип FileTypeDetector.probeContentType детектора типа файла, в свою очередь,
     * вызывается для поиска типа файла. <p>
     *
     * Если файл распознается, возвращается тип содержимого. <br>
     * Если файл не распознается каким-либо из установленных детекторов типа файла,
     * то для угадывания типа содержимого вызывается детектор типа файла по умолчанию. <p>
     *
     * Данный вызов виртуальной машины Java поддерживает общесистемный список детекторов типов файлов.
     * Установленные детекторы типов файлов загружаются с использованием средства загрузки поставщика услуг,
     * определенного классом ServiceLoader. <p>
     *
     * Установленные детекторы типов файлов загружаются с помощью загрузчика системного класса.
     * Если загрузчик системного класса не найден, используется загрузчик класса расширения; <br>
     *
     * Если загрузчик класса расширения не найден, используется загрузчик класса загрузки. <p>
     * Детекторы типа файла обычно устанавливаются путем размещения их в файле JAR в CLASSPATH приложения или
     * в каталоге расширения,
     *
     * файл JAR содержит файл конфигурации поставщика с именем java.nio.file.spi.FileTypeDetector
     * в каталоге ресурсов META-INF / services,
     *
     * а в файле перечислены одно или несколько полностью определенных имен конкретного подкласса
     * FileTypeDetector, которые имеют конструктор нулевого аргумента. <p>
     *
     * Если процесс обнаружения или создания экземпляров установленных детекторов типов файлов не выполняется,
     * генерируется неуказанная ошибка. <p>
     *
     * Порядок размещения установленных провайдеров является специфичным для реализации. <p>
     *
     * Возвращаемое значение этого метода представляет собой строковую форму значения
     * типа содержимого многопользовательской интернет-почты (MIME), как определено RFC 2045:
     * Многоцелевые расширения электронной почты Интернета (MIME). <p>
     *
     * Часть первая: формат интернет-сообщений.
     * Строка гарантированно может быть проанализирована в соответствии с грамматикой в ​​RFC.
     * @return Тип содержимого файла или null, если тип содержимого не может быть определен
     */
    public String probeContentType(){
        try {
            return Files.probeContentType(path);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Открывает файл для чтения, возвращая BufferedReader,
     * который может использоваться для эффективного чтения текста из файла. <p>
     *
     * Байты из файла декодируются в символы с использованием указанной кодировки. Чтение начинается в начале файла. <p>
     *
     * Методы Reader, которые читают из файла, вызывают исключение IOException,
     * если считывается некорректная или неуправляемая последовательность байтов.
     * @param cs кодировка
     * @return новый буферный считыватель с размером буфера по умолчанию для чтения текста из файла
     */
    public BufferedReader reader( Charset cs ){
        try {
            return Files.newBufferedReader(path, cs!=null ? cs : Charset.defaultCharset() );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Открывает файл для чтения, возвращая BufferedReader,
     * который может использоваться для эффективного чтения текста из файла. <p>
     *
     * Байты из файла декодируются в символы с использованием указанной кодировки. Чтение начинается в начале файла. <p>
     *
     * Методы Reader, которые читают из файла, вызывают исключение IOException,
     * если считывается некорректная или неуправляемая последовательность байтов.
     * @param cs кодировка
     * @return новый буферный считыватель с размером буфера по умолчанию для чтения текста из файла
     */
    public BufferedReader reader( String cs ){
        try {
            return Files.newBufferedReader(path, cs!=null ? Charset.forName(cs) : Charset.defaultCharset() );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Открывает или создает файл для записи, возвращая BufferedWriter,
     * который может использоваться для эффективного ввода текста в файл. <p>
     *
     * Параметр options указывает, как файл создается или открывается.
     * Если параметров нет, этот метод работает так, как если бы присутствовали опции CREATE, TRUNCATE_EXISTING и WRITE. <p>
     *
     * Другими словами, он открывает файл для записи, создавая файл,
     * если он не существует, или изначально обрезает существующий обычный файл до размера 0, если он существует. <p>
     *
     * Методы Writer для записи текста IOException, если текст не может быть закодирован с использованием указанной кодировки.
     * @param cs кодировка
     * @return новый буферный писатель с размером буфера по умолчанию для записи текста в файл
     */
    public BufferedWriter writer( Charset cs ){
        try {
            return Files.newBufferedWriter(path, cs);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Открывает или создает файл, возвращая поисковый байтовый канал для доступа к файлу. <p>
     * Этот метод открывает или создает файл точно таким же образом, как метод newByteChannel.
     * @param oopts параметры, определяющие способ открытия файла
     * @return новый доступный байтовый канал
     */
    public SeekableByteChannel channel( OpenOption ... oopts ){
        try {
            return Files.newByteChannel(path, oopts);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Открывает или создает файл, возвращая поисковый байтовый канал для доступа к файлу. <p>
     *
     * Параметр options определяет способ открытия файла. <br>
     * Опции READ и WRITE определяют, должен ли файл быть открыт для чтения и / или записи. <br>
     * Если ни один из параметров (или параметр APPEND) не присутствует, файл открывается для чтения.  <br>
     * По умолчанию чтение или запись начинаются в начале файла. <br>
     * В дополнение к READ и WRITE могут присутствовать следующие параметры:
     *
     * <ul>
     * <li>APPEND Если эта опция присутствует, файл открывается для записи, и каждый вызов метода записи
     * канала сначала продвигает позицию до конца файла, а затем записывает запрошенные данные.
     * Независимо от того, выполняется ли продвижение позиции и запись данных в одной атомной операции,
     * зависит от системы и, следовательно, не определена.
     * Этот параметр не может использоваться в сочетании с параметрами READ или TRUNCATE_EXISTING.
     *
     * <li>TRUNCATE_EXISTING Если этот параметр присутствует, существующий файл усекается до размера 0 байтов.
     * Эта опция игнорируется, когда файл открывается только для чтения.
     *
     * <li>CREATE_NEW
     * Если этот параметр присутствует, создается новый файл, если он не существует или является символической ссылкой.
     * При создании файла проверка наличия файла и создание файла, если он не существует,
     * является атомарным относительно других действий файловой системы.
     * Эта опция игнорируется, когда файл открывается только для чтения.
     *
     * <li>CREATE Если этот параметр присутствует, тогда существующий файл открывается,
     * если он существует, в противном случае создается новый файл.
     * Этот параметр игнорируется, если присутствует опция CREATE_NEW или файл открывается только для чтения.
     *
     * <li>DELETE_ON_CLOSE Когда этот параметр присутствует,
     * реализация выполняет попытку удалить файл при закрытии методом SeekableByteChannel.close.
     * Если метод close не вызывается, предпринимается попытка с максимальной эффективностью удалить файл
     * при завершении виртуальной машины Java.
     *
     * <li>SPARSE При создании нового файла этот параметр является подсказкой о том,
     * что новый файл будет разреженным. Этот параметр игнорируется, если не создается новый файл.
     *
     * <li>SYNC Требуется, чтобы каждое обновление содержимого или метаданных файла было записано
     * синхронно с основным запоминающим устройством. (см. «Синхронизированная целостность файла ввода / вывода»).
     *
     * <li>DSYNC Требуется, чтобы каждое обновление содержимого файла записывалось
     * синхронно с базовым устройством хранения. (см. «Синхронизированная целостность файла ввода / вывода»).
     * </ul>
     *
     * Реализация также может поддерживать дополнительные варианты реализации.
     * Параметр attrs является необязательным атрибутом FileAttribute для
     * атомарного набора при создании нового файла.
     * <p>
     *
     * В случае провайдера по умолчанию возвращаемый запрашиваемый байтовый канал является FileChannel.<p>
     *
     * Примеры использования: <p>
     *
     * <code>File file = ... <br>
     * // открыть файл для чтения <br>
     * ReadableByteChannel rbc = file.channel (EnumSet.of(READ)); <br>
     * <br>
     * // открываем файл для записи в конец существующего файла, создавая<br>
     * // файл, если он еще не существует <br>
     * WritableByteChannel wbc = file.channel (EnumSet.of(CREATE, APPEND)); <br>
     * <br>
     * // создаем файл с начальными разрешениями, открывая его для чтения и записи<br>
     * FileAttribute&lt;SetPosixFilePermission&gt; perms = ...<br>
     * SeekableByteChannel sbc = file.channel (EnumSet.of(CREATE_NEW, READ, WRITE), perms);<br>
     * </code>
     * @param options Опции открытия
     * @param attrs необязательный список атрибутов файла для атомарного набора при создании файла
     * @return новый доступный байтовый канал
     */
    public SeekableByteChannel channel( Set<? extends OpenOption> options, FileAttribute<?>... attrs ){
        try {
            return Files.newByteChannel(path, options, attrs);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Читает все байты из файла. <p>
     *
     * Метод гарантирует, что файл закрыт, когда все байты были прочитаны или
     * ошибка ввода-вывода или другое исключение среды выполнения. <p>
     *
     * Обратите внимание, что этот метод предназначен для простых случаев,
     * когда удобно считать все байты в массив байтов.  <p>
     *
     * Он не предназначен для чтения в больших файлах.
     * @return массив байтов, содержащий байты, считанные из файла
     */
    public byte[] readAllBytes(){
        try {
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="write()">
    /** Записывает байты в файл. <p>
     *
     * Параметр options указывает, как файл создается или открывается. <p>
     *
     * Если параметров нет, этот метод работает так, как если бы присутствовали опции
     * CREATE, TRUNCATE_EXISTING и WRITE. <p>
     *
     * Другими словами, он открывает файл для записи, создает файл, если он не существует,
     * или изначально обрезает существующий обычный файл до размера 0. <p>
     *
     * Все байты в массиве байтов записываются в файл. <p>
     *
     * Метод гарантирует, что файл закрыт, когда все байты были записаны
     * (или выбрана ошибка ввода-вывода или другое исключение во время выполнения). <p>
     *
     * Если возникает ошибка ввода-вывода, она может сделать это после того,
     * как файл был создан или усечен, или после того, как некоторые байты были записаны в файл. <p>
     *
     * Пример использования: по умолчанию метод создает новый файл или перезаписывает существующий файл. <br>
     * Предположим, вы вместо этого хотите добавить байты в существующий файл:
     * <pre>
     * File file = ...
     * byte [] bytes = ...
     * file.write (bytes, StandardOpenOption.APPEND);
     * </pre>
     * @param bytes массив байтов с байтами для записи
     * @param oopts параметры, определяющие способ открытия файла
     * @return файл
     */
    public File write( byte[] bytes, OpenOption ... oopts ){
        try {
            return new File( Files.write(path, bytes, oopts) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readAllLines(cs)">
    /**
     * Прочитайте все строки из файла. <p>
     *
     * Этот метод гарантирует, что файл закрыт, когда все байты были прочитаны
     * или ошибка ввода-вывода или другое исключение среды выполнения.  <p>
     *
     * Байты из файла декодируются в символы с использованием указанной кодировки. <p>
     *
     * Этот метод распознает следующие термины:
     *
     * <ul>
     * <li> u000D, за которым следует u000A, CARRIAGE RETURN, а затем LINE FEED
     * <li> u000A, LINE FEED
     * <li> u000D, CARRIAGE RETURN
     * </ul>
     *
     * Дополнительные терминаторы линий Unicode могут быть распознаны в будущих выпусках. <p>
     *
     * Обратите внимание, что этот метод предназначен для простых случаев,
     * когда удобно читать все строки за одну операцию. <p>
     *
     * Он не предназначен для чтения в больших файлах.
     * @param cs Кодировка
     * @return строки текста
     */
    public List<String> readAllLines(Charset cs){
        try {
            return Files.readAllLines(path, cs==null ? Charset.defaultCharset() : cs);
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readAllLines(cs)">
    /**
     * Прочитайте все строки из файла. <p>
     *
     * Этот метод гарантирует, что файл закрыт, когда все байты были прочитаны
     * или ошибка ввода-вывода или другое исключение среды выполнения.  <p>
     *
     * Байты из файла декодируются в символы с использованием указанной кодировки. <p>
     *
     * Этот метод распознает следующие термины:
     *
     * <ul>
     * <li> u000D, за которым следует u000A, CARRIAGE RETURN, а затем LINE FEED
     * <li> u000A, LINE FEED
     * <li> u000D, CARRIAGE RETURN
     * </ul>
     *
     * Дополнительные терминаторы линий Unicode могут быть распознаны в будущих выпусках. <p>
     *
     * Обратите внимание, что этот метод предназначен для простых случаев,
     * когда удобно читать все строки за одну операцию. <p>
     *
     * Он не предназначен для чтения в больших файлах.
     * @param cs Кодировка
     * @return строки текста
     */
    public List<String> readAllLines(String cs){
        try {
            return Files.readAllLines(path, cs==null ? Charset.defaultCharset() : Charset.forName(cs) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    //</editor-fold>

    /**
     * Читает тексовое содержание файла. <p>
     * Он не предназначен для чтения больших файлов.
     * @param cs кодировка текста
     * @return Содержание
     */
    public String readText(Charset cs){
        if( cs==null )throw new IllegalArgumentException("cs == null");
        StringBuilder sb = new StringBuilder();

        Reader rd = reader(cs);
        try{
            char[] cbuff = new char[1024*8];
            while(true){
                int readed = rd.read(cbuff);
                if( readed<0 )break;
                if( readed>0 ){
                    sb.append(cbuff,0,readed);
                }
            }
            return sb.toString();
        }catch(IOException ex){
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }finally{
            if( rd!=null ){
                try {
                    rd.close();
                } catch (IOException ex) {
                    //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOError(ex);
                }
            }
        }
    }

    /**
     * Читает тексовое содержание файла. <p>
     * Он не предназначен для чтения больших файлов.
     * @param cs кодировка текста
     * @return Содержание
     */
    public String readText(String cs){
        if( cs==null )throw new IllegalArgumentException("cs == null");
        StringBuilder sb = new StringBuilder();

        Reader rd = reader(cs);
        try{
            char[] cbuff = new char[1024*8];
            while(true){
                int readed = rd.read(cbuff);
                if( readed<0 )break;
                if( readed>0 ){
                    sb.append(cbuff,0,readed);
                }
            }
            return sb.toString();
        }catch(IOException ex){
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }finally{
            if( rd!=null ){
                try {
                    rd.close();
                } catch (IOException ex) {
                    //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOError(ex);
                }
            }
        }
    }

    /**
     * Пишет текст в файл с использованием указанной кадировки
     * @param text текст
     * @param cs Кодировка
     */
    public void writeText( String text, Charset cs ){
        if( text==null )throw new IllegalArgumentException("text == null");
        if( cs==null )throw new IllegalArgumentException("cs == null");

        Writer wr = writer(cs);
        try{
            try {
                wr.write(text);
            } catch (IOException ex) {
                //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        }finally{
            try {
                wr.close();
            } catch (IOException ex) {
                //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        }
    }

    /**
     * Пишет текст в файл с использованием указанной кадировки
     * @param text текст
     * @param cs Кодировка
     */
    public void writeText( String text, String cs ){
        if( text==null )throw new IllegalArgumentException("text == null");
        //if( cs==null )throw new IllegalArgumentException("cs == null");

        Writer wr = writer(cs!=null ? Charset.forName(cs) : Charset.defaultCharset());
        try{
            try {
                wr.write(text);
            } catch (IOException ex) {
                //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        }finally{
            try {
                wr.close();
            } catch (IOException ex) {
                //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="write()">
    /**
     * Пишет строки текста в файл. <p>
     *
     * Каждая строка представляет собой последовательность символов и записывается в файл
     * последовательно с каждой строкой, завершаемой разделителем строк платформы,
     * как определено системным свойством line.separator.  <p>
     *
     * Символы кодируются в байты с использованием указанной кодировки. <p>
     *
     * Параметр options указывает, как файл создается или открывается.
     * Если параметров нет, этот метод работает так, как если бы присутствовали опции
     * CREATE, TRUNCATE_EXISTING и WRITE.  <p>
     *
     * Другими словами, он открывает файл для записи, создает файл, если он не существует,
     * или изначально обрезает существующий обычный файл до размера 0.  <p>
     *
     * Метод гарантирует, что файл будет закрыт, когда все строки будут записаны
     * ( или ошибка ввода-вывода или другое исключение среды выполнения).  <p>
     *
     * Если возникает ошибка ввода-вывода, она может сделать это после того,
     * как файл был создан или усечен, или после того, как некоторые байты были записаны в файл.
     * @param lines строки текста
     * @param cs Кодировка
     * @param options Опции
     * @return файл
     */
    public File write( Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options ){
        try {
            return new File( Files.write(path, lines, cs, options) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="write()">
    /**
     * Пишет строки текста в файл. <p>
     *
     * Каждая строка представляет собой последовательность символов и записывается в файл
     * последовательно с каждой строкой, завершаемой разделителем строк платформы,
     * как определено системным свойством line.separator.  <p>
     *
     * Символы кодируются в байты с использованием указанной кодировки. <p>
     *
     * Параметр options указывает, как файл создается или открывается.
     * Если параметров нет, этот метод работает так, как если бы присутствовали опции
     * CREATE, TRUNCATE_EXISTING и WRITE.  <p>
     *
     * Другими словами, он открывает файл для записи, создает файл, если он не существует,
     * или изначально обрезает существующий обычный файл до размера 0.  <p>
     *
     * Метод гарантирует, что файл будет закрыт, когда все строки будут записаны
     * ( или ошибка ввода-вывода или другое исключение среды выполнения).  <p>
     *
     * Если возникает ошибка ввода-вывода, она может сделать это после того,
     * как файл был создан или усечен, или после того, как некоторые байты были записаны в файл.
     * @param lines строки текста
     * @param cs Кодировка
     * @param options Опции
     * @return файл
     */
    public File write( Iterable<? extends CharSequence> lines, String cs, OpenOption... options ){
        try {
            return new File( Files.write(path, lines, cs!=null ? Charset.forName(cs) : Charset.defaultCharset(), options) );
        } catch (IOException ex) {
            //Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    //</editor-fold>

    /**
     * Открытвает буффер для случайного доступа
     * @param options опции
     * @return буфер
     */
    public FChannelBuffer randomAccess(OpenOption... options){
        return FChannelBuffer.open(this, options);
    }

    /**
     * Открытвает буффер для случайного доступа
     * @param options опции
     * @param attrs атрибуты
     * @return буфер
     */
    public FChannelBuffer randomAccess(Set<? extends OpenOption> options, FileAttribute<?>... attrs){
        return FChannelBuffer.open(this, options, attrs);
    }
}
