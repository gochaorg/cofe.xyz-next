package xyz.cofe.udp.atom;

import java.util.Arrays;
import java.util.Optional;
import java.util.zip.Adler32;

/**
 * Блок данных, часть сообщения
 */
public class Block {
    //region messageId : int - идентификатор сообщения
    private int messageId;

    /**
     * Возвращает идентификатор сообщения
     * @return идентификатор сообщения
     */
    public int getMessageId() {
        return messageId;
    }

    /**
     * Указывает идентификатор сообщения
     * @param messageId идентификатор сообщения
     */
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    //endregion
    //region blockId : int - идентификатор блока данных в сообщении
    private int blockId;

    /**
     * Возвращает идентификатор блока данных в сообщении
     * @return идентификатор блока данных в сообщении
     */
    public int getBlockId() {
        return blockId;
    }

    /**
     * Указывает идентификатор блока данных в сообщении
     * @param blockId идентификатор блока данных в сообщении
     */
    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
    //endregion
    //region headSize : int - размер заголовка
    private int headSize;

    /**
     * Возвращает размер заголовка
     * @return размер заголовка
     */
    public int getHeadSize() {
        return headSize;
    }

    /**
     * Указывает размер заголовка
     * @param headSize размер заголовка
     */
    public void setHeadSize(int headSize) {
        this.headSize = headSize;
    }
    //endregion
    //region dataSize : int - размер данных
    private int dataSize;

    /**
     * Возвращает размер данных
     * @return размер данных
     */
    public int getDataSize() {
        return dataSize;
    }

    /**
     * Указывает размер данных
     * @param dataSize размер данных
     */
    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
    //endregion
    //region blockSize : int
    private int blockSize;

    /**
     * Возвращает размер блока
     * @return размер блока
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Указывает размер блока
     * @param blockSize
     */
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }
    //endregion

    //region head : byte[]
    private byte[] headBytes;

    /**
     * Возвращает заголовок
     * @return заголовок
     */
    public byte[] getHeadBytes() {
        return headBytes;
    }

    /**
     * Указывает заголовок
     * @param headBytes заголовок
     */
    public void setHeadBytes(byte[] headBytes) {
        this.headBytes = headBytes;
    }
    //endregion
    //region head : BlockHead
    private volatile BlockHead head;

    /**
     * Возвращает заголовок
     * @return заголовок или null
     */
    public BlockHead getHead(){
        if( head!=null )return head;
        synchronized (this) {
            if( head!=null )return head;

            byte[] hbytes = headBytes;
            if( hbytes==null ){
                System.out.println("head null");
                return null;
            }

            head = BlockHead.fromBytes(hbytes);
            return head;
        }
    }
    //endregion

    //region data : byte[]
    private byte[] data;

    /**
     * Возвращает данные
     * @return данные
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Указывает данные
     * @param data данные
     */
    public void setData(byte[] data) {
        this.data = data;
    }
    //endregion

    //region valid data

    /**
     * Проверка контрольной суммы данных
     * @param adler32 функция проверки, возможно null
     * @return true - данные целые
     */
    public boolean dataValid( Adler32 adler32 ){
        if( dataSize==0 )return true;

        if( adler32==null ){
            adler32 = new Adler32();
        }

        adler32.reset();

        BlockHead bh = getHead();
        if( bh==null )return false;
        if( data==null )return false;
        if( data.length < dataSize )return false;

        adler32.update(data,0,dataSize);
        long sum = adler32.getValue();
        boolean matched = bh.adler32()==sum;
        return matched;
    }

    /**
     * Проверка контрольной суммы данных
     * @return true - данные целые
     */
    public boolean dataValid(){
        return dataValid(null);
    }

    /**
     * Чтение данных
     * @return данные
     */
    public Optional<byte[]> read(){
        if( data==null )return Optional.empty();
        if( data.length < dataSize )return Optional.empty();
        if( data.length == dataSize )return Optional.of(data);
        if( data.length > dataSize && dataSize>=0 ){
            byte[] d = Arrays.copyOf(data,dataSize);
            return Optional.of(d);
        }
        return Optional.empty();
    }
    //endregion
}
