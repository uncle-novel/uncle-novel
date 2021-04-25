package com.unclezs.novel.app.main.ui.home.views.widgets.rule;

import com.unclezs.novel.app.framework.collection.SimpleObservableList;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

/**
 * 规则项目集合
 *
 * @author blog.unclezs.com
 * @date 2021/4/22 11:33
 */
@DefaultProperty("items")
public class RuleItems extends VBox {

  @Getter
  private final List<RuleItem> items = new SimpleObservableList<>() {
    @Override
    public void onAdd(RuleItem item) {
      getChildren().add(item);
    }

    @Override
    public void onRemove(RuleItem item) {
      getChildren().remove(item);
    }
  };
  private final HBox titleBox = NodeHelper.addClass(new HBox(), "title-box");
  /**
   * 标题
   */
  @Getter
  private String title;
  /**
   * 启动debug按钮
   */
  @Getter
  private boolean debug;
  /**
   * 调试按钮点击事件监听
   */
  @Getter
  @Setter
  private EventHandler<? super MouseEvent> onDebug;


  public RuleItems() {
    getStyleClass().addAll("items", "no-name");
  }

  public void setTitle(String title) {
    this.title = title;
    addTitleBox();
    titleBox.getChildren().add(0, NodeHelper.addClass(new Label(title), "title"));
    getStyleClass().remove("no-name");
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
    if (debug) {
      addTitleBox();
      IconButton debugButton = new IconButton(IconFont.DEBUG, "调试");
      debugButton.setOnMouseClicked(e -> {
        if (onDebug != null) {
          onDebug.handle(e);
        }
      });
      titleBox.getChildren().add(debugButton);
    }
  }

  private void addTitleBox() {
    if (titleBox.getChildren().isEmpty()) {
      getChildren().add(0, titleBox);
    }
  }
}
