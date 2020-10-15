package xyz.cofe.mapitf;

import java.lang.reflect.InvocationHandler;

public interface InvokeHandler
        extends
        InvocationHandler,
        GetString, GetProxyInterfaces, GetValues,
        GetScn, OnScnChanged, OffScnChanged
{
}
