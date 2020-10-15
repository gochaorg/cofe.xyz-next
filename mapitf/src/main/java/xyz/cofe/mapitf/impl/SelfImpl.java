package xyz.cofe.mapitf.impl;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.cofe.mapitf.CompileCtx;
import xyz.cofe.mapitf.CompileResult;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.BiFunction;

public class SelfImpl {
    @SuppressWarnings("ConstantConditions")
    public static Optional<CompileResult> compileSelfInterfaces(
            @NonNull CompileCtx ctx
    ){
        if( ctx==null )throw new IllegalArgumentException("ctx==null");
        Type[] params = ctx.method.getGenericParameterTypes();

        if( params.length != 0 )return Optional.empty();
        if( Void.class.equals(ctx.method.getReturnType()) )return Optional.empty();

        if( !ctx.handler.isSelfInterface(ctx.method.getGenericReturnType()) )return Optional.empty();

        BiFunction<Object,Object[],@Nullable Object> fn = (fp, fa)->fp;
        return Optional.of(new CompileResult(fn));
    }
}
