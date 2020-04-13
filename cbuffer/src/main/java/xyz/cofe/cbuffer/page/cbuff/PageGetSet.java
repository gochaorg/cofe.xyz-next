package xyz.cofe.cbuffer.page.cbuff;

import xyz.cofe.cbuffer.page.GetPageSize;

import java.io.ByteArrayOutputStream;


public interface PageGetSet extends RWPageData, ToPageAddress, GetPageSize {
    /**
     * Чтение данных
     * @param offset смещение от 0
     * @param dataLen кол-во данных
     * @return данные
     */
    default byte[] get(long offset,int dataLen){
        if( offset<0 ) throw new IllegalArgumentException("offset<0");
        if( dataLen<0 ) throw new IllegalArgumentException("dataLen<0");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        int pageSize = getPageSize();
        byte[] buff = new byte[pageSize];
        long off = offset;

        // читаем пока данных не накопиться нужное кол-во
        while( bytes.size()<dataLen ){
            int[] paddr = toPageAddress(off);
            if( paddr==null ){
                //недопустимый адрес
                //TODO notify
                return bytes.toByteArray();
            }

            // размер запрашиваемой порции данных
            int chunckSize = Math.max(buff.length, dataLen-bytes.size());
            int readed = readPageData(buff, 0, paddr, chunckSize);
            if( readed>0 ){
                bytes.write(buff, 0, readed);
            } else if( readed<=0 ){
                //TODO notify
                //данные закочились неожиданно
                return bytes.toByteArray();
            }
            off += readed;
        }

        return bytes.toByteArray();
    }

    /**
     * Запись данных
     * @param offset смещение
     * @param data буфер
     * @param dataOffset смещение в буфере
     * @param dataLen кол-во байт в буфере
     */
    default void set(long offset,byte[] data, int dataOffset, int dataLen){
        if( offset<0 ) throw new IllegalArgumentException("offset<0");
        if( dataOffset<0 ) throw new IllegalArgumentException("dataOffset<0");
        if( data==null ) throw new IllegalArgumentException("data==null");
        if( dataLen<0 ) throw new IllegalArgumentException("dataLen<0");
        if( dataLen==0 )return;

        int dataEndOffset = dataOffset + dataLen;
        if( dataEndOffset>data.length )throw new IllegalArgumentException("dataOffset out of bounds");

        // Размер страницы
        int pageSize = getPageSize();

        long off = offset;
        int[] paddr = toPageAddress(off);
        
    }
}
