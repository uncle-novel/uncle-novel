package com.unclezs.novel.app.jfx;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 14:28
 */
public class LoaderTest {
    @Test
    public void test() throws Exception {
//        String text = FileUtil.readUtf8String(LoaderTest.class.getResource("/test.txt").toURI().toURL().toString());
//        System.out.println(text);

        long millis = System.currentTimeMillis();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(LoaderTest.class.getResourceAsStream("/test.txt"));
        System.out.println(System.currentTimeMillis() - millis);
        long l = System.currentTimeMillis();
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//*[@id=\"list\"]/dl/dd[1]/a/@href");
        String value = (String) expr.evaluate(document, XPathConstants.STRING);
        System.out.println(value);
        System.out.println(System.currentTimeMillis() - l);
    }
}
