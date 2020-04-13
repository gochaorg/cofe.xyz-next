/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy <nt.gocha@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package xyz.cofe.gui.swing.properties;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import xyz.cofe.iter.Eterable;
import xyz.cofe.text.template.SimpleTypes;

/**
 * Чтение карты как набор свойств
 * @author nt.gocha@gmail.com
 */
public class ReadBeanMap implements ReadBeanNodes, PropertyDBService
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ReadBeanMap.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(ReadBeanMap.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(ReadBeanMap.class.getName(), method, result);
    }

    private static void logFine(String message,Object ... args){
        logger.log(Level.FINE, message, args);
    }
    private static void logFiner(String message,Object ... args){
        logger.log(Level.FINER, message, args);
    }
    private static void logFinest(String message,Object ... args){
        logger.log(Level.FINEST, message, args);
    }
    private static void logInfo(String message,Object ... args){
        logger.log(Level.INFO, message, args);
    }
    private static void logWarning(String message,Object ... args){
        logger.log(Level.WARNING, message, args);
    }
    private static void logSevere(String message,Object ... args){
        logger.log(Level.SEVERE, message, args);
    }
    private static void logException(Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    @Override
    public void register(PropertyDB pdb) {
        if( pdb!=null ){
            ReadBeanMap rbp = new ReadBeanMap();
            rbp.setPdb(pdb);
            pdb.registerReadBeanNodes(rbp);
        }
    }

    private volatile PropertyDB pdb;

    /**
     * Указывает ссылку на "базу" свойств
     * @return база свойств
     */
    public synchronized PropertyDB getPdb() { return pdb; }

    /**
     * Указывает ссылку на "базу" свойств
     * @param pdb база свойств
     */
    public synchronized void setPdb(PropertyDB pdb) { this.pdb = pdb; }

    @Override
    public synchronized Eterable readBeanNodes( Object bean) {
        if( bean==null )return null;

        Class beanCls = bean.getClass();

        boolean exp = true;
        if( pdb!=null ){
            exp = pdb.isExpandableType(beanCls);
        }else{
            if( bean instanceof String ){
                exp = false;
            }else if( SimpleTypes.isSimple(beanCls) ){
                exp = false;
            }
        }

        if( !exp )return null;

        if( !(bean instanceof Map ) )return null;

        List res = new ArrayList();

        for( Object oe : ((Map)bean).entrySet() ){
            if( !(oe instanceof Map.Entry) )continue;
            Map.Entry me = (Map.Entry)oe;

            Object key = me.getKey();
            Object val = me.getValue();

//            res.add(
//                new NamedEntry(key==null ? "null" : key.toString(), val)
//                    .setAsString(
//                        (key==null ? "null" : key.toString())+
//                        " => "+
//                        (val==null ? "null" : val.toString())
//                    )
//            );

            LinkedList kv = new LinkedList();
            kv.add(new NamedEntry("key", key));
            kv.add(new NamedEntry("value", val));

//            res.add(
//                new NamedEntry(key==null ? "null" : key.toString(), kv)
//                    .setAsString(
//                        (key==null ? "null" : key.toString())+
//                        " => "+
//                        (val==null ? "null" : val.toString())
//                    )
//            );
            res.add(
                new NamedEntries(key==null ? "null" : key.toString(), kv)
            );
        }

        LinkedList single = new LinkedList();
        single.add(new NamedEntries("map interface", res));

        return Eterable.single(single);
    }
}
