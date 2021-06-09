package xyz.cofe.udp.atom;

/**
 * Заголовок блока данных
 */
public class BlockHead {
    private int messageSize;

    /**
     * Возвращает размер всего сообщения
     * @return Размер сообщения
     */
    public int messageSize(){ return messageSize; }

    /**
     * Указывает размер всего сообщения
     * @param v Размер сообщения
     * @return SELF ссылка
     */
    public BlockHead messageSize(int v){ messageSize=v; return this; }

    private long adler32;

    /**
     * Возвращает контрольную сумму данных
     * @return контрольная сумма
     */
    public long adler32(){ return adler32; }

    /**
     * Указывает контрольную сумму данных
     * @param v контрольная сумма
     * @return SELF ссылка
     */
    public BlockHead adler32(long v){
        adler32 = v;
        return this;
    }

    public static final int HEAD_SIZE_MIN = 8;

    /**
     * Вычисление размера заголовка
     * @return размер заголовка
     */
    public int computeSize(){
        return 8;
    }

    /**
     * Представление заголовка массивом байт
     * @return массив байт
     */
    public byte[] toBytes(){
        byte[] bytes = new byte[HEAD_SIZE_MIN];

        bytes[3] = (byte)(adler32 & 0xFF);
        bytes[2] = (byte)((adler32 >> 8) & 0xFF);
        bytes[1] = (byte)((adler32 >> 8*2) & 0xFF);
        bytes[0] = (byte)((adler32 >> 8*3) & 0xFF);

        bytes[7] = (byte)(messageSize & 0xFF);
        bytes[6] = (byte)((messageSize >> 8) & 0xFF);
        bytes[5] = (byte)((messageSize >> 8*2) & 0xFF);
        bytes[4] = (byte)((messageSize >> 8*3) & 0xFF);

        return bytes;
    }

    /**
     * Чтение заголовка из массива байт
     * @param head массив байт
     * @return заголовок
     */
    public static BlockHead fromBytes(byte[] head){
        if( head==null )throw new IllegalArgumentException( "head==null" );
        if( head.length< HEAD_SIZE_MIN)return null;

        BlockHead bh = new BlockHead();

        long adlr32 = 0;
        adlr32 = adlr32 | ( head[3] & 0xFF);
        adlr32 = adlr32 | ((long)(head[2] & 0xFF) << 8);
        adlr32 = adlr32 | ((long)(head[1] & 0xFF) << 8*2);
        adlr32 = adlr32 | ((long)(head[0] & 0xFF) << 8*3);
        bh.adler32 = adlr32;

        int msize = 0;
        msize = msize | ( head[7] & 0xFF);
        msize = msize | ((head[6] & 0xFF) << 8);
        msize = msize | ((head[5] & 0xFF) << 8*2);
        msize = msize | ((head[4] & 0xFF) << 8*3);
        bh.messageSize = msize;

        return bh;
    }
}
