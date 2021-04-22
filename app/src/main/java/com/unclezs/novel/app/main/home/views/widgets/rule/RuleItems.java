package com.unclezs.novel.app.main.home.views.widgets.rule;

import com.unclezs.novel.app.framework.collection.SimpleObservableList;
import com.unclezs.novel.app.framework.util.NodeHelper;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
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
  };
  @Getter
  private String name;


  public RuleItems() {
    getStyleClass().addAll("items", "no-name");
  }

  public void setName(String name) {
    this.name = name;
    Label label = NodeHelper.addClass(new Label(name), "name");
    getStyleClass().remove("no-name");
    getChildren().add(0, label);
  }

}
