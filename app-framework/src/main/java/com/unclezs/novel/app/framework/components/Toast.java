package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @date 2021/4/21 9:36
 */
@Slf4j
public class Toast {

  public static final String DEFAULT_STYLE_CLASS = "toast";
  public static final long DEFAULT_SHOW_TIME = 2000L;
  public static final int MAX_TOAST_COUNT = 3;
  private static final VBox BOX = NodeHelper.addClass(new VBox(), "box");

  /**
   * 提示消息样式的toast
   *
   * @param message 消息
   */
  public static void info(String message) {
    toast(null, message, Type.INFO, DEFAULT_SHOW_TIME);
  }

  /**
   * 提示消息样式的toast
   *
   * @param message 消息
   */
  public static void info(StackPane container, String message) {
    toast(container, message, Type.INFO);
  }

  /**
   * 警告样式的toast
   *
   * @param message 消息
   */
  public static void warn(String message) {
    toast(null, message, Type.WARN, DEFAULT_SHOW_TIME);
  }

  /**
   * 警告消息样式的toast
   *
   * @param message 消息
   */
  public static void warn(StackPane container, String message) {
    toast(container, message, Type.WARN);
  }

  /**
   * 成功样式的toast
   *
   * @param message 消息
   */
  public static void success(String message) {
    toast(null, message, Type.SUCCESS, DEFAULT_SHOW_TIME);
  }

  /**
   * 成功消息样式的toast
   *
   * @param message 消息
   */
  public static void success(StackPane container, String message) {
    toast(container, message, Type.SUCCESS);
  }

  /**
   * 错误样式的toast
   *
   * @param message 消息
   */
  public static void error(String message) {
    toast(null, message, Type.ERROR, DEFAULT_SHOW_TIME);
  }

  /**
   * 错误消息样式的toast
   *
   * @param message 消息
   */
  public static void error(StackPane container, String message) {
    toast(container, message, Type.ERROR);
  }

  /**
   * @param container 容器
   * @param message   消息
   * @param type      类型
   */
  public static void toast(StackPane container, String message, Type type) {
    toast(container, message, type, DEFAULT_SHOW_TIME);
  }

  /**
   * @param container 容器
   * @param message   消息
   * @param type      类型
   * @param time      显示时间
   */
  public static void toast(StackPane container, String message, Type type, long time) {
    if (container == null) {
      container = (StackPane) AppContext.getInstance().getPrimaryStage().getScene().getRoot();
    }
    if (container == null) {
      log.warn("吐司容器未指定，并且没有找到默认容器！");
      return;
    }
    Pair<StackPane, Node> toastPair = createToast(message, type);
    container.getChildren().add(toastPair.getKey());
    StackPane finalContainer = container;
    Executor.runFx(() -> {
      // 从父容器移除
      finalContainer.getChildren().remove(toastPair.getKey());
      // 从box移除
      BOX.getChildren().remove(toastPair.getValue());
    }, time);
  }

  /**
   * 创建吐司面板
   *
   * @param msg  消息
   * @param type 类型
   * @return key: toast跟容器，value:toast的标签节点
   */
  private static Pair<StackPane, Node> createToast(String msg, Type type) {
    Label message = NodeHelper.addClass(new Label(msg, new Icon(type.iconFont)), type.name().toLowerCase());
    if (BOX.getChildren().size() == MAX_TOAST_COUNT) {
      BOX.getChildren().remove(0);
    }
    BOX.getChildren().add(message);
    StackPane toast = NodeHelper.addClass(new StackPane(BOX), DEFAULT_STYLE_CLASS);
    toast.setTranslateY(-60);
    toast.setMouseTransparent(true);
    toast.setFocusTraversable(true);
    return new Pair<>(toast, message);
  }

  public enum Type {
    /**
     * 吐司消息类型
     */
    INFO(IconFont.INFO),
    SUCCESS(IconFont.SUCCESS),
    ERROR(IconFont.ERROR),
    WARN(IconFont.WARN);
    @Getter
    private final IconFont iconFont;

    Type(IconFont iconFont) {
      this.iconFont = iconFont;
    }
  }
}
