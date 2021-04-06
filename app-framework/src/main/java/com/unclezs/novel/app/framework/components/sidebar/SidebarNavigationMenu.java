package com.unclezs.novel.app.framework.components.sidebar;

import com.unclezs.novel.app.framework.components.SelectableButton;
import com.unclezs.novel.app.framework.util.ReflectionUtils;
import lombok.Getter;

/**
 * menu button
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 18:33
 */
@Getter
public class SidebarNavigationMenu extends SelectableButton {

  private static final String DEFAULT_STYLE_CLASS = "sidebar-nav-menu";
  /**
   * 跳转的视图
   */
  private SidebarNavigationView actionView;
  /**
   * 视图控制器
   */
  private String view;

  public SidebarNavigationMenu() {
    getStyleClass().add(DEFAULT_STYLE_CLASS);
  }

  /**
   * set the page that the button clicks to jump to.
   *
   * @param view view controller
   */
  public void setView(String view) {
    this.view = view;
    this.actionView = SidebarNavigationView.loadView(ReflectionUtils.forName(view));
  }
}
