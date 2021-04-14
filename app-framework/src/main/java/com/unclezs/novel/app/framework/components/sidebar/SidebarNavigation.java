package com.unclezs.novel.app.framework.components.sidebar;

import com.unclezs.novel.app.framework.collection.SimpleObservableList;
import com.unclezs.novel.app.framework.factory.ViewFactory;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.framework.util.ViewUtils;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

/**
 * 侧边菜单导航栏
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 18:50
 */
@Getter
@Setter
@DefaultProperty("menus")
public class SidebarNavigation extends HBox {

  /**
   * 侧边菜单
   */
  private VBox sidebar;
  /**
   * 内容面板
   */
  private StackPane content;
  /**
   * 菜单数据
   */
  private ObservableList<SidebarMenu> menus;
  /**
   * 菜单项
   */
  private List<SidebarMenuItem> menuItems;
  /**
   * 当前视图
   */
  private SidebarView<? extends Node> currentView;


  public SidebarNavigation() {
    init();
  }

  /**
   * 初始化
   */
  private void init() {
    // 样式
    getStyleClass().add("sidebar-nav");
    getStylesheets().add(ResourceUtils.loadCss("css/components/sidebar-navigation.css"));
    // 菜单容器
    ScrollPane sidebarContainer = new ScrollPane();
    sidebarContainer.getStyleClass().add("sidebar");
    this.sidebar = ViewUtils.addClass(new VBox(), "sidebar-menus");
    sidebarContainer.setContent(sidebar);
    // 创建menu列表
    this.menuItems = new ArrayList<>();
    this.menus = new SimpleObservableList<>() {
      @Override
      public void onAdd(SidebarMenu element) {
        addMenu(element);
      }

      @Override
      public void onRemove(SidebarMenu element) {
        removeMenu(element);
      }
    };
    // 内容视图容器
    content = new StackPane();
    content.getStyleClass().add("content");
    HBox.setHgrow(content, Priority.ALWAYS);
    // 装入组件
    getChildren().setAll(sidebarContainer, content);
  }

  /**
   * 删除一组菜单
   *
   * @param sidebarMenu 菜单组
   */
  public void removeMenu(SidebarMenu sidebarMenu) {
    this.sidebar.getChildren().removeAll(sidebarMenu.getMenuItems());
    this.menuItems.removeAll(sidebarMenu.getMenuItems());
    if (sidebarMenu.getText() != null) {
      this.sidebar.getChildren().remove(sidebarMenu);
    }
  }

  /**
   * 添加一组菜单
   *
   * @param menu 菜单组
   */
  public void addMenu(SidebarMenu menu) {
    if (menu.getText() != null) {
      this.sidebar.getChildren().add(menu);
    }
    this.menuItems.addAll(menu.getMenuItems());
    this.sidebar.getChildren().addAll(menu.getMenuItems());
    for (SidebarMenuItem menuItem : menu.getMenuItems()) {
      menuItem.getSidebarView().setNavigation(this);
      menuItem.setNavigation(this);
      // 设置默认页面
      if (menuItem.isSelected()) {
        menuItem.fire();
      }
    }
  }

  /**
   * 切换页面
   *
   * @param view   视图
   * @param bundle 数据
   */
  public void navigate(SidebarView<? extends Parent> view, NavigateBundle bundle) {
    if (bundle == null) {
      bundle = new NavigateBundle();
    }
    // 上一个页面如果为菜单页面，则切换选中状态
    if (currentView != null) {
      String fromViewName = currentView.getClass().getName();
      String toViewName = view.getClass().getName();
      // 同页跳转直接忽略
      if (toViewName.equals(fromViewName)) {
        return;
      }
      // onHidden 周期函数触发
      currentView.onHidden();
      bundle.setFrom(fromViewName);
      menuItems.forEach(menuItem -> {
        if (menuItem.getView().equals(toViewName)) {
          menuItem.setSelected(true);
        } else if (menuItem.isSelected()) {
          menuItem.setSelected(false);
        }
      });
    }
    // onShow 周期函数触发
    view.onShow(bundle);
    // 切换页面
    content.getChildren().setAll(view.getRoot());
    this.currentView = view;
  }

  /**
   * 切换页面
   *
   * @param viewClass 视图
   * @param bundle    数据
   */
  public void navigate(Class<?> viewClass, NavigateBundle bundle) {
    SidebarView<? extends Parent> view = ViewFactory.me().getController(viewClass);
    navigate(view, bundle);
  }

  /**
   * 切换页面
   *
   * @param viewClass 视图
   */
  public void navigate(Class<?> viewClass) {
    navigate(viewClass, null);
  }

  /**
   * 切换页面
   *
   * @param view 视图
   */
  public void navigate(SidebarView<? extends Parent> view) {
    navigate(view, null);
  }
}
