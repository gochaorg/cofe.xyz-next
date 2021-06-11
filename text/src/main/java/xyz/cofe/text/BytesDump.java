package xyz.cofe.text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.fn.Tuple3;

/**
 * Дамп байт массива
 * <br />
 *
 * Пример
 * <pre>
 * byte[] bytes = (
 *     "0123456789abcdef _ABCDEFG"
 * ).getBytes(StandardCharsets.ISO_8859_1);
 *
 * BytesDump dump = new BytesDump.Builder().relative( decoder -&gt; {
 *     decoder
 *         .name(0, 2, "head")
 *         .decode(4, bytes1 -&gt; new String(bytes1,StandardCharsets.ISO_8859_1));
 * }).build();
 *
 * System.out.println(dump.dump(bytes));
 * </pre>
 *
 * Вывод:
 * <pre>
 *        0|30 31                                          |head
 *        2|      32 33 34 35                              |2345
 *        6|                  36 37 38 39 61 62 63 64 65 66
 *       10|20 5f 41 42 43 44 45 46 47
 * </pre>
 */
public class BytesDump {
    //region offsetAlign : int = 16
    protected int offsetAlign = 16;

    /**
     * Возвращает как выравнивать начала массива байт, по какому размеру
     * @return выравнивание
     */
    public int getOffsetAlign(){ return offsetAlign; }

    /**
     * Указывает как выравнивать начала массива байт, по какому размеру
     * @param v выравнивание
     */
    public void setOffsetAlign(int v){ offsetAlign = v; }
    //endregion
    //region ptrAlign : int = 16
    protected int ptrAlign = 16;

    /**
     * Возвращает как выравнивать текущую позицию, по какому размеру
     * @return выравнивание
     */
    public int getPtrAlign(){ return ptrAlign; }

    /**
     * Указывает как выравнивать текущую позицию, по какому размеру
     * @param v выравнивание
     */
    public void setPtrAlign(int v){ ptrAlign = v; }
    //endregion
    //region offsetWidth : int = 8
    /**
     * Кол-во символом отведенных для смещения
     */
    protected int offsetWidth = 8;

    /**
     * Кол-во символом отведенных для смещения
     * @return Кол-во символов
     */
    public int getOffsetWidth(){
        return offsetWidth;
    }

    /**
     * Кол-во символом отведенных для смещения
     * @param offsetWidth Кол-во символов
     */
    public void setOffsetWidth(int offsetWidth){
        this.offsetWidth = offsetWidth;
    }
    //endregion
    //region offsetRadix : int = 16
    protected int offsetRadix = 16;
    public int getOffsetRadix(){
        return offsetRadix;
    }
    public void setOffsetRadix(int offsetRadix){
        this.offsetRadix = offsetRadix;
    }
    //endregion
    //region offsetDelimiter : String = "|"
    protected String offsetDelimiter = "|";
    public String getOffsetDelimiter(){
        return offsetDelimiter;
    }
    public void setOffsetDelimiter(String offsetDelimiter){
        this.offsetDelimiter = offsetDelimiter;
    }
    //endregion
    //region descDelimiter : String = "|"
    protected String descDelimiter = "|";
    public String getDescDelimiter(){
        return descDelimiter;
    }
    public void setDescDelimiter(String descDelimiter){
        this.descDelimiter = descDelimiter;
    }
    //endregion
    //region defBytesPerLine : int = 16
    protected int defBytesPerLine = 16;

    /**
     * Возвращает кол-во байт на линию по умолчанию
     * @return кол-во байт на линию по умолчанию
     */
    public int getDefBytesPerLine(){
        return defBytesPerLine;
    }

    /**
     * Указывает кол-во байт на линию по умолчанию
     * @param defBytesPerLine кол-во байт на линию по умолчанию
     */
    public void setDefBytesPerLine(int defBytesPerLine){
        this.defBytesPerLine = defBytesPerLine;
    }
    //endregion

