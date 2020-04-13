/*
 * The MIT License
 *
 * Copyright 2017 user.
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

package xyz.cofe.gui.swing.color;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Условный модификатор цвета, взависимости от длины цикла/фазы
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class NColorModificator extends ColorModificator {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(NColorModificator.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }

    private static boolean isLogSevere(){
        Level logLevel = logger.getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }

    private static boolean isLogWarning(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }

    private static boolean isLogInfo(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }

    private static boolean isLogFine(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }

    private static boolean isLogFiner(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }

    private static boolean isLogFinest(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
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

    private static void logEntering(String method,Object ... params){
        logger.entering(NColorModificator.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(NColorModificator.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(NColorModificator.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умоланию
     */
    public NColorModificator() {
    }

    /**
     * Конструктор по умоланию
     * @param phase фаза
     * @param cycle длинна цикла
     * @param src безусловый модификатор
     */
    public NColorModificator(int phase, int cycle, ColorModificator src) {
        super(src);
        this.phase = phase;
        this.cycle = cycle;
    }

    /**
     * Конструктор копирования
     * @param src образец для копирования
     */
    public NColorModificator(NColorModificator src) {
        super(src);
        if( src!=null ){
            this.phase = src.phase;
            this.cycle = src.cycle;
        }
    }

    @Override
    public NColorModificator clone(){
        return new NColorModificator(this);
    }

    protected int phase = 0;

    /**
     * Указывает фазу цикла
     * @return фаза цикла
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Указывает фазу цикла
     * @param phase  фаза цикла
     */
    public void setPhase(int phase) {
        this.phase = phase;
    }

    /**
     * Указывает фазу цикла
     * @param phase фаза цикла
     * @return self ссылка
     */
    public NColorModificator phase(int phase){
        NColorModificator cm = clone();
        cm.setPhase(phase);
        return cm;
    }

    protected int cycle = 2;

    /**
     * Указывает длинну цикла
     * @return Длинная цикла
     */
    public int getCycle() {
        return cycle;
    }

    /**
     * Указывает длинну цикла
     * @param cycle Длинная цикла
     */
    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    /**
     * Указывает длинну цикла
     * @param cycle Длинная цикла
     * @return self ссылка
     */
    public NColorModificator cycle(int cycle){
        NColorModificator cm = clone();
        cm.setCycle(cycle);
        return cm;
    }

    @Override
    public NColorModificator alpha(float alpha) {
        return (NColorModificator)super.alpha(alpha);
    }

    @Override
    public NColorModificator alpher(float addAplha) {
        return (NColorModificator)super.alpher(addAplha);
    }

    @Override
    public NColorModificator rotate(int rotateHue) {
        return (NColorModificator)super.rotate(rotateHue);
    }

    @Override
    public NColorModificator rotate(float rotateHue) {
        return (NColorModificator)super.rotate(rotateHue);
    }

    @Override
    public NColorModificator hue(int newHue) {
        return (NColorModificator)super.hue(newHue);
    }

    @Override
    public NColorModificator hue(float newHue) {
        return (NColorModificator)super.hue(newHue);
    }

    @Override
    public NColorModificator sate(float addSaturation) {
        return (NColorModificator)super.sate(addSaturation);
    }

    @Override
    public NColorModificator saturation(float newSaturation) {
        return (NColorModificator)super.saturation(newSaturation);
    }

    @Override
    public NColorModificator brighter(float addBright) {
        return (NColorModificator)super.brighter(addBright);
    }

    @Override
    public NColorModificator bright(float newBright) {
        return (NColorModificator)super.bright(newBright);
    }
}
