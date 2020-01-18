/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

package xyz.cofe.collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Карта Строка/Объект, где ключ является регистро независимым
 * @author gocha
 */
public class ICaseStringMap<V> extends LinkedHashMap<String, V>
{
    protected boolean ignoreCase = true;

    /**
     * Конструктор
     */
    public ICaseStringMap(){
    }

    /**
     * Конструктор
     * @param ignoreCase игнорировать регистр
     */
    public ICaseStringMap(boolean ignoreCase){
        this.ignoreCase = ignoreCase;
    }

    /**
     * Возвращает факт игнорирования регистра
     * @return true - регистр игнорируется
     */
    public boolean isIgnoreCase()
    {
        return ignoreCase;
    }

    /**
     * Проверка наличия ключа
     * @param key ключ
     * @return true - есть в карте
     */
    @Override
    @SuppressWarnings("element-type-mismatch")
    public boolean containsKey(Object key)
    {
        if( key==null )return super.containsKey(key);
        if( isIgnoreCase() ){
            if( key instanceof String ){
                key = ((String)key).toLowerCase();
            }
            return super.containsKey(key);
        }else{
            return super.containsKey(key);
        }
    }

    /**
     * Получение значения по ключу
     * @param key ключ
     * @return значение
     */
    @Override
    @SuppressWarnings("element-type-mismatch")
    public V get(Object key)
    {
        if( key==null )return super.get(key);
        if( isIgnoreCase() ){
            if( key instanceof String ){
                key = ((String)key).toLowerCase();
            }
            return super.get(key);
        }else{
            return super.get(key);
        }
    }

    /**
     * Установка значения по ключу
     * @param key ключ
     * @param value значние
     * @return предыдущее значение
     */
    @Override
    public V put(String key, V value)
    {
        if( key==null )return super.get(key);
        if( isIgnoreCase() ){
            if( key instanceof String ){
                key = ((String)key).toLowerCase();
            }
            return super.put(key, value);
        }else{
            return super.put(key, value);
        }
    }

    /**
     * Слияние данных из другой карты
     * @param m карта
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m)
    {
        if( m==null ){super.putAll(m);return;}
        if( isIgnoreCase() ){
            for( String k : m.keySet() ){
                put( k, m.get(k) );
            }
        }else{
            super.putAll(m);
            return;
        }
    }
}
