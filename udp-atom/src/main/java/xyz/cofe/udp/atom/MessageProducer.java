package xyz.cofe.udp.atom;

import java.util.function.Consumer;
import java.util.zip.Adler32;

/**
 * Запись сообщения в поток байтов
 */
public class MessageProducer {
    /**
     * Конструктор
     * @param writer функция записи потока байт
     */
    public MessageProducer(Consumer<byte[]> writer){
        if( writer==null )throw new IllegalArgumentException( "writer==null" );
        this.writer = writer;
    }

    /**
     * Конструктор по умолчанию
     */
    public MessageProducer(){
    }

    private volatile Consumer<byte[]> writer;

    /**
     * Возвращает функция записи потока байт
     * @return функция записи
     */
    public Consumer<byte[]> writer(){ return writer; }

    /**
     * Указывает функция записи потока байт
     * @param writer функция записи
     * @return SELF ссылка
     */
    public MessageProducer writer(Consumer<byte[]> writer){
        this.writer = writer;
        return this;
    }

    /**
     * Минимальный размер блока
     */
    public static final int MIN_BLOCK_SIZE = BlockHead.HEAD_SIZE_MIN + BlockCodec.BLOCK_MIN_SIZE;

    private volatile int blockSize = 1490;

    /**
     * Указывает размер максимальный блока
     * @param size максимальный размер блока, не может быть меньше MIN_BLOCK_SIZE
     * @return SELF ссылка
     * @see #MIN_BLOCK_SIZE
     */
    public synchronized MessageProducer blockSize(int size){
        if( size<MIN_BLOCK_SIZE )throw new IllegalArgumentException( "size<MIN_BLOCK_SIZE(="+MIN_BLOCK_SIZE+")" );
        this.blockSize = size;
        return this;
    }

    /**
     * Возвращает размер максимальный блока
     * @return максимальный размер блока
     */
    public int blockSize(){
        return blockSize;
    }

    private volatile int messageId = 0;

    /**
     * Запись данных
     * @param buff буфер
     * @param off смещение в буфере
     * @param len размер данных
     */
    public synchronized void write(byte[] buff, int off, int len){
        write(buff,off,len,null);
    }

    /**
     * Создание заголовка блока
     * @return заголовок блока
     */
    protected BlockHead createBlockHead(){
        return new BlockHead();
    }

    /**
     * Запись данных
     * @param buff буфер
     * @param off смещение в буфере
     * @param len размер данных
     * @param headConf настройка заголовка, или null
     */
    public synchronized void write(byte[] buff, int off, int len, Consumer<BlockHead> headConf){
        if( buff==null )throw new IllegalArgumentException( "buff==null" );
        if( off<0 )throw new IllegalArgumentException( "off<0" );
        if( len<0 )throw new IllegalArgumentException( "len<0" );
        if( off+len>buff.length )throw new IllegalArgumentException( "off+len>buff.length" );

        Consumer<byte[]> writer = this.writer;
        if( writer==null )throw new IllegalStateException("writer==null");

        Adler32 adler32 = new Adler32();

        int msg = messageId;
        messageId++;

        int sent = 0;
        int blockId = 0;
        while (sent < len){
            int avail = len - sent;
            BlockHead bh = createBlockHead().messageSize(len);

            if( headConf!=null ){
                headConf.accept(bh);
            }

            int sendSize = Math.min(avail, blockSize -  BlockCodec.BLOCK_MIN_SIZE - bh.computeSize());
            int ptr = off + sent;

            //boolean lastBlock = (sent + sendSize) >= len;
            //bh.last(lastBlock);

            adler32.reset();
            adler32.update(buff,ptr,sendSize);

            long sum = adler32.getValue();
            bh.adler32(sum);

            byte[] data = encode(buff, ptr, sendSize, msg, blockId, bh);

            Consumer<SendBlock> ls = onSend;
            if( ls!=null ) {
                sendBlock.messageId = msg;
                sendBlock.blockId = blockId;
                //sendBlock.lastBlock = lastBlock;
                sendBlock.send = data;
                sendBlock.buff = buff;
                sendBlock.off = off;
                sendBlock.len = len;
                sendBlock.adlr32 = sum;
                sendBlock.ptr = ptr;
                sendBlock.sendSize = sendSize;
                ls.accept(sendBlock);
            }

            blockId++;
            writer.accept(data);
            sent += sendSize;
        }
    }

    /**
     * Кодирование байт блока
     * @param buff     буфер с данными
     * @param off      указатель/смещение в буфере
     * @param len размер данных
     * @param msgId      ид. сообщения
     * @param blockId  ид. блока
     * @param bh       заголовок блока
     * @return байты
     */
    protected byte[] encode(byte[] buff, int off, int len, int msgId, int blockId, BlockHead bh){
        return BlockCodec.encode(buff, off, len, msgId, blockId, bh.toBytes());
    }

    private SendBlock sendBlock = new SendBlock();

    public static class SendBlock {
        public int messageId;
        public int blockId;
        //public boolean lastBlock;
        public byte[] send;
        public byte[] buff;
        public int off;
        public int len;
        public long adlr32;
        public int sendSize;
        public int ptr;
    }

    private volatile Consumer<SendBlock> onSend = null;
    public MessageProducer onSend( Consumer<SendBlock> ls ){
        if( ls==null )throw new IllegalArgumentException( "ls==null" );
        onSend = ls;
        return this;
    }

    /**
     * Запись байтов в поток
     * @param buff байты
     */
    public void write(byte[] buff){
        if( buff==null )throw new IllegalArgumentException( "buff==null" );
        write(buff, null);
    }

    /**
     * Запись байтов в поток
     * @param buff байты
     * @param headConf конфигурация заголовка блока
     */
    public void write(byte[] buff, Consumer<BlockHead> headConf){
        if( buff==null )throw new IllegalArgumentException( "buff==null" );
        write( buff, 0, buff.length, headConf);
    }
}
