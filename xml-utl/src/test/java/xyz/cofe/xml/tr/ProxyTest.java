package xyz.cofe.xml.tr;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xyz.cofe.io.fn.IOFun;
import xyz.cofe.iter.Eterable;
import xyz.cofe.xml.XmlUtil;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.List;

public class ProxyTest {
    public interface Itf1 {
        String hello();
    }

    @Test
    public void test01(){
        InvocationHandler hdlr = ( proxy, method, args ) -> {
            System.out.println("call "+method.getName()+" args "+args);
            return null;
        };

        Itf1 itf1 = (Itf1) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Itf1.class}, hdlr);

        System.out.println( itf1.hello() );
    }

    public interface Itf2A {
        @XPath("path1")
        String path1();
    }

    public interface Itf2B extends Itf2A {
        @XPath("path2")
        String path2();
    }

    @Test
    public void test02(){
        Class citf2 = Itf2B.class;
        Eterable.of(citf2.getMethods()).forEach( m->{
            System.out.println("method "+m.getName()+" ann="+m.getAnnotation(XPath.class));
        });
    }

    @Test
    public void test03(){
        URL url = ProxyTest.class.getResource("/xml1.xml");
        if( url==null )return;

        try{
            String xmlStr = IOFun.readText(url,"utf-8");
            System.out.println("xml:\n"+xmlStr);

            Document xmlDoc = XmlUtil.parseXml(xmlStr);
            System.out.println("xml doc:"+xmlDoc);

            System.out.println( XmlUtil.xpath(xmlDoc,"./root/str").as("NONE") );

            Node root = XmlUtil.xpath(xmlDoc,"./root").getNode();
            Assert.assertTrue(root!=null);
            System.out.println(root);

            Node nstr = XmlUtil.xpath(root,"./str").getNode();
            Assert.assertTrue(nstr!=null);

            String nstrContent = XmlUtil.xpath(root, "./str/text()").getString();
            Assert.assertTrue(nstrContent!=null);
        } catch( IOException e ){
            e.printStackTrace();
            return;
        }
    }

    @Test
    public void testXObj01(){
        URL url = ProxyTest.class.getResource("/xml1.xml");
        if( url==null )return;

        try{
            String xmlStr = IOFun.readText(url,"utf-8");
            System.out.println("xml:\n"+xmlStr);

            Document xmlDoc = XmlUtil.parseXml(xmlStr);
            System.out.println("xml doc:"+xmlDoc);

            XPathFactory xfactory = XmlUtil.getXPathFactory();
            javax.xml.xpath.XPath xpath = xfactory.newXPath();

            try{
                XPathExpression strXExp = xpath.compile("./root/str");
                Object strRes = new XIFProxy(xmlDoc).fetch(strXExp,String.class);
                System.out.println("matched="+strRes);

                Assert.assertTrue(strRes!=null);
                Assert.assertTrue(strRes instanceof String);
                Assert.assertTrue(strRes.toString().equals("hello"));

                XPathExpression numXExp = xpath.compile("./root/num");
                Object numRes = new XIFProxy(xmlDoc).fetch(numXExp,Integer.class);
                System.out.println("matched="+numRes);

                Assert.assertTrue(numRes!=null);
                Assert.assertTrue(numRes instanceof Number);
                Assert.assertTrue(((Number)numRes).intValue() == 123);

                XPathExpression nonExistsXExp = xpath.compile("./root/num/notag");
                nonExistsXExp.evaluate(xmlDoc);

            } catch( XPathExpressionException e ){
                throw new Error(e);
            }

        } catch( IOException e ){
            e.printStackTrace();
            return;
        }
    }

    public interface TestXObj02 {
        @XPath("root/str")
        String str();

        @XPath("root/num")
        Integer num();

        List<List<Integer>> invalidType();
        List<TestXObj02> validType();
    }

    @Test
    public void testXObj02(){
        URL url = ProxyTest.class.getResource("/xml1.xml");
        if( url==null )return;

        try{
            String xmlStr = IOFun.readText(url,"utf-8");
            System.out.println("xml:\n"+xmlStr);

            Document xmlDoc = XmlUtil.parseXml(xmlStr);
            System.out.println("xml doc:"+xmlDoc);

            TestXObj02 xo = XIFProxy.proxy(xmlDoc,TestXObj02.class);
            Assert.assertTrue(xo!=null);

            System.out.println("str="+xo.str());
            System.out.println("num="+xo.num());

            Method m_invalidType = TestXObj02.class.getMethod("invalidType");
            XIFProxy ext1 = new XIFProxy(xmlDoc);
            boolean allow_ext1 = ext1.isFetchable(m_invalidType.getGenericReturnType());
            Assert.assertTrue(!allow_ext1);

            Method m_validType = TestXObj02.class.getMethod("validType");
            boolean allow_ext2 = ext1.isFetchable(m_validType.getGenericReturnType());
            Assert.assertTrue(allow_ext2);

        } catch( NoSuchMethodException | IOException e ){
            e.printStackTrace();
        }
    }

    public interface TestXObj3 {
        @XPath("/root/contact")
        List<Contact> rootContacts();
    }

    public interface Contact {
        @XPath("name")
        String name();

        @XPath("follow/contact")
        List<Contact> follow();
    }

    @Test
    public void testXObj3(){
        URL url = ProxyTest.class.getResource("/xml2.xml");
        if( url==null )return;

        try{
            String xmlStr = IOFun.readText(url,"utf-8");
            System.out.println("xml:\n"+xmlStr);

            Document xmlDoc = XmlUtil.parseXml(xmlStr);
            System.out.println("xml doc:"+xmlDoc);

            TestXObj3 xo = XIFProxy.proxy(xmlDoc,TestXObj3.class);
            List<Contact> rootContacts = xo.rootContacts();
            Assert.assertTrue(rootContacts!=null);
            for( Contact rc : rootContacts ){
                Assert.assertTrue(rc!=null );
                System.out.println("root="+rc.name());

                List<Contact> follow = rc.follow();
                Assert.assertTrue(follow!=null );
                for( Contact foll : follow ){
                    System.out.println("follow="+foll.name());
                }
            }
        } catch( IOException e ){
            e.printStackTrace();
        }
    }
}