    //region Decode / Decoded

    /**
     * Декодирование очередной порции байтов
     */
    public static class Decode {
        protected byte[] bytes;
        protected int offset;
        protected int length;
        protected int pointer;

        public Decode(byte[] bytes, int offset, int length, int pointer){
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
            this.pointer = pointer;
        }
        public byte[] getBytes(){ return bytes; }
        public int getOffset(){ return offset; }
        public int getLength(){ return length; }
        public int getPointer(){ return pointer; }

        /**
         * Просмотр байтов
         * @param len кол-во просматриваемых байтов
         * @return сегмент массива: массив, указатель, кол-во доступных байтов
         */
        public Optional<Tuple3<byte[],Integer,Integer>> lookupBytes(int len){
            if( len<1 )return Optional.empty();

            int available = length - (pointer - offset);
            if( available < len )return Optional.empty();

            return Optional.of( Tuple3.of(bytes, pointer, available) );
        }

        /**
         * Просмотр байтов
         * @param length кол-во просматриваемых байтов
         * @return массив байтов
         */
        public Optional<byte[]> lookup(int length){
            Optional<Tuple3<byte[],Integer,Integer>> opt = lookupBytes(length);
            return opt.map(
                integerIntegerTuple3 -> Arrays.copyOfRange(
                    integerIntegerTuple3.a(), integerIntegerTuple3.b(), integerIntegerTuple3.b()+length )
            );
        }

        /**
         * Формирование расшифровки
         * @param length кол-во расшифрованных байт
         * @param message описание
         * @return расшифровка
         */
        public Optional<Decoded> respone( int length, String message ){
            if( length<1 )throw new IllegalArgumentException( "length<1" );
            if( message==null )throw new IllegalArgumentException( "message==null" );
            return Optional.of(new Decoded(length, message));
        }
    }

    /**
     * Расшифровка набора байт
     */
    public static class Decoded {
        private final int length;
        private final String message;

        /**
         * Конструктор
         * @param length кол-во расшифрованных байт
         * @param message описание
         */
        public Decoded(int length, String message){
            this.length = length;
            this.message = message;
        }

        /**
         * Возвращает кол-во расшифрованных байт
         * @return кол-во байт
         */
        public int getLength(){
            return length;
        }

        /**
         * Возвращает описание
         * @return описание
         */
        public String getMessage(){
            return message;
        }
    }
    private static class DecodeMutable extends Decode {
        public DecodeMutable(byte[] bytes, int offset, int length, int pointer){
            super(bytes, offset, length, pointer);
        }

        public void setPointer( int ptr ){
            this.pointer = ptr;
        }
        public DecodeMutable pointer( int p ){
            setPointer(p);
            return this;
        }
    }
    //endregion

    //region preview : Function<Decode, Optional<Decoded>>
    protected Function<Decode, Optional<Decoded>> preview;

    /**
     * Указывает функцию дешифровки порции байт.
     *
     * <pre>
     * dump.setPreview( d -&gt; {
     *     if( d.getPointer()==2 ) return d.respone(2,"2 chars: "+new String(d.getBytes(),d.getPointer(),2,StandardCharsets.ISO_8859_1));
     *     if( d.getPointer()==4 ) return d.respone(4,"4 chars: "+new String(d.getBytes(),d.getPointer(),4,StandardCharsets.ISO_8859_1));
     *     return Optional.empty();
     * });
     * </pre>
     * @return функция дешифровки
     */
    public Function<Decode, Optional<Decoded>> getPreview(){
        return preview;
    }

    /**
     * Указывает функцию дешифровки порции байт
     * @param preview функция дешифровки
     */
    public void setPreview(Function<Decode, Optional<Decoded>> preview){
        this.preview = preview;
    }
    //endregion

    private static String align( int width, int i, int radix ){
        String s = Integer.toString( i, radix );
        if( s.length()<width ){
            return Text.repeat(" ",width-s.length())+s;
        }
        return s;
    }

