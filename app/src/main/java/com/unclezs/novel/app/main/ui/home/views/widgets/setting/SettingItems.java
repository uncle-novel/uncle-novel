package com.unclezs.novel.app.main.ui.home.views.widgets.setting;

import com.unclezs.novel.app.framework.util.NodeHelper;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/4/28 11:29
 */
public class SettingItems extends VBox {

  private final Label nameLabel;
  private final VBox itemBox;
  @Getter
  private List<Node> items;

  public SettingItems(@NamedArg("name") String name, @NamedArg("items") List<Node> items) {
    this(name);
    setItems(items.toArray(Node[]::new));
  }

  public SettingItems(String name) {
    getStyleClass().addAll("setting-items");
    nameLabel = NodeHelper.addClass(new Label(name), "items-name");
    itemBox = NodeHelper.addClass(new VBox(), "items-box");
    getChildren().addAll(nameLabel, itemBox);
  }

  public void setItems(Node... items) {
    itemBox.getChildren().setAll(items);
  }
}
