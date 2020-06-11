package xyz.cofe.text.template;

import xyz.cofe.fn.Pair;
import xyz.cofe.iter.Eterable;
import xyz.cofe.simpletypes.SimpleTypes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер значения свойств
 * @author gocha
 */
public class PropertyController implements ValueController, SetOwner
{
    private Object owner;
    private Class type;
    private Method set;
    private Method get;
    private String name;

    public PropertyController(Object objOwner,String name, Class type,Method set,Method get)
    {
//        if (objOwner == null) {
//            throw new IllegalArgumentException("objOwner == null");
//        }
        if (type == null) {
            throw new IllegalArgumentException("type == null");
        }
        if (set == null) {
            throw new IllegalArgumentException("set == null");
        }
        if (get == null) {
            throw new IllegalArgumentException("get == null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name == null");
        }

        this.name = name;
        this.owner = objOwner;
        this.type = type;
        this.set = set;
        this.get = get;
    }

    public Method getGetMethod()
    {
        return get;
    }

    public Object getOwner()
    {
        return owner;
    }

    @Override
    public void setOwner(Object newOwner)
    {
        owner = newOwner;
    }

    public Method getSetMethod()
    {
        return set;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Class getType()
    {
        return type;
    }

    @Override
    public Object getValue() throws Throwable
    {
        return get.invoke(owner);
    }

    @Override
    public void setValue(Object value) throws Throwable
    {
        set.invoke(owner, value);
    }

    public static Map<String,ValueController> buildControllersMap( Object obj)
    {
        Iterable<? extends ValueController> ctrollers = buildControllers(obj);

        Map<String,ValueController> res = new HashMap<>();
        if( ctrollers==null )return res;

        for( ValueController vc : ctrollers )
        {
            res.put(vc.getName(), vc);
        }

        return res;
    }

    public static Eterable<PropertyController> buildPropertiesList( Class cls)
    {
        if (cls == null) {
            throw new IllegalArgumentException("cls == null");
        }

        List<PropertyController> props = new ArrayList<PropertyController>();
        List<Pair<String,Method>> methods = new ArrayList<Pair<String,Method>>();
        Method[] _methods = cls.getMethods();

        for( Method m : _methods )
        {
            String name = m.getName();
            Class methodReturn = m.getReturnType();

            boolean toLower = false;
            if( name.startsWith("get") && name.length()>3 )
            {
                name = name.substring(3);
                toLower = true;
            }else if( name.startsWith("is") && name.length()>2 && SimpleTypes.isBoolean(methodReturn) )
            {
                name = name.substring(2);
                toLower = true;
            }else if( name.startsWith("set") && name.length()>3 )
            {
                name = name.substring(3);
                toLower = true;
            }
            if( toLower && name.length()>0 && Character.isUpperCase(name.charAt(0)) )
            {
                String newName = "" + Character.toLowerCase(name.charAt(0));
                if( name.length()>1 )
                {
                    newName = newName + name.substring(1);
                }
                name = newName;
            }

            final Method f_m = m;
            final String f_name = name;
            Pair<String,Method> v = Pair.of(f_name, f_m);

            methods.add(v);
        }

        for( Pair<String,Method> pmGet : methods )
        {
            Method mGet = pmGet.b();

            Class ret = mGet.getReturnType();
            if( SimpleTypes.isVoid(ret) )continue;

            Class[] paramsGet = mGet.getParameterTypes();
            if( paramsGet.length!=0 )continue;

            if( ! mGet.getName().startsWith("get") &&
                !(mGet.getName().startsWith("is") && SimpleTypes.isBoolean(ret)) )continue;

            for( Pair<String,Method> pmSet : methods )
            {
                Method mSet = pmSet.b();

                if( !mSet.getName().startsWith("set") )continue;

                Class[] paramsSet = mSet.getParameterTypes();
                if( paramsSet.length!=1 )continue;

                Class param = paramsSet[0];
                if( !param.equals(ret) )continue;

                String pnA = pmGet.a();
                String pnB = pmSet.a();

                if( pnA==null || pnB==null )continue;
                if( !pnA.equals(pnB) )continue;

                PropertyController pController = new PropertyController(null, pnA, ret, mSet, mGet);
                props.add(pController);
            }
        }

        return Eterable.of(props);
    }

    public static Eterable<ValueController> buildControllers(Object obj)
    {
        if (obj == null) {
            throw new IllegalArgumentException("obj == null");
        }

        Class cOType = obj.getClass();
        Eterable itr = buildPropertiesList(cOType);
        for( Object o : itr ){
            if( o instanceof PropertyController )
                ((PropertyController)o).setOwner(obj);
        }
        return itr;
    }
}
