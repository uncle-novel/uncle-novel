package com.unclezs.novel.app.framework.components.sidebar;

import com.unclezs.novel.app.framework.components.SelectableButton;
import com.unclezs.novel.app.framework.factory.ViewFactory;
import com.unclezs.novel.app.framework.util.ReflectUtils;
import javafx.scene.Parent;
import lombok.Getter;
import lombok.Setter;

/**
 * menu button
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 18:33
 */
@Getter
@Setter
public class SidebarMenuItem extends SelectableButton {

  private static final String DEFAULT_STYLE_CLASS = "sidebar-menu-item";
  /**
   * 视图的全限定类名
   */
  private String view;
  /**
   * 跳转的视图
   */
  private SidebarView<? extends Parent> sidebarView;
  private SidebarNavigation navigation;

  public SidebarMenuItem() {
    getStyleClass().add(DEFAULT_STYLE_CLASS);
    // 点击触发菜单切换
    setOnAction(e -> {
      NavigateBundle bundle = new NavigateBundle();
      bundle.setMenuTrigger(true);
      navigation.navigate(sidebarView, bundle);
    });
  }

  /**
   * 设置按钮单击跳转到的页面
   *
   * @param view view controller
   */
  public void setView(String view) {
    this.view = view;
    this.sidebarView = ViewFactory.me().getController(ReflectUtils.forName(view));
  }
}
