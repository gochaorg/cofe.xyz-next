package xyz.cofe.cbuffer.page.cbuff;

import xyz.cofe.cbuffer.page.GetPageSize;

public interface ToOffset extends GetPageSize {
    /**
     * Преобразование страничного адреса в глобальный адрес
     * @param pageAddress страничный адрес ([0] - индекс страницы, [1] - смещение в странице)
     * @return глобальный адрес или -1
     */
    default long toOffset(int[] pageAddress){
        if( pageAddress==null ) throw new IllegalArgumentException("pageAddress==null");
        if( pageAddress.length<2 ) throw new IllegalArgumentException("pageAddress.length<2");
        int pi = pageAddress[0];
        int po = pageAddress[1];
        int ps = getPageSize();
        if( po>=ps )return -1;
        if( ps<1 )return -1;
        return ((long)pi) * ((long)ps) + ((long)po);
    }
}
