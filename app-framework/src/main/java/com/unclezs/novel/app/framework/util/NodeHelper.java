package com.unclezs.novel.app.framework.util;


import com.unclezs.novel.app.framework.exception.FxException;
import javafx.css.Styleable;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollBar;
import lombok.experimental.UtilityClass;


/**
 * 视图组件创建工具
 *
 * @author blog.unclezs.com
 * @since 2021/03/02 14:41
 */
@UtilityClass
public class NodeHelper {

  public static final String SELECTOR_SCROLL_BAR = ".scroll-bar";

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

  /**
   * 查找滚动条
   *
   * @param node        容器
   * @param orientation 方向
   * @return scrollBar
   */
  @FXML
  public static ScrollBar findScrollBar(Node node, Orientation orientation) {
    for (Node view : node.lookupAll(SELECTOR_SCROLL_BAR)) {
      if (view instanceof ScrollBar) {
        ScrollBar scrollBar = (ScrollBar) view;
        if (scrollBar.getOrientation() == orientation) {
          return scrollBar;
        }
      }
    }
    throw new FxException("滚动条未找到：{}", node);
  }
}
