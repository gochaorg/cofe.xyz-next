package xyz.cofe.io.fs;

import xyz.cofe.collection.ImTreeWalk;
import xyz.cofe.iter.Eterable;
import xyz.cofe.iter.TreeIterator;
import xyz.cofe.iter.TreeStep;

import java.util.Iterator;

/**
 * Алгоритм обхода дерева каталогов
 */
public class FileTreeBuilder implements Eterable<File> {
    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    protected FileTreeBuilder(FileTreeBuilder sample) {
        if( sample==null )throw new IllegalArgumentException("sample == null");
        this.from = sample.from;
        this.dirFollow = sample.dirFollow == null ? null : sample.dirFollow.clone();
    }

    /**
     * Конструктор
     * @param from каталог (или файл) относительно начинается обход
     */
    public FileTreeBuilder(File from){
        if( from==null )throw new IllegalArgumentException("from==null");
        this.from = from;
    }

    private File from;

    /**
     * Указывает каталог с которого начинаеться обход
     * @return начальный каталог
     */
    public File getFrom(){
        return from;
    }

    private volatile DirFollow dirFollow;

    /**
     * Указывает алгоритм обхода
     * @return алгоритм обхода
     */
    public synchronized DirFollow getDirFollow(){
        if( dirFollow!=null ) return dirFollow;
        dirFollow = new DirFollow();
        return dirFollow;
    }

    /**
     * Указывает алгоритм обхода
     * @param follow алгоритм обхода
     * @return Клон с новым алгоритмом
     */
    public FileTreeBuilder dirFollow( DirFollow follow ){
        FileTreeBuilder bld = new FileTreeBuilder(this);
        bld.dirFollow = follow;
        return bld;
    }

    /**
     * Провереть что дочерний элемент является каталогом
     * @return true (по умолчанию) - осуществлять проверку
     */
    public boolean isCheckDir(){ return getDirFollow().isCheckDir(); }

    /**
     * Провереть что дочерний элемент является каталогом
     * @param checkDir true (по умолчанию) - осуществлять проверку
     * @return Клон с новыми настройками
     */
    public FileTreeBuilder checkDir(boolean checkDir) {
        DirFollow df = getDirFollow().clone();
        df.setCheckDir(checkDir);
        return dirFollow(df);
    }

    /**
     * Проверять наличие закольцованности при обходе дочерних узлов
     * @return true (по умолчанию) - проверять наличие закольцованности
     */
    public boolean isCheckCycle(){ return getDirFollow().isCheckCycle(); }

    /**
     * Проверять наличие закольцованности при обходе дочерних узлов
     * @param checkCycle true (по умолчанию) - проверять наличие закольцованности
     * @return Клон с новыми настройками
     */
    public FileTreeBuilder checkCycle(boolean checkCycle) {
        DirFollow df = getDirFollow().clone();
        df.setCheckCycle(checkCycle);
        return dirFollow(df);
    }

    /**
     * Возвращает поведение при возникновении ошибки
     * @return поведение при возникновении ошибки
     */
    public DirFollow.ErrorBehavior getErrorBehavior(){
        return getDirFollow().getErrorBehavior();
    }

    /**
     * Указывает поведение при возникновении ошибки
     * @param errorBehavior поведение при возникновении ошибки
     * @return Клон с новыми настройками
     */
    public FileTreeBuilder errorBehavior(DirFollow.ErrorBehavior errorBehavior) {
        DirFollow df = getDirFollow().clone();
        df.setErrorBehavior(errorBehavior);
        return dirFollow(df);
    }

    /**
     * Возвращает следовать ли символичным ссылкам
     * @return true - следовать ссылкам
     */
    public boolean isFollowLinks(){
        return getDirFollow().isFollowLinks();
    }

    /**
     * Указывает следовать ли символичным ссылкам
     * @param followLinks true - следовать ссылкам
     * @return Клон с новыми настройками
     */
    public FileTreeBuilder followLinks(boolean followLinks) {
        DirFollow df = getDirFollow().clone();
        df.setFollowLinks(followLinks);
        return dirFollow(df);
    }

    public Eterable<TreeStep<File>> tree(){
        return new Eterable<TreeStep<File>>() {
            @Override
            public Iterator<TreeStep<File>> iterator() {
                return new FileTreeIterator(from, getDirFollow());
            }
        };
    }

    /**
     * Возвращает итератор по дереву каталогов
     * @return итератор
     */
    public Eterable<File> go(){
        return tree().map( t -> t!=null ? t.getNode() : null );
    }

    /**
     * Возвращает итератор по дереву каталогов
     * @return итератор
     */
    @Override
    public Iterator<File> iterator() {
        return go().iterator();
    }
}
