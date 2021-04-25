package com.unclezs.novel.app.framework.util;


import com.unclezs.novel.app.framework.exception.FxException;
import javafx.css.Styleable;
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

  public static final String SELECTOR_SCROLL_BAR_VERTICAL = ".scroll-bar:vertical";
  public static final String SELECTOR_SCROLL_BAR_HORIZONTAL = ".scrollbar:horizontal";

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
   * 查找纵向滚动条
   *
   * @param node 容器
   * @return scrollBar
   */
  public static ScrollBar findVBar(Node node) {
    ScrollBar vBar = (ScrollBar) node.lookup(SELECTOR_SCROLL_BAR_VERTICAL);
    if (vBar != null) {
      return vBar;
    }
    throw new FxException("纵向滚动条未找到：{}", node);
  }

  /**
   * 查找横向滚动条
   *
   * @param node 容器
   * @return scrollBar
   */
  public static ScrollBar findHBar(Node node) {
    ScrollBar vBar = (ScrollBar) node.lookup(SELECTOR_SCROLL_BAR_HORIZONTAL);
    if (vBar != null) {
      return vBar;
    }
    throw new FxException("横向滚动条未找到：{}", node);
  }
}
