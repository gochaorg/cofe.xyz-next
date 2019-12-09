package xyz.cofe.gui.swing.properties;

import xyz.cofe.gui.swing.bean.UiBean;
import xyz.cofe.gui.swing.properties.editor.FileEditor;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@UiBean( hiddenPeroperties = {"class"} )
public class TestNode {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TestNode.class.getName());
    private static final Level logLevel = logger.getLevel();

    private static final boolean isLogSevere =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.SEVERE.intValue();

    private static final boolean isLogWarning =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.WARNING.intValue();

    private static final boolean isLogInfo =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.INFO.intValue();

    private static final boolean isLogFine =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINE.intValue();

    private static final boolean isLogFiner =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINER.intValue();

    private static final boolean isLogFinest =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINEST.intValue();

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
        logger.entering(TestNode.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TestNode.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TestNode.class.getName(), method, result);
    }
    //</editor-fold>

    private static Random rnd = new Random();
    public static final AtomicInteger idseq = new AtomicInteger(0);

    public int id = idseq.incrementAndGet();
    private final PropertyChangeSupport propcsup;

    public TestNode(){
        propcsup = new PropertyChangeSupport(this);
    }

    public TestNode(String name){
        this.name = name;
        propcsup = new PropertyChangeSupport(this);
    }

    private String[] itms = new String[]{
        "alpha", "beta", "gamma", "delta", "epsilon", "zeta", "eta", "theta",
        "iota", "kappa", "lambda", "mu", "nu", "xi", "pi", "rho", "sigma", "tau",
        "upsilon", "phi", "chi", "psi", "omega"
    };

    @UiBean(shortDescription = "Идентификатор объекта", displayName = "identifier")
    public int getId(){ return id; }

    public void addPropertyChangeListener( PropertyChangeListener listener) {
        this.propcsup.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propcsup.removePropertyChangeListener(listener);
    }

    //<editor-fold defaultstate="collapsed" desc="booleans">
    //<editor-fold defaultstate="collapsed" desc="boolVal">
    protected boolean boolVal = false;

    public boolean isBoolVal() {
        return boolVal;
    }
    public void setBoolVal(boolean boolVal) {
        this.boolVal = boolVal;
        System.out.println("setBoolVal "+boolVal);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="booleanVal">
    protected Boolean booleanVal;

    public Boolean getBooleanVal() {
        return booleanVal;
    }

    public void setBooleanVal(Boolean booleanVal) {
        this.booleanVal = booleanVal;
        System.out.println("setBooleanVal "+booleanVal);
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="shortVal">
    private short shortVal;
    public short getShortVal() {
        return shortVal;
    }
    public void setShortVal(short shortVal) {
        this.shortVal = shortVal;
        System.out.println("setShortVal "+shortVal);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="shortValue">
    private Short shortValue;
    public Short getShortValue() {
        return shortValue;
    }
    public void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
        System.out.println("setShortValue "+shortValue);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="intVal">
    private int intVal = 0;
    public int getIntVal() {
        return intVal;
    }
    public void setIntVal(int intVal) {
        this.intVal = intVal;
        System.out.println("setIntVal "+intVal);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="integerVal">
    private Integer integerVal;
    public Integer getIntegerVal() {
        return integerVal;
    }
    public void setIntegerVal(Integer integerVal) {
        this.integerVal = integerVal;
        System.out.println("setIntegerVal "+integerVal);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="longVal">
    private long longVal;
    public long getLongVal() {
        return longVal;
    }
    public void setLongVal(long longVal) {
        this.longVal = longVal;
        System.out.println("setLongVal "+longVal);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="longValue">
    private Long longValue;
    public Long getLongValue() {
        return longValue;
    }
    public void setLongValue(Long longValue) {
        this.longValue = longValue;
        System.out.println("setLongValue "+longValue);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="floatVal">
    private float floatVal;

    public float getFloatVal() {
        return floatVal;
    }

    public void setFloatVal(float floatVal) {
        this.floatVal = floatVal;
        System.out.println("setFloatVal "+floatVal);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="floatValue">
    private Float floatValue;
    public Float getFloatValue() {
        return floatValue;
    }
    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
        System.out.println("setFloatValue "+floatValue);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="doubleVal">
    private double doubleVal;
    public double getDoubleVal() {
        return doubleVal;
    }
    public void setDoubleVal(double doubleVal) {
        this.doubleVal = doubleVal;
        System.out.println("setDoubleVal "+doubleVal);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="doubleValue">
    private Double doubleValue;
    public Double getDoubleValue() {
        return doubleValue;
    }
    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
        System.out.println("setDoubleValue "+doubleValue);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="en">
    public static enum EN1 {
        val1, val2, val3, val4
    }
    protected EN1 en = EN1.val1;
    public EN1 getEn() {
        return en;
    }
    public void setEn(EN1 en) {
        this.en = en;
        System.out.println("selected en = "+en);
    }
    //</editor-fold>
    public String getInvalidProp(){ return "invalid"; }

    //<editor-fold defaultstate="collapsed" desc="nameChanges">
    protected int nameChanges = 0;

    public int getNameChanges() {
        return nameChanges;
    }

    public void setNameChanges(int nameChanges) {
        Object old = this.nameChanges;
        this.nameChanges = nameChanges;
        propcsup.firePropertyChange("nameChanges", old, nameChanges);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="name : String">
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        System.out.println("setted name from="+old+" to="+name);
        propcsup.firePropertyChange("name", old, name);

        setNameChanges(getNameChanges()+1);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="strings : List<String>">
    protected List<String> strings;

    public List<String> getStrings() {
        if( strings!=null ){
            return strings;
        }
        strings = new ArrayList<String>();
        strings.addAll(Arrays.asList(
            "line 1",
            "line two",
            "another line",
            "Lorem ipsum dolor sit amet,",
            " consectetur adipiscing elit. ",
            "Nulla faucibus pretium nibh, ",
            "quis cursus arcu porta non. ",
            "Cras faucibus risus sem. ",
            "Donec venenatis luctus erat ",
            "eget vehicula. ",
            "Donec ut ex nec ex luctus ",
            "accumsan in eget eros. ",
            "Donec suscipit eu justo ",
            "sed scelerisque. ",
            "Cras a feugiat nisl, ",
            "lobortis malesuada odio."
        ));
        return strings;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="children : List<TestNode>">
    protected List<TestNode> children;

    @UiBean(hiddenPeroperties = {"class", "empty"})
    public List<TestNode> getChildren(){
        if( children!=null ){
            return children;
        }

        children = new ArrayList<TestNode>();
        for( int i=0; i<itms.length/2 + rnd.nextInt(itms.length/2); i++ )
            children.add(new TestNode(itms[i]));

        return children;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="set : Set<TestNode>">
    protected Set<TestNode> set;

    @UiBean(hiddenPeroperties = {"class", "empty"})
    public Set<TestNode> getSet(){
        if( set!=null )return set;
//            set = new LinkedHashSet<TestNode>();
        set = new TreeSet<>(new Comparator<TestNode>() {
            @Override
            public int compare(TestNode o1, TestNode o2) {
                if( o1==null && o2==null )return 0;
                if( o1!=null && o2==null )return -1;
                if( o1==null && o2!=null )return 1;

                String n1 = o1.getName();
                String n2 = o2.getName();

                if( n1==null && n2==null )return 0;
                if( n1!=null && n2==null )return -1;
                if( n1==null && n2!=null )return 1;

                return n1.compareTo(n2);
            }
        }
        );
        for( int i=0; i<5+rnd.nextInt(itms.length); i++ ){
            set.add(new TestNode(itms[i%itms.length]));
        }
        return set;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="map : Map<TestNode, TestNode>">
    protected Map<TestNode, TestNode> map;

    @UiBean(hiddenPeroperties = {"class", "empty"})
    public Map<TestNode,TestNode> getMap(){
        if( map!=null )return map;
//            map = new LinkedHashMap<TestNode, TestNode>();
        map = new TreeMap<>(new Comparator<TestNode>() {
            @Override
            public int compare(TestNode o1, TestNode o2) {
                if( o1==null && o2==null )return 0;
                if( o1!=null && o2==null )return -1;
                if( o1==null && o2!=null )return 1;

                String n1 = o1.getName();
                String n2 = o2.getName();

                if( n1==null && n2==null )return 0;
                if( n1!=null && n2==null )return -1;
                if( n1==null && n2!=null )return 1;

                return n1.compareTo(n2);
            }
        });
        for( int i=0; i<5+rnd.nextInt(itms.length); i++ ){
            map.put(
                new TestNode(itms[rnd.nextInt(itms.length)])
                ,new TestNode(itms[rnd.nextInt(itms.length)])
            );
        }
        return map;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hashCode(), equals()">
//        @Override
//        public int hashCode() {
//            int hash = 3;
//            hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
//            return hash;
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            }
//            if (obj == null) {
//                return false;
//            }
//            if (getClass() != obj.getClass()) {
//                return false;
//            }
//            final TestNode other = (TestNode) obj;
//            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
//                return false;
//            }
//            return true;
//        }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="toString()">
    @Override
    public String toString() {
        return "TestNode{"+"name="+name+" id="+id+'}';
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nodeColor">
    protected Color nodeColor = null;
    protected Color[] avaliabeColors = new Color[]{
        Color.black, Color.white, Color.red, Color.blue, Color.green, Color.CYAN, Color.DARK_GRAY
        ,Color.GRAY,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.YELLOW
    };

    public Color getNodeColor() {
        if( nodeColor==null ){
            int i = rnd.nextInt(avaliabeColors.length);
            nodeColor = avaliabeColors[i];
        }
        return nodeColor;
    }

    public void setNodeColor(Color nodeColor) {
        this.nodeColor = nodeColor;
    }

    protected Color testColor = null;

    @UiBean(propertyEditor = TColorEditor.class)
    public Color getTestColor() {
        if( testColor==null ){
            int i = rnd.nextInt(avaliabeColors.length);
            testColor = avaliabeColors[i];
        }
        return testColor;
    }

    public void setTestColor(Color testColor) {
        this.testColor = testColor;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="file">
    private java.io.File file;

    @UiBean(propertyEditor = FileEditor.class)
    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
        System.out.println("setFile "+file);
    }
    //</editor-fold>
}
