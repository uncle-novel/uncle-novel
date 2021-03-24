package com.unclezs.novel.app.jfx;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathReader implements Closeable {

  private InputStream is = null;

  private Document document;

  /**
   * @param is
   * @return
   */
  public static XPathReader instance(InputStream is) {
    XPathReader pr = new XPathReader();
    pr.is = is;
    try (is) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      pr.document = builder.parse(is);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return pr;
  }

  public static XPathReader instanceFromResource(String resource) {
    return instance(XPathReader.class.getClassLoader().getResourceAsStream(resource));
  }

  /**
   * @param content
   * @return
   */
  public static XPathReader instaceFromString(String content) {
    try {
      InputStream is = new ByteArrayInputStream(content.getBytes());
      return instance(is);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * @param node
   * @param attr
   * @return
   */
  public String attr(Node node, String attr) {
    return node.getAttributes().getNamedItem(attr).getNodeValue();
  }

  /**
   * @param query
   * @param node
   * @return
   */
  public Node singleNode(String query, Node node) {
    try {
      XPath xpath = XPathFactory.newInstance().newXPath();
      XPathExpression expr = xpath.compile(query);

      NodeList nodeList = (NodeList) expr.evaluate(node, XPathConstants.NODESET);

      if (null != nodeList && nodeList.getLength() > 0) {
        return nodeList.item(0);
      } else {
        return null;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param query
   * @param node
   * @return
   */
  public List<Node> nodeList(String query, Node node) {
    List<Node> nodes = new ArrayList<>();

    try {
      XPath xpath = XPathFactory.newInstance().newXPath();
      XPathExpression expr = xpath.compile(query);

      NodeList nodeList = (NodeList) expr.evaluate(node, XPathConstants.NODESET);

      if (null != nodeList && nodeList.getLength() > 0) {
        for (int i = 0; i < nodeList.getLength(); i++) {
          nodes.add(nodeList.item(i));
        }
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return nodes;
  }

  /**
   * @param query
   * @return
   */
  public List<Node> nodeList(String query) {
    return nodeList(query, document);
  }

  /**
   * @param query
   * @return
   */
  public Node singleNode(String query) {
    return singleNode(query, document);

  }

  /**
   * @param query
   * @return
   */
  public boolean exists(String query) {
    return !isEmpty(nodeList(query));
  }

  /**
   * @param nl
   * @return
   */
  private boolean isEmpty(List<Node> nl) {
    return nl.isEmpty();
  }

  /**
   * @param query
   * @return
   */
  public String value(String query) {
    try {
      XPath xpath = XPathFactory.newInstance().newXPath();
      XPathExpression expr = xpath.compile(query);

      return (String) expr.evaluate(document, XPathConstants.STRING);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void close() throws IOException {
    is.close();
  }

}
