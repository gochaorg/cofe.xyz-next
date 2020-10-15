package xyz.cofe.mapitf.impl;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.cofe.mapitf.CompileCtx;
import xyz.cofe.mapitf.CompileResult;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@SuppressWarnings("ConstantConditions")
public class ObjImpl {
    public static Optional<CompileResult> compileEquals(
        @NonNull CompileCtx ctx
    ){
        if( ctx==null )throw new IllegalArgumentException("ctx==null");
        if( ctx.method.getDeclaringClass()==Object.class ){
            if( ctx.method.getName().equals("equals") && ctx.args!=null && ctx.args.length==1 ){
                BiFunction<Object,Object[],@Nullable Object> fn = (fp, fargs) -> Objects.equals(fp, fargs[0]);
                return Optional.of(new CompileResult(fn));
            }
        }
        return Optional.empty();
    }

    public static Optional<CompileResult> compileHash(
        @NonNull CompileCtx ctx
    ){
        if( ctx==null )throw new IllegalArgumentException("ctx==null");
        if( ctx.method.getDeclaringClass()==Object.class ){
            if( ctx.method.getName().equals("hashCode") && (ctx.args==null || ctx.args.length==0) ){
                BiFunction<Object,Object[],@Nullable Object> fn = (fp,fa)->ctx.handler.hashCode();
                return Optional.of(new CompileResult(fn));
            }
        }
        return Optional.empty();
    }

    public static Optional<CompileResult> compileToString(
        @NonNull CompileCtx ctx
    ){
        if( ctx==null )throw new IllegalArgumentException("ctx==null");
        if( ctx.method.getDeclaringClass()==Object.class ){
            if( ctx.method.getName().equals("toString") && (ctx.args==null || ctx.args.length==0) ){
                BiFunction<Object,Object[],@Nullable Object> fn = (fp,fa)->ctx.handler.getString();
                return Optional.of(new CompileResult(fn));
            }
        }
        return Optional.empty();
    }
}
