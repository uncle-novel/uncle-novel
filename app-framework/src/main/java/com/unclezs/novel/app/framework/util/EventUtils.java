package com.unclezs.novel.app.framework.util;

import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @since 2021/4/24 20:35
 */
@UtilityClass
public class EventUtils {

  public static final int DOUBLE_CLICK_COUNT = 2;

  public static void setOnMousePrimaryClick(Node node, Consumer<MouseEvent> handler) {
    node.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY) {
        handler.accept(event);
      }
    });
  }

  public static void setOnMouseDoubleClick(Node node, Consumer<MouseEvent> handler) {
    node.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == DOUBLE_CLICK_COUNT) {
        handler.accept(event);
      }
    });
  }
}
