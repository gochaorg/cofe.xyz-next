package xyz.cofe.mapitf;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.cofe.collection.BasicEventList;
import xyz.cofe.collection.EventList;
import xyz.cofe.mapitf.impl.CtorImpl;
import xyz.cofe.mapitf.impl.ObjImpl;
import xyz.cofe.mapitf.impl.SelfImpl;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataProxy implements InvokeHandler {
    private final Class<?>[] interfaces;
    private final PropertyMapper propertyMapper;

    public DataProxy( Class<?>... interfaces ){
        this.interfaces = interfaces;
        this.propertyMapper = PropertyMapper.common();
    }
    public DataProxy( Map<String,@Nullable Object> vals, Class<?>... interfaces ){
        this.interfaces = interfaces;
        this.values = vals;
        this.propertyMapper = PropertyMapper.common();
    }
    @SuppressWarnings("ConstantConditions")
    public DataProxy(Map<String,@Nullable Object> vals, @NonNull PropertyMapper propertyMapper, Class<?>... interfaces ){
        if( propertyMapper==null )throw new IllegalArgumentException("propertyMapper==null");
        this.interfaces = interfaces;
        this.values = vals;
        this.propertyMapper = propertyMapper;
    }

    @Override
    public Class<?>[] getProxyInterfaces() {
        return interfaces;
    }

    private final AtomicLong scn = new AtomicLong();
    private final Collection<Consumer<? super GetScn>> scnListeners = new CopyOnWriteArraySet<>();

    public AtomicLong getScn(){ return scn; }

    @SuppressWarnings("UnusedReturnValue")
    public AutoCloseable onScnChanged(Consumer<? super GetScn> changed ){
        if( changed==null )throw new IllegalArgumentException("changed==null");
        scnListeners.add(changed);
        return ()->{
            scnListeners.remove(changed);
        };
    }
    public void offScnChanged( Consumer<? super GetScn> changed ){
        if( changed==null )throw new IllegalArgumentException("changed==null");
        scnListeners.remove(changed);
    }
    private final AtomicInteger nextScnCalls = new AtomicInteger(0);
    private void nextScn(){
        try{
            int lvl = nextScnCalls.incrementAndGet();
            if( lvl>1 )return;

            scn.incrementAndGet();
            for( Consumer<? super GetScn> ls : scnListeners ){
                ls.accept(this);
            }
        }finally {
            nextScnCalls.decrementAndGet();
        }
    }

    private final Map<Method, BiFunction<Object,Object[],@Nullable Object>> methodReaders = new ConcurrentHashMap<>();

    @SuppressWarnings("nullness")
    private final Consumer<? super GetScn> scnIncrementor = (src)->{
        nextScn();
    };

    private volatile List<Function<CompileCtx,Optional<CompileResult>>> compilersInst;
    private List<Function<CompileCtx,Optional<CompileResult>>> compilers(){
        if( compilersInst!=null )return compilersInst;
        synchronized (this) {
            if( compilersInst!=null )return compilersInst;
            compilersInst = new ArrayList<>();
            compilersInst.add(ObjImpl::compileEquals);
            compilersInst.add(ObjImpl::compileToString);
            compilersInst.add(ObjImpl::compileHash);
            compilersInst.add(ctx -> CtorImpl.compileCtor(ctx, propertyMapper));
            compilersInst.add(SelfImpl::compileSelfInterfaces);
            compilersInst.add(this::compileList);
            return compilersInst;
        }
    }

    private Optional<CompileResult> compileList( @NonNull CompileCtx ctx ){
        Optional<String> valueNameOpt = propertyMapper.map(ctx.method);
        if( !valueNameOpt.isPresent() )return Optional.empty();

        String methodName = valueNameOpt.get();

        BiFunction<Object,Object[],@Nullable Object> fn = null;

        Type[] params = ctx.method.getGenericParameterTypes();
        if( params.length == 0 && !Void.class.equals(ctx.method.getReturnType()) ){
            if( ctx.method.getGenericReturnType() instanceof ParameterizedType ){
                ParameterizedType ptype = (ParameterizedType) ctx.method.getGenericReturnType();
                if( List.class.equals(ptype.getRawType()) ){
                    Type[] ptArgs = ptype.getActualTypeArguments();
                    if( ptArgs.length==1 ){
                        Type ptArg = ptArgs[0];
                        if( ptArg instanceof Class && ((Class<?>) ptArg).isInterface() ){
                            EventList<Object> evList;

                            Object oval = getValues().get(methodName);
                            if( oval instanceof List ){
                                if( oval instanceof EventList ){
                                    evList = (EventList<Object>)oval;
                                }else {
                                    evList = new BasicEventList<>((List) oval);
                                }
                            } else {
                                evList = new BasicEventList<>();
                            }

                            evList.onScn((from,to,casue)->getScn().incrementAndGet());
                            evList.onInserted((idx,old,newv)->{
                                try{
                                    Object obj = Proxy.getInvocationHandler(newv);
                                    if( obj instanceof DataProxy ){
                                        ((DataProxy) obj).onScnChanged(scnIncrementor);
                                    }
                                }catch( IllegalArgumentException ex ){
                                    Logger.getLogger(DataProxy.class.getName()).log(Level.WARNING,"onInserted onScnChanged",ex);
                                    return;
                                }
                            });
                            evList.onUpdated((idx,old,newv)->{
                                try{
                                    Object obj = Proxy.getInvocationHandler(old);
                                    if( obj instanceof DataProxy ){
                                        ((DataProxy) obj).offScnChanged(scnIncrementor);
                                    }
                                }catch( IllegalArgumentException ex ){
                                    Logger.getLogger(DataProxy.class.getName()).log(Level.WARNING,"onUpdated offScnChanged",ex);
                                    return;
                                }
                                try{
                                    Object obj = Proxy.getInvocationHandler(newv);
                                    if( obj instanceof DataProxy ){
                                        ((DataProxy) obj).onScnChanged(scnIncrementor);
                                    }
                                }catch( IllegalArgumentException ex ){
                                    Logger.getLogger(DataProxy.class.getName()).log(Level.WARNING,"onUpdated onScnChanged",ex);
                                    return;
                                }
                            });
                            evList.onDeleted((idx,old,newv)->{
                                try{
                                    Object obj = Proxy.getInvocationHandler(old);
                                    if( obj instanceof DataProxy ){
                                        ((DataProxy) obj).offScnChanged(scnIncrementor);
                                    }
                                }catch( IllegalArgumentException ex ){
                                    Logger.getLogger(DataProxy.class.getName()).log(Level.WARNING,"onDeleted offScnChanged",ex);
                                    return;
                                }
                            });

                            getValues().put(methodName,evList);

                            fn = (fp,fa)->getValues().get(methodName);
                            return Optional.of(new CompileResult(fn));
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("nullness")
    @Nullable
    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable{
        if( method == null ) throw new IllegalArgumentException("method==null");

        BiFunction<Object,Object[],@Nullable Object> fn = methodReaders.get(method);
        if( fn!=null )return fn.apply(proxy,args);

        if( method.getDeclaringClass()==GetScn.class ){
            return getScn();
        }

        CompileCtx cctx = new CompileCtx(this,proxy,method,args);

        for( Function<CompileCtx,Optional<CompileResult>> compiler : compilers() ){
            Optional<CompileResult> cmpl = compiler.apply(cctx);
            if( cmpl.isPresent() ){
                methodReaders.put(method,cmpl.get().fn);
                return cmpl.get().fn.apply(proxy,args);
            }
        }

        Optional<String> valueNameOpt = propertyMapper.map(method);
        if(!valueNameOpt.isPresent()){
            throw new Error("can't invoke "+method+" method not mapped");
        }
        @NonNull String valueName = valueNameOpt.get();

        Type[] params = method.getGenericParameterTypes();
        if( params.length==1 && args!=null && args.length>0 ){
            return write(valueName, args[0], params[0], method.getGenericReturnType(), proxy);
        }

        throw new Error("can't invoke "+method);
    }

    public String getString(){
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<interfaces.length; i++ ){
            if(i>0)sb.append("+");
            sb.append(interfaces[i].getName());
        }
        sb.append("{");

        sb.append("scn:").append(getScn().get());

        int ei = 0;
        for( Map.Entry<String,@Nullable Object> en : getValues().entrySet()){
            if( en.getKey()==null )continue;
            sb.append(", ");

            Object val = en.getValue();
            if( val==null ){
                sb.append(en.getKey()).append("=").append("null");
            }else{
                sb.append(en.getKey()).append("=").append(en.getValue());
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString(){
        return getString();
    }

    private volatile @Nullable Map<String,@Nullable Object> values;
    public synchronized Map<String,@Nullable Object> getValues(){
        if( values==null )values = new LinkedHashMap<>();
        return values;
    }
    public synchronized void setValues(Map<String,@Nullable  Object> vals){
        values = vals;
    }

    @Nullable
    private Object write( String field, Object value, Type expectedValueType, Type returnValueType, Object proxy ){
        try{
            synchronized( this ){
                Map<String,@Nullable Object> vals = getValues();

                vals.put(field, value);

                if( isSelfInterface(returnValueType) )
                    return proxy;

                if( returnValueType.equals(Void.class) ) return null;

                return null;
            }
        } finally {
            nextScn();
        }
    }
}
