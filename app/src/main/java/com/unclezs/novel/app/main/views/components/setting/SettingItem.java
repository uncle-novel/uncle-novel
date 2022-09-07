package com.unclezs.novel.app.main.views.components.setting;

import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.beans.NamedArg;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @since 2021/4/22 11:24
 */
public class SettingItem extends VBox {

  @Getter
  private Node content;

  public SettingItem(@NamedArg("name") String name, @NamedArg("content") Node content) {
    getStyleClass().add("setting-item");
    Label nameLabel = NodeHelper.addClass(new Label(name), "item-name");
    VBox container = NodeHelper.addClass(new VBox(content), "item-content");
    getChildren().addAll(nameLabel, container);
  }
}
