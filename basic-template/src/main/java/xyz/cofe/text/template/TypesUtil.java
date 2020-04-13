package xyz.cofe.text.template;

import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.iter.Eterable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TypesUtil {
    /**
     * Предикаты по работе с типами данных JVM
     */
    public static class Predicates
    {
        /**
         * Возвращает предикат проверки метода без параметров
         * @return Предикат
         */
        public static Predicate<Method> hasEmptyParameters()
        {
            return hasParameters(emptyParametersArray);
        }

        /**
         * Возвращает предикат строгой проверкти типов аргументов метода
         * @param params Типы аргметов метода
         * @return Предикат
         */
        public static Predicate<Method> hasParameters(Class ... params)
        {
            if (params == null) {
                throw new IllegalArgumentException("params == null");
            }
            final Class[] _p = params;
            return new Predicate<Method>() {
                @Override
                public boolean test(Method value) {
                    Class[] p = value.getParameterTypes();
                    if( p.length!=_p.length )return false;
                    for( int i=0; i<p.length; i++ )
                    {
                        if( !p[i].equals(_p[i]) )return false;
                    }
                    return true;
                }
            };
        }

        /**
         * Предикат стравения <i>value</i> <b>instanceOf</b> <i>target</i>
         * @param target Класс
         * @return Предикат
         */
        public static Predicate<Class> classInstanceOf(Class target)
        {
            final Class fTarget = target;
            return new Predicate<Class>() {
                @Override
                public boolean test(Class value) {
                    if( fTarget==null )return value==null;
                    if( value==null )return false;
                    return AinstanceOfB(value,fTarget);
                }
            };
        }

        /**
         * Предикат сравнения <i>value</i> <b>equals</b> ( <i>target</i> )
         * @param target Тип данных
         * @return Предикат
         */
        public static Predicate<Class> classEquals(Class target)
        {
            final Class fTarget = target;
            return new Predicate<Class>() {
                @Override
                public boolean test(Class value) {
                    if( fTarget==null )return value==null;
                    return value==null ? false : fTarget.equals(value);
                }
            };
        }

        /**
         * Предикат - возвращает true, если метода возвращает указанный тип
         * @param type Возвращаемый тип
         * @return Предикат
         */
        public static Predicate<Method> returns(Class type)
        {
            if (type == null) {
                throw new IllegalArgumentException("type == null");
            }
            final Class tip = type;
            return new Predicate<Method>()
            {
                @Override
                public boolean test(Method value)
                {
                    Class ret = value.getReturnType();
                    //                return ret.equals(tip);
                    return AinstanceOfB(ret, tip);
                }
            };
        }

        /**
         * Предикат - возвращает true, если название метода начинается с указанного текста
         * @param text Текст
         * @return Предикат
         */
        public static Predicate<Method> nameStart(String text)
        {
            if (text == null) {
                throw new IllegalArgumentException("text == null");
            }
            final String txt = text;
            return new Predicate<Method>() {
                @Override
                public boolean test(Method value) {
                    return value.getName().startsWith(txt);
                }
            };
        }

        /**
         * Предикат , возвращает true если метод имеет указаную аннатацию
         * @param annClass Аннатация
         * @return Предикат
         */
        public static Predicate<Method> hasAnnotation(Class annClass)
        {
            if (annClass == null) {
                throw new IllegalArgumentException("annClass == null");
            }

            final Class ann = annClass;

            return new Predicate<Method>() {
                @Override
                public boolean test(Method value)
                {
                    Object a = value.getAnnotation(ann);
                    return a!=null;
                }
            };
        }

        /**
         * Предикат: Сверяет на возможность вызова метода с указанными аргументами
         * @param args Параметры
         * @return Предикат
         */
        public static Predicate<Method> callableArguments(Object[] args)
        {
            if (args == null) {
                throw new IllegalArgumentException("args == null");
            }
            final Object[] fa = args;

            return new Predicate<Method>() {
                @Override
                public boolean test(Method value)
                {
                    Class[] types = value.getParameterTypes();
                    return isCallableArguments(types, fa);
                }
            };
        }
    }

    /**
     * Предикаты по работе с типами данных JVM
     */
    public static final Predicates predicates = new Predicates();

    /**
     * Итераторы по работе с типами данных JVM
     */
    public static class Iterators
    {
        /**
         * Возвращает отсортированную последовательность по определенному критерию
         * @param <T> Тип значений в последовательностях
         * @param src Исходная последовательность
         * @param comparer Критерий сортировки
         * @return Отсортированная последовательность
         */
        public static <T> Iterable<T> sort( Iterable<T> src, Comparator<T> comparer)
        {
            if (src == null) {
                throw new IllegalArgumentException("src == null");
            }
            if (comparer == null) {
                throw new IllegalArgumentException("comparer == null");
            }

            List<T> list = toList(src,ArrayList.class);
            Collections.sort(list, comparer);

            return list;
        }

        /**
         * Возвращает отсортированную последовательность
         * @param <T> Тип значений в последовательностях
         * @param src Исходная последовательность
         * @return Отсортированная последовательность
         */
        public static <T extends Comparable<? super T>> Iterable<T> sort(Iterable<T> src)
        {
            if (src == null) {
                throw new IllegalArgumentException("src == null");
            }

            List<T> list = toList(src,ArrayList.class);
            Collections.sort(list);

            return list;
        }

        /**
         * Конвертирует последовательность в массив
         * @param <T> Тип объектов в последовательности
         * @param src Исходная последовательность
         * @param array Пустой массив
         * @return Сконвертированная последовательность
         */
        public static <T> T[] toArray(Iterable<? extends T> src, T[] array )
        {
            if (src == null) {
                throw new IllegalArgumentException("src == null");
            }
            if (array == null) {
                throw new IllegalArgumentException("array == null");
            }

            return toArrayList(src).toArray(array);
        }

        /**
         * Конвертирует последовательность в список
         * @param <T> Тип объектов в последовательности
         * @param src Исходная последовательность
         * @return Список
         */
        public static <T> ArrayList<T> toArrayList(Iterable<? extends T> src)
        {
            return (ArrayList<T>)toList(src, ArrayList.class);
        }

//        /**
//         * Конвертирует последовательность в список
//         * @param <T> Тип объектов в последовательности
//         * @param src Исходная последовательность
//         * @return Список
//         */
//        public static <T> Vector<T> toVector(Iterable<? extends T> src)
//        {
//            return (Vector<T>)toList(src, Vector.class);
//        }

        /**
         * Конвертирует последовательность в список
         * @param <T> Тип объектов в последовательности
         * @param src Исходная последовательность
         * @param listClass Класс реализующий список (должен иметь конструктор по умолчанию)
         * @return Список или null если не смог создать список
         */
        public static <T> List<T> toList(Iterable<? extends T> src,Class<? extends List> listClass)
        {
            if (src == null) {
                throw new IllegalArgumentException("src == null");
            }
            if (listClass == null) {
                throw new IllegalArgumentException("listClass == null");
            }

            try
            {
                List result = listClass.newInstance();
                //            addTo(src, result);
                for(T o : src)result.add(o);
                return result;
            } catch (InstantiationException ex) {
                Logger.getLogger(TypesUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TypesUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }

        /**
         * Итератор - Фильт возвращающий объекты заданого класса (сравнивае строго)
         * @param <T> Интересующий класс
         * @param src Исходное множество объектов
         * @param c Интересующий класс
         * @param includeNull Включать или нет пустые ссылки
         * @return Последовательность объектов определенного класса
         */
        public static <T> Iterable<T> classFilter(Eterable src,Class<T> c,boolean includeNull)
        {
            if (src == null) {
                throw new IllegalArgumentException("src == null");
            }
            if (c == null) {
                throw new IllegalArgumentException("c == null");
            }

            final boolean incNull = includeNull;
            final Class need = c;

            Predicate<T> p = new Predicate<T>() {
                public boolean test(T value) {
                    if( value==null && incNull )return true;
                    Class c = value.getClass();
                    return need.equals(c);
                }
            };

            return src.filter(p);
        }

        /**
         * Итератор - Фильт возвращающий объекты заданого класса.
         * <p>
         * Сравнение объектов производиться функцией isAssignableFrom т.е.
         * <b><i>объект</i> instanceof <i>Интересующий класс</i></b>
         * </p>
         * @param <T> Интересующий класс
         * @param src Исходное множество объектов
         * @param c Интересующий класс
         * @param includeNull Включать или нет пустые ссылки
         * @return Последовательность объектов определенного класса
         */
        public static <T> Iterable<T> isClassFilter(Eterable src,Class<T> c,boolean includeNull)
        {
            if (src == null) {
                throw new IllegalArgumentException("src == null");
            }
            if (c == null) {
                throw new IllegalArgumentException("c == null");
            }

            final boolean incNull = includeNull;
            final Class need = c;

            Predicate<T> p = new Predicate<T>() {
                @Override
                public boolean test(T value) {
                    if( value==null )
                    {
                        if( incNull )return true;
                    }else{
                        Class c = value.getClass();
                        return need.isAssignableFrom(c);
                    }
                    return false;
                }
            };

            return src.filter(p);
        }

        /**
         * Возвращает параметры/агруметы метода
         * @param method Метод
         * @return Пераметры
         */
        public static Eterable<Class> paramtersOf(Method method){
            if (method == null) {
                throw new IllegalArgumentException("method == null");
            }
            Class[] params = method.getParameterTypes();
            return Eterable.of(params);
        }

        /**
         * Возвращает публичные методы объекта
         * @param src объект
         * @param predicate Условие отбора
         * @return Перечисление методов
         */
        public static Eterable<Method> methodsOf( Object src, Predicate<Method> predicate)
        {
            if (src == null) {
                throw new IllegalArgumentException("src == null");
            }
            if (predicate == null) {
                throw new IllegalArgumentException("predicate == null");
            }
            Class c = src.getClass();
            return Eterable.of(c.getMethods()).filter(predicate);
        }

        /**
         * Возвращает публичные методы объекта
         * @param obj объект
         * @return Перечисление методов
         */
        public static Eterable<Method> methodsOf( Object obj )
        {
            if (obj == null) {
                throw new IllegalArgumentException("obj == null");
            }
            return Eterable.of(obj.getClass().getMethods());
        }

        /**
         * Возвращает публичные методы класса
         * @param cls Класс
         * @return Перечисление методов
         */
        public static Eterable<Method> methodsOf( Class cls )
        {
            if (cls == null) {
                throw new IllegalArgumentException("cls == null");
            }
            return Eterable.of(cls.getMethods());
        }

        /**
         * Возвращает публичные поля класса
         * @param cls Класс
         * @return Перечисление полей
         */
        public static Eterable<Field> fieldsOf( Class cls )
        {
            if (cls == null) {
                throw new IllegalArgumentException("cls == null");
            }
            return Eterable.of(cls.getFields());
        }

        public static Eterable<ValueController> fieldsControllersOf( Class cls, Object owner ){
            if (cls == null) {
                throw new IllegalArgumentException("cls == null");
            }
            List<ValueController> _vc = new ArrayList<ValueController>();
            for( Field f : fieldsOf(cls) ){
                FieldController fc = new FieldController(owner, f);
                _vc.add( fc );
            }
            return  Eterable.of(_vc);
        }

        /**
         * Возвращает объевленные методы только в этом классе данного объекта
         * @param obj Объект
         * @return Перечисление методов
         */
        public static Eterable<Method> declaredMethodsOf( Object obj )
        {
            if (obj == null) {
                throw new IllegalArgumentException("obj == null");
            }
            return Eterable.of(obj.getClass().getDeclaredMethods());
        }

        /**
         * Возвращает публичные свойства объекта
         * @param object Объект
         * @return Свойства
         */
        public static Eterable<ValueController> propertiesOf(Object object)
        {
            if (object == null) {
                throw new IllegalArgumentException("object == null");
            }

            return PropertyController.buildControllers(object);
        }

        /**
         * Возвращает публичные свойства объекта
         * @param cls класс
         * @return Свойства
         */
        public static Eterable<? extends ValueController> propertiesOfClass(Class cls)
        {
            if (cls == null) {
                throw new IllegalArgumentException("object == null");
            }

            return PropertyController.buildPropertiesList(cls);
        }
    }

    /**
     * Итераторы по работе с типами данных JVM
     */
    public static final Iterators iterators = new Iterators();

    /**
     * Выполняет конструкция A <b>instanceOf</b> B
     * @param cA Класс A
     * @param cB Класс B
     * @return true - удалетворяет конструкции, false - не удавлетворяет
     */
    public static boolean AinstanceOfB(Class cA, Class cB)
    {
        if (cA == null) {
            throw new IllegalArgumentException("cA == null");
        }
        if (cB == null) {
            throw new IllegalArgumentException("cB == null");
        }
        return cB.isAssignableFrom(cA);
    }

    /**
     * Сверяет на возможность вызова метода с указанными аргументами
     * @param types Типы принимаемых параметорв
     * @param args Параметры
     * @return true - вызвать возможно, false - не возможно вызвать
     */
    public static boolean isCallableArguments(Class[] types,Object[] args)
    {
        if (types == null) {
            throw new IllegalArgumentException("types == null");
        }
        if (args == null) {
            throw new IllegalArgumentException("args == null");
        }

        if (types.length != args.length) {
            return false;
        }

        boolean callable = true;

        for (int paramIdx = 0; paramIdx < types.length; paramIdx++) {
            Class cMethodPrm = types[paramIdx];
            if (args[paramIdx] == null) {
                if( cMethodPrm.isPrimitive() ){
                    callable = false;
                    break;
                }
                continue;
            }

            Class cArg = args[paramIdx].getClass();

            boolean assign = cMethodPrm.isAssignableFrom(cArg);
            if (!assign)
            {
                callable = false;
                break;
            }
        }

        return callable;
    }

    private static NodesExtracter classMethodsExtracter = null;

    /**
     * Возвращает интерфейс доступа к методам класса
     * @return интерфейс доступа к методам класса
     */
    public static NodesExtracter classMethodsExtracter()
    {
        if( classMethodsExtracter!=null )return classMethodsExtracter;
        classMethodsExtracter = new NodesExtracter() {
            @Override
            public Iterable extract(Object from)
            {
                if( from==null )return null;
                if( !(from instanceof Class) )return null;
                return Iterators.methodsOf((Class)from);
            }
        };
        return null;
    }

    private static NodesExtracter methodParametersExtracter = null;

    /**
     * Возвращает интерфейс доступа к типам параметров метода
     * @return интерфейс доступа к типам параметров метода
     */
    public static NodesExtracter methodParametersExtracter()
    {
        if( methodParametersExtracter!=null )return methodParametersExtracter;
        methodParametersExtracter = new NodesExtracter() {
            @Override
            public Iterable extract(Object from)
            {
                if( from==null )return null;
                if( !(from instanceof Method) ) {
                    return null;
                }
                return Iterators.paramtersOf((Method)from);
            }
        };
        return null;
    }

    /**
     * Пустой массив: Class[]
     */
    public static final Class[] emptyParametersArray = new Class[]{};

}
