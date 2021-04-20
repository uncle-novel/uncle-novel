package com.unclezs.novel.app.framework.components.sidebar;


import com.unclezs.novel.app.framework.core.View;
import javafx.scene.Parent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 侧边菜单视图控制器基类
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 14:12
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class SidebarView<V extends Parent> extends View<V> {

  protected SidebarNavigation navigation;

  /**
   * 侧边栏被创建完成时调用（navigation已装配好）
   */
  public void onCreated() {
    // do something
  }

  /**
   * 页面显示之前 处理数据
   *
   * @param bundle 页面跳转数据
   */
  public void onShow(SidebarNavigateBundle bundle) {
    // do something
  }

  /**
   * 页面显示之后 处理数据
   *
   * @param bundle 页面跳转数据
   */
  public void onShown(SidebarNavigateBundle bundle) {
    // do something
  }

  /**
   * 注入导航，并触发onCreated
   *
   * @param navigation 导航
   */
  public void setNavigation(SidebarNavigation navigation) {
    this.navigation = navigation;
    this.onCreated();
  }
}
