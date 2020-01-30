/*
 * The MIT License
 *
 * Copyright 2015 Kamnev Georgiy (nt.gocha@gmail.com).
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

package xyz.cofe.text;

import xyz.cofe.fn.Pair;
import xyz.cofe.iter.Eterable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Функции по обработки текста
 */
public class Text {
    // <editor-fold defaultstate="collapsed" desc="byte to hex text">
    /**
     * Возвращает символное-hex представление байтов
     * @param bytes Байты
     * @return Строка - символное-hex представление байтов
     */
    public static String getHex(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes==null");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(getHex(bytes[i]));
        }
        return sb.toString();
    }

    /**
     * Возвращает символное-hex представление байтов
     * @param bytes Байты
     * @return Строка - символное-hex представление байтов
     */
    public static String getHex(Byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes==null");
        }
        StringBuilder sb = new StringBuilder();
        for (Byte byte1 : bytes) {
            sb.append(getHex(byte1));
        }
        return sb.toString();
    }

    /**
     * Возвращает двух символьное представление байта
     *
     * @param byteValue байт
     * @return два символа представляющих байт (00 .. FF)
     */
    public static String getHex(byte byteValue) {
        return Integer.toString((byteValue & 0xff) + 0x100, 16).substring(1).toUpperCase();
    }

    /**
     * Возвращает символное-hex представление байтов
     * @param bytes байты
     * @param off смещение
     * @param len кол-во байт
     * @return Строка - символное-hex представление байтов
     */
    public static String encodeHex(byte[] bytes, int off, int len){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(getHex(bytes[i+off]));
        }
        return sb.toString();
    }

    /**
     * Возвращает символное-hex представление байтов
     * @param bytes байты
     * @param off смещение
     * @param len кол-во байт
     * @return Строка - символное-hex представление байтов
     */
    public static String encodeHex(Byte[] bytes, int off, int len){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(getHex(bytes[i+off]));
        }
        return sb.toString();
    }

    /**
     * Возвращает символное-hex представление байтов
     * @param bytes байты
     * @return Строка - символное-hex представление байтов
     */
    public static String encodeHex(byte[] bytes){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        return getHex(bytes);
    }

    /**
     * Возвращает символное-hex представление байтов
     * @param bytes байты
     * @return Строка - символное-hex представление байтов
     */
    public static String encodeHex(Byte[] bytes){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        return getHex(bytes);
    }

    /**
     * Возвращает баты их hex представления
     * @param bytes - символное-hex представление байтов
     * @return bytes байты
     */
    public static byte[] decodeHex(String bytes){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        int len = bytes.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(bytes.charAt(i), 16) << 4)
                + Character.digit(bytes.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Возвращает баты их hex представления
     * @param bytes - символное-hex представление байтов
     * @param offset - смещение байтов в строке
     * @param len - кол-во символов соответ байтам
     * @return bytes байты
     */
    public static byte[] decodeHex(String bytes,int offset,int len){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        //int len = bytes.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(bytes.charAt(i+offset), 16) << 4)
                + Character.digit(bytes.charAt(i+1+offset), 16));
        }
        return data;
    }

    /**
     * Возвращает баты их hex представления
     * @param bytes - символное-hex представление байтов
     * @return bytes байты
     */
    public static Byte[] decodeHexBytes(String bytes){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        int len = bytes.length();
        Byte[] data = new Byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(bytes.charAt(i), 16) << 4)
                + Character.digit(bytes.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Возвращает баты их hex представления
     * @param bytes - символное-hex представление байтов
     * @param offset - смещение байтов в строке
     * @param len - кол-во символов соответ байтам
     * @return bytes байты
     */
    public static Byte[] decodeHexBytes(String bytes,int offset,int len){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        //int len = bytes.length();
        Byte[] data = new Byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(bytes.charAt(i+offset), 16) << 4)
                + Character.digit(bytes.charAt(i+1+offset), 16));
        }
        return data;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="html encode/decode">
    protected static Map<String, String> text2HtmlCharMap = null;

    /**
     * Карта преобразования текста в html валидный текст
     *
     * @return Карта text / html
     */
    public static Map<String, String> getText2HtmlCharMap() {
        // http://ru.wikipedia.org/wiki/%D0%92%D0%B8%D0%BA%D0%B8%D0%BF%D0%B5%D0%B4%D0%B8%D1%8F:%D0%A1%D0%BF%D0%B5%D1%86%D0%B8%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5_%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B
        if (text2HtmlCharMap != null)
            return text2HtmlCharMap;
        text2HtmlCharMap = new HashMap<>();
        text2HtmlCharMap.put("&", "&amp;");
        text2HtmlCharMap.put("<", "&lt;");
        text2HtmlCharMap.put(">", "&gt;");
        text2HtmlCharMap.put("\"", "&quot;");
        text2HtmlCharMap.put("'", "&apos;");

        // Неразрывный пробел - " "
        text2HtmlCharMap.put("\u00a0", "&nbsp;");

        // copyright - "©"
        text2HtmlCharMap.put("\u00a9", "&copy;");

        // registered trade mark - "®"
        text2HtmlCharMap.put("\u00ae", "&reg;");

        // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK - "«"
        text2HtmlCharMap.put("\u00ab", "&laquo;");

        // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK - "»"
        text2HtmlCharMap.put("\u00bb", "&raquo;");

        // SINGLE LEFT-POINTING ANGLE QUOTATION MARK - "‹"
        text2HtmlCharMap.put("\u2039", "&lsaquo;");

        // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK - "›"
        text2HtmlCharMap.put("\u203a", "&rsaquo;");

        // DOUBLE LOW-9 QUOTATION MARK - "„" (открывающая)
        text2HtmlCharMap.put("\u201e", "&bdquo;");

        // LEFT DOUBLE QUOTATION MARK - "“" (закрывающая , открывающая)
        text2HtmlCharMap.put("\u201c", "&ldquo;");

        // RIGHT DOUBLE QUOTATION MARK - "”" (закрывающая)
        text2HtmlCharMap.put("\u201d", "&rdquo;");

        // DOUBLE HIGH-REVERSED-9 QUOTATION MARK - "‟" (открывающая)
        text2HtmlCharMap.put("\u201F", "&#8223;");

        // RIGHT SINGLE QUOTATION MARK - "’" (закрывающая)
        text2HtmlCharMap.put("\u2019", "&rsquo;");

        // SINGLE LOW-9 QUOTATION MARK - "‚" (открывающая)
        text2HtmlCharMap.put("\u201a", "&sbquo;");

        // SINGLE HIGH-REVERSED-9 QUOTATION MARK - "‛" (открывающая)
        text2HtmlCharMap.put("\u201B", "&#8219;");

        // LEFT SINGLE QUOTATION MARK - "‘" (открывающая,закрывающая)
        text2HtmlCharMap.put("\u2018", "&lsquo;");

        text2HtmlCharMap.put("\u00bf", "&iquest;"); // "¿"
        text2HtmlCharMap.put("\u00a1", "&iexcl;"); // "¡"
        text2HtmlCharMap.put("\u00a7", "&sect;"); // "§"
        text2HtmlCharMap.put("\u00b6", "&para;"); // "¶"
        text2HtmlCharMap.put("\u2022", "&bull;"); // "•"
        text2HtmlCharMap.put("\u2014", "&mdash;"); // "—"
        text2HtmlCharMap.put("\u2026", "&hellip;"); // "…"

        text2HtmlCharMap.put("\u221a", "&radic;"); // "√"
        text2HtmlCharMap.put("\u222b", "&int;"); // "∫"
        text2HtmlCharMap.put("\u2202", "&part;"); // "∂"
        text2HtmlCharMap.put("\u2211", "&sum;"); // "∑"
        text2HtmlCharMap.put("\u220f", "&prod;"); // "∏"

        return text2HtmlCharMap;
    }

    /**
     * Декодирует текст из html кодировки
     *
     * @param html HTML кодировка (допускется null)
     * @return Декодированный текст (допускется null) Декодирует символы: <ol> <li><b>&amp;lt;</b> в &lt;</li>
     * <li><b>&amp;gt;</b> в &gt;</li> <li><b>&amp;amp;</b> в &amp;</li> <li><b>&amp;quot;</b> в &quot;</li>
     * <li><b>&amp;apos;</b> в '</li> <li><b>&amp;nbsp;</b> в <i>пробел</i></li> </ol>
     */
    public static String htmlDecode(String html) {
        if (html == null) {
//            throw new IllegalArgumentException("html == null");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int idx = -1;
        while (true) {
            idx++;
            if (idx >= html.length())
                break;

            String current = html.substring(idx);
            if (current.startsWith("&amp;")) {
                sb.append("&");
                idx += 4;
            } else if (current.startsWith("&nbsp;")) {
                sb.append(" ");
                idx += 5;
            } else if (current.startsWith("&lt;")) {
                sb.append("<");
                idx += 3;
            } else if (current.startsWith("&quot;")) {
                sb.append("\"");
                idx += 5;
            } else if (current.startsWith("&gt;")) {
                sb.append(">");
                idx += 3;
            } else if (current.startsWith("&apos;")) {
                sb.append("'");
                idx += 5;
            } else {
                sb.append(html.charAt(idx));
            }
        }
        return sb.toString();
    }

    /**
     * Кодирует текст в html экранированный текст. Экранирует символы: <ol> <li><b>&lt;</b> в &amp;lt;</li>
     * <li><b>&gt;</b> в &amp;gt;</li> <li><b>&amp;</b> в &amp;amp;</li> </ol>
     *
     * @param text Исходный текст (допускется null)
     * @return html текст (допускется null)
     */
    public static String htmlEncode(String text) {
        if (text == null)
            return null;

        StringBuilder u = new StringBuilder(text.trim());
        for (int i = 0; i < u.length(); ++i) {
            char c = u.charAt(i);
            if (c == '<') {
                u.replace(i, i + 1, "&lt;");
                i += 3;
            } else if (c == '>') {
                u.replace(i, i + 1, "&gt;");
                i += 3;
            } else if (c == '&') {
                u.replace(i, i + 1, "&amp;");
                i += 4;
            }
        }
        return u.toString();
    }

    /**
     * Кодирует текст в html экранированный текст.<br> Экранирует символы: <ol> <li><b>&lt;</b> в &amp;lt;</li>
     * <li><b>&gt;</b> в &amp;gt;</li> <li><b>&amp;</b> в &amp;amp;</li> <li><b>"</b> в &amp;quot;</li> <li><b>'</b> в
     * &amp;apos;</li> </ol>
     *
     * @param text Исходный текст (допускется null)
     * @return html текст (допускется null)
     */
    public static String attrEncode(String text) {
        if (text == null)
            return null;

        StringBuilder u = new StringBuilder(text);
        for (int i = 0; i < u.length(); ++i) {
            char c = u.charAt(i);
            switch (c) {
                case '<':
                    u.replace(i, i + 1, "&lt;");
                    i += 3;
                    break;
                case '>':
                    u.replace(i, i + 1, "&gt;");
                    i += 3;
                    break;
                case '&':
                    u.replace(i, i + 1, "&amp;");
                    i += 4;
                    break;
                case '"':
                    u.replace(i, i + 1, "&quot;");
                    i += 5;
                    break;
                case '\'':
                    u.replace(i, i + 1, "&apos;");
                    i += 5;
                    break;
                default:
                    break;
            }
        }
        return u.toString();
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="attrDecode()">
    /**
     * Декодирование атрибута HTML
     * @param attr Атрибут HTML кодированный
     * @return Декодированный HTML атрибут
     */
    public static String attrDecode(String attr){
        if( attr==null )return null;
        StringBuilder res = new StringBuilder();
        int p = 0;
        while( true ){
            if( p>=attr.length() )break;
            String txt4 = lookupText(attr, p, 4);
            if( txt4.equalsIgnoreCase("&lt;") ){
                res.append("<");
                p += 4;
                continue;
            }
            if( txt4.equalsIgnoreCase("&gt;") ){
                res.append(">");
                p += 4;
                continue;
            }
            String txt5 = lookupText(attr, p, 5);
            if( txt5.equalsIgnoreCase("&amp;") ){
                res.append("&");
                p += 5;
                continue;
            }
            String txt6 = lookupText(attr, p, 5);
            if( txt6.equalsIgnoreCase("&quot;") ){
                res.append("\"");
                p += 6;
                continue;
            }
            if( txt6.equalsIgnoreCase("&apos;") ){
                res.append("\'");
                p += 6;
                continue;
            }
            String txt1 = lookupText(attr, p, 1);
            if( txt1.length()>=1 ){
                res.append(txt1);
                p += 1;
            }else{
                break;
            }
        }
        return res.toString();
    }
//</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="urlDecode()">
    /**
     * Декодирует текст методом URL-encoding, используя кодировку UTF-8
     *
     * @param text Кодированый текст
     * @return исходный текст
     */
    public static String urlDecode(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text==null");
        }
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex);
        }
    }

    /**
     * Декодирует текст методом URL-encoding
     *
     * @param text Кодированый текст
     * @param charset Кодировка
     * @return исходный текст
     */
    public static String urlDecode(String text, String charset) {
        try {
            return URLDecoder.decode(text, charset);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="urlEncode()">
    /**
     * Кодирует текст методом URL-encoding, используя кодировку UTF-8
     *
     * @param text Текст
     * @return Кодированый текст
     */
    public static String urlEncode(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text==null");
        }
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex);
        }
    }

    /**
     * Кодирует текст методом URL-encoding
     *
     * @param text Текст
     * @param charset кодировка
     * @return Кодированый текст
     */
    public static String urlEncode(String text, String charset) {
        try {
            return URLEncoder.encode(text, charset);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex);
        }
    }
    // </editor-fold>

    /**
     * Кодирует карту в форме QueryString
     * @param map Карта
     * @param allowNullKey true - допускается использование null ключей
     * @param allowNullValue true - допускается использование null значений
     * @return QueryString
     */
    public static String queryStringEncodeMap(
        Map<String,String> map,
        boolean allowNullKey,
        boolean allowNullValue
    ){
        if( map==null )throw new IllegalArgumentException( "map==null" );
        StringBuilder sb = new StringBuilder();
        for( Map.Entry<String,String> en : map.entrySet() ){
            String k = en.getKey();
            String v = en.getValue();

            if( k==null && v==null )continue;

            if( k==null ){
                if( allowNullKey ){
                    if( sb.length()>0 )sb.append("&");
                    sb.append("=");
                    sb.append(urlEncode(v));
                    continue;
                }else{
                    continue;
                }
            }

            if( v==null ){
                if( allowNullValue ){
                    if( sb.length()>0 )sb.append("&");
                    sb.append(urlEncode(k));
                    sb.append("=");
                    continue;
                }else{
                    continue;
                }
            }

            if( sb.length()>0 )sb.append("&");
            sb.append(urlEncode(k));
            sb.append("=");
            sb.append(urlEncode(v));
        }
        return sb.toString();
    }

    /**
     * Декодирует QueryString
     * @param queryString строка запроса
     * @return карта
     */
    public static Map<String,String> queryStringDecodeMap(
        String queryString
    ){
        if( queryString==null )throw new IllegalArgumentException( "queryString==null" );
        Map<String,String> res = new LinkedHashMap();
        for( String kval : queryStringSplit(queryString, null, true, null, true) ){
            String[] kv = queryStringKeyValueSplit(kval, true);
            if( kv!=null && kv.length==2 ){
                res.put(kv[0], kv[1]);
            }
        }
        return res;
    }

    /**
     * Декодирует QueryString
     * @param queryString строка запроса
     * @return карта
     */
    public static Map<String,List<String>> queryStringDecodeMultiMap(
        String queryString
    ){
        if( queryString==null )throw new IllegalArgumentException( "queryString==null" );
        Map<String,List<String>> res = new LinkedHashMap();
        for( String kval : queryStringSplit(queryString, null, true, null, true) ){
            String[] kv = queryStringKeyValueSplit(kval, true);
            if( kv!=null && kv.length==2 ){
                List<String> lvals = res.get(kv[0]);
                if( lvals==null ){
                    lvals = new ArrayList<>();
                    res.put(kv[0], lvals);
                }
                lvals.add(kv[1]);
            }
        }
        return res;
    }

    /**
     * Разделяет кодированную пару ключ/значение (QueryString) на декодированные пары
     * @param keyValuePair строка ключ/значение, разделенные знаком Равно
     * @param allowNullValue true - допускается отсуствие значения или/и символа Равно
     * @return Декодированная пара или null
     */
    public static String[] queryStringKeyValueSplit( String keyValuePair, boolean allowNullValue ){
        if( keyValuePair==null )throw new IllegalArgumentException( "keyValuePair==null" );
        if( !keyValuePair.contains("=") ){
            if( allowNullValue ){
                return new String[]{urlEncode(keyValuePair),""};
            }else{
                return null;
            }
        }

        String[] kvArr = keyValuePair.split("=", 2);

        if( kvArr.length<2 ){
            if( allowNullValue ){
                return new String[]{urlDecode(keyValuePair),""};
            }else{
                return null;
            }
        }

        if( kvArr.length>2 ){
            return new String[]{ urlDecode(kvArr[0]), urlDecode(kvArr[1]), };
        }

        return new String[]{ urlDecode(kvArr[0]), urlDecode(kvArr[1]) };
    }

    /**
     * Разделяет query string по символу амперсанда на наборы ключ/значени
     * @param queryString query строка
     * @param specChars перечень специальных символов, таких как амперсанд - &amp;amp; и т.д.
     * Если указано null - то испобльзуется значение getText2HtmlCharMap()
     * @param specCharsIgnoreCase - true - игнорировать регистр букв в специальных символах
     * @param specCharConv функция конвертации специальных симвлов в нормальные,
     * если указанно null, то используется значение из getText2HtmlCharMap()
     * @param checkComment если true - то проверяет наличие символа решетки,
     * и если сам символ есть, и все что за ним следует - не попадет в резуьтат
     * @return Список кодированых пар значений  (ключ=значение)
     * @see #getText2HtmlCharMap()
     */
    public static List<String> queryStringSplit(
        String queryString,
        Iterable<String> specChars,
        boolean specCharsIgnoreCase,
        Function<String,String> specCharConv,
        boolean checkComment
    ){
        if( queryString==null )throw new IllegalArgumentException( "queryString==null" );

        specChars =
            specChars == null
                ? getText2HtmlCharMap().values()
                : specChars;

        if( specCharConv==null ){
            Map<String,String> spec2txt = new LinkedHashMap<>();
            for( Map.Entry<String,String> e : getText2HtmlCharMap().entrySet() ){
                spec2txt.put(e.getValue(), e.getKey());
            }
            specCharConv = Convertors.map(spec2txt,"","");
        }

        StringBuilder buff = new StringBuilder();
        List<String> keyValuePairs = new ArrayList<>();

        int i = -1;
        while( true ){
            i++;
            if( i>=queryString.length() )break;

            char c = queryString.charAt(i);

            if( c=='#' && checkComment ){
                break;
            }

            if( c=='&' ){
                boolean speccMatched = false;
                for( String specc : specChars ){
                    if( specc==null )continue;
                    if( specc.length()==0 )continue;

                    String l = lookupText(queryString, i, specc.length());

                    if( specCharsIgnoreCase ){
                        if( specc.equalsIgnoreCase(l) ){
                            i += specc.length()-1;

                            if( specCharConv!=null ){
                                String v = specCharConv.apply(specc);
                                if( v!=null ){
                                    buff.append(v);
                                }
                            }else{
                                buff.append(specc);
                            }

                            speccMatched = true;
                            break;
                        }
                    }else{
                        if( specc.equals(l) ){
                            i += specc.length()-1;

                            if( specCharConv!=null ){
                                String v = specCharConv.apply(specc);
                                if( v!=null ){
                                    buff.append(v);
                                }
                            }else{
                                buff.append(specc);
                            }

                            speccMatched = true;
                            break;
                        }
                    }
                }
                if( speccMatched )continue;

                keyValuePairs.add(buff.toString());
                buff.setLength(0);
            }else{
                buff.append(c);
            }
        }

        if( buff.length()>0 ){
            keyValuePairs.add(buff.toString());
        }

        return keyValuePairs;
    }

    //<editor-fold defaultstate="collapsed" desc="lookupText()">
    /**
     * Возвращает строку из указанной позиции заданной или меньше длинны
     * @param source исходный текст
     * @param beginIndex позиция, больше или равно 0, может быть больше длины строки
     * @param len длина искомого текста (может быть за пределами границ текста), больше или равен 0
     * @return текст, если за границами вернет пустую строку
     */
    public static String lookupText(String source,int beginIndex,int len){
        if( source==null )throw new IllegalArgumentException( "source==null" );
        if( len<0 )throw new IllegalArgumentException( "len<0" );
        if( beginIndex<0 )throw new IllegalArgumentException( "beginIndex<0" );

        if( beginIndex>=source.length() )return "";

        int endIdx = beginIndex + len;
        if( beginIndex>=endIdx )return "";

        if( endIdx > source.length() )endIdx = source.length();

        return source.substring(beginIndex, endIdx);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="matchText()">
    /**
     * Сравнивает часть текста с образцом
     * @param source текст
     * @param needly образец
     * @param beginIndex индекс (может быть за пределами строки, но не меньше 0)
     * @param ignoreCase игнорировать регистр
     * @return совпадает / не совпадает
     */
    public static boolean matchText(String source,String needly,int beginIndex,boolean ignoreCase ){
        if( source==null )throw new IllegalArgumentException( "source==null" );
        if( needly==null )throw new IllegalArgumentException( "needly==null" );
        String partOfSrc = lookupText(source, beginIndex, needly.length());
        return ignoreCase ? partOfSrc.equalsIgnoreCase(needly) : partOfSrc.equals(needly);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nextNewLine()">
    /**
     * Ищет начало новой строки, учитывает различ. варианты перевода строк
     * @param text Текст
     * @param beginIndex с какого индекса начать поиск
     * @return Пара (начало строки, предшеств. сиволы перевода строк) или null
     */
    public static Pair<Integer,String> nextNewLine( String text, int beginIndex){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        for( int i=beginIndex; i<text.length(); i++ ){
            char c0 = text.charAt(i);
            char c1 = (i+1 < text.length()) ? text.charAt(i+1) : '\000';
            if( c0=='\n' && c1=='\r' ){
                return Pair.of(i+2,"\n\r");
            }else if( c0=='\r' && c1=='\n' ){
                return Pair.of(i+2,"\r\n");
            }else if( c0=='\n' && c1!='\r' ){
                return Pair.of(i+1,"\n");
//                return i+1;
            }else if( c0=='\r' && c1!='\n' ){
                return Pair.of(i+1,"\r");
//                return i+1;
            }
        }
        return null;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dropFirstEmptyLines()">
    /**
     * Убирает из набора строк первые пустые (null или их длина меньше 1) строки
     * @param lines Набор строк
     * @return Набор строк
     */
    //@SuppressWarnings("UnnecessaryContinue")
    public static String[] dropFirstEmptyLines(String[] lines){
        if (lines== null) {
            throw new IllegalArgumentException("lines==null");
        }
        ArrayList<String> nlines = new ArrayList<>();
        boolean drop = true;
        for( String line : lines ){
            if( drop ){
                if( line==null || line.trim().length()<1 ){
                    continue;
                }else{
                    drop = false;
                    nlines.add(line);
                }
            }else{
                nlines.add( line );
            }
        }
        return nlines.toArray(new String[]{});
    }

    /**
     * Убирает из набора строк первые пустые (null или их длина меньше 1) строки
     * @param lines Набор строк
     * @return Набор строк
     */
    @SuppressWarnings("UnnecessaryContinue")
    public static List<String> dropFirstEmptyLines(Iterable<String> lines){
        if (lines== null) {
            throw new IllegalArgumentException("lines==null");
        }
        ArrayList<String> nlines = new ArrayList<>();
        boolean drop = true;
        for( String line : lines ){
            if( drop ){
                if( line==null || line.trim().length()<1 ){
                    continue;
                }else{
                    drop = false;
                    nlines.add(line);
                }
            }else{
                nlines.add( line );
            }
        }
        return nlines;
//        return nlines.toArray(new String[]{});
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dropLastEmptyLines()">
    /**
     * Убирает из набора строк последние пустые (null или их длина меньше 1) строки
     * @param lines Набор строк
     * @return Набор строк
     */
    @SuppressWarnings("UnnecessaryContinue")
    public static String[] dropLastEmptyLines(String[] lines){
        ArrayList<String> nlines = new ArrayList<>();
        boolean drop = true;
        for( int i=lines.length-1; i>=0; i-- ){
            String line = lines[i];
            if( drop ){
                if( line==null || line.trim().length()<1 ){
                    continue;
                }else{
                    drop = false;
                    nlines.add(line);
                }
            }else{
                nlines.add( line );
            }
        }
        Collections.reverse(nlines);
        return nlines.toArray(new String[]{});
    }

    /**
     * Убирает из набора строк последние пустые (null или их длина меньше 1) строки
     * @param lines Набор строк
     * @return Набор строк
     */
    @SuppressWarnings("UnnecessaryContinue")
    public static List<String> dropLastEmptyLines(Iterable<String> lines){
        ArrayList<String> nlines = new ArrayList<>();
        ArrayList<String> slines = new ArrayList<>();
        if(lines!=null){
            for( String l : lines ){
                slines.add(0, l);
            }
        }
        boolean drop = true;
        for( String line : slines ){
            if( drop ){
                if( line==null || line.trim().length()<1 ){
                    continue;
                }else{
                    drop = false;
                    nlines.add(line);
                }
            }else{
                nlines.add( line );
            }
//        }
        }
        Collections.reverse(nlines);
        return nlines;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="convert()">
    /**
     * Обрабатывает набор строк согласно ковертору,
     * т.е. каждая строка пропускается через конвертор и его результат попадает результирующий набор
     * @param source Исходный набор строк
     * @param convertor Конвертор строки
     * @return Результат конвертации
     */
    public static Eterable<String> convert( Iterable<String> source, Function<String,String> convertor){
        if( source==null )throw new IllegalArgumentException( "source==null" );
        if( convertor==null )return Eterable.of(source);
        return Eterable.of(source).map(convertor);
    }

    /**
     * Обрабатывает набор строк согласно ковертору,
     * т.е. каждая строка пропускается через конвертор и его результат попадает результирующий набор
     * @param source Исходный набор строк
     * @param convertor Конвертор строки
     * @return Результат конвертации
     */
    public static String[] convert(String[] source,Function<String,String> convertor){
        if( source==null ) throw new IllegalArgumentException( "source==null" );
        if( convertor==null ) throw new IllegalArgumentException( "convertor==null" );
        String[] res = new String[source.length];
        for( int i=0; i<source.length; i++ ){
            res[i] = convertor.apply(source[i]);
        }
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="indexOfNonWSChar()">
    /**
     * Возвращает первый индекс не пробельного символа
     * @param text Текст
     * @param begin Индекс с которого начинается поиск
     * @param endexclusive Индекс исключ. по которй ведется поиск
     * @return индекс или -1
     */
    public static int indexOfNonWSChar( String text,int begin,int endexclusive ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        for( int i=begin; i<endexclusive; i++ ){
            char c = text.charAt(i);
            if( !Character.isWhitespace(c) ){
                return i;
            }
        }
        return -1;
    }

    /**
     * Возвращает первый индекс не пробельного символа
     * @param text Текст
     * @return индекс или -1
     */
    public static int indexOfNonWSChar( String text ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        return indexOfNonWSChar(text, 0, text.length());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="trimStart()">
    /**
     * Удаляет пробельные символы с начала строки
     * @param text Строка
     * @return Строка без пробелов в начале
     */
    public static String trimStart( String text ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        StringBuilder res = new StringBuilder();
        int state = 0;
        for( int i=0; i<text.length(); i++ ){
            char c = text.charAt(i);
            switch( state ){
                case 0:
                    if( !Character.isWhitespace(c) ){
                        res.append( c );
                        state = 1;
                    }
                    break;
                case 1:
                    res.append( c );
                    break;
            }
        }
        return res.toString();
    }

    /**
     * Удаляет пробельные символы с начала строки
     * @param text Строка
     * @param max Максимальное кол-во удаляемых пробельных символов или -1
     * @return Строка без пробелов в начале
     */
    public static String trimStart( String text, int max ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        StringBuilder res = new StringBuilder();
        int state = max <= 0 ? 1 : 0;
        int co = 0;
        for( int i=0; i<text.length(); i++ ){
            char c = text.charAt(i);
            switch( state ){
                case 0:
                    if( !Character.isWhitespace(c) ){
                        res.append( c );
                        state = 1;
                    }
                    co++;
                    if( co>=max && max>0 )state = 1;
                    break;
                case 1:
                    res.append( c );
                    break;
            }
        }
        return res.toString();
    }

    /**
     * Возвращает строку исключая указанный текст (если он присуствует 1 или более раз) в начале. <p>
     * <code>
     * trimStart( "ababtcv" , "ab" ) // Результат <i>"tcv"</i>
     * </code> <p>
     *
     * @param text Исходная строка
     * @param trimText Текст который необходимо удалить
     * @return Результат
     */
    public static String trimStart(String text, String trimText) {
        if (text == null) {
            throw new IllegalArgumentException("text == null");
        }
        if (trimText == null) {
            throw new IllegalArgumentException("trimText == null");
        }
        if (trimText.length() < 1 || text.length() < 1)
            return text;

        String result = text;

        while (true) {
            if (result.length() <= 0)
                break;
            if (result.startsWith(trimText)) {
                int cutlength = trimText.length();
                if (cutlength > result.length())
                    break;
                if (cutlength == result.length()) {
                    result = "";
                    break;
                }
                result = result.substring(cutlength);
            } else {
                break;
            }
        }

        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="trimEnd()">
    /**
     * Возвращает строку исключая указанный текст (если он присуствует 1 или более раз) в конце. <p>
     * <code>
     * trimEnd( "ababtcvtcv" , "tcv" ) // Результат <i>"abab"</i>
     * </code> <p>
     *
     * @param text Исходная строка
     * @param trimText Текст который необходимо удалить
     * @return Результат
     */
    public static String trimEnd(String text, String trimText) {
        if (text == null) {
            throw new IllegalArgumentException("text == null");
        }
        if (trimText == null) {
            throw new IllegalArgumentException("trimText == null");
        }
        if (trimText.length() < 1 || text.length() < 1)
            return text;

        String result = text;

        while (true) {
            if (result.length() <= 0)
                break;
            if (result.endsWith(trimText)) {
                int endindex = result.length() - trimText.length();
                if (endindex < 0)
                    break;

                result = result.substring(0, endindex);
            } else {
                break;
            }
        }

        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="repeat()">
    /**
     * Повторяет текст указанное кол-во раз.
     *
     * @param text Текст
     * @param count Сколько раз повторить ( &lt;= 0 - пустая строка; == 1 - исходная; &gt;= 2 - соответ. кол-во
     * повторов)
     * @return Результат
     */
    public static String repeat(String text, int count) {
        if (count < 1)
            return "";
        if (count == 1)
            return text;
        String res = "";
        for (int i = 0; i < count; i++)
            res += text;
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="split()">
    /**
     * Разделяет строку на подстроки, где в качестве разделитя указана строка. <table border="1" summary="Пример преобразований"> <tr
     * style="font-weight:bold"> <td>Исходная строка</td><td>Разделитель</td><td>Результат</td> </tr> <tr>
     * <td>"delim"</td><td>"delim"</td><td>""</td> </tr> <tr> <td>"delimabc"</td><td>"delim"</td><td>""<br>"abc"</td>
     * </tr> <tr> <td>"abcdelimabc"</td><td>"delim"</td><td>"abc"<br>"abc"</td> </tr> <tr>
     * <td>"abcdelim"</td><td>"delim"</td><td>"abc"<br>""</td> </tr> <tr> <td>"abc"</td><td>"delim"</td><td>"abc"</td>
     * </tr> <tr> <td>"abc"</td><td>""</td><td>"abc"</td> </tr> </table>
     *
     * @param src Исходня строка
     * @param splitter Разделитель
     * @return Подстроки
     */
    public static String[] split(String src, String splitter) {
        if (src == null) {
            throw new IllegalArgumentException("src == null");
        }
        if (splitter == null) {
            throw new IllegalArgumentException("splitter == null");
        }

        ArrayList<String> result = new ArrayList<String>();

        if (splitter.equals(src)) {
            result.add("");
        } else if (splitter.length() > src.length()) {
            result.add(src);
        } else {
            int offset = 0;
            while (true) {
                if (offset > src.length())
                    break;
                if (offset == src.length()) {
                    result.add("");
                    break;
                }
                int next = src.indexOf(splitter, offset);
                if (next < 0) {
                    String s = src.substring(offset, src.length());
                    result.add(s);
                    break;
                } else {
                    String s = src.substring(offset, next);
                    result.add(s);
                    offset = next + splitter.length();
                }
            }
        }
        return result.toArray(new String[]{});
    }

    public static Eterable<String> splitIterable(String src,String splitter){
        String[] arr = split(src,splitter);
        return Eterable.of(arr);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hjoin()">
    /**
     * Объединяет строки вставляя между ними заданную строку
     *
     * @param lines Строки
     * @param glue Вставка
     * @return Результат склейки
     */
    public static String join(Iterable<String> lines, String glue) {
        return join(lines, glue, false);
    }

    /**
     * Объединяет строки вставляя между ними заданную строку
     *
     * @param lines Строки
     * @param glue Вставка
     * @param withNulls включить также пустые ссылки
     * @return Результат склейки
     */
    public static String join(Iterable<String> lines, String glue, boolean withNulls) {
        if (lines == null) {
            throw new IllegalArgumentException("lines == null");
        }
        if (glue == null) {
            throw new IllegalArgumentException("glue == null");
        }
        StringBuilder res = new StringBuilder();
        int idx = -1;
        for( String line : lines ){
            if( line==null && !(withNulls) )continue;

            idx++;
            if( idx>0 )res.append(glue);

            res.append(line!=null ? line : "null");
        }
        return res.toString();
    }

    /**
     * Объединяет строки вставляя между ними заданную строку
     *
     * @param lines Строки
     * @param glue Вставка
     * @param from С какой строки начать (withNulls - влияет на нумерацию)
     * @param count Сколько строк объединять? <b>-1</b> - без ограничения
     * @param withNulls включить также пустые ссылки
     * @return Результат склейки
     */
    public static String join(Iterable<String> lines, String glue, int from, int count, boolean withNulls) {
        if (lines == null) {
            throw new IllegalArgumentException("lines == null");
        }
        if (glue == null) {
            throw new IllegalArgumentException("glue == null");
        }
        if( count==0 )return "";

        StringBuilder res = new StringBuilder();

        int included = -1;
        int lineIdx = -1;

        for( String line : lines ){
            if( line==null && !(withNulls) )continue;
            lineIdx++;

            if( from>=0 && lineIdx<from )continue;
            if( count>0 && included>=count && count!=Integer.MAX_VALUE )break;

            included++;
            if( included>0 )res.append(glue);

            res.append(line!=null ? line : "null");
        }
        return res.toString();
    }

    /**
     * Объединяет строки вставляя между ними заданную строку
     *
     * @param lines Строки
     * @param glue Вставка
     * @param from С какой строки начать
     * @param count Сколько строк объединять
     * @return Результат склейки
     */
    public static String join(String[] lines, String glue, int from, int count) {
        if (lines == null) {
            throw new IllegalArgumentException("lines == null");
        }
        if (glue == null) {
            throw new IllegalArgumentException("glue == null");
        }
        return join(lines, glue, from, count, false);
    }

    /**
     * Объединяет строки вставляя между ними заданную строку
     *
     * @param lines Строки
     * @param glue Вставка
     * @param from С какой строки начать
     * @param count Сколько строк объединять
     * @param withNulls включить также пустые ссылки
     * @return Результат склейки
     */
    public static String join(String[] lines, String glue, int from, int count, boolean withNulls) {
        if (lines == null) {
            throw new IllegalArgumentException("lines == null");
        }
        if (glue == null) {
            throw new IllegalArgumentException("glue == null");
        }
        if( count==0 )return "";
        int includedLinesCount = 0;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int idx = from + i;

            boolean rightOutside = idx>=lines.length;
            if( rightOutside )break;

            boolean leftOutside = idx<0;
            if( leftOutside )continue;

            String line = lines[idx];
            if( !withNulls && line==null )continue;

            if( includedLinesCount>0 ){
                sb.append(glue);
            }

            sb.append(line);
            includedLinesCount++;
        }
        return sb.toString();
    }

    /**
     * Объединяет строки вставляя между ними заданную строку
     *
     * @param lines Строки
     * @param glue Вставка
     * @return Результат склейки
     */
    public static String join(String[] lines, String glue) {
        if (lines == null) {
            throw new IllegalArgumentException("lines == null");
        }
        if (glue == null) {
            throw new IllegalArgumentException("glue == null");
        }
        return join(lines, glue, 0, lines.length);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="indexOf(), in()">
    /**
     * Проверяет наличие строки среди заданных
     * @param src искомая строка
     * @param arr массив строк
     * @return true - искомая строка присуствует среди заданных / false - отсуствует
     */
    public static boolean in( String src, String ... arr ){
        return indexOf(src, arr) >= 0;
    }

    /**
     * Возвращает индекс искомой строки среди указанных строк
     * @param src искомая строка
     * @param arr массив строк
     * @return индекс строки в массиве или -1 если не найдена
     */
    public static int indexOf( String src, String ... arr ){
        if( src==null )throw new IllegalArgumentException( "src==null" );
        if( arr==null )throw new IllegalArgumentException( "arr==null" );
        int idx = -1;
        for( String a : arr ){
            idx++;
            if( a==null )continue;
            if( src.equals(a) ){
                return idx;
            }
        }
        return -1;
    }

    /**
     * Возвращает индексы строк удовлетворяющих заданному критерию
     * @param src критерий поиска
     * @param arr массив строк
     * @return индексы строк массива удовлетворяющих критерию
     */
    public static int[] indexesOf( Predicate<String> src, String ... arr ){
        if( src==null )throw new IllegalArgumentException( "src==null" );
        if( arr==null )throw new IllegalArgumentException( "arr==null" );

        int[] res = new int[]{};
        int idx = -1;

        for( String a : arr ){
            idx++;
            if( a==null )continue;
            if( src.test(a) ){
                res = Arrays.copyOf(res, res.length+1);
                res[res.length-1] = idx;
            }
        }
        return res;
    }

    /**
     * Возвращает индексы строк удовлетворяющих заданному критерию
     * @param src критерий поиска
     * @param arr массив строк
     * @return индексы строк массива удовлетворяющих критерию
     */
    public static int[] indexesOf( Predicate<String> src, Iterable<String> arr ){
        if( src==null )throw new IllegalArgumentException( "src==null" );
        if( arr==null )throw new IllegalArgumentException( "arr==null" );

        int[] res = new int[]{};
        int idx = -1;

        for( String a : arr ){
            idx++;
            if( a==null )continue;
            if( src.test(a) ){
                res = Arrays.copyOf(res, res.length+1);
                res[res.length-1] = idx;
            }
        }
        return res;
    }

    /**
     * Проверяет наличие строки среди заданных
     * @param src искомая строка
     * @param list массив строк
     * @return true - строка присуствует среди заданных
     */
    public static boolean in( String src, Iterable<String> list ){
        return indexOf(src, list) >= 0;
    }

    /**
     * Возвращает индекс искомой строки среди заданых
     * @param src искомая строка
     * @param list массив строк
     * @return индекс или -1 если не найдена
     */
    public static int indexOf( String src, Iterable<String> list ){
        if( src==null )throw new IllegalArgumentException( "src==null" );
        if( list==null )throw new IllegalArgumentException( "list==null" );
        int idx = -1;
        for( String a : list ){
            idx++;
            if( a==null )continue;
            if( src.equals(a) ){
                return idx;
            }
        }
        return -1;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="wordWrap()">
    private static String ww_getNext(String text, int offset) {
        if (text == null)
            return null;
        if (offset >= text.length())
            return null;
        if (offset < 0)
            return null;

        char c = text.charAt(offset);
        if (Character.isLetterOrDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (true) {
                if (offset >= text.length())
                    break;

                c = text.charAt(offset);
                if (!Character.isLetterOrDigit(c))
                    break;
                offset++;
                sb.append(c);
            }
            return sb.toString();
        } else {
            return new String(new char[]{c});
        }
    }

    /**
     * Перенос слов на несколько строк.
     * @param text Исходный текст
     * @param maxwidth Максимальная длина колонки
     * @return Набор строк
     */
    public static Eterable<String> wordWrapIterable(String text,int maxwidth){
        String[] src = wordWrap(text, maxwidth);
        return Eterable.of(src);
    }

    /**
     * Перенос слов на несколько строк.
     *
     * @param text Исходный текст
     * @param maxwidth Максимальная длина колонки
     * @return Набор строк
     */
    public static String[] wordWrap(String text, int maxwidth) {
        if (text == null) {
            throw new IllegalArgumentException("text==null");
        }
        if (maxwidth < 1) {
            throw new IllegalArgumentException("maxwidth<1");
        }
        if (text.length() == 0)
            return new String[]{text};
        if (text.length() < maxwidth)
            return new String[]{text};

        ArrayList<String> lines = new ArrayList<>();
        int offset = 0;
        StringBuilder sb = new StringBuilder();
        String tok = null;
        while (true) {
            if (tok == null) {
                tok = ww_getNext(text, offset);
                if (tok == null)
                    break;
                offset += tok.length();
            }

            if (tok.length() == maxwidth) {
                if (sb.length() > 0) {
                    lines.add(sb.toString());
                    sb.setLength(0);
                }
                lines.add(tok);
                tok = null;
                continue;
            }

            if (tok.length() > maxwidth) {
                if (sb.length() > 0) {
                    lines.add(sb.toString());
                    sb.setLength(0);
                }
                lines.add(tok.substring(0, maxwidth));
                tok = tok.substring(maxwidth);
                continue;
            }

            if (tok.length() == 0) {
                tok = null;
                continue;
            }

            int sbLen = sb.length();
            if (sbLen + tok.length() > maxwidth) {
                if (sb.length() > 0) {
                    lines.add(sb.toString());
                    sb.setLength(0);
                }
                sb.append(tok);
                tok = null;
            } else {
                sb.append(tok);
                tok = null;
            }
        }
        if (sb.length() > 0) {
            lines.add(sb.toString());
            sb.setLength(0);
        }
        return lines.toArray(new String[]{});
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="align()">
    /**
     * Выравнивает текст до определенной длины.
     * Если текст привышает или равен указанную длину, то возвращается как есть
     * @param lines Текст
     * @param align Как выравнивать.
     * @param padText Текст который добавляется для выравнивания (Возможно null - тогда будет использоваться один пробел)
     * @param len Длина текста, не меньше нуля по каторой выравнивается.
     * @return Текст
     */
    public static String[] align(String[] lines,Align align,String padText,int len){
        if (lines== null) {
            throw new IllegalArgumentException("lines==null");
        }
        if (align== null) {
            throw new IllegalArgumentException("align==null");
        }
        String[] res = new String[lines.length];
        for( int i=0; i<lines.length; i++ ){
            res[i] = align(lines[i], align, padText, len);
        }
        return res;
    }

    /**
     * Выравнивает текст по краю заданной длинны
     * @param lines Исходный текст
     * @param align Выравнивание
     * @param padText Текст который дополняется слева/справа
     * @param len длина колонки
     * @return Выравненый текст
     */
    public static Iterable<String> align(Iterable<String> lines,Align align,String padText,int len){
        if( lines==null ) throw new IllegalArgumentException( "lines==null" );
        if( align==null ) throw new IllegalArgumentException( "align==null" );
        return convert(lines, Convertors.align(align, padText, len));
    }

    /**
     * Выравнивает текст до определенной длинны.
     * Если текст привышает или равен указанную длину, то возвращается как
     * есть
     * @param text Текст
     * @param align Как выравнивать. <br>
     * <b>Begin</b> -  Выравнивать по левому краю, пробельные символы добавляются в конец. <br>
     * <b>Center</b> - Выравнивать по центру, пробельные символы добавляются с обоих краев. <br>
     * <b>End</b>    - Выравнивать по правому краю, пробельные символы добавляются в начало. <br>
     * @param padText Текст который добавляется для выравнивания (Возможно null - тогда будет использоваться один пробел)
     * @param len Длина текста, не меньше нуля по каторой выравнивается.
     * @return Текст
     */
    public static String align(String text, Align align, String padText, int len){
        return align(text, align, padText, len, false);
    }

    /**
     * Выравнивает текст до определенной длинны.
     * Если текст привышает или равен указанную длину, то возвращается как
     * есть
     * @param text Текст
     * @param align Как выравнивать. <br>
     * <b>Begin</b> -  Выравнивать по левому краю, пробельные символы добавляются в конец. <br>
     * <b>Center</b> - Выравнивать по центру, пробельные символы добавляются с обоих краев. <br>
     * <b>End</b>    - Выравнивать по правому краю, пробельные символы добавляются в начало. <br>
     * @param padText Текст который добавляется для выравнивания (Возможно null - тогда будет использоваться один пробел)
     * @param len Длина текста, не меньше нуля по каторой выравнивается.
     * @param trimText Усекать пробелы в начале и в конце строки ()
     * @return Текст
     */
    public static String align(String text, Align align, String padText, int len, boolean trimText) {
        if (text == null) {
            throw new IllegalArgumentException("text==null");
        }

        if (align== null) {
            throw new IllegalArgumentException("align==null");
        }

        if (padText == null) {
            padText = " ";
        } else if (padText.length() < 1){
            padText = " ";
        }

        if (len < 0) {
            throw new IllegalArgumentException("len<0");
        }

        if( trimText )text = text.trim();

        if (text.length() >= len)
            return text;

        int add = len - text.length();
        int idx = -1;
        int padLen = padText.length();
        StringBuilder sb = new StringBuilder();
        boolean insCBegin = true;

        if (align == Align.Begin)
            sb.append(text);
        if (align == Align.Center)
            sb.append(text);

        for (int i = 0; i < add; i++) {
            idx++;
            if (idx >= padLen)
                idx = 0;
            char c = padText.charAt(idx);

            if (align == Align.Center) {
                if (insCBegin) {
                    sb.insert(0, c);
                } else {
                    sb.append(c);
                }
                insCBegin = !insCBegin;
            } else {
                sb.append(c);
            }
        }

        if (align == Align.End)
            sb.append(text);

        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="indent()">
    /**
     * Добавляет отступ в начале каждой строки
     * @param indent отступ
     * @param srcText текст
     * @return текст с отступом
     */
    public static String indent( String indent, String srcText){
        if( srcText==null )throw new IllegalArgumentException( "srcText==null" );
        if( indent==null )throw new IllegalArgumentException( "indent==null" );

        String[] lines = splitNewLines(srcText);
        for( int i=0; i<lines.length; i++ ){
            lines[i] = indent + lines[i];
        }
        return join(lines, EndLine.Default.get());
    }

    /**
     * Добавляет отступ в начало каждой строки
     *
     * @param indent Отступ
     * @param source Исходный текст
     * @param lineDelim Разделитель строк
     * @return Строки с отступом
     */
    public static String indent(String indent, String source, String lineDelim) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
//        if (lineDelim == null) {
//            throw new IllegalArgumentException("lineDelim == null");
//        }
        if( lineDelim==null )lineDelim = EndLine.Default.get();
        if (indent == null) {
            throw new IllegalArgumentException("indent == null");
        }
        String[] sourceLines = splitNewLines(source);
        String[] res = new String[sourceLines.length];
        for (int i = 0; i < sourceLines.length; i++) {
            res[i] = indent + sourceLines[i];
        }
        return join(res, lineDelim);
    }

    /**
     * Добавляет отступ
     *
     * @param sourceLines Исходный набор строк
     * @param indent Отступ
     * @return Строки с отступом
     */
    public static String[] indent(String indent, String[] sourceLines) {
        if (sourceLines == null) {
            throw new IllegalArgumentException("sourceLines == null");
        }
        if (indent == null) {
            throw new IllegalArgumentException("indent == null");
        }

        String[] res = new String[sourceLines.length];
        for (int i = 0; i < sourceLines.length; i++) {
            res[i] = indent + sourceLines[i];
        }
        return res;
    }

    /**
     * Добавляет отступ в начало каждой строки
     * @param indent Отступ
     * @param source Исходный текст
     * @return Строки с отступом
     */
    public static Iterable<String> indent(String indent, Iterable<String> source){
        if( source==null ) throw new IllegalArgumentException( "source==null" );
        if( indent==null ) throw new IllegalArgumentException( "indent==null" );
        return convert( source, Convertors.wrap(indent, "") );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="splitNewLines()">
    /**
     * Разделяет текст по строки по символу(ам) перевода строк (CR+LF/CR/LF (\r\n, \r, \n))
     * @param text Исходный текст
     * @return набор строк
     */
    public static Eterable<String> splitNewLinesIterable(String text){
        String[] arr = splitNewLines(text);
        return Eterable.of(arr);
    }

    /**
     * Делит текст на строки согласно символам перевода строк: CR+LF/CR/LF (\r\n, \r, \n).<br> Сами символы (CR,LF) не
     * входят в результирующий набор строк.
     *
     * @param line Текст
     * @return Результат.
     */
    public static String[] splitNewLines(String line) {
        if (line == null) {
            throw new IllegalArgumentException("line==null");
        }
        int idx = 0;
        int len = line.length();
        char c1;
        char c2;
        StringBuilder buff = new StringBuilder();
        ArrayList<String> lines = new ArrayList<>();
        while (idx < len) {
            c1 = line.charAt(idx);
            c2 = idx < (len - 1) ? line.charAt(idx + 1) : (char) 0;

            //CR+LF - Windows,Dos
            if (c1 == '\r' && c2 == '\n') {
                lines.add(buff.toString());
                buff.setLength(0);
                idx += 2;
                continue;
            }

            // Acorn BBC, RISC OS
            if (c1 == '\n' && c2 == '\r') {
                lines.add(buff.toString());
                buff.setLength(0);
                idx += 2;
                continue;
            }

            // Mac os
            if (c1 == '\r' && c2 != '\n') {
                lines.add(buff.toString());
                buff.setLength(0);
                idx += 1;
                continue;
            }

            // Unix, linux, ....
            if (c1 == '\n' && c2 != '\r') {
                lines.add(buff.toString());
                buff.setLength(0);
                idx += 1;
                continue;
            }

            buff.append(c1);
            idx++;
        }
        lines.add(buff.toString());
        return lines.toArray(new String[]{});
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="encode / parse String const">
    /**
     * Кодирует текст в строковую констануту вида языка С.
     * <br> Экраниует символы: " \ \t \r \n \b \f <p>
     * Примеры:
     * <table summary="Пример преобразований">
     * <tr><td>abc</td><td>"abc"</td></tr>
     *
     * <tr><td>abc "dfg" sss</td><td>"abc \"dfg\" sss"</td></tr>
     * <tr><td>abc\sss</td><td>"abc\\sss"</td></tr>
     * </table>
     * @param srcText Исходный текст
     * @param quoteChar Символ экранирования
     * @return Кодированый текст
     */
    public static String encodeStringConstant(String srcText,char quoteChar) {
        if (srcText == null) {
//            throw new IllegalArgumentException("srcText == null");
            return "null";
        }

        StringBuilder txt = new StringBuilder();
        txt.append(quoteChar);
        for (int i = 0; i < srcText.length(); i++) {
            char c = srcText.charAt(i);
            switch (c) {
                case '\'':
                    txt.append("\\\'");
                    break;
                case '"':
                    txt.append("\\\"");
                    break;
                case '\\':
                    txt.append("\\\\");
                    break;
                case '\t':
                    txt.append("\\t");
                    break;
                case '\r':
                    txt.append("\\r");
                    break;
                case '\n':
                    txt.append("\\n");
                    break;
                case '\b':
                    txt.append("\\b");
                    break;
                case '\f':
                    txt.append("\\f");
                    break;
                default:
                    txt.append(c);
            }
        }
        txt.append(quoteChar);

        return txt.toString();
    }

    /**
     * Кодирует текст в строковую констануту вида языка С. <br>
     * Экраниует символы: " \ \t \r \n \b \f <p> Примеры:
     * <table  summary="Пример преобразований">
     * <tr><td>abc</td><td>"abc"</td></tr>
     * <tr><td>abc "dfg" sss</td><td>"abc \"dfg\" sss"</td></tr>
     * <tr><td>abc\sss</td><td>"abc\\sss"</td></tr>
     * </table>
     *
     * @param srcText Исходный текст
     * @return Кодированый текст
     */
    public static String encodeStringConstant(String srcText) {
        return encodeStringConstant(srcText, '"');
//        if (srcText == null) {
////            throw new IllegalArgumentException("srcText == null");
//            return "null";
//        }
//
//        StringBuilder txt = new StringBuilder();
//        txt.append("\"");
//        for (int i = 0; i < srcText.length(); i++) {
//            char c = srcText.charAt(i);
//            switch (c) {
//                case '"':
//                    txt.append("\\\"");
//                    break;
//                case '\\':
//                    txt.append("\\\\");
//                    break;
//                case '\t':
//                    txt.append("\\t");
//                case '\r':
//                    txt.append("\\r");
//                    break;
//                case '\n':
//                    txt.append("\\n");
//                    break;
//                case '\b':
//                    txt.append("\\b");
//                    break;
//                case '\f':
//                    txt.append("\\f");
//                    break;
//                default:
//                    txt.append(c);
//            }
//        }
//        txt.append("\"");
//
//        return txt.toString();
    }

    /**
     * Результат анализа текста
     */
    public static interface ParseStringResult
    {

        /**
         * Декодированная строка
         *
         * @return Декодированная строка
         */
        String decodedString();

        /**
         * Исходная строка
         *
         * @return Исходная строка
         */
        String sourceString();

        /**
         * Индекс начала константы
         *
         * @return Индекс
         */
        int beginIndex();

        /**
         * Индекс конца константы (исключительно)
         *
         * @return Индекс
         */
        int endIndex();
    }

    public static class SimpleParseResult implements ParseStringResult
    {

        public String decodedString = null;
        public String sourceString = null;
        public int beginIndex = -1;
        public int endIndex = -2;

        @Override
        public String decodedString() {
            return decodedString;
        }

        @Override
        public String sourceString() {
            return sourceString;
        }

        @Override
        public int beginIndex() {
            return beginIndex;
        }

        @Override
        public int endIndex() {
            return endIndex;
        }
    }

    /**
     * Анализирует текстовую константу языка С <p> <b>Синтаксис строки</b><br>
     * <code>Константа ::= {Пробельный символ} Кавычки {Кодированный символ} Кавычки <br>
     * Пробельный символ ::= пробел | перевод строи | возврат корретки | табуляция <br>
     * Кавычки ::= '"' <br>
     * Кодированный символ ::= ( '\' экранированный символ ) | обычный символ <br>
     * Экранированный символ ::= 'n' | 'r' | 't' | '"' | '\'
     * </code> </p>
     *
     * @param constant Константа (или null)
     * @param startIndex Индекс (0 ... длина строки)
     * @return Результат анализа или null, если не соответствует кодированию (или входные параметры не удалетворяют
     * условию)
     */
    public static ParseStringResult parseStringConstat(String constant, int startIndex) {
        if (constant == null) {
            return null;
        }

        if (startIndex < 0 || startIndex >= constant.length())
            return null;

        // 0 - ws
        // 1 - char
        // 2 - escaped char
        // -1 - error
        int state = 0;
        int endIndex = -1;
        String buf = "";

        for (int i = startIndex; i < constant.length(); i++) {
            if (state > 99 || state < 0)
                break;
            char c = constant.charAt(i);
            switch (state) {
                case 0:
                    if (Character.isWhitespace(c)) {
                        state = 0;
                    } else if (c == '"') {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    switch (c) {
                        case '\\':
                            state = 2;
                            break;
                        case '"':
                            state = 99999;
                            endIndex = i + 1;
                            break;
                        default:
                            buf += c;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        case 'n':
                            buf += "\n";
                            state = 1;
                            break;
                        case 'r':
                            buf += "\r";
                            state = 1;
                            break;
                        case 't':
                            buf += "\t";
                            state = 1;
                            break;
                        case '\"':
                            buf += "\"";
                            state = 1;
                            break;
                        case '\\':
                            buf += "\\";
                            state = 1;
                            break;
                        default:
                            state = -1;
                            break;
                    }
                    break;
            }
        }

        if (state < 0)
            return null;

        SimpleParseResult res = new SimpleParseResult();
        res.sourceString = constant;
        res.decodedString = buf;
        res.beginIndex = startIndex;
        res.endIndex = endIndex;

        return state > 99 ? res : null;
    }// </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="wildcard()">
    /**
     * Создает шаблон регулярного выражения из маски (*?)
     * @param wildcard Маска
     * @param escapeAllowed Допускается в маске использовать экранирующие символы
     * @param ignoreCase Игнорировать регистр
     * @return шаблон регулярного выражения
     */
    @SuppressWarnings("UnusedAssignment")
    public static Pattern wildcard(String wildcard,boolean escapeAllowed,boolean ignoreCase){
        if (wildcard== null) {
            throw new IllegalArgumentException("wildcard==null");
        }
        StringBuilder sb = new StringBuilder();

        if( ignoreCase )
            sb.append("(?is)");
        else
            sb.append("(?s)");

        if( escapeAllowed ){
            boolean qOpen = false;
            boolean esc = false;
            for( int i=0; i<wildcard.length(); i++ ){
                char c = wildcard.charAt(i);
                switch( c ){
                    case '?':
                        if( esc ){
                            if( !qOpen ){
                                sb.append("\\Q");
                                qOpen = true;
                            }
                            sb.append("?");
                        }else{
                            if( qOpen ){
                                sb.append("\\E");
                                qOpen = false;
                            }
                            sb.append(".");
                        }
                        break;
                    case '*':
                        if( esc ){
                            if( !qOpen ){
                                sb.append("\\Q");
                                qOpen = true;
                            }
                            sb.append("*");
                        }else{
                            if( qOpen ){
                                sb.append("\\E");
                                qOpen = false;
                            }
                            sb.append(".*?");
                        }
                        break;
                    case '\\':
                        if( esc ){
                            if( !qOpen ){
                                sb.append("\\Q");
                                qOpen = true;
                            }
                            sb.append('\\');
                            esc = false;
                        }else{
                            esc = true;
                        }
                        break;
                    default:
                        if( !qOpen ){
                            sb.append("\\Q");
                            qOpen = true;
                        }
                        sb.append(c);
                        break;
                }
            }
            if( qOpen ){
                sb.append("\\E");
                qOpen = false;
            }
        }else{
            wildcard = wildcard.replace("?", ".");
            wildcard = wildcard.replace("*", ".*?");
            sb.append(wildcard);
        }

//        int state = 0;
//        boolean esc = false;
//        for( int i=0; i<wildcard.length(); i++ ){
//            char c = wildcard.charAt(i);
//            switch( c ){
//                case '?':
//                    sb.append( esc ? "\\?" : ".");
//                    if( esc )esc = false;
//                    break;
//                case '*':
//                    sb.append( esc ? "\\*" : ".*?");
//                    if( esc )esc = false;
//                    break;
//                case '\\':
//                    if( esc ){
//                        sb.append("\\\\");
//                        esc = false;
//                    }else{
//                        if( escapeAllowed ){
//                            esc = true;
//                        }else{
//                            sb.append("\\\\");
//                        }
//                    }
//                    break;
//                case '{': case '}':
//                case '[': case ']':
//                case '(': case ')':
//                case '.': case '+': case '$': case '^': case '|':
//                    sb.append("\\");
//                    sb.append(c);
//                    esc = false;
//                    break;
//                default:
//                    sb.append(c);
//                    esc = false;
//                    break;
//            }
//        }
        return Pattern.compile(sb.toString());
    }
    //</editor-fold>

    /**
     * Форматирование числа
     * @param format Формат числа:
     * Пример форматов:
     * <table summary="Примеры формата">
     * <tr>
     * <td>+00000000.000</td>
     * <td>12.23456</td>
     * <td><span
     *  style="background-color:#eeeeee;letter-spacing:2px;">"+00012345.235"</span></td>
     *
     * </tr><tr>
     *
     * <td>00000000.000</td>
     * <td>12.23456</td>
     * <td><span style="background-color:#eeeeee">"00012345.235"</span></td>
     * </tr><tr>
     *
     * <td>### ###.## ##</td>
     * <td>1234567.2345678</td>
     * <td><span style="background-color:#eeeeee">"1 234 567.23 45 67 8"</span></td>
     *
     * </tr><tr>
     *
     * <td>+###000.00</td>
     * <td>12.23456</td>
     * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;+012.23"</span></td>
     *
     * </tr><tr>
     *
     * <td>###000.0#</td>
     * <td>12.23456</td>
     * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;012.23"</span></td>
     *
     * </tr><tr>
     *
     * <td>###000.00####</td>
     * <td>12.2345</td>
     * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;012.2345&nbsp;&nbsp;"</span></td>
     *
     * </tr><tr>
     *
     * <td>###000.00####</td>
     * <td>12.23</td>
     * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;012.23&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
     *
     * </tr><tr>
     *
     * <td>###000.00####</td>
     * <td>12.2</td>
     * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;012.20&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
     *
     * </tr><tr>
     *
     * <td>#</td>
     * <td>1234.2345</td>
     * <td><span style="background-color:#eeeeee">"1234"</span></td>
     *
     * </tr><tr>
     *
     * <td>+###000,00####*100</td>
     * <td>12</td>
     * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;+1200&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
     *
     * </tr><tr>
     *
     * <td>+###000,00####*-5.5-2.1</td>
     * <td>12</td>
     * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;&nbsp;-68.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
     *
     * </tr>
     *
     * </table>
     * @param num Число
     * @return Строка представляющее число
     * @see FullDecFormat
     */
    public static String format( String format, Number num ){
        if( format==null )throw new IllegalArgumentException("format==null");
        return FullDecFormat.create(format).format(num);
    }

    //<editor-fold defaultstate="collapsed" desc="Конверторы текста">
    /**
     * Конверторы текста
     */
    public static class Convertors {
        public final static Function<String,String> map(
            final Map<String,String> srcMap,
            final String defValue,
            final String nullValue ){
            return new Function<String, String>() {
                @Override
                public String apply( String from ) {
                    if( from==null )return defValue;
                    if( srcMap==null )return defValue;

                    if( !srcMap.containsKey(from) )
                        return defValue;

                    String v = srcMap.get(from);
                    if( v==null )return nullValue;

                    return v;
                }
            };
        }

        /**
         * Выравнивает текст по краю заданной длинны
         * @param align Выравнивание
         * @param padText Текст который дополняется слева/справа
         * @param len длина колонки
         * @return Выравниватель
         */
        public final static Function<String,String> align(final Align align,final String padText,final int len){
            return new Function<String, String>() {
                @Override
                public String apply( String from ) {
                    return Text.align(from, align, padText, len);
                }
            };
        }

        /**
         * Применяет функцию encodeStringConstant.
         * На вход может быть подана null ссылка, в результате будет текст <code>null</code>
         * @see Text#encodeStringConstant(java.lang.String)
         */
        public final static Function<String,String> toStringConst = new Function<String, String>() {
            @Override
            public String apply(String from) {
                if( from==null )return "null";
                String str = encodeStringConstant(from);
                return str;
            }
        };

        /**
         * Применяет функцию parseStringConstat.
         * Если входной текст будет совпадать с <code>null</code> (без учета регистра и концевых пробелов), то будет возращена null ссылка
         * @see Text#parseStringConstat(java.lang.String, int)
         */
        public final static Function<String,String> fromStringConst = new Function<String, String>() {
            @Override
            public String apply(String from) {
                if( from==null )return null;
                if( from.trim().equalsIgnoreCase("null") )
                    return null;
                ParseStringResult res = parseStringConstat(from, 0);
                if( res==null )return null;
                return res.decodedString();
            }
        };

        /**
         * Приводит текст к нижнему регистру
         */
        public final static Function<String,String> toLowerCase = new Function<String, String>() {
            @Override
            public String apply(String from) {
                return from.toLowerCase();
            }
        };

        /**
         * Приводит текст к верхнему регистру
         */
        public final static Function<String,String> toUpperCase = new Function<String, String>() {
            @Override
            public String apply(String from) {
                return from.toUpperCase();
            }
        };

        /**
         * Усекает текст
         * @param trimStart Вырезаемый текст с начала
         * @param trimEnd Вырезаемый текст с конца
         * @return Конвертор текста
         * @see Text#trimStart(java.lang.String, java.lang.String)
         * @see Text#trimEnd(java.lang.String, java.lang.String)
         */
        public static Function<String,String> trim(String trimStart,String trimEnd){
            final String s = trimStart;
            final String e = trimEnd;
            return new Function<String, String>() {
                @Override
                public String apply(String from) {
                    if( from==null )return from;
                    if( s!=null && s.length()>0 )from = Text.trimStart(from, s);
                    if( e!=null && e.length()>0 )from = Text.trimEnd(from, e);
                    return from;
                }
            };
        }

        /**
         * Кодировщик исходного текста в HTML
         */
        public static final Function<String,String> htmlEncode = new Function<String, String>() {
            @Override
            public String apply(String from) {
                if( from==null )return null;
                return htmlEncode(from);
            }
        };

        /**
         * ДеКодировщик HTML в исходный текст
         */
        public static final Function<String,String> htmlDecode = new Function<String, String>() {
            @Override
            public String apply(String from) {
                if( from==null )return null;
                return htmlDecode(from);
            }
        };

        /**
         * Кодировщик исходного текста в атрибут HTML
         */
        public static final Function<String,String> attrEncode = new Function<String, String>() {
            @Override
            public String apply(String from) {
                if( from==null )return null;
                return attrEncode(from);
            }
        };

        /**
         * Создает цепочу преобразований
         * @param convertors Последовательность преобразований
         * @return Преобразователь текста
         */
        public static Function<String,String> sequence( Function<String,String> ... convertors ){
            final Function<String,String>[] convs = convertors;
            return new Function<String, String>() {
                @Override
                public String apply(String from) {
                    for( Function<String,String> c : convs ){
                        from = c.apply(from);
                    }
                    return from;
                }
            };
        }

        /**
         * Обвертка вокруг текста
         * @param before Префикс
         * @param after Суфикс
         * @return Ковертор
         */
        public static Function<String,String> wrap( Supplier<String> before, Supplier<String> after ){
            final Supplier<String> sbefore = before;
            final Supplier<String> safter = after;
            return new Function<String, String>() {
                @Override
                public String apply(String text) {
                    if( sbefore!=null && safter!=null )return sbefore.get() + text + safter.get();
                    if( sbefore==null && safter!=null )return text + safter.get();
                    if( sbefore!=null && safter==null )return sbefore.get() + text;
                    return text;
                }
            };
        }

        /**
         * Обвертка вокруг текста
         * @param before Префикс
         * @param after Суфикс
         * @return Ковертор
         */
        public static Function<String,String> wrap( String before, String after ){
            final String sbefore = before;
            final String safter = after;
            return new Function<String, String>() {
                @Override
                public String apply(String text) {
                    if( sbefore!=null && safter!=null )return sbefore + text + safter;
                    if( sbefore==null && safter!=null )return text + safter;
                    if( sbefore!=null && safter==null )return sbefore + text;
                    return text;
                }
            };
        }

        /**
         * Заменяет символы перевода текста на указанный
         * @param newLine Символы перевода строки
         * @return Ковертор
         */
        public static Function<String,String> newlineReplace( String newLine ){
            final String nl = newLine;
            final StringBuilder sb = new StringBuilder();
            return new Function<String, String>() {
                @Override
                public String apply(String text) {
                    if( text!=null ){
                        String[] lines = Text.splitNewLines(text);
                        sb.setLength(0);
                        for( int i=0; i<lines.length; i++ ){
                            if( i>0 && nl!=null )sb.append(nl);
                            sb.append(lines[i]);
                        }
                        return sb.toString();
                    }
                    return text;
                }
            };
        }
        /**
         * Заменяет символы перевода текста на указанный
         * @param newLine Символы перевода строки
         * @return Ковертор
         */
        public static Function<String,String> newlineReplace( Supplier<String> newLine ){
            final Supplier<String> nl = newLine;
            final StringBuilder sb = new StringBuilder();
            return new Function<String, String>() {
                @Override
                public String apply(String text) {
                    if( text!=null ){
                        String[] lines = Text.splitNewLines(text);
                        sb.setLength(0);
                        for( int i=0; i<lines.length; i++ ){
                            if( i>0 && nl!=null )sb.append(nl.get());
                            sb.append(lines[i]);
                        }
                        return sb.toString();
                    }
                    return text;
                }
            };
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Текстовые предикаты">
    /**
     * Текстовые предикаты
     */
    public static class Predicates{
        /**
         * Создает предикат проверки совпадения значения хотябы с одним из заданных
         * @param ignoreCase Игнорировать регистр
         * @param values значения для проверки
         * @return Предикат
         */
        public static Predicate<String> in(
            final boolean ignoreCase,
            final String ... values
        ){
            return in( ignoreCase, Arrays.asList(values) );
        }

        /**
         * Создает предикат проверки совпадения значения хотябы с одним из заданных
         * @param ignoreCase Игнорировать регистр
         * @param values значения для проверки
         * @return Предикат
         */
        public static Predicate<String> in(
            final boolean ignoreCase,
            final Iterable<String> values
        ){
            if( values==null )throw new IllegalArgumentException("values == null");

            return new Predicate<String>() {
                @Override
                public boolean test(String value) {
                    for( String sample : values ){
                        if( sample==null ){
                            if( value==null ){
                                return true;
                            }
                            continue;
                        }

                        boolean m = ignoreCase ? sample.equalsIgnoreCase(value) : sample.equals(value);

                        if(m){
                            return true;
                        }
                    }
                    return false;
                }
            };
        }

        /**
         * Создает предикат проверки совпадения значения хотябы с одним из заданных
         * @param values значения для проверки
         * @param matcher Функция сравнения
         * @return Предикат
         */
        public static Predicate<String> in(
            final Iterable<String> values,
            final BiFunction<String, String, Boolean> matcher
        ){
            if( values==null )throw new IllegalArgumentException("values == null");
            if( matcher==null )throw new IllegalArgumentException("matcher == null");

            return new Predicate<String>() {
                @Override
                public boolean test(String value) {
                    for( String sample : values ){
                        boolean m = matcher.apply(sample, value);
                        if(m){
                            return true;
                        }
                    }
                    return false;
                }
            };
        }

        /**
         * Создает предкат сравнения с регулярным выражением
         * @param regex Регулярное выражение
         * @return Предикат
         */
        public static Predicate<String> matchRegex(java.util.regex.Pattern regex){
            if (regex == null) {
                throw new IllegalArgumentException("regex == null");
            }
            final java.util.regex.Pattern reg = regex;

            return new Predicate<String>() {
                @Override
                public boolean test(String value)
                {
                    if( value==null )return false;
                    return reg.matcher(value).matches();
                }
            };
        }

        /**
         * Создает предикат сравнения строки
         * @param source Образец для сравнения
         * @return Предикат
         */
        public static Predicate<String> equals(String source){
            final CharSequence src = source;
            return new Predicate() {
                @Override
                public boolean test(Object value) {
                    if( src==null && value==null )return true;
                    if( src!=null && value==null )return false;
                    if( src==null && value!=null )return false;
                    return src.equals(value);
                }
            };
        }

        /**
         * Создает предикат сравнения строки без учета регистра
         * @param source Образец для сравнения
         * @return Предикат
         */
        public static Predicate<String> equalsIgnoreCase(String source){
            final CharSequence src = source;
            return new Predicate() {
                @Override
                public boolean test(Object value) {
                    if( src==null && value==null )return true;
                    if( src!=null && value==null )return false;
                    if( src==null && value!=null )return false;
                    return src.toString().equalsIgnoreCase(((CharSequence)value).toString());
//                    return false;
                }
            };
        }

        /*
         * Создает предикат проверки
         * @param strings
         * @param pred
         * @return
         *
        public static Predicate<String> in(String[] strings,Predicate<String> pred){
            final String[] strs = strings;
            final Predicate<String> p = pred;
            return new Predicate<String>() {
                @Override
                public boolean validate(String value) {
                    if( strs==null )return false;
                    if( p==null )return false;
                    for( String str : strs ){
                        if( p.validate(str) ){
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
        */
        /*
        public static Predicate<String> in(Iterable<String> strings,Predicate<String> pred){
            final Iterable<String> strs = strings;
            final Predicate<String> p = pred;
            return new Predicate<String>() {
                @Override
                public boolean validate(String value) {
                    if( strs==null )return false;
                    if( p==null )return false;
                    for( String str : strs ){
                        if( p.validate(str) ){
                            return true;
                        }
                    }
                    return false;
                }
            };
        }*/

        private static Pattern _numericPattern = null;

        /**
         * Проверка что указанный текс является числом.<br>
         * Формат: <code>(?is)^\s*(\-|\+)?\s*\d+(\.(\d+))?\s*$</code>
         * @return Предикат проверки
         */
        public static Predicate<String> isNumeric(){
            if( _numericPattern==null )
                _numericPattern =
                    Pattern.compile("(?is)^\\s*(\\-|\\+)?\\s*\\d+(\\.(\\d+))?\\s*$");
            return matchRegex(_numericPattern);
        }
    }
    //</editor-fold>
}
