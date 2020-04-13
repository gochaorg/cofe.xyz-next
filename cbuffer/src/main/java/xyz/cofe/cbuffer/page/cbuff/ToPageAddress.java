package xyz.cofe.cbuffer.page.cbuff;

import xyz.cofe.cbuffer.page.GetPageSize;

public interface ToPageAddress extends GetPageSize {
    /**
     * Преобразование глобального адреса, в адресацию страниц
     * @param off глобальный адрес, от 0 и больше
     * @return
     * result[0] - индекс страницы, <br>
     * result[1] - смещение в странице <br>
     * или null - преобразование не возможно (например глобальное смещение меньше 0)
     */
    default int[] toPageAddress(long off){
        if( off<0 )return null;

        int ps = getPageSize();
        if( ps<1 ){
            //TODO notify
            return null;
        }

        if( ps==1 ){
            if( off>Integer.MAX_VALUE ){
                //TODO notify
                return null;
            }
            return new int[]{ ((Long)off).intValue(), 0 };
        }

        long pi = off / ps;
        if( pi>Integer.MAX_VALUE )return null; //TODO notify

        long po = off % ps;
        if( po>Integer.MAX_VALUE )return null; //TODO notify

        return new int[]{ ((Long)pi).intValue(), ((Long)po).intValue() };
    }
}
