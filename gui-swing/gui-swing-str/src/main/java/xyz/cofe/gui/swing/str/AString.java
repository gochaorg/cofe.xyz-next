package xyz.cofe.gui.swing.str;

import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Pair;
import xyz.cofe.iter.Eterable;
import xyz.cofe.text.Text;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Строка текста с атрибутами - используется в отображении текста
 * @author Kamnev Georgiy
 */
public class AString extends BaseAString {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(AString.class.getName());

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
        logger.entering(AString.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(AString.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(AString.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param iterators итераторы по символам
     */
    public AString(AttributedCharacterIterator[] iterators) {
        super(iterators);
    }

    /**
     * Конструктор
     * @param text обычный текст
     */
    public AString(String text) {
        super(text);
    }

    /**
     * Конструктор
     * @param text текст
     * @param attributes атрибуты отображения текста
     */
    public AString(String text, Map<? extends AttributedCharacterIterator.Attribute, ?> attributes) {
        super(text, attributes);
    }

    /**
     * Конструктор
     * @param text итератор по тексту
     */
    public AString(AttributedCharacterIterator text) {
        super(text);
    }

    /**
     * Конструктор для создания подстроки
     * @param text исходная строка
     * @param beginIndex начальный индекс
     * @param endIndex конечный индекс исключительно
     */
    public AString(AttributedCharacterIterator text, int beginIndex, int endIndex) {
        super(text, beginIndex, endIndex);
    }

    /**
     * Конструктор для создания подстроки
     * @param text исходная строка
     * @param beginIndex начальный индекс
     * @param endIndex конечный индекс исключительно
     * @param attributes атрибуты отображения текста
     */
    public AString(AttributedCharacterIterator text, int beginIndex, int endIndex, AttributedCharacterIterator.Attribute[] attributes) {
        super(text, beginIndex, endIndex, attributes);
    }

    /**
     * Конструктор копирования
     * @param astr образец
     */
    public AString(AttributedString astr) {
        super(astr);
    }

    @Override
    public AString clone() {
        return new AString(this.getIterator());
    }

    //<editor-fold defaultstate="collapsed" desc="substring(begin,endEx) : BaseAString">
    /**
     * Выделение под строки
     * @param begin индекс начала подстроки
     * @param endEx индекс конца (иск) подстроки
     * @return подстрока
     */
    public AString substring( int begin, int endEx ){
        if( begin<0 )throw new IllegalArgumentException("begin("+begin+")<0");
        if( begin>endEx )throw new IllegalArgumentException("begin("+begin+")>endEx("+endEx+")");
        if( length()<=0 ){
            return new AString("");
        }
        if( begin>length() )begin = length();
        if( endEx>length() )endEx = length();
        return new AString(getIterator(), begin, endEx);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="concat( ... astrings ) : AString">

    /**
     * Склейка строк
     * @param astrings строки
     * @return слееная строка
     */
    public AString concat( BaseAString ... astrings ){
        if (astrings== null) {
            throw new IllegalArgumentException("astrings==null");
        }

        if( astrings.length<=0 )return this;

        Map<BaseAString,Integer> begins = new LinkedHashMap<>();
        Map<BaseAString,Integer> ends = new LinkedHashMap<>();

        StringBuilder sb = new StringBuilder();

        Eterable<BaseAString> aiters =
                Eterable.<BaseAString>single(this).union(Eterable.of(astrings));

        int ptr = 0;
        for( BaseAString sstr : aiters ){
            int len = sstr.length();
            if( len<=0 )continue;

            String str = sstr.text();
            sb.append(str);

            int begin = ptr;
            int end = ptr+str.length();

            begins.put(sstr, begin);
            ends.put(sstr, end);
        }

        AString astr = new AString( sb.toString() );

        for( BaseAString sstr : begins.keySet() ){
            int begin = begins.get(sstr);
            int end = ends.get(sstr);
            int cnt = end - begin;

            if( Math.abs(begin - end)<=0 )continue;

            AttributedCharacterIterator sitr = sstr.getIterator();
            int ssi = -1;
            for( int si=sitr.getBeginIndex(); si<sitr.getEndIndex(); si++ ){
                ssi++;
                sitr.setIndex(si);

                Map<AttributedCharacterIterator.Attribute,Object> m = sitr.getAttributes();
                if( ssi<cnt ){
                    for( Map.Entry<AttributedCharacterIterator.Attribute, Object> me : m.entrySet() ){
                        AttributedCharacterIterator.Attribute a = me.getKey();
                        Object v = me.getValue();
                        astr.addAttribute(a, v, begin+ssi, begin+ssi+1);
                    }
                }
            }
        }

        return astr;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="splitNewLines()">
    /**
     * разделяет текст на набор строк по симвоу перевода на новую строку
     * @return Список из пар начало-конец (иск) для каждой строки
     */
    public List<Pair<Integer,Integer>> newLinesIntervals(){
        ArrayList<Pair<Integer,Integer>> res = new ArrayList<>();
        String srctext = text();

        int ptr = 0;
        while( true ){
            Pair<Integer,String> nextline = Text.nextNewLine(srctext, ptr);
            if( nextline!=null ){
                int begin = ptr;
                int end = nextline.a() - nextline.b().length();
                ptr = nextline.a();
                Pair<Integer,Integer> p = Pair.of( begin, end );
                res.add(p);
            }else{
                if( ptr<srctext.length() ){
                    int begin = ptr;
                    int end = srctext.length();
                    Pair<Integer,Integer> p = Pair.of( begin, end );
                    res.add(p);
                }
                break;
            }
        }

        return res;
    }

    /**
     * Разделяет текст на набор строк по символу(ам) перевода строк
     * @return Набор строк
     */
    public List<AString> splitNewLines(){
        ArrayList<AString> lines = new ArrayList<>();
        for( Pair<Integer,Integer> lineBeginEnd : newLinesIntervals() ){
            AString str = substring(lineBeginEnd.a(), lineBeginEnd.b());
            lines.add( str );
        }
        return lines;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="textLayout">
    /**
     * Создает неизменное графическое изображение данной строки
     * @param tm Определяет способы расчета положения текста
     * @return неизменное графическое изображение стилизованных символьных данных.
     */
    public TextLayout textLayout(TextMeasurer tm){
        if (tm== null) throw new IllegalArgumentException("tm==null");

        TextLayout tl = tm.getLayout(0, length());
        return tl;
    }

    /**
     * Создает неизменное графическое изображение данной строки
     * @param frc контекст отображения текста
     * @return неизменное графическое изображение стилизованных символьных данных.
     */
    public TextLayout textLayout(FontRenderContext frc){
        if (frc== null) throw new IllegalArgumentException("frc==null");
        TextMeasurer tm = new TextMeasurer(getIterator(),frc);
        return textLayout(tm);
    }

    /**
     * Создает неизменное графическое изображение данной строки
     * @param gs контекст отображения текста
     * @return неизменное графическое изображение стилизованных символьных данных.
     */
    public TextLayout textLayout(Graphics2D gs){
        if (gs== null) throw new IllegalArgumentException("gs==null");
        return textLayout(gs.getFontRenderContext());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rstring">

    /**
     * Формирует RichString для отображения текст
     * @param tm контекст отображения
     * @return строка
     */
    public RichString rstring(TextMeasurer tm){
        return new RichString( textLayout(tm) );
    }

    /**
     * Формирует RichString для отображения текст
     * @param frc контекст отображения
     * @return строка
     */
    public RichString rstring(FontRenderContext frc){
        return new RichString( textLayout(frc) );
    }

    /**
     * Формирует RichString для отображения текст
     * @param gs контекст отображения
     * @return строка
     */
    public RichString rstring(Graphics2D gs){
        return new RichString( textLayout(gs) );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Форматирование">
    /**
     * Указывает форматирование строки
     */
    public static class Format {
        protected int begin;
        protected int end;
        protected AString str;

        protected ConcurrentLinkedQueue<Fn1<AString,AString>> formatters;

        /**
         * Конструктор
         * @param str строка
         * @param begin начало строки
         * @param end конец строки исключительно
         */
        public Format(AString str,int begin,int end){
            this.begin = begin;
            this.end = end;
            this.str = str;
            this.formatters = new ConcurrentLinkedQueue<>();
        }

        /**
         * Применяет настройки форматирования и возвращает новую строку
         * @return отформатированная строка
         */
        public AString apply(){
            AString str = this.str.clone();
            while(true){
                Fn1<AString,AString> fmt = formatters.poll();
                if( fmt==null )break;

                AString nstr = fmt.apply(str);
                if( nstr!=null ){
                    str = nstr;
                }
            }

            return str;
        }

        /**
         * Указывает имя шрифта (семейство)
         * @param family имя семейства шрифта
         * @return self ссылка
         */
        public Format family( final String family ){
            if( family==null )throw new IllegalArgumentException("family == null");
            formatters.add(str->{
                str.addAttribute(TextAttribute.FAMILY, family, begin, end);
                return str;
            });
            return this;
        }

        /**
         * Предустановленные шрифты
         */
        public class Family {
            public Format dialogInput(){
                //addAttribute(TextAttribute.FAMILY, Font.DIALOG_INPUT, begin, end);
                //return Format.this;
                return family(Font.DIALOG_INPUT);
            }
            public Format dialog(){
                //addAttribute(TextAttribute.FAMILY, Font.DIALOG, begin, end);
                //return Format.this;
                return family(Font.DIALOG);
            }
            public Format serif(){
                //addAttribute(TextAttribute.FAMILY, Font.SERIF, begin, end);
                //return Format.this;
                return family(Font.SERIF);
            }
            public Format sansSerif(){
                //addAttribute(TextAttribute.FAMILY, Font.SANS_SERIF, begin, end);
                //return Format.this;
                return family(Font.SANS_SERIF);
            }
            public Format monospaced(){
                //addAttribute(TextAttribute.FAMILY, Font.MONOSPACED, begin, end);
                //return Format.this;
                return family(Font.MONOSPACED);
            }
        }

        /**
         * Указывает предустановленные шрифты
         * @return указатель
         */
        public Family family(){ return new Family(); }

        /**
         * Указывает шрифт
         * @param font шрифт
         * @return self ссылка
         */
        public Format font( final Font font ){
            if( font==null )throw new IllegalArgumentException("font == null");
            //addAttribute(TextAttribute.FONT, font, begin, end);
            //return this;
            formatters.add(str->{
                str.addAttribute(TextAttribute.FONT, font, begin, end);
                return str;
            });
            return this;
        }

        /**
         * Указывает цвет текста
         * @param color цвет
         * @return self ссылка
         */
        public Format foreground( final Color color ){
            if( color==null )throw new IllegalArgumentException("color == null");
            //addAttribute(TextAttribute.FOREGROUND, color, begin, end);
            //return this;
            formatters.add(str->{
                str.addAttribute(TextAttribute.FOREGROUND, color, begin, end);
                return str;
            });
            return this;
        }

        /**
         * Указывает цвет фона
         * @param color цвет
         * @return self ссылка
         */
        public Format background( final Color color ){
            if( color==null )throw new IllegalArgumentException("color == null");
            //addAttribute(TextAttribute.BACKGROUND, color, begin, end);
            //return this;
            formatters.add(str->{
                str.addAttribute(TextAttribute.BACKGROUND, color, begin, end);
                return str;
            });
            return this;
        }

        /**
         * Указывает вес шрифта
         */
        public class Weight {
            public Format regular(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_REGULAR, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format bold(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_BOLD, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format light(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_LIGHT, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format medium(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_MEDIUM, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format semiBold(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_SEMIBOLD, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format ultraBold(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_ULTRABOLD, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format heavy(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_HEAVY, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_HEAVY, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format extraLight(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRA_LIGHT, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_EXTRA_LIGHT, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format extraBold(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_EXTRABOLD, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format demiLight(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMILIGHT, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_DEMILIGHT, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format demiBold(){
                //addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WEIGHT,  TextAttribute.WEIGHT_DEMIBOLD, begin, end);
                    return str;
                });
                return Format.this;
            }
        }

        /**
         * Указатель веса шрифта
         * @return указатель
         */
        public Weight weight(){
            return new Weight();
        }

        /**
         * Указатель наклонности шрифта
         */
        public class Posture {
            public Format oblique(){
                //addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, begin, end);
                //return Format.this;

                formatters.add(str->{
                    str.addAttribute(TextAttribute.POSTURE,  TextAttribute.POSTURE_OBLIQUE, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format regular(){
                //addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR, begin, end);
                //return Format.this;

                formatters.add(str->{
                    str.addAttribute(TextAttribute.POSTURE,  TextAttribute.POSTURE_REGULAR, begin, end);
                    return str;
                });
                return Format.this;
            }
        }

        /**
         * Указание наклона шрифта
         * @return  Указатель наклонности шрифта
         */
        public Posture posture(){ return new Posture(); }

        /**
         * Указатель ширины шрифта
         */
        public class Width {
            public Format regular(){
                //addAttribute(TextAttribute.WIDTH, TextAttribute.WIDTH_REGULAR, begin, end);
                //return Format.this;

                formatters.add(str->{
                    str.addAttribute(TextAttribute.WIDTH,  TextAttribute.WIDTH_REGULAR, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format extended(){
                //addAttribute(TextAttribute.WIDTH, TextAttribute.WIDTH_EXTENDED, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.WIDTH,  TextAttribute.WIDTH_EXTENDED, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format condensed(){
                //addAttribute(TextAttribute.WIDTH, TextAttribute.WIDTH_CONDENSED, begin, end);
                //return Format.this;

                formatters.add(str->{
                    str.addAttribute(TextAttribute.WIDTH,  TextAttribute.WIDTH_CONDENSED, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format semiCondensed(){
                //addAttribute(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_CONDENSED, begin, end);
                //return Format.this;

                formatters.add(str->{
                    str.addAttribute(TextAttribute.WIDTH,  TextAttribute.WIDTH_SEMI_CONDENSED, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format semiExtended(){
                //addAttribute(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_EXTENDED, begin, end);
                //return Format.this;

                formatters.add(str->{
                    str.addAttribute(TextAttribute.WIDTH,  TextAttribute.WIDTH_SEMI_EXTENDED, begin, end);
                    return str;
                });
                return Format.this;
            }
        }

        /**
         * Указание ширины шрифта
         * @return указатель
         */
        public Width width(){ return new Width(); }

        /**
         * Указание размера шрифта
         * @param size размер
         * @return self ссылка
         */
        public Format size( final Number size ){
            if( size==null )throw new IllegalArgumentException("size==null");
            //addAttribute(TextAttribute.SIZE, size, begin, end);
            //return this;

            formatters.add(str->{
                str.addAttribute(TextAttribute.SIZE,  size, begin, end);
                return str;
            });
            return Format.this;
        }

        /**
         * Указание трансформаии (поворота/масштабирования/..) текста
         * @param ta трансформация
         * @return self ссылка
         */
        public Format transform( final TransformAttribute ta ){
            if( ta==null )throw new IllegalArgumentException("ta==null");
            //addAttribute(TextAttribute.TRANSFORM, ta, begin, end);
            //return this;

            formatters.add(str->{
                str.addAttribute(TextAttribute.TRANSFORM,  ta, begin, end);
                return str;
            });
            return Format.this;
        }
        /**
         * Указание трансформаии (поворота/масштабирования/..) текста
         * @param at трансформация
         * @return self ссылка
         */
        public Format transform( final AffineTransform at ){
            if( at==null )throw new IllegalArgumentException("at==null");
            //addAttribute(TextAttribute.TRANSFORM, new TransformAttribute(at), begin, end);
            //return this;

            formatters.add(str->{
                str.addAttribute(TextAttribute.TRANSFORM,  at, begin, end);
                return str;
            });
            return Format.this;
        }

        /**
         * Указание верхнего индекса
         * @return self ссылка
         */
        public Format superScript(){
            //addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, begin, end);
            //return this;

            formatters.add(str->{
                str.addAttribute(TextAttribute.SUPERSCRIPT,  TextAttribute.SUPERSCRIPT_SUPER, begin, end);
                return str;
            });
            return Format.this;
        }

        /**
         * Указание нижнего индекса
         * @return self ссылка
         */
        public Format subScript(){
            //addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, begin, end);
            //return this;
            formatters.add(str->{
                str.addAttribute(TextAttribute.SUPERSCRIPT,  TextAttribute.SUPERSCRIPT_SUB, begin, end);
                return str;
            });
            return Format.this;
        }

        /**
         * Указание вида подчеркивания
         */
        public class Underline {
            public Format on(){
                //addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.UNDERLINE,  TextAttribute.UNDERLINE_ON, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format lowDashed(){
                //addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DASHED, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.UNDERLINE,  TextAttribute.UNDERLINE_LOW_DASHED, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format lowDotted(){
                //addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.UNDERLINE,  TextAttribute.UNDERLINE_LOW_DOTTED, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format lowGray(){
                //addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.UNDERLINE,  TextAttribute.UNDERLINE_LOW_GRAY, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format onePixel(){
                //addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.UNDERLINE,  TextAttribute.UNDERLINE_LOW_ONE_PIXEL, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format twoPixel(){
                //addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.UNDERLINE,  TextAttribute.UNDERLINE_LOW_TWO_PIXEL, begin, end);
                    return str;
                });
                return Format.this;
            }
        }

        /**
         * Указание подчеркивания текста
         * @return Указатель
         */
        public Underline underline(){
            return new Underline();
        }

        /**
         * Указание перечеркнутого текста
         * @return self ссылка
         */
        public Format strikethrough(){
            //addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, begin, end);
            //return this;
            formatters.add(str->{
                str.addAttribute(TextAttribute.STRIKETHROUGH,  TextAttribute.STRIKETHROUGH_ON, begin, end);
                return str;
            });
            return Format.this;
        }

        /**
         * Использование керринга
         * @return self ссылка
         */
        public Format kerning(){
            //addAttribute(TextAttribute.KERNING, TextAttribute.KERNING_ON, begin, end);
            //return this;
            formatters.add(str->{
                str.addAttribute(TextAttribute.KERNING,  TextAttribute.KERNING_ON, begin, end);
                return str;
            });
            return Format.this;
        }

        /**
         * Использоание лигатуры
         * @return self использование лигатуры
         */
        public Format ligatures(){
            //addAttribute(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON, begin, end);
            //return this;
            formatters.add(str->{
                str.addAttribute(TextAttribute.LIGATURES,  TextAttribute.LIGATURES_ON, begin, end);
                return str;
            });
            return Format.this;
        }

        /**
         * Указание выравнивания текста
         */
        public class Justification {
            public Format full(){
                //addAttribute(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.JUSTIFICATION,  TextAttribute.JUSTIFICATION_FULL, begin, end);
                    return str;
                });
                return Format.this;
            }

            public Format none(){
                //addAttribute(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_NONE, begin, end);
                //return Format.this;
                formatters.add(str->{
                    str.addAttribute(TextAttribute.JUSTIFICATION,  TextAttribute.JUSTIFICATION_NONE, begin, end);
                    return str;
                });
                return Format.this;
            }
        }

        /**
         * Указание выравнивания текста
         * @return указатель
         */
        public Justification justification(){ return new Justification(); }

        /**
         * Очистка текста от форматирования
         */
        public class Clear {
            public Format all(){
                formatters.add(str->{
                    str.clearAttributes(begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format justification(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.JUSTIFICATION);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format ligatures(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.LIGATURES);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format kerning(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.KERNING);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format strikethrough(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.STRIKETHROUGH);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format underline(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.UNDERLINE);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format sscript(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.SUPERSCRIPT);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format transform(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.TRANSFORM);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format size(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.SIZE);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format width(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.WIDTH);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format posture(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.POSTURE);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format weight(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.WEIGHT);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format background(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.BACKGROUND);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format foreground(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.FOREGROUND);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format family(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.FAMILY);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
            public Format font(){
                formatters.add(str->{
                    LinkedHashSet<AttributedCharacterIterator.Attribute> set
                        = new LinkedHashSet<>();
                    set.add(TextAttribute.FONT);
                    str.removeAttributes(set, begin, end);
                    return str;
                });
                return Format.this;
            }
        }

        /**
         * Очистка текста от форматирования
         * @return очиститель
         */
        public Clear clear(){ return new Clear(); }
    }

    /**
     * Форматирование фрагмента строки
     * @param begin начало строки
     * @param end конец строки исключительно
     * @return форматирование
     */
    public Format format( int begin, int end ){
        return new Format(this, begin, end);
    }

    /**
     * Форматирование строки
     * @return форматирование
     */
    public Format format(){
        int len = length();
        return format(0, len);
    }
    //</editor-fold>
}
