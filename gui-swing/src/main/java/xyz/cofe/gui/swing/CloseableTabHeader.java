/*
 * The MIT License
 *
 * Copyright 2015 Kamnev Georgiy (nt.gocha@gmail.com).
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
import java.awt.event.ActionEvent;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTabbedPane;
import xyz.cofe.gui.swing.BasicAction;
import xyz.cofe.gui.swing.Icon;

/**
 * Заголовок вкладки с кнопкой закрыть
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class CloseableTabHeader
extends TabHeader
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(CloseableTabHeader.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(CloseableTabHeader.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(CloseableTabHeader.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(CloseableTabHeader.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(CloseableTabHeader.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(CloseableTabHeader.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(CloseableTabHeader.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>
    
    /**
     * Закрыть вкладку
     */
    public static class CloseAction extends BasicAction implements Closeable
    {
        private JTabbedPane tabbedPane = null;
        private Component tabOwner = null;
        
        /**
         * Иконка закрытия вкладки
         */
        private static Icon closeButtonIcon =
                new xyz.cofe.gui.swing.Icon(
                        TabPane.class.getResource("close-14x14-norm.png"));

        public CloseAction(JTabbedPane tabbedPane, Component tabOwner){
            if (tabOwner== null) {
                throw new IllegalArgumentException("tabOwner==null");
            }
            if (tabbedPane== null) {
                throw new IllegalArgumentException("tabbedPane==null");
            }
            this.tabbedPane = tabbedPane;
            this.tabOwner = tabOwner;

            setSmallIcon(closeButtonIcon);
            setShortDescription("Закрыть");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if( tabOwner==null || tabbedPane==null )return;
            int idx = tabbedPane.indexOfComponent(tabOwner);
            if( idx<0 )return;
            tabbedPane.removeTabAt(idx);
        }

        @Override
        public void close() throws IOException {
            tabOwner = null;
            tabbedPane = null;
        }
    }

    /**
     * Конструктор
     * @param tabbedPane Таб-панель
     * @param tabOwner Владелец вкладки
     * @param useOwnerName Использовать имя владельца в качестве текста заголовка
     */
    public CloseableTabHeader(JTabbedPane tabbedPane, Component tabOwner, boolean useOwnerName)
    {
        super(tabbedPane,tabOwner,useOwnerName);
        getActions().add(new CloseAction(tabbedPane, tabOwner));
    }
}
