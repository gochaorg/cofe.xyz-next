package xyz.cofe.mapitf;

import java.lang.reflect.Type;

public interface GetProxyInterfaces {
    Class<?>[] getProxyInterfaces();
    default boolean isSelfInterface( Type expected ){
        if( expected == null ) throw new IllegalArgumentException("expected==null");
        for( Class<?> c : getProxyInterfaces() ){
            if( c.equals(expected) ) return true;
        }
        return false;
    }
}
