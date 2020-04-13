package xyz.cofe.text.lex;

import java.util.function.Function;

/**
 * Парсер ключевых слов.
 * Ключевые слова анализируются в порядке убывания длины ключевого слова -
 * т.е. сначала самые длинные ключевые слова, затем более короткие
 * @author gocha
 */
public class KeywordsParser implements TokenParser
{
    protected String id = null;

    /**
     * Конструктор
     */
    public KeywordsParser(){
    }

    /**
     * Конструктор
     * @param id Идентификатор токена
     */
    public KeywordsParser(String id){
        this.id = id;
    }

    /**
     * Конструктор
     * @param ignoreCase Игнорировать регистр букв
     * @param keywords список ключевых слов
     */
    public KeywordsParser(boolean ignoreCase, String ... keywords){
        this( keywords, ignoreCase, null );
    }

    /**
     * Конструктор
     * @param keywords список ключевых слов
     * @param ignoreCase Игнорировать регистр букв
     * @param matchedTextConvertor Функц. конвертирования совпавших слов, возможно null
     */
    public KeywordsParser( String[] keywords, boolean ignoreCase, Function<String,String> matchedTextConvertor){
        if (keywords== null) {
            throw new IllegalArgumentException("keywords==null");
        }

        this.matchedTextConvertor = matchedTextConvertor;

        this.keywords = new Keywords(ignoreCase);
        this.keywords.putAll(keywords);
    }

    /**
     * Конструктор
     * @param keywords список ключевых слов
     */
    public KeywordsParser(Keywords keywords){
        if (keywords== null) {
            throw new IllegalArgumentException("keywords==null");
        }
        this.keywords = keywords;
    }

    // <editor-fold defaultstate="collapsed" desc="keywords">
    /**
     * список ключевых слов
     */
    private Keywords keywords = null;

    /**
     * Указывает список ключевых слов
     *
     * @return список ключевых слов
     */
    public Keywords getKeywords() {
        if (keywords == null)
            keywords = new Keywords();
        return keywords;
    }

    /**
     * Указывает список ключевых слов
     *
     * @param keywords список ключевых слов
     */
    public void setKeywords(Keywords keywords) {
        if (keywords == null)
            keywords = new Keywords();
        this.keywords = keywords;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="matchedTextConvertor">
    /**
     * Функц. конвертирования совпавших слов
     */
    protected Function<String, String> matchedTextConvertor = null;

    /**
     * Указывает функц. конвертирования совпавших слов, возможно null
     *
     * @return Функц. конвертирования совпавших слов, возможно null
     */
    public Function<String, String> getMatchedTextConvertor() {
        return matchedTextConvertor;
    }

    /**
     * Указывает функц. конвертирования совпавших слов, возможно null
     *
     * @param matchedTextConvertor Функц. конвертирования совпавших слов, возможно null
     */
    public void setMatchedTextConvertor(Function<String, String> matchedTextConvertor) {
        this.matchedTextConvertor = matchedTextConvertor;
    }
    // </editor-fold>

    @Override
    public Keyword parse(String source, int offset) {
        Keywords kws = getKeywords();

        for( String kwText : kws.getKeywords() ){
            boolean m = LexerUtil.match(
                source,
                offset,
                getKeywords().isIgnoreCase(),
                kwText);

            if( m ){
                Keywords.KeywordDesc desc = kws.get(kwText);
                if( desc!=null ){
                    return desc.create(
                        source,
                        offset,
                        kwText.length(),
                        matchedTextConvertor==null ? kwText : matchedTextConvertor.apply(kwText),
                        id);
                }else{
                    Keyword kw = new Keyword();
                    kw.setSource(source);
                    kw.setBegin(offset);
                    kw.setLength(kwText.length());
                    if( matchedTextConvertor!=null ) kwText = matchedTextConvertor.apply(kwText);
                    kw.setKeyword(kwText);
                    if( id!=null )kw.setId(id);
                    return kw;
                }
            }
        }
        return null;
    }
}
