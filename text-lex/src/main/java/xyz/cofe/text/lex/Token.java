package xyz.cofe.text.lex;

/**
 * Лексема (Токен)
 * @author gocha
 */
public class Token
{
    /**
     * Конструктор по умолчанию
     */
    public Token(){
    }

    /**
     * Конструктор
     * @param id идентификатор
     * @param source Исходный текст
     * @param begin Смещение в тексте
     * @param len Кол-во символов
     */
    public Token( String id, String source, int begin, int len){
        this.id = id;
        this.source = source;
        this.begin = begin;
        this.length = len;
    }

    /**
     * Конструктор копирования
     * @param src образчик
     */
    protected Token( Token src ){
        if( src!=null ){
            this.id = src.id;
            this.begin = src.begin;
            this.length = src.length;
            this.source = src.source;
        }
    }

    /**
     * Создает клон объекта
     * @return Клон
     */
    @Override
    public Token clone(){
        return new Token(this);
    }

    // <editor-fold defaultstate="collapsed" desc="begin">
    /**
     * Указывает на начало (смещение) лексемы в тексте
     */
    protected int begin;

    /**
     * Указывает на начало (смещение) лексемы в тексте
     * @return начало лексемы
     */
    public int getBegin() {
        return begin;
    }

    /**
     * Указывает на начало (смещение) лексемы в тексте
     * @param begin начало лексемы
     */
    public void setBegin(int begin) {
        this.begin = begin;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="length">
    /**
     * Указывает на длину (в символах) лексемы в тексте
     */
    protected int length;

    /**
     * Указывает на длину (в символах) лексемы в тексте
     * @return Длина лексемы
     */
    public int getLength() {
        return length;
    }

    /**
     * Указывает на длину (в символах) лексемы в тексте
     * @param length Длина лексемы
     */
    public void setLength(int length) {
        this.length = length;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="source">
    /**
     * Указывает на исходный текст
     */
    protected String source;

    /**
     * Указывает на исходный текст
     * @return Исходный текст
     */
    public String getSource() {
        return source;
    }

    /**
     * Указывает на исходный текст
     * @param source Исходный текст
     */
    public void setSource(String source) {
        this.source = source;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getMatchedText()">
    /**
     * Возвращает совпавший текст
     * @return Совпавший текст
     */
    public String getMatchedText() {
        if (source == null)
            throw new IllegalStateException("source==null");
        if (begin < 0)
            throw new IllegalStateException("begin<0");
        if (length < 0)
            throw new IllegalStateException("length<0");
        if (begin + length > source.length())
            throw new IllegalStateException("begin+length>source.length()");
        if (length == 0)
            return "";

        return source.substring(begin, begin + length);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="id">
    /**
     * Указывает на идентификатор лексемы
     */
    protected String id = null;

    /**
     * Указывает на идентификатор лексемы
     * @return Идентификатор лексемы
     */
    public String getId() {
        if( id==null ){
            id = this.getClass().getSimpleName();
        }
        return this.id;
    }

    /**
     * Указывает на идентификатор лексемы
     * @param id Идентификатор лексемы
     */
    public void setId(String id) {
        this.id = id;
    }// </editor-fold>
}
