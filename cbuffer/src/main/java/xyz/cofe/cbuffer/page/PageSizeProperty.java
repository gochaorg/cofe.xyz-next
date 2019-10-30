package xyz.cofe.cbuffer.page;

public interface PageSizeProperty extends GetPageSize {
    @Override
    default int getPageSize(){ return PageSizePropertyHolder.get(this); }

    default void setPageSize(int size){
        PageSizePropertyHolder.set(this, size);
    }
}
