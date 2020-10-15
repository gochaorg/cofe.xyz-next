package xyz.cofe.mapitf;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.BiFunction;

public class CompileResult {
    public CompileResult( BiFunction<Object,Object[],@Nullable Object> fn ){
        if( fn==null )throw new IllegalArgumentException("fn==null");
        this.fn = fn;
    }

    public final BiFunction<Object,Object[],@Nullable Object> fn;
}
