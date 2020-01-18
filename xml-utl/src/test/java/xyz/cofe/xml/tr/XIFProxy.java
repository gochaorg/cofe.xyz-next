package xyz.cofe.xml.tr;

import org.w3c.dom.Node;
import xyz.cofe.typeconv.ExtendedCastGraph;
import xyz.cofe.typeconv.TypeCastGraph;
import xyz.cofe.xml.XmlUtil;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XFetcher implements InvocationHandler {
    protected final Node node;
    public XFetcher( Node node){
        if( node==null ) throw new IllegalArgumentException("node==null");
        this.node = node;
    }

    @SuppressWarnings({ "unchecked", "ClassGetClass" })
    public static <T> T proxy( Node node, Class<T> itf){
        if( itf==null ) throw new IllegalArgumentException("itf==null");
        if( !itf.isInterface() )throw new IllegalArgumentException("can't extract from non interface ("+itf+")");

        XFetcher extracter = new XFetcher(node);
        //if( !extracter.isExtractable(itf) )throw new IllegalArgumentException("can't extract from "+itf);

        return (T)
            Proxy.newProxyInstance(
                XFetcher.class.getClassLoader(),//itf.getClass().getClassLoader(),
                new Class[]{ itf },
                extracter
            );
    }

    public static boolean isAtom(Class cls){
        if( cls==null ) throw new IllegalArgumentException("cls==null");
        if( cls==null ) throw new IllegalArgumentException("cls==null");
        if( Number.class.isAssignableFrom(cls) )return true;
        if( CharSequence.class.isAssignableFrom(cls) )return true;
        if( Boolean.class.isAssignableFrom(cls) )return true;
        if( cls.isPrimitive() )return true;
        return false;
    }

    private static final TypeCastGraph tc = new ExtendedCastGraph();
    private static final List<Class> primitiveWrappers = new ArrayList<Class>(){{
        add(Byte.class);
        add(Short.class);
        add(Integer.class);
        add(Long.class);
        add(Float.class);
        add(Double.class);
        add(Character.class);
        add(Boolean.class);
    }};
    public static Object castAtom( String txt, Class target ){
        if( target==null ) throw new IllegalArgumentException("target==null");

        if( txt==null ){
            if( target.isPrimitive() ){
                if( target==int.class ) return 0;
                if( target==short.class ) return (short) 0;
                if( target==byte.class ) return (byte) 0;
                if( target==long.class ) return (long) 0;
                if( target==double.class ) return (double) 0;
                if( target==float.class ) return (float) 0;
                if( target==boolean.class ) return (boolean) false;
                if( target==char.class ) return (char) 0;
                throw new ClassCastException("can't cast to "+target+" from null");
            } else{
                return null;
            }
        }else{
            if( target.isPrimitive() ){
                if( txt.length()<1 ){
                    if( target==int.class ) return 0;
                    if( target==short.class ) return (short) 0;
                    if( target==byte.class ) return (byte) 0;
                    if( target==long.class ) return (long) 0;
                    if( target==double.class ) return (double) 0;
                    if( target==float.class ) return (float) 0;
                    if( target==boolean.class ) return (boolean) false;
                    if( target==char.class ) return (char) 0;
                    throw new ClassCastException("can't cast to "+target+" from empty string");
                } else {
                    if( target==int.class ) return Integer.parseInt(txt);
                    if( target==short.class ) return Short.parseShort(txt);
                    if( target==byte.class ) return Byte.parseByte(txt);
                    if( target==long.class ) return Long.parseLong(txt);
                    if( target==double.class ) return Double.parseDouble(txt);
                    if( target==float.class ) return Float.parseFloat(txt);
                    if( target==boolean.class ) return Boolean.parseBoolean(txt);
                    if( target==char.class ) return txt.charAt(0);
                    throw new ClassCastException("can't cast to "+target+" from string");
                }
            }else if( primitiveWrappers.contains(target) ){
                if( txt.length()<1 ){
                    return null;
                }else {
                    return tc.cast(txt,target);
                }
            }else{
                if( target==String.class )return txt;
                return tc.cast(txt,target);
            }
        }
    }

    public Object fetch( XPathExpression xpathQuery, Type targetType ){
        if( xpathQuery==null ) throw new IllegalArgumentException("xpathQuery==null");
        if( targetType==null ) throw new IllegalArgumentException("targetType==null");

        if( targetType instanceof Class ){
            Class targetClass = (Class)targetType;
            if( targetClass.isAssignableFrom(String.class) ){
                try{
                    String str = (String) xpathQuery.evaluate(node, XPathConstants.STRING);
                    return str;
                } catch( XPathExpressionException e ){
                    throw new Error(e);
                }
            } else if( isAtom(targetClass) ){
                try{
                    String str = (String) xpathQuery.evaluate(node, XPathConstants.STRING);
                    return castAtom(str, targetClass);
                } catch( XPathExpressionException e ){
                    throw new Error(e);
                }
            }
        }

        throw new IllegalArgumentException("targetType(="+targetType+") not acceptable");
    }

    public boolean isExtractable( Type t ){
        return isExtractable(null, t);
    }

    protected boolean isExtractable( Set<Type> visited, Type t ){
        if( t==null ) throw new IllegalArgumentException("t==null");

        // защита от рекурсии
        if( visited==null )visited = new HashSet<>();
        if( visited.contains(t) )return false;
        visited.add(t);

        if( t instanceof Class ){
            Class c = (Class)t;
            if( isAtom(c)) return true;
            if( !c.isInterface() )return false;

            TypeVariable<Class>[] genericTypes = c.getTypeParameters();
            if( genericTypes.length>0 ){
                return false;
            }
            return true;
        }
        if( t instanceof ParameterizedType ){
            // Проверка что указанный тип является:
            // List<A> , где A - интерфейс для получения данных - т.е. isExtractable(A)==true & A.isInterface()==true
            ParameterizedType pt = (ParameterizedType)t;

            // Проверяем pt - это List<A>
            Type rawType = pt.getRawType();
            if( !(rawType instanceof Class) ){
                return false;
            }

            Class rawClass = (Class)rawType;
            if( !rawClass.isInterface() )return false;
            if( !(List.class == rawClass) )return false;

            // Проверяем что List<A>, A - это интерфейс, и он isExtractable(A)==true
            Type[] actTypeArgs = pt.getActualTypeArguments();
            if( actTypeArgs.length!=1 || !(actTypeArgs[0] instanceof Class) ){
                return false;
            }

            Class listGenericArg = (Class)actTypeArgs[0];
            if( isAtom(listGenericArg) )return true;
            if( !listGenericArg.isInterface() )return false;
            return isExtractable(visited,listGenericArg);
        }
        return false;
    }
    public boolean isExtractable( Method m ){
        if( m==null ) throw new IllegalArgumentException("m==null");
        if( m.getParameterCount()>0 )return false;

        Object ann = m.getAnnotation(XPath.class);
        if( ann==null )return false;

        Type genericReturnType = m.getGenericReturnType();
        if( isExtractable(genericReturnType) )return true;

        return false;
    }

    public static Object defaultPrimitiveValue( Class prim ){
        if( prim==null ) throw new IllegalArgumentException("prim==null");
        if( !prim.isPrimitive() ) throw new IllegalArgumentException("!prim.isPrimitive()");

        if( prim==Void.class )return null;
        if( prim==boolean.class )return false;
        if( prim==char.class )return (char)0;
        if( prim==byte.class )return (byte)0;
        if( prim==short.class )return (short)0;
        if( prim==int.class )return (int)0;
        if( prim==long.class )return (long)0;
        if( prim==float.class )return (float)0;
        if( prim==double.class )return (double)0;

        throw new IllegalArgumentException("can't resolve default value for "+prim);
    }

    private static final XPathFactory factory = XmlUtil.getXPathFactory();
    private static final javax.xml.xpath.XPath xpath = factory.newXPath();
    private static final Map<Method,XPathExpression> xpathExpressions = new HashMap<>();

    private XPathExpression exressionOf( Method method ){
        return xpathExpressions.computeIfAbsent(method, m->{
            XPath xpathAnn = m.getAnnotation(XPath.class);
            try{
                return xpath.compile(xpathAnn.value());
            } catch( XPathExpressionException e ){
                throw new Error(e);
            }
        });
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        Type gret = method.getGenericReturnType();
        Class cret = method.getReturnType();

        if( !isExtractable(method) ){
            return cret.isPrimitive() ? defaultPrimitiveValue(cret) : null;
        }

        return fetch(exressionOf(method), gret);
    }
}
