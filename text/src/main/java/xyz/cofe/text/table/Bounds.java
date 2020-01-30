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

package xyz.cofe.text.table;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Хранит размеры текстового блока
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class Bounds {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Bounds.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Bounds.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(Bounds.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Bounds.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Bounds.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Bounds.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Bounds.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Кол-во символов по ширене
     */
    protected int width = 0;

    /**
     * Кол-во симвлов по высоте
     */
    protected int height = 0;

    /**
     * Конструктор
     * @param w ширина блока
     * @param h высота влока
     */
    public Bounds(int w,int h){
        this.width = w;
        this.height = h;
    }

    /**
     * Конструктор копирования
     * @param s образец
     */
    public Bounds(Bounds s){
        if( s!=null ){
            this.width = s.width;
            this.height = s.height;
        }
    }

    /**
     * Возвращает ширину блока
     * @return ширина
     */
    public int getWidth() {
        return width;
    }

    /**
     * Возвращает высоту блока
     * @return Высота блока
     */
    public int getHeight() {
        return height;
    }

    /**
     * Клонирует объект
     * @return клон
     */
    @Override
    public Bounds clone(){
        return new Bounds(this);
    }

    /**
     * Вычисляет максимальные габариты
     * @param tcells текстовые ячейки
     * @return максимальные габариты
     */
    public static Bounds max( TextCell ... tcells ){
        int maxHeight = -1;
        int maxWidth = -1;
        for( TextCell tc : tcells ){
            int h = tc.getHeight();
            if( maxHeight<h )maxHeight = h;

            int w = tc.getMaxWidth();
            if( maxWidth<w )maxWidth = w;
        }

        Bounds b = new Bounds(maxWidth,maxHeight);
        return b;
    }

    /**
     * Вычисляет максимальные габариты
     * @param lines текстовые ячейки
     * @return габариты - по ширине соответ. максимальной длинной строке, по высоте кол-ву строк
     */
    public static Bounds get( String ... lines ){
        if( lines==null )throw new IllegalArgumentException( "lines==null" );
        int w = 0;
        int h = lines.length;
        for( String line : lines ){
            if( w<line.length() )w=line.length();
        }
        return new Bounds(w, h);
    }
}
