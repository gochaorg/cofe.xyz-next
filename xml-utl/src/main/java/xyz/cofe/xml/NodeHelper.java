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
package xyz.cofe.xml;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * Поддержка работы с узлами XML
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class NodeHelper 
implements Node
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(NodeHelper.class.getName()).log(Level.FINE, message, args);
    }
    
    private static void logFiner(String message,Object ... args){
        Logger.getLogger(NodeHelper.class.getName()).log(Level.FINER, message, args);
    }
    
    private static void logFinest(String message,Object ... args){
        Logger.getLogger(NodeHelper.class.getName()).log(Level.FINEST, message, args);
    }
    
    private static void logInfo(String message,Object ... args){
        Logger.getLogger(NodeHelper.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(NodeHelper.class.getName()).log(Level.WARNING, message, args);
    }
    
    private static void logSevere(String message,Object ... args){
        Logger.getLogger(NodeHelper.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(NodeHelper.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public final Node node;

    /**
     * Конструктор
     * @param node узел xml 
     */
    public NodeHelper( Node node ){
        if( node==null )throw new IllegalArgumentException( "node==null" );
        this.node = node;
    }

    //<editor-fold defaultstate="collapsed" desc="delegate to element">
    @Override
    public String getNodeName() {
        return node.getNodeName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return node.getNodeValue();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        node.setNodeValue(nodeValue);
    }

    @Override
    public short getNodeType() {
        return node.getNodeType();
    }

    @Override
    public Node getParentNode() {
        return node.getParentNode();
    }

    @Override
    public NodeList getChildNodes() {
        return node.getChildNodes();
    }

    @Override
    public Node getFirstChild() {
        return node.getFirstChild();
    }

    @Override
    public Node getLastChild() {
        return node.getLastChild();
    }

    @Override
    public Node getPreviousSibling() {
        return node.getPreviousSibling();
    }

    @Override
    public Node getNextSibling() {
        return node.getNextSibling();
    }

    @Override
    public NamedNodeMap getAttributes() {
        return node.getAttributes();
    }

    @Override
    public Document getOwnerDocument() {
        return node.getOwnerDocument();
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return node.insertBefore(newChild, refChild);
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return node.replaceChild(newChild, oldChild);
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        return node.removeChild(oldChild);
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        return node.appendChild(newChild);
    }

    @Override
    public boolean hasChildNodes() {
        return node.hasChildNodes();
    }

    @Override
    public Node cloneNode(boolean deep) {
        return node.cloneNode(deep);
    }

    @Override
    public void normalize() {
        node.normalize();
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return node.isSupported(feature, version);
    }

    @Override
    public String getNamespaceURI() {
        return node.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return node.getPrefix();
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        node.setPrefix(prefix);
    }

    @Override
    public String getLocalName() {
        return node.getLocalName();
    }

    @Override
    public boolean hasAttributes() {
        return node.hasAttributes();
    }

    @Override
    public String getBaseURI() {
        return node.getBaseURI();
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        return node.compareDocumentPosition(other);
    }

    @Override
    public String getTextContent() throws DOMException {
        return node.getTextContent();
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        node.setTextContent(textContent);
    }

    @Override
    public boolean isSameNode(Node other) {
        return node.isSameNode(other);
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return node.lookupPrefix(namespaceURI);
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return node.isDefaultNamespace(namespaceURI);
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return node.lookupNamespaceURI(prefix);
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return node.isEqualNode(arg);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return node.getFeature(feature, version);
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return node.setUserData(key, data, handler);
    }

    @Override
    public Object getUserData(String key) {
        return node.getUserData(key);
    }
    //</editor-fold>

    /**
     * Возвращает вложенные(дочерние) тэги
     * @return вложенные тэги
     */
    public List<ElementHelper> getElements(){
        List<ElementHelper> els = new ArrayList<ElementHelper>();
        for( Element e : XmlUtil.elements(node) ){
            if( e!=null )els.add( new ElementHelper(e) );
        }
        return els;
    }

    /**
     * Выполняет запрос XPath к заданному узлу
     * @param xpath запрос
     * @return Строка или null
     */
    public String xpathString( String xpath ){
        return XmlUtil.xpath(node, xpath).getString();
    }

    /**
     * Выполняет запрос XPath к заданному узлу
     * @param xpath запрос
     * @return узлы или null
     */
    public List<ElementHelper> xpathElements( String xpath ){
        NodeList nl = XmlUtil.xpath(node, xpath).getNodeList();
        List<ElementHelper> els = new ArrayList<ElementHelper>();
        for( int i=0; i<nl.getLength(); i++ ){
            Node n = nl.item(i);
            if( n instanceof Element ){
                els.add( new ElementHelper((Element)n) );
            }
        }
        return els;
    }
}
