package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @since 2021/5/5 13:57
 */
public class PlaceHolder extends VBox {

  private final Label label;
  @Getter
  private String tip;

  public PlaceHolder(@NamedArg("tip") String tip) {
    NodeHelper.addClass(this, "placeholder");
    label = NodeHelper.addClass(new Label(tip), "tip");
    this.getChildren().addAll(new Icon(IconFont.EMPTY), label);
    this.setAlignment(Pos.CENTER);
  }

  public void setTip(String tip) {
    this.tip = tip;
    this.label.setText(tip);
  }
}