    //region dump()
    /**
     * Дамп байтов
     * @param buff байт-массив
     * @return Дамп
     */
    public String dump( byte[] buff ){
        if( buff==null )throw new IllegalArgumentException( "buff==null" );
        return dump(buff,0,buff.length);
    }

    /**
     * Дамп байтов
     * @param buff байт-массив
     * @param off смещение в массиве
     * @param len кол-во байт
     * @return Дамп
     */
    public String dump( byte[] buff, int off, int len ){
        if( buff==null )throw new IllegalArgumentException( "buff==null" );
        if( off<0 )throw new IllegalArgumentException( "off<0" );
        if( len<0 )throw new IllegalArgumentException( "len<0" );
        if( off+len>buff.length )throw new IllegalArgumentException( "off+len>buff.length" );

        StringBuilder sb = new StringBuilder();
        boolean newLine = true;
        int perLine = 0;
        int maxPerLine = defBytesPerLine;
        int lineIdx = -1;
        String decodedCurrLine = null;

        DecodeMutable decodeMutable = new DecodeMutable(buff, off, len, off);

        if( offsetAlign>1 ){
            int n = off % offsetAlign;
            if( n>0 ){
                int off1 = off - n;
                sb.append(align(offsetWidth, off1, offsetRadix)).append(offsetDelimiter);
                for( int i=0;i<n;i++ ){
                    sb.append("   ");
                }
                perLine = n;
                newLine = false;

                if( preview!=null ){
                    Optional<Decoded> o = preview.apply(decodeMutable);
                    if( o.isPresent() ){
                        int lineLen = o.get().getLength();
                        if( lineLen>0 ){
                            maxPerLine = lineLen + perLine;
                            decodedCurrLine = o.get().getMessage();
                        }
                    }
                }
            }
        }

        for( int ptr = off; ptr<(off+len); ptr++ ) {
            if( newLine ) {
                sb.append(align(offsetWidth, ptr, offsetRadix)).append(offsetDelimiter);
                newLine = false;
                lineIdx++;

                maxPerLine = defBytesPerLine;
                decodedCurrLine = null;
                if( preview!=null ){
                    Optional<Decoded> o = preview.apply( decodeMutable.pointer(ptr) );
                    if( o.isPresent() ){
                        int lineLen = o.get().getLength();
                        if( lineLen>0 ){
                            maxPerLine = lineLen;
                            decodedCurrLine = o.get().getMessage();
                        }
                    }
                }
            }
            perLine++;

            int b = buff[ptr];
            int hval = (b & 0xF0) >> 4;
            int lval = (b & 0x0F);
            sb.append(Integer.toHexString(hval)).append(Integer.toHexString(lval));

            if( perLine>=maxPerLine ){
                int appends = defBytesPerLine - perLine;
                if( appends>0 ){
                    for( int i=0;i<appends;i++ ){
                        sb.append("   ");
                    }
                }

                if( decodedCurrLine!=null ){
                    sb.append(descDelimiter).append(decodedCurrLine);
                }

                sb.append("\n");
                newLine = true;
                perLine = 0;

                if( ptrAlign>1 ){
                    int n = (ptr+1) % ptrAlign;
                    if( n>0 ){
                        sb.append(align(offsetWidth, ptr+1, offsetRadix)).append(offsetDelimiter);
                        for( int i=0;i<n;i++ ){
                            sb.append("   ");
                        }

                        perLine = n;
                        maxPerLine = defBytesPerLine;
                        decodedCurrLine = null;
                        if( preview!=null && ((ptr+1)<len) ){
                            Optional<Decoded> o = preview.apply(decodeMutable.pointer(ptr+1));
                            if( o.isPresent() ){
                                int lineLen = o.get().getLength();
                                if( lineLen>0 ){
                                    maxPerLine = lineLen + perLine;
                                    decodedCurrLine = o.get().getMessage();
                                }
                            }
                        }

                        newLine = false;
                        lineIdx++;
                    }
                }
            }else{
                sb.append(" ");
            }
        }

        return sb.toString();
    }
    //endregion

