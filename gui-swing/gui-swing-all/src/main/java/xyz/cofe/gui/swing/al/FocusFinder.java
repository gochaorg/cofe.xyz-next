/*
 * The MIT License
 *
 * Copyright 2016 nt.gocha@gmail.com.
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

package xyz.cofe.gui.swing.al;


import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import xyz.cofe.fn.Pair;

/**
 * Поиск выбранных объект/компонентов среди сфокусированных
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class FocusFinder {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(FocusFinder.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(FocusFinder.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(FocusFinder.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(FocusFinder.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(FocusFinder.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(FocusFinder.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(FocusFinder.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>
    
    /**
     * Поиск выбранных ообъектов
     */
    public interface FindSelected {
        public <T> List<? extends T> findSelected( Class<T> cls );
    }

    public static Object findThroughFocus(Class targetClass){
        if( targetClass==null )return null;

        KeyboardFocusManager kfm =
                DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager();

        if( kfm==null )return null;

        Component fown1 = kfm.getFocusOwner();
        Component fown2 = kfm.getFocusedWindow();
        Component fown3 = kfm.getCurrentFocusCycleRoot();
        Component fown4 = kfm.getPermanentFocusOwner();
        Component[] fOwnCmpts = new Component[]{
            fown1, fown2, fown3, fown4
        };

        for( Component fown : fOwnCmpts ){
            Pair<Boolean,Object> byFocus = matchWindowClass(fown, targetClass);

            if( byFocus==null )continue;
            if( byFocus.a()==null )continue;
            if( !byFocus.a() )continue;

            return byFocus.b();
        }

        return null;
    }

    public static Object findThroughMouse(Class targetClass){
        if( targetClass==null )return null;

        Point location = MouseInfo.getPointerInfo().getLocation();

        Window[] windows = JFrame.getWindows();
        for( int wi=0; wi<windows.length; wi++ ){
            Window wnd = windows[wi];

            Point plocal = new Point(location);
            SwingUtilities.convertPointFromScreen(plocal, wnd);

            Component cw = wnd.getComponentAt(plocal);
            if( cw!=null ){
                Pair<Boolean,Object> byMouse = matchWindowClass(cw, targetClass);
                if( byMouse.a() ){
                    return byMouse.b();
                }
            }
        }

        return null;
    }

    public static Pair<Boolean,Object> matchWindowClass( Component cmpt, Class targetClass ){
//        logInfo("matchWindowClass( {1}, {0} )", targetClass, cmpt.getClass());
        if( cmpt==null )return Pair.of(false, null);

        Class ccls = cmpt.getClass();
        boolean asgn = targetClass.isAssignableFrom(ccls);
        boolean asgn2 = ccls.isAssignableFrom(targetClass);

        if( asgn ){
            return Pair.of(true, cmpt);
        }
        
        if( cmpt instanceof FindSelected ){
            List l = ((FindSelected)cmpt).findSelected(targetClass);
            if( l!=null && !l.isEmpty() ){
                return Pair.of(true,l);
            }
        }

        return matchWindowClass(cmpt.getParent(), targetClass);
    }
}


