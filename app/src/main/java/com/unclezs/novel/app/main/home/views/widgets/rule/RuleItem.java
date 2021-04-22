package com.unclezs.novel.app.main.home.views.widgets.rule;

import com.unclezs.novel.app.framework.collection.SimpleObservableList;
import com.unclezs.novel.app.framework.util.NodeHelper;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/4/22 11:24
 */
@DefaultProperty("content")
public class RuleItem extends HBox {

  @Getter
  private final List<Node> content = new SimpleObservableList<>() {
    @Override
    public void onAdd(Node element) {
      HBox.setHgrow(element, Priority.ALWAYS);
      getChildren().add(element);
    }
  };
  @Getter
  private String name;


  public RuleItem() {
    getStyleClass().add("item");
    Label label = NodeHelper.addClass(new Label(), "name");
    getChildren().add(label);
  }

  public void setName(String name) {
    this.name = name;
    Node label = getChildren().get(0);
    if (label instanceof Label) {
      ((Label) label).setText(name);
    }
  }
}
