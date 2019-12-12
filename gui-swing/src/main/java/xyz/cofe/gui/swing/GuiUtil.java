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
package xyz.cofe.gui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.iter.Eterable;
import xyz.cofe.iter.TreeStep;

/**
 * Класс утилита для работы с пользовательским интерфейсом
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class GuiUtil
{
    /**
     * Возвращает фрейм для указанного компонента, если компонент размещен на фрейме
     * @param cmpnt Компонент
     * @return Фрейм, или null если компонент не привязан к фрейму
     */
    public static JFrame getJFrameOfComponent(Component cmpnt)
    {
        if (cmpnt == null) {
            throw new IllegalArgumentException("cmpnt == null");
        }

        Container cntr = cmpnt.getParent();
        while(true)
        {
            if( cntr==null )break;
            if( cntr instanceof JFrame )break;

            cntr = cntr.getParent();
        }

        if( cntr instanceof JFrame )return (JFrame)cntr;
        return null;
    }

    /**
     * Возвращает окно для указанного компонента, если компонент размещен на jryt
     * @param cmpnt Компонент
     * @return Окно, или null если компонент не привязан к окну
     */
    public static Window getWindowOfComponent(Component cmpnt)
    {
        if (cmpnt == null) {
            throw new IllegalArgumentException("cmpnt == null");
        }

        Container cntr = cmpnt.getParent();
        while(true)
        {
            if( cntr==null )break;
            if( cntr instanceof Window )break;

            cntr = cntr.getParent();
        }

        if( cntr instanceof Window )return (Window)cntr;
        return null;
    }

    /**
     * Возвращает фрейм для указанного компонента, если компонент размещен на фрейме
     * @param cmpnt Компонент
     * @return Фрейм, или null если компонент не привязан к фрейму
     */
    public static Frame getFrameOfComponent(Component cmpnt)
    {
        if (cmpnt == null) {
            throw new IllegalArgumentException("cmpnt == null");
        }
        
        Container cntr = cmpnt.getParent();
        while(true)
        {
            if( cntr==null )break;
            if( cntr instanceof Frame )break;
            
            cntr = cntr.getParent();
        }
        
        if( cntr instanceof Frame )return (Frame)cntr;
        return null;
    }

    private static Eterable<Component> emptyComponents = Eterable.empty();

    /**
     * Возвращает дочерние объекты указаного компонента
     */
    public static NodesExtracter<Component,Component> childComponentExtracter = (Component from) ->
    {
        if( from==null )return emptyComponents;
        if( from instanceof Container )
        {
            Container cont = (Container)from;
            Component[] childrenArray = cont.getComponents();
            var childrenIters = Eterable.of(childrenArray);
            return childrenIters;
        }
        return emptyComponents;
    };

    /**
     * Возвращает итератор по древу компонентов
     * @param root Корень дерева
     * @return Компоненты (включая корень и все вложенные компоненты)
     */
    public static Eterable<Component> walk(Component root)
    {
        if (root == null)
        {
            throw new IllegalArgumentException("root == null");
        }

        return Eterable.tree(root, childComponentExtracter).walk();
    }
    
    /**
     * Возвращает итератор по древу компонентов
     * @param root Корень дерева
     * @return Компоненты (включая корень и все вложенные компоненты)
     */
    public static Eterable<TreeStep<Component>> tree( Component root){
        if( root==null )throw new IllegalArgumentException("root==null");
        return Eterable.tree(root, childComponentExtracter).go();
    }

    /**
     * Проверяет принадлежность компонента, что он является дочерним по отношению к дрогому (родительскому)
     * Или самим сабой
     * @param parent Родительский компонент
     * @param child Дочерний компонент
     * @return true - Является дочерним, false - не является
     */
    public static boolean isChildOrSelfOf(Component parent,Component child)
    {
        if (parent == null) {
            throw new IllegalArgumentException("parent == null");
        }
        if (child == null) {
            throw new IllegalArgumentException("child == null");
        }
        
        Eterable<Component> tree = walk(parent);
        //boolean in = Eterators.in(child, tree);
        boolean in = tree.filter( x -> child==x ).count() > 0;

        return in;
    }

    /**
     * Установка системного скина
     * @return Факт установки
     */
    public static boolean setSystemLookAndFeel()
    {
        try {
            // Set System L&F
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
            return true;
        }
        catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(GuiUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(GuiUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        catch (InstantiationException ex) {
            Logger.getLogger(GuiUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        catch (IllegalAccessException ex) {
            Logger.getLogger(GuiUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Возвращает устанновленные скины
     * @return Перечень скиной
     */
    public static UIManager.LookAndFeelInfo[] getInstalledLookAndFeels(){
        UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
        return info;
    }

    /**
     * Устанавливает указанный скин
     * @param lf Скин
     * @return Факт установки
     */
    public static boolean setLookAndFeel(UIManager.LookAndFeelInfo lf){
        try {
            UIManager.setLookAndFeel(lf.getClassName());
            return true;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GuiUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (InstantiationException ex) {
            Logger.getLogger(GuiUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GuiUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(GuiUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Центрирует окно
     * @param window окно
     */
    public static void centerWindow(Window window)
    {
        if (window == null) {
            throw new IllegalArgumentException("window == null");
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point p = ge.getCenterPoint();
        window.setLocation(p.x - window.getWidth()/2, p.y - window.getHeight()/2);
    }

    /**
     * Устанавливает размеры окна пропорционально размеру экрана
     * @param window Окно
     * @param sizeX Пропорции по гоизонтали (0..1)
     * @param sizeY Пропорции по вертикали (0..1)
     */
    public static void setWindowDesktopSize(Window window,double sizeX,double sizeY)
    {
        if (window == null) {            
            throw new IllegalArgumentException("window == null");
        }
        if( sizeX<0 || sizeX>1 )throw new IllegalArgumentException("size<0 | size>1");
        if( sizeY<0 || sizeY>1 )throw new IllegalArgumentException("size<0 | size>1");

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int screenX = ge.getCenterPoint().x * 2;
        int screenY = ge.getCenterPoint().y * 2;

        window.setSize((int)((double)screenX*sizeX), (int)((double)screenY*sizeY));
    }
    
    /**
     * Класс помошник для позицианирования окна на экране <br>
     * Возможно вызвать несколько методов объединив их в цепочку методов,
     * по завершению работы, будет созда объект Reciver&lt;Windows&gt; 
     * который можно повесить на событие Swing
     * @see SwingListener
     */
    public static class WindowReciverBuilder {
        protected List<Consumer<Window>> actions = new ArrayList<>();
        
        /**
         * Разместить окно по центру
         * @return self ссылка
         */
        public WindowReciverBuilder center(){
            Consumer<Window> rw = new Consumer<Window>() {
                @Override
                public void accept(Window wnd) {
                    if( wnd==null )return;
                    centerWindow(wnd);
                }
            };
            actions.add(rw);
            return this;
        }
        
        /**
         * Задать минимальные размеры окна
         * @param w ширина
         * @param h высота
         * @return self ссылка
         */
        public WindowReciverBuilder minSize(final int w,final int h){
            Consumer<Window> rw = new Consumer<Window>() {
                @Override
                public void accept(Window wnd) {
                    if( wnd==null )return;
                    int cw = wnd.getWidth();
                    int ch = wnd.getHeight();
                    if( cw<w || ch<h ){
                        int tw = Math.max(cw, w);
                        int th = Math.max(ch, h);
                        wnd.setSize(tw, th);
                    }
                }
            };
            actions.add(rw);
            return this;
        }
        
        /**
         * Задать относительные размеры
         * @param w коэф размеров от 0... до ...1
         * @param h коэф размеров от 0... до ...1
         * @return self ссылка
         */
        public WindowReciverBuilder relativeSize(final double w, final double h){
            Consumer<Window> rw = new Consumer<Window>() {
                public Rectangle desktopRect(){
                    /*GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    GraphicsDevice gd = ge.getDefaultScreenDevice();
                    if( gd!=null ){
                        DisplayMode dm = gd.getDisplayMode();
                        if( dm!=null ){
                            return new Rectangle(dm.getWidth(), dm.getHeight());
                        }
                    }

                    Point p = ge.getCenterPoint();
                    return new Rectangle(0, 0, p.x * 2, p.y * 2);
                    */

                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    GraphicsDevice gd = ge.getDefaultScreenDevice();
                    return ge.getMaximumWindowBounds();
                }

                @Override
                public void accept(Window wnd) {
                    if( wnd==null )return;
                    if( w>0 && h>0 ){
                        Rectangle r = desktopRect();
                        wnd.setSize((int)(w * r.getWidth()), (int)(h * r.getHeight()));
                    }
                }
            };
            actions.add(rw);
            return this;
        }
        
        /**
         * Установить размер окна
         * @param w ширина
         * @param h высота
         * @return self-ссылка
         */
        public WindowReciverBuilder size(final int w, final int h){
            Consumer<Window> rw = new Consumer<Window>() {
                @Override
                public void accept(Window wnd) {
                    if( wnd==null )return;
                    if( w>0 && h>0 ){
                        wnd.setSize(w,h);
                    }
                }
            };
            actions.add(rw);
            return this;
        }
        
        /**
         * Расчет расположения окна
         */
        public static class CalcTargetPos {
            public int xCont;
            public int yCont;
            public int xContRB;
            public int yContRB;
            
            public int xWnd;
            public int yWnd;
            public int xWndRB;
            public int yWndRB;
            public int wWnd;
            public int hWnd;
            
            public int xTrgt;
            public int yTrgt;
            public int wTrgt;
            public int hTrgt;
            public int xTrgtRB;
            public int yTrgtRB;
            
            public CalcTargetPos(Window wnd, Rectangle container, Position pos){
                xCont = container.x;
                yCont= container.y;

                xContRB = container.x + container.width - 1;
                yContRB = container.y + container.height - 1;

                xWnd = wnd.getX();
                yWnd = wnd.getY();
                xWndRB = wnd.getX() + wnd.getWidth() - 1;
                yWndRB = wnd.getY() + wnd.getHeight()- 1;
                wWnd = wnd.getWidth();
                hWnd = wnd.getHeight();

                xCont += pos.left!=null ? pos.left : 0;
                yCont += pos.top!=null ? pos.top : 0;

                xContRB -= pos.right!=null  ? pos.right  : 0;
                yContRB -= pos.bottom!=null ? pos.bottom : 0;

                if( pos.left!=null && pos.right!=null ){
                    xTrgt = Math.min(xCont, xContRB);
                    wTrgt = Math.abs(xCont - xContRB);
                    xTrgtRB = xTrgt + wTrgt;
                }else if( pos.left!=null ){
                    xTrgt = xCont;
                    wTrgt = wWnd;
                    xTrgtRB = xTrgt + wWnd;
                }else if( pos.right!=null ){
                    xTrgt = xContRB - wWnd;
                    wTrgt = wWnd;
                    xTrgtRB = xTrgt + wWnd;
                }else{
                    xTrgt = xWnd;
                    xTrgtRB = xWndRB;
                    wTrgt = wWnd;
                }

                if( pos.top!=null && pos.bottom!=null ){
                    yTrgt = Math.min(yCont, yContRB);
                    hTrgt = Math.abs(yCont - yContRB);
                    yTrgtRB = yTrgt + hTrgt;
                }else if( pos.top!=null ){
                    yTrgt = yCont;
                    hTrgt = hWnd;
                    yTrgtRB = yTrgt + hWnd;
                }else if( pos.bottom!=null ){
                    yTrgt = yContRB - hWnd;
                    hTrgt = hWnd;
                    yTrgtRB = yTrgt + hWnd;
                }else{
                    yTrgt = yWnd;
                    yTrgtRB = yWndRB;
                    hTrgt = hWnd;
                }
            }
        }
        
        /**
         * Класс помошник для размежения окна
         */
        public class Position {
            private Integer left;
            private Integer top;
            private Integer right;
            private Integer bottom;
            
            /**
             * Указывает расположение слева от края
             * @param left расположение слева
             * @return self ссылка
             */
            public Position left( int left ){
                this.left = left;
                return this;
            }
            
            /**
             * Указывает расположение окна справа от края
             * @param right расположение справа
             * @return self ссылка
             */
            public Position right( int right ){
                this.right = right;
                return this;
            }
            
            /**
             * Указывает расположение окна сверху от края
             * @param top расположение сверху
             * @return self ссылка
             */
            public Position top( int top ){
                this.top = top;
                return this;
            }
            
            /**
             * Указывает расположение окна снизу от края
             * @param bottom расположение снизу
             * @return self ссылка
             */
            public Position bottom( int bottom ){
                this.bottom = bottom;
                return this;
            }
            
            /**
             * Указывает настройки расположения
             * @return ссылка на WindowReciverBuilder
             */
            public WindowReciverBuilder set(){
                Consumer<Window> rw = new Consumer<Window>() {
                    public Rectangle desktopRect(){
                        /*GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        GraphicsDevice gd = ge.getDefaultScreenDevice();
                        if( gd!=null ){
                            DisplayMode dm = gd.getDisplayMode();
                            if( dm!=null ){
                                return new Rectangle(dm.getWidth(), dm.getHeight());
                            }
                        }
                        
                        Point p = ge.getCenterPoint();
                        return new Rectangle(0, 0, p.x * 2, p.y * 2);
                        */
                        
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        GraphicsDevice gd = ge.getDefaultScreenDevice();
                        return ge.getMaximumWindowBounds();
                    }
                    
                    public Rectangle screenRect(){
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        GraphicsDevice gd = ge.getDefaultScreenDevice();
                        if( gd!=null ){
                            DisplayMode dm = gd.getDisplayMode();
                            if( dm!=null ){
                                return new Rectangle(dm.getWidth(), dm.getHeight());
                            }
                        }
                        
                        Point p = ge.getCenterPoint();
                        return new Rectangle(0, 0, p.x * 2, p.y * 2);
                    }
                    
                    // ok - tested
                    private void setLeft( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        
                        //int w = ctp.wWnd - (ctp.xTrgt - ctp.xWnd);
                        //w = w > 0 ? w : 1; // bind right
                        
                        wnd.setLocation(ctp.xTrgt, ctp.yWnd);
                        //wnd.setSize(ctp.wWnd, ctp.hWnd);
                    }
                    
                    private void setRight( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setBottom( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setTop( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setLeftTop( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        //wnd.setSize(ctp.wWnd, ctp.hTrgt);
                    }
                    
                    private void setLeftRight( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setLeftBottom( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setRightTop( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setRightBottom( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setTopBottom( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setLeftRightTop( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setLeftTopBottom( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setLeftRightBottom( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setRightTopBottom( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    private void setLeftRightTopBottom( Window wnd, Rectangle container ){
                        CalcTargetPos ctp = new CalcTargetPos(wnd, container, Position.this);
                        wnd.setLocation(ctp.xTrgt, ctp.yTrgt);
                        wnd.setSize(ctp.wTrgt, ctp.hTrgt);
                    }
                    
                    @Override
                    public void accept(Window wnd) {
                        if( wnd==null )return;
                        
                        int wx = wnd.getX();
                        int wy = wnd.getY();
                        int wh = wnd.getHeight();
                        int ww = wnd.getWidth();
                        
                        //Rectangle bound = desktopRect();
                        Rectangle bound = screenRect();
                        
                        if(       left!=null && right!=null && top!=null && bottom!=null ){
                            setLeftRightTopBottom(wnd, bound);
                        }else if( left!=null && right!=null && top!=null && bottom==null ){
                            setLeftRightTop(wnd, bound);
                        }else if( left!=null && right!=null && top==null && bottom!=null ){
                            setLeftRightBottom(wnd, bound);
                        }else if( left!=null && right!=null && top==null && bottom==null ){
                            setLeftRight(wnd, bound);
                        }else if( left!=null && right==null && top!=null && bottom!=null ){
                            setLeftTopBottom(wnd, bound);
                        }else if( left!=null && right==null && top!=null && bottom==null ){
                            setLeftTop(wnd, bound);
                        }else if( left!=null && right==null && top==null && bottom!=null ){
                            setLeftBottom(wnd, bound);
                        }else if( left!=null && right==null && top==null && bottom==null ){
                            setLeft(wnd, bound);
                        }else if( left==null && right!=null && top!=null && bottom!=null ){
                            setRightTopBottom(wnd, bound);
                        }else if( left==null && right!=null && top!=null && bottom==null ){
                            setRightTop(wnd, bound);
                        }else if( left==null && right!=null && top==null && bottom!=null ){
                            setRightBottom(wnd, bound);
                        }else if( left==null && right!=null && top==null && bottom==null ){
                            setRight(wnd, bound);
                        }else if( left==null && right==null && top!=null && bottom!=null ){
                            setTopBottom(wnd, bound);
                        }else if( left==null && right==null && top!=null && bottom==null ){
                            setTop(wnd, bound);
                        }else if( left==null && right==null && top==null && bottom!=null ){
                            setBottom(wnd, bound);
                        }else if( left==null && right==null && top==null && bottom==null ){
                        }
                    }
                };
                
                actions.add(rw);
                return WindowReciverBuilder.this;
            }
        }
        
        /**
         * Указание расположения окна
         * @return Указание расположения окна
         */
        public Position pos(){
            return new Position();
        }
        
        /**
         * Создание приемника события WindowEvent, который работает когда проиходит событие
         * @return приемник события
         */
        public Consumer<WindowEvent> build(){
            return new Consumer<WindowEvent>() {
                @Override
                public void accept(WindowEvent we) {
                    if( we==null )return;
                    
                    Window wnd = we.getWindow();
                    if( wnd==null )return;
                    
                    if( actions!=null ){
                        for( Consumer<Window> rw : actions ){
                            if( rw!=null )rw.accept(wnd);
                        }
                    }
                }
            };
        }
        
        /**
         * Применяет настройки к окну
         * @param wnd окно
         */
        public void apply(Window wnd){
            if( wnd==null )throw new IllegalArgumentException("wnd == null");
            if( actions!=null ){
                for( Consumer<Window> rw : actions ){
                    if( rw!=null )rw.accept(wnd);
                }
            }
        }
    }
    
    /**
     * Создание настроек расположения окна
     * @return настройки расположения окна
     */
    public static WindowReciverBuilder windowReciver(){
        return new WindowReciverBuilder();
    }
    
    /**
     * Возвращает размеры экрана
     * @return размеры экрана
     */
    public static Rectangle getScreenRectangle(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if( gd!=null ){
            DisplayMode dm = gd.getDisplayMode();
            if( dm!=null ){
                return new Rectangle(dm.getWidth(), dm.getHeight());
            }
        }

        Point p = ge.getCenterPoint();
        return new Rectangle(0, 0, p.x * 2, p.y * 2);
    }
    
    /**
     * Проверкяет на совпадение комбинации клавиш и событие нажатия на клавиутаре
     * @param ke Событие нажатие на клавиатуре
     * @param keyStrokes комбинации клавиш
     * @return true - есть совпадения / false - нет совпладения
     */
    public static boolean match( KeyEvent ke, KeyStroke ... keyStrokes ){
        if( keyStrokes==null || ke==null )return false;
        
        for( KeyStroke ks : keyStrokes ){
            if( ks==null )continue;
            if( ke.getID() != ks.getKeyEventType() )continue;
            
            if( ke.getID() == KeyEvent.KEY_TYPED ){
                if( ke.getKeyChar() != ks.getKeyChar() )continue;
            }else{
                if( ke.getKeyCode() != ks.getKeyCode() )continue;
            }
            
            int mod = ks.getModifiers();
            boolean shift = (java.awt.event.InputEvent.SHIFT_DOWN_MASK & mod) != 0;
            boolean ctrl = (java.awt.event.InputEvent.CTRL_DOWN_MASK & mod) != 0;
            boolean meta = (java.awt.event.InputEvent.META_DOWN_MASK & mod) != 0;
            boolean alt = (java.awt.event.InputEvent.ALT_DOWN_MASK & mod) != 0;
            boolean altGraph = (java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK & mod) != 0;
            
            //java.awt.event.InputEvent.CTRL_DOWN_MASK
            //java.awt.event.InputEvent.META_DOWN_MASK
            //java.awt.event.InputEvent.ALT_DOWN_MASK
            //java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK
            
            boolean altMatched = Objects.equals(ke.isAltDown(), alt);
            boolean altGrMatched = Objects.equals(ke.isAltGraphDown(), altGraph);
            boolean metaMatched = Objects.equals(ke.isMetaDown(), meta);
            boolean ctrlMatched = Objects.equals(ke.isControlDown(), ctrl);
            boolean shftMatched = Objects.equals(ke.isShiftDown(), shift);
            
            if( !altMatched )continue;
            if( !altGrMatched )continue;
            if( !metaMatched )continue;
            if( !ctrlMatched )continue;
            if( !shftMatched )continue;
            
            return true;
        }
        
        return false;
    }
}
