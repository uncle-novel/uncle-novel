package com.unclezs.novel.app.framework.components.sidebar;

import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

/**
 * 侧边导航栏菜单
 *
 * @author blog.unclezs.com
 * @since 2021/02/27 11:25
 */
@Getter
@Setter
@DefaultProperty("menuItems")
public class SidebarMenu extends Label {

  public static final String GROUP_LABEL_CLASS = "sidebar-menu";
  private ObservableList<SidebarMenuItem> menuItems = FXCollections.observableArrayList();
  private String name;

  public SidebarMenu() {
    this.getStyleClass().add(GROUP_LABEL_CLASS);
  }

  /**
   * 设置分组名称
   *
   * @param name 名称
   */
  public void setName(String name) {
    this.name = name;
    this.setText(name);
  }
}
