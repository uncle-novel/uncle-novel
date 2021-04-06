package com.unclezs.novel.app.packager.util;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML 工具
 *
 * @author blog.unclezs.com
 * @date 2021/4/6 21:07
 */
@UtilityClass
public class XmlUtils {

  /**
   * Pretiffy an XML file
   *
   * @param file Xml file
   * @throws Exception Something went wrong
   */
  public static void prettify(File file) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(file);
    trimWhitespace(document);
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
    transformer.transform(new DOMSource(document), new StreamResult(file));
  }

  /**
   * Removes whitespaces from nodes
   *
   * @param node Root node
   */
  public static void trimWhitespace(Node node) {
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); ++i) {
      Node child = children.item(i);
      if (child.getNodeType() == Node.TEXT_NODE) {
        child.setTextContent(child.getTextContent().trim());
      }
      trimWhitespace(child);
    }
  }

}
