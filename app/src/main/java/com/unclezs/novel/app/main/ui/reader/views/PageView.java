package com.unclezs.novel.app.main.ui.reader.views;

import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.ui.reader.animation.NextPageTransition;
import com.unclezs.novel.app.main.ui.reader.animation.PrePageTransition;
import javafx.animation.Transition;
import javafx.beans.NamedArg;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.Region;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/5/8 19:06
 */
public class PageView extends Label {

  public static final String DEFAULT_STYLE_CLASS = "page-view";
  @Getter
  private final Transition preTransition;
  @Getter
  private final Transition nextTransition;
  @Getter
  private final Label title = NodeHelper.addClass(new Label(), "title");


  public PageView(@NamedArg("container") Region container) {
    preTransition = new PrePageTransition(this, container);
    nextTransition = new NextPageTransition(this, container);
    this.setWrapText(true);
    this.setTextOverrun(OverrunStyle.CLIP);
    this.setMaxHeight(Double.MAX_VALUE);
    this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    this.setContentDisplay(ContentDisplay.TOP);
    this.title.maxWidthProperty().bind(widthProperty());
  }

  /**
   * 设置标题
   *
   * @param titleText 标题
   */
  public void setTitle(String titleText) {
    if (titleText == null) {
      this.setGraphic(null);
    } else {
      this.setGraphic(this.title);
      this.title.setText(titleText);
    }
  }

}