    /**
     * "Строитель"
     */
    public static class Builder {
        private final Map<Integer,Tuple2<Integer,Function<byte[],String>>> absoluteDecoded = new HashMap<>();
        private final Map<Integer,Tuple2<Integer,Function<byte[],String>>> relativeDecoded = new HashMap<>();

        public Function<Decode,Optional<Decoded>> preview(){
            return decode -> {
                Tuple2<Integer,Function<byte[],String>> abs = absoluteDecoded.get(decode.getPointer());
                if( abs!=null ){
                    int len = abs.a();
                    Function<byte[],String> fun = abs.b();
                    if( len>0 ){
                        Optional<byte[]> bytes = decode.lookup(len);
                        if( bytes.isPresent() ){
                            return decode.respone(len, fun.apply(bytes.get()));
                        }
                    }
                }

                int off = decode.getPointer() - decode.getOffset();
                Tuple2<Integer,Function<byte[],String>> rel = relativeDecoded.get(off);
                if( rel!=null ){
                    int len = rel.a();
                    Function<byte[],String> fun = rel.b();
                    if( len>0 ){
                        Optional<byte[]> bytes = decode.lookup(len);
                        if( bytes.isPresent() ){
                            return decode.respone(len, fun.apply(bytes.get()));
                        }
                    }
                }

                return Optional.empty();
            };
        }

        /**
         * Описание структуры байт-массива
         */
        public static class Decoder {
            public final Builder builder;
            public final Map<Integer,Tuple2<Integer,Function<byte[],String>>> map;

            public Decoder( Builder builder, Map<Integer,Tuple2<Integer,Function<byte[],String>>> map){
                if( map==null )throw new IllegalArgumentException( "map==null" );
                if( builder==null )throw new IllegalArgumentException( "builder==null" );
                this.map = map;
                this.builder = builder;
            }

            public NextDecoder name( int off, int len, String name ){
                if( off<0 )throw new IllegalArgumentException( "off<0" );
                if( len<1 )throw new IllegalArgumentException( "len<1" );

                map.put(off, Tuple2.of(len, b -> name));
                return new NextDecoder( builder, map, off, len );
            }

            public NextDecoder decode( int off, int len, Function<byte[],String> decoder ){
                if( off<0 )throw new IllegalArgumentException( "off<0" );
                if( len<1 )throw new IllegalArgumentException( "len<1" );

                map.put(off, Tuple2.of(len, decoder));
                return new NextDecoder( builder, map, off, len );
            }
        }

        /**
         * Описание структуры байт-массива
         */
        public static class NextDecoder extends Decoder {
            public final int baseOffset;
            public final int baseLen;

            public NextDecoder(
                Builder builder,
                Map<Integer, Tuple2<Integer, Function<byte[], String>>> map,
                int baseOffset, int baseLen
            ){
                super(builder, map);
                this.baseOffset = baseOffset;
                this.baseLen = baseLen;
            }

            public NextDecoder name( int len, String name ){
                if( len<1 )throw new IllegalArgumentException( "len<1" );

                int off = baseOffset + baseLen;
                map.put(off, Tuple2.of(len, b -> name));

                return new NextDecoder( builder, map, off, len );
            }

            public NextDecoder decode( int len, Function<byte[],String> decoder ){
                if( len<1 )throw new IllegalArgumentException( "len<1" );

                int off = baseOffset + baseLen;
                map.put(off, Tuple2.of(len, decoder));

                return new NextDecoder( builder, map, off, len );
            }

            public NextDecoder byteValue(){
                return byteValue(null);
            }

            public NextDecoder byteValue( Function<Integer,String> toString ){
                return decode( 1, bytes -> {
                    return toString!=null ? toString.apply((int)bytes[0]) : Integer.toString((int)bytes[0]);
                });
            }

