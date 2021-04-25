package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.beans.NamedArg;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
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
public class TitleBar extends AnchorPane {

  public static final String DEFAULT_STYLE_CLASS = "title-bar";

  private String title;
  private Node content;

  public TitleBar(@NamedArg("title") String title) {
    this(title, null);
  }

  public TitleBar(@NamedArg("title") String title, @NamedArg("content") Node content) {
    NodeHelper.addClass(this, DEFAULT_STYLE_CLASS);

    Label titleLabel = NodeHelper.addClass(new Label(title), "title");
    AnchorPane.setLeftAnchor(titleLabel, 10D);

    if (content != null) {
      NodeHelper.addClass(content, "content");
      AnchorPane.setRightAnchor(content, 10D);
      getChildren().addAll(titleLabel, content);
    } else {
      getChildren().add(titleLabel);
    }
  }
}
