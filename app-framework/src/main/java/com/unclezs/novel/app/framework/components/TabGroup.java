package com.unclezs.novel.app.framework.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

/**
 * @author blog.unclezs.com
 * @since 2021/4/30 9:38
 */
public class TabGroup {

  @Getter
  private final List<TabButton> tabs = new ArrayList<>();
  @Setter
  private Consumer<TabButton> onSelected;

  /**
   * 设置选中tab
   *
   * @param tabButton tab
   */
  public void selectTab(TabButton tabButton) {
    for (TabButton tab : tabs) {
      if (tab != tabButton) {
        tab.setSelected(false);
      } else if (onSelected != null) {
        onSelected.accept(tab);
      }
    }
  }

  public TabButton findTab(Object userData) {
    for (TabButton tab : tabs) {
      if (Objects.equals(userData, tab.getUserData())) {
        return tab;
      }
    }
    return null;
  }
}
