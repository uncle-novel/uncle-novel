package com.uncles.novel.app.jfx.framework.ui.components.sidebar;

import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 11:25
 */
@DefaultProperty("menus")
public class SidebarNavigationMenuGroup {
    private String name;
    private ObservableList<SidebarNavigationMenu> sidebarNavigationMenus = FXCollections.observableArrayList();
    private Label groupLabel;
    public static final String GROUP_LABEL_CLASS = "sidebar-nav-menu-group-label";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (groupLabel == null) {
            groupLabel = new Label(name);
            groupLabel.getStyleClass().add(GROUP_LABEL_CLASS);
        }
        this.groupLabel.setText(name);
        this.name = name;
    }

    public ObservableList<SidebarNavigationMenu> getMenus() {
        return sidebarNavigationMenus;
    }

    public void setMenus(ObservableList<SidebarNavigationMenu> sidebarNavigationMenus) {
        this.sidebarNavigationMenus = sidebarNavigationMenus;
    }

    public Label getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(Label groupLabel) {
        this.groupLabel = groupLabel;
    }
}
