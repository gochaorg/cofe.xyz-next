package xyz.cofe.text.lex;

import xyz.cofe.collection.ICaseStringMap;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Набор ключевых слов (лексем)
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class Keywords {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Keywords.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Keywords.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(Keywords.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Keywords.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Keywords.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Keywords.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Keywords.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public static class KeywordDesc {
        public String id = null;
        public Class type = null;

        public KeywordDesc() {
        }

        public KeywordDesc(KeywordDesc src) {
            if( src!=null ){
                this.id = src.id;
                this.type = src.type;
            }
        }

        @Override
        public KeywordDesc clone(){
            return new KeywordDesc(this);
        }

        public KeywordDesc(String id, Class type) {
            this.id = id;
            this.type = type;
        }

        public Keyword create(String source,int offset,int len,String keywordText,String id){
            Keyword kw = null;
            if( type!=null ){
                try {
                    Object o = type.newInstance();
                    if( o instanceof Keyword ){
                        kw = (Keyword)o;
                    }
                } catch (InstantiationException ex) {
                    Logger.getLogger(Keywords.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Keywords.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if( kw==null ){
                kw = new Keyword();
            }

            if( this.id!=null )kw.setId(this.id);
            if( id!=null )kw.setId(id);

            kw.setSource(source);
            kw.setBegin(offset);
            kw.setLength(len);

            if( keywordText!=null )kw.setKeyword(keywordText);
            return kw;
        }
    }

    private final Map<String,KeywordDesc> keywords;
    private final boolean ignoreCase;

    public Keywords(){
        keywords = new ICaseStringMap<>(false);
        ignoreCase = false;
    }

    public Keywords(boolean ignoreCase){
        keywords = new ICaseStringMap<Keywords.KeywordDesc>(ignoreCase);
        this.ignoreCase = ignoreCase;
    }

    public Keywords(Keywords src){
        this.ignoreCase = src==null ? false : src.ignoreCase;
        keywords = new ICaseStringMap<Keywords.KeywordDesc>(ignoreCase);

        if( src!=null ){
            for( Map.Entry<String,KeywordDesc> en : src.keywords.entrySet() ){
                String k = en.getKey();
                KeywordDesc desc = en.getValue();
                put( k,desc );
            }
        }
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    @Override
    public Keywords clone(){
        return new Keywords(this);
    }

    public void putAll( String ... kw ){
        putAll( Arrays.asList(kw) );
    }

    public void putAll( Iterable<String> kw ){
        if( kw!=null ){
            for( String k : kw ){
                if( k!=null ){
                    put( k );
                }
            }
        }
    }

    public Keywords put( String keyword, KeywordDesc desc ){
        if( keyword==null )throw new IllegalArgumentException( "keyword==null" );
        if( desc==null )throw new IllegalArgumentException( "desc==null" );
        keywords.put(keyword, desc);
        sortedKeyWords = null;
        return this;
    }

    public Keywords put( String keyword ){
        put( keyword, new KeywordDesc() );
        return this;
    }

    public Keywords put( String keyword, String id ){
        put( keyword, new KeywordDesc( id, null) );
        return this;
    }

    public Keywords put( String keyword, Class type ){
        put( keyword, new KeywordDesc( null, type) );
        return this;
    }

    public Keywords put( String keyword, String id, Class type ){
        put( keyword, new KeywordDesc( id, type) );
        return this;
    }

    public void remove( String keyword ){
        sortedKeyWords = null;
        keywords.remove(keyword);
    }

    public Keywords clear(){
        sortedKeyWords = null;
        keywords.clear();
        return this;
    }

    public KeywordDesc get( String keyword ){
        return keywords.get(keyword);
    }

    private String[] sortedKeyWords = null;
    private String[] getSortedKeyWords(){
        if( sortedKeyWords!=null )return sortedKeyWords;
        String[] kw = keywords.keySet().toArray(new String[]{});
        sortedKeyWords = sortKeywordsArray(kw, ignoreCase);
        return sortedKeyWords;
    }

    public String[] getKeywords(){
        return getSortedKeyWords();
    }

    /**
     * Сортирует слова: самые длинные идут первыми, потом в зависимости в алфавитном порядке
     * @param keywords Ключевые слова
     * @param ignoreCase Игнорировать регистр букв
     * @return Отсортированные слова
     */
    private static String[] sortKeywordsArray(String[] keywords,boolean ignoreCase){
        final boolean ic = ignoreCase;
        final int direction = -1;
        Set<String> kw = new TreeSet<>(( o1, o2 )->{
            if( o1==null && o2==null )return 0;
            if( o1==null && o2!=null )return -1;
            if( o1!=null && o2==null )return 1;
            if( o1.length()>o2.length() ){
                return 1 * direction;
            }else if( o1.length()<o2.length() ){
                return -1 * direction;
            }
            if( ic ){
                return o1.compareToIgnoreCase(o2) * direction;
            }else{
                return o1.compareTo(o2) * direction;
            }
        });
        for( String k : keywords ){
            if( k==null )continue;
            kw.add(k);
        }
        keywords = kw.toArray(new String[]{});
        return keywords;
    }
}