            public NextDecoder longValue(){
                return longValue( true, null );
            }

            public NextDecoder longValue( Function<Long,String> toString ){
                return longValue( true, toString );
            }

            public NextDecoder longValue( boolean BE, Function<Long,String> toString ){
                return decode( 8, bytes -> {
                    int off = 0;
                    int s = 1;

                    if( !BE ){
                        off = 7;
                        s = -1;
                    }

                    long v = 0;
                    v = v | ((long) (bytes[off] & 0xFF) << 8 * 7); off += s;
                    v = v | ((long) (bytes[off] & 0xFF) << 8 * 6); off += s;
                    v = v | ((long) (bytes[off] & 0xFF) << 8 * 5); off += s;
                    v = v | ((long) (bytes[off] & 0xFF) << 8 * 4); off += s;
                    v = v | ((long) (bytes[off] & 0xFF) << 8 * 3); off += s;
                    v = v | ((long) (bytes[off] & 0xFF) << 8 * 2); off += s;
                    v = v | ((long) (bytes[off] & 0xFF) << 8);     off += s;
                    v = v | ((long) (bytes[off] & 0xFF));          off += s;

                    return
                        toString!=null ? toString.apply(v) : Long.toString(v);
                });
            }

            public NextDecoder intValue(){
                return intValue( true, null );
            }

            public NextDecoder intValue( Function<Integer,String> toString ){
                return intValue( true, toString );
            }

            public NextDecoder intValue( boolean BE, Function<Integer,String> toString ){
                return decode( 8, bytes -> {
                    int off = 0;
                    int s = 1;

                    if( !BE ){
                        off = 3;
                        s = -1;
                    }

                    int v = 0;
                    v = v | ((int) (bytes[off] & 0xFF) << 8 * 3); off += s;
                    v = v | ((int) (bytes[off] & 0xFF) << 8 * 2); off += s;
                    v = v | ((int) (bytes[off] & 0xFF) << 8);     off += s;
                    v = v | ((int) (bytes[off] & 0xFF));          off += s;

                    return
                        toString!=null ? toString.apply(v) : Integer.toString(v);
                });
            }

            public NextDecoder shortValue(){
                return shortValue( true, null );
            }

            public NextDecoder shortValue( Function<Integer,String> toString ){
                return shortValue( true, toString );
            }

            public NextDecoder shortValue( boolean BE, Function<Integer,String> toString ){
                return decode( 8, bytes -> {
                    int off = 0;
                    int s = 1;

                    if( !BE ){
                        off = 1;
                        s = -1;
                    }

                    int v = 0;
                    v = v | ((int) (bytes[off] & 0xFF) << 8);     off += s;
                    v = v | ((int) (bytes[off] & 0xFF));          off += s;

                    return
                        toString!=null ? toString.apply(v) : Integer.toString(v);
                });
            }
        }

        /**
         * Описание структуры байтов используя абсолютное смещение в массиве байтов
         * @param decoder декодер для описания структуры
         * @return SELF ссылка
         */
        public Builder absolute(Consumer<NextDecoder> decoder){
            if( decoder==null )throw new IllegalArgumentException( "decoder==null" );
            decoder.accept( new NextDecoder(this,absoluteDecoded,0,0) );
            return this;
        }

        /**
         * Описание структуры байтов используя относительное смещение в массиве байтов
         * - относительно начала отображения
         * @param decoder декодер для описания структуры
         * @return SELF ссылка
         */
        public Builder relative(Consumer<NextDecoder> decoder){
            if( decoder==null )throw new IllegalArgumentException( "decoder==null" );
            decoder.accept( new NextDecoder(this,relativeDecoded,0,0) );
            return this;
        }

        /**
         * Создание "дампера"
         * @return "дампер"
         */
        public BytesDump build(){
            BytesDump dump = new BytesDump();
            dump.setPreview(preview());
            return dump;
        }
    }
}
