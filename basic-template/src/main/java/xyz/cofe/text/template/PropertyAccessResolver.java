package xyz.cofe.text.template;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Осущесвтляет доступ к свойству/пол. объекта по имени
 * @author nt.gocha@gmail.com
 */
public class PropertyAccessResolver {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    private final Map<String, PropertyDescriptor> propertiesDesc;
    //    private final Map<String,PropertyController> properties;
    private final Map<String,FieldController> fields;
//    private final Map<String,ValueController> vcs;

    /**
     * Конструктор
     * @param contextClass класс-контекст относительно которого происходит resolving к полям/свойствам
     */
    public PropertyAccessResolver( Class contextClass ){
        if( contextClass==null )throw new IllegalArgumentException( "contextClass==null" );

        propertiesDesc = new LinkedHashMap<>();
        BeanInfo bi;
        try {
            bi = java.beans.Introspector.getBeanInfo(contextClass);
            for( PropertyDescriptor pd : bi.getPropertyDescriptors() ){
                propertiesDesc.put(pd.getName(), pd);
            }
        } catch ( IntrospectionException ex) {
            Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.SEVERE, null, ex);
        }

//        properties = new LinkedHashMap<String, PropertyController>();
//        for( ValueController vc : TypesUtil.Iterators.propertiesOfClass(contextClass) ){
//            if( vc instanceof xyz.cofe.types.PropertyController ){
//                PropertyController pc = (PropertyController)vc;
//                properties.put(pc.getName(), pc);
//            }
//        }

        fields = new LinkedHashMap<String, FieldController>();
        for( ValueController vc : TypesUtil.Iterators.fieldsControllersOf(contextClass, null)){
            if( vc instanceof FieldController ){
                FieldController fc = (FieldController)vc;
                fields.put(fc.getName(), fc);
            }
        }

//        vcs = new LinkedHashMap<String, ValueController>();
//        vcs.putAll(fields);
//        vcs.putAll(properties);
    }

    /**
     * Возвращает свойства класса-контекста
     * @return свойства
     */
    public Map<String, PropertyDescriptor> getProperties() {
        return propertiesDesc;
    }

    /**
     * Возвращает поля класса-контекста
     * @return поля
     */
    public Map<String, FieldController> getFields() {
        return fields;
    }

    /**
     * Смена владельца свойств/полей
     * @param owner новый владелец
     */
    private void updateOwner( Object owner ){
        for( Object o : fields.values() ){
            if( o instanceof SetOwner )((SetOwner)o).setOwner(owner);
        }
    }

    /**
     * Разрешение (resolving) для доступа к полю/свойству объекта
     * @param context контекст
     * @param indexes цепочка-последовательность свойств/полей через которые осуществляется доступ к значению
     * @return значение
     */
    public Object resolve( Object context, String ... indexes ){
        if( context==null )throw new IllegalArgumentException( "context==null" );
        if( indexes==null )throw new IllegalArgumentException( "indexes==null" );

        if( indexes.length==0 ){
            return context;
        }

        Object v = null;

        // try proerty desc
        boolean propReaded = false;
        if( v==null ){
            PropertyDescriptor pd = propertiesDesc.get(indexes[0]);
            if( pd!=null ){
                Method readMethod = pd.getReadMethod();
                if( readMethod!=null ){
                    try {
                        v = readMethod.invoke(context);
                        propReaded = true;
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.SEVERE, null, ex);
                    } catch ( InvocationTargetException ex) {
                        Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        // use reflection
        boolean fieldReaded = false;
        if( v==null && !propReaded ){
            updateOwner(context);
            ValueController fvc = fields.get(indexes[0]);
            if( fvc!=null ){
                try {
                    v = fvc.getValue();
                    fieldReaded = true;
                } catch( Throwable ex ) {
                    Logger.getLogger(PropertyAccessResolver.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
        }

        // try as Map
        if( v==null && !fieldReaded ){
            if( context instanceof Map ){
                v = ((Map)context).get(indexes[0]);
            }
        }

        if( v==null )return null;

        if( indexes.length==1 )return v;
        String[] nidx = Arrays.copyOfRange(indexes, 1, indexes.length);

        PropertyAccessResolver reslv = new PropertyAccessResolver(v.getClass());
        return reslv.resolve(v, nidx);
    }
}
