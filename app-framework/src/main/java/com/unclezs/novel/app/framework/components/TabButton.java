package com.unclezs.novel.app.framework.components;

import javafx.event.ActionEvent;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @since 2021/4/30 9:26
 */
public class TabButton extends SelectableButton {

  public static final String DEFAULT_STYLE_CLASS = "tab-button";
  @Getter
  private TabGroup tabGroup;

  public TabButton() {
    getStyleClass().add(DEFAULT_STYLE_CLASS);
    this.addEventFilter(ActionEvent.ACTION, event -> {
      if (!isSelected()) {
        setSelected(true);
      }
    });
  }

  /**
   * 设置tab组
   *
   * @param tabGroup 组
   */
  public void setTabGroup(TabGroup tabGroup) {
    this.tabGroup = tabGroup;
    this.tabGroup.getTabs().add(this);
  }

  @Override
  public void setSelected(boolean selected) {
    super.setSelected(selected);
    if (selected && tabGroup != null) {
      tabGroup.selectTab(this);
    }
  }
}
