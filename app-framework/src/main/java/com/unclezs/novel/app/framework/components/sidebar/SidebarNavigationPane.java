package com.unclezs.novel.app.framework.components.sidebar;

import com.unclezs.novel.app.framework.util.ResourceUtils;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.DefaultProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NonNull;

/**
 * 左侧菜单面板
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 14:41
 */
@Getter
@DefaultProperty("sidebar")
public class SidebarNavigationPane extends HBox {

  public static final String DEFAULT_STYLE_CLASS = "sidebar-nav-pane";
  private static final String USER_AGENT_STYLESHEET = ResourceUtils
      .loadCss("/css/components/sidebar-navigation.css");
  private final Map<String, SidebarNavigationView> viewMap = new HashMap<>(16);
  private SidebarNavigation sidebar;
  private SidebarNavigationView currentView;


  public SidebarNavigationPane() {
    getStyleClass().add(DEFAULT_STYLE_CLASS);
    getStylesheets().add(USER_AGENT_STYLESHEET);
  }

  public static void navigateTo(SidebarNavigationPane root,
      @NonNull Class<? extends SidebarNavigationView> view, NavigateBundle bundle) {
    root.navigateTo(view, bundle);
  }

  /**
   * 设置菜单
   *
   * @param sidebar 菜单列表
   */
  public void setSidebar(@NonNull SidebarNavigation sidebar) {
    if (this.sidebar != null) {
      getChildren().remove(this.sidebar);
    }
    this.sidebar = sidebar;
    getChildren().add(0, sidebar);
    // 监听新的节点
    sidebar.getContainer().getChildren().addListener((ListChangeListener<Node>) c -> {
      while (c.next()) {
        for (Node node : c.getAddedSubList()) {
          if (node instanceof SidebarNavigationMenu) {
            SidebarNavigationMenu menu = (SidebarNavigationMenu) node;
            menu.setOnMouseClicked(e -> navigateTo(menu.getActionView().getClass()));
          }
        }
      }
    });
    // 如果加入的按钮有被选中，则跳转
    for (SidebarNavigationMenu menu : this.sidebar.getMenus()) {
      menu.setOnMouseClicked(e -> navigateTo(menu.getActionView().getClass()));
      if (menu.isSelected()) {
        menu.setSelected(false);
        navigateTo(menu.getActionView().getClass());
      }
    }
  }

  /**
   * 根据view的全限定类名 找到相应的按钮
   *
   * @param className view类名
   * @return 按钮
   */
  public SidebarNavigationMenu findMenuByName(String className) {
    for (SidebarNavigationMenu menu : getSidebar().getMenus()) {
      if (menu.getView().equals(className)) {
        return menu;
      }
    }
    return null;
  }

  /**
   * 设置面板
   *
   * @param content 面板view
   */
  public void setCurrentView(SidebarNavigationView content) {
    if (this.currentView != null) {
      Node view = this.currentView.getView();
      getChildren().remove(view);
    }
    getChildren().add(content.getView());
    this.currentView = content;
  }

  /**
   * 页面切换
   *
   * @param destinationViewClass 目标view
   * @param bundle               携带的数据
   */
  public final void navigateTo(@NonNull Class<? extends SidebarNavigationView> destinationViewClass,
      NavigateBundle bundle) {
    // 同页禁止跳转
    if (currentView != null && destinationViewClass == currentView.getClass()) {
      return;
    }
    if (bundle == null) {
      bundle = new NavigateBundle();
    }
    if (currentView != null) {
      SidebarNavigationMenu actionMenu = findMenuByName(currentView.getClass().getName());
      if (actionMenu != null) {
        bundle.setMenuTrigger(true);
        actionMenu.setSelected(false);
        actionMenu.getActionView().onHidden();
      } else {
        // 支持没有菜单按钮的跳转，触发onHidden回调
        viewMap.get(currentView.getClass().getName()).onHidden();
      }
      bundle.setFrom(currentView.getClass().getName());
    }
    SidebarNavigationMenu actionMenu = findMenuByName(destinationViewClass.getName());
    // 跳转目的页面
    SidebarNavigationView destination;
    if (actionMenu != null) {
      actionMenu.setSelected(true);
      destination = actionMenu.getActionView();
    } else {
      // 支持没有菜单按钮的跳转
      destination = viewMap.get(destinationViewClass.getName());
      if (destination == null) {
        destination = SidebarNavigationView.loadView(destinationViewClass);
        viewMap.put(destinationViewClass.getName(), destination);
      }
    }
    // 触发页面生命周期 onShow
    destination.onShow(bundle);
    setCurrentView(destination);
  }

  /**
   * 页面跳转
   *
   * @param destination 跳转的页面
   */
  public final void navigateTo(@NonNull Class<? extends SidebarNavigationView> destination) {
    navigateTo(destination, null);
  }
}
