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
import java.util.logging.Level;
import java.util.logging.Logger;

import xyz.cofe.iter.Eterable;
import xyz.cofe.text.template.SimpleTypes;

/**
 * Чтение списка как набор свойств
 * @author nt.gocha@gmail.com
 */
public class ReadBeanList implements ReadBeanNodes, PropertyDBService
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ReadBeanList.class.getName());

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
        logger.entering(ReadBeanList.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(ReadBeanList.class.getName(), method, result);
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
            ReadBeanList rbp = new ReadBeanList();
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

        if( !(bean instanceof List ) )return null;

        List res = new ArrayList<>();

        int li = -1;
        for( Object le : (List)bean ){
            li++;

            exp = true;
            if( pdb!=null && le!=null ){
                exp = pdb.isExpandableType(le.getClass());
            }

            res.add(
                new NamedEntry("item#"+li, le)
                    .setAsString(le==null || exp ? null : le.getClass().toString())
            );
        }

        LinkedList single = new LinkedList();
        single.add(new NamedEntries("list interface", res));

        return Eterable.single(single);
    }
}
