package xyz.cofe.udp.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.text.BytesDump;

/**
 * Кодирование и декодирование блока данных.
 *
 * <p>
 * Структура блока
 *
 * <pre>
 * порядок байтов -  little-endian - от младшего к старшему
 *
 * Смещение      Размер     Описание
 * ----------------------------------
 * 0             4          messageId,
 * 4             4          blockId,
 * 8             2          headerSize,
 * 10            2          dataSize
 * 12            headerSize данные заголовка
 * 12+headerSize dataSize   данные
 * </pre>
 */
public class BlockCodec {
    private static final Logger log = LoggerFactory.getLogger(BlockCodec.class);

    /**
     * Минимальный размер блока
     */
    public static final int BLOCK_MIN_SIZE = 12;

    /**
     * Максимальный размер блока
     */
    public static final int MAX_DATA_SIZE = 65536;

    /**
     * Кодирует блок данных
     * @param buff данные
     * @param off смещение в массиве buff
     * @param len размер данных
     * @param messageId номер сообщения
     * @param blockId номер блока сообщения
     * @param head заголовок или null
     * @return блок данных
     * @see BlockCodec
     */
    public static byte[] encode(byte[] buff,int off,int len, int messageId, int blockId, byte[] head) {
        if( buff==null )throw new IllegalArgumentException( "buff==null" );
        if( off<0 )throw new IllegalArgumentException( "off<0" );
        if( len<0 )throw new IllegalArgumentException( "len<0" );
        if( len>MAX_DATA_SIZE )throw new IllegalArgumentException( "len>MAX_DATA_SIZE(="+MAX_DATA_SIZE+")" );
        if( off+len>buff.length )throw new IllegalArgumentException( "off+len>buff.length" );

        int headerSize = 0;

        headerSize += 4; // messageId
        headerSize += 4; // blockId
        headerSize += 4; // header size, buff.len
        if( head !=null )headerSize += head.length;

        byte[] bres = new byte[headerSize + len];
        if( len>0 ) {
            System.arraycopy(buff, off, bres, headerSize, len);
        }

        bres[0] = (byte)(messageId & 0xFF);
        bres[1] = (byte)((messageId >> 8) & 0xFF);
        bres[2] = (byte)((messageId >> 16) & 0xFF);
        bres[3] = (byte)((messageId >> 24) & 0xFF);

        bres[4] = (byte)(blockId & 0xFF);
        bres[5] = (byte)((blockId >> 8) & 0xFF);
        bres[6] = (byte)((blockId >> 16) & 0xFF);
        bres[7] = (byte)((blockId >> 24) & 0xFF);

        bres[8] = (byte)((headerSize) & 0xFF);
        bres[9] = (byte)((headerSize >> 8) & 0xFF);

        bres[10] = (byte)((len) & 0xFF);
        bres[11] = (byte)((len >> 8) & 0xFF);

        if( head !=null && head.length>0 ){
            System.arraycopy(head,0,bres,12, head.length);
        }

        return bres;
    }

    /**
     * Декодирует блок
     * @param buff буфер
     * @param off смещение в буфере
     * @param len размер данных в буфере
     * @return блок
     * @see BlockCodec
     */
    public static Block decode(byte[] buff, int off, int len){
        if( buff==null )throw new IllegalArgumentException( "buff==null" );
        if( off<0 )throw new IllegalArgumentException( "off<0" );
        if( len<0 )throw new IllegalArgumentException( "len<0" );
        //if( len>65536 )throw new IllegalArgumentException( "len>65536" );
        if( off+len>buff.length )throw new IllegalArgumentException( "off+len>buff.length" );

        if( len<BLOCK_MIN_SIZE )throw new IllegalArgumentException( "len<BLOCK_MIN_SIZE(="+BLOCK_MIN_SIZE+")" );

        Block block = new Block();
        block.setBlockSize(len);

        int msgId = 0;
        msgId = msgId | ( buff[off  ] & 0xFF);
        msgId = msgId | ((buff[off+1] & 0xFF) << 8);
        msgId = msgId | ((buff[off+2] & 0xFF) << 16);
        msgId = msgId | ((buff[off+3] & 0xFF) << 24);
        block.setMessageId(msgId);

        int blkId = 0;
        blkId = blkId | ( buff[off+4] & 0xFF);
        blkId = blkId | ((buff[off+5] & 0xFF) << 8);
        blkId = blkId | ((buff[off+6] & 0xFF) << 16);
        blkId = blkId | ((buff[off+7] & 0xFF) << 24);
        block.setBlockId(blkId);

        int hsize = 0;
        hsize = hsize | ( buff[off+8] & 0xFF);
        hsize = hsize | ((buff[off+9] & 0xFF) << 8);
        block.setHeadSize(hsize);

        int dsize = 0;
        dsize = dsize | ( buff[off+10] & 0xFF);
        dsize = dsize | ((buff[off+11] & 0xFF) << 8);
        block.setDataSize(dsize);

        boolean hsizeScc = false;

        if( hsize>12 && hsize<=len ){
            byte[] head = new byte[hsize-BLOCK_MIN_SIZE];
            System.arraycopy(buff,off+BLOCK_MIN_SIZE,head,0,head.length);
            block.setHeadBytes(head);
            hsizeScc = true;
        } else {
            if( hsize==0 )hsizeScc = true;
            block.setHeadBytes(new byte[0]);

            //System.out.println(BytesDump.dump(buff,off,len,4,4,2,2,1,4,4));

            final int f_msgId = msgId;
            final int f_blkId = blkId;
            final int f_hsize = hsize;
            final int f_dsize = dsize;

            log.debug("bad block {}",
            new BytesDump.Builder()
                .relative( decoder -> {
                    decoder
                        .name(4, "msgId "+f_msgId)
                        .name(4, "blkId "+f_blkId)
                        .name(2, "hsize "+f_hsize)
                        .name(2, "dsize "+f_dsize)
                    ;
                })
                .build()
                .dump(buff,off,len));
        }

        if( dsize>0 && dsize<(len-BLOCK_MIN_SIZE) ){
            byte[] data = new byte[dsize];
            if( hsizeScc ) {
                System.arraycopy(buff, hsize + off, data, 0, dsize);
            } else {
                System.arraycopy(buff, BLOCK_MIN_SIZE, data, 0, dsize);
            }
            block.setData(data);
        } else {
            block.setData(new byte[0]);
        }

        return block;
    }
}
