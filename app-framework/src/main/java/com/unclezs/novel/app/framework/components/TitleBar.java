package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.beans.NamedArg;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;
import lombok.Setter;

/**
 * 标题栏
 *
 * @author blog.unclezs.com
 * @date 2021/4/25 12:51
 */
@Getter
@Setter
public class TitleBar extends HBox {

  public static final String DEFAULT_STYLE_CLASS = "title-bar";

  private String title;
  private Node content;

  public TitleBar(@NamedArg("title") String title) {
    this(title, null);
  }

  public TitleBar(@NamedArg("title") String title, @NamedArg("content") Node content) {
    NodeHelper.addClass(this, DEFAULT_STYLE_CLASS);

    Label titleLabel = NodeHelper.addClass(new Label(title), "title");
    titleLabel.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(titleLabel, Priority.ALWAYS);

    if (content != null) {
      NodeHelper.addClass(content, "content");
      getChildren().addAll(titleLabel, content);
    } else {
      getChildren().add(titleLabel);
    }
  }
}
