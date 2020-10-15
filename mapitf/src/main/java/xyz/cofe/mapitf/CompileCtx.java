package xyz.cofe.mapitf;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

public class CompileCtx {
    public CompileCtx(
            InvokeHandler handler,
            Object proxy,
            Method method,
            Object[] args
    ) {
        if(handler==null)throw new IllegalArgumentException("handler==null");
        if(proxy==null)throw new IllegalArgumentException("proxy==null");
        if(method==null)throw new IllegalArgumentException("method==null");

        this.handler = handler;
        this.proxy = proxy;
        this.method = method;
        this.args = args;
    }

    public final InvokeHandler handler;
    public final Object proxy;
    public final Method method;
    public final Object[] args;
}
