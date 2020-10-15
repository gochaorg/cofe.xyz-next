package xyz.cofe.mapitf.impl;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.cofe.mapitf.*;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public class CtorImpl {
    @SuppressWarnings("ConstantConditions")
    public static Optional<CompileResult> compileCtor(
            @NonNull CompileCtx ctx,
            @NonNull PropertyMapper propertyMapper
    ){
        if( ctx==null )throw new IllegalArgumentException("ctx==null");
        if( propertyMapper==null )throw new IllegalArgumentException("propertyMapper==null");
        Type[] params = ctx.method.getGenericParameterTypes();

        if( params.length != 0 )return Optional.empty();
        if( Void.class.equals(ctx.method.getReturnType()) )return Optional.empty();

        Ctor ctor = ctx.method.getAnnotation(Ctor.class);
        if( ctor==null )return Optional.empty();

        BiFunction<Object,Object[],@Nullable Object> fn = (fp,fargs)->{
            Map<String,@Nullable Object> m = new LinkedHashMap<>();
            return Mapper.bind(m, ctx.method.getReturnType(), propertyMapper);
        };

        return Optional.of(new CompileResult(fn));
    }
}
