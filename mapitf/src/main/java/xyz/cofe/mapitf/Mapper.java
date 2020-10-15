package xyz.cofe.mapitf;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Proxy;
import java.util.Map;

public class Mapper {
    @NonNull
    @SuppressWarnings({"nullness", "unchecked", "ConstantConditions"})
    public static <T> T bind(Map<String,@Nullable Object> vals, Class<T> itf ){
        if( vals==null )throw new IllegalArgumentException("vals==null");
        if( itf==null )throw new IllegalArgumentException("itf==null");
        T ref = (T)Proxy.newProxyInstance(itf.getClassLoader(),new Class<?>[]{itf}, new DataProxy(vals,itf));
        if( ref==null )throw new Error("Bug");
        return ref;
    }

    @NonNull
    @SuppressWarnings({"nullness", "unchecked", "ConstantConditions"})
    public static <T> T bind(Map<String,@Nullable Object> vals, Class<T> itf, @NonNull PropertyMapper propertyMapper ){
        if( vals==null )throw new IllegalArgumentException("vals==null");
        if( itf==null )throw new IllegalArgumentException("itf==null");
        if( propertyMapper==null )throw new IllegalArgumentException("propertyMapper==null");

        T ref = (T)Proxy.newProxyInstance(itf.getClassLoader(),new Class<?>[]{itf}, new DataProxy(vals,propertyMapper,itf));
        if( ref==null )throw new Error("Bug");
        return ref;
    }
}
