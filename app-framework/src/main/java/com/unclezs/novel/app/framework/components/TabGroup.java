package com.unclezs.novel.app.framework.components;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/4/30 9:38
 */
public class TabGroup {

  @Getter
  private final List<TabButton> tabs = new ArrayList<>();

  /**
   * 设置选中tab
   *
   * @param tabButton tab
   */
  public void selectTab(TabButton tabButton) {
    for (TabButton tab : tabs) {
      if (tab != tabButton) {
        tab.setSelected(false);
      }
    }
  }
}
