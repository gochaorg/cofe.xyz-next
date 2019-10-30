package xyz.cofe.cbuffer.page.cbuff;

import xyz.cofe.cbuffer.ContentBuffer;

public interface RWPageData {
    /**
     * Чтение данных страницы в буфер
     * @param buff буфер в который происходит чтение
     * @param buffOffset смещение в буфере
     * @param pageAddress страничный адрес
     * @param len кол-во запрашиваемых данных
     * @return кол-во прочитанных данных
     */
    int readPageData(byte[] buff,int buffOffset, int[] pageAddress,int len);

    /**
     * Запись данных в страницу из буфера
     * @param pageAddress страничный адрес
     * @param buff буфер
     * @param buffOffset смещение буфере
     */
    void writePageData(int[] pageAddress,byte[] buff,int buffOffset);
}
