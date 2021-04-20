package com.unclezs.novel.app.framework.util;


import javafx.css.Styleable;
import javafx.scene.Parent;
import lombok.experimental.UtilityClass;


/**
 * 视图组件创建工具
 *
 * @author blog.unclezs.com
 * @since 2021/03/02 14:41
 */
@UtilityClass
public class NodeHelper {

  /**
   * 给view设置css class
   *
   * @param node       节点
   * @param classNames 类名列表
   */
  public static <T extends Styleable> T addClass(T node, String... classNames) {
    if (node == null) {
      return null;
    }
    if (classNames != null) {
      node.getStyleClass().addAll(classNames);
    }
    return node;
  }

  /**
   * 给view设置css class
   *
   * @param styleSheets class样式路径
   * @param node        节点
   * @param classNames  类名列表
   */
  @SuppressWarnings("unused")
  public static <T extends Parent> T addStyleSheet(T node, String styleSheets, String... classNames) {
    if (node == null) {
      return null;
    }
    if (styleSheets != null) {
      node.getStylesheets().add(styleSheets);
    }
    addClass(node, classNames);
    return node;
  }


}
