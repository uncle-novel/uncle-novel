package com.uncles.novel.app.jfx.framework.ui.components.sidebar;

import com.sun.javafx.collections.TrackableObservableList;
import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.beans.DefaultProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * 侧边菜单导航栏
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 18:50
 */
@DefaultProperty("menus")
public class SidebarNavigation extends VBox {
    private static final String DEFAULT_STYLE_CLASS = "sidebar-nav";
    private static final String USER_AGENT_STYLESHEET = ResourceUtils.load("/css/controls/sidebar-navigation.css").toExternalForm();
    private Consumer<SidebarNavigationMenu> onNavigationMenuClick;
    /**
     * 菜单列表
     */
    private final ObservableList<SidebarNavigationMenuGroup> menus = new TrackableObservableList<>() {
        @Override
        protected void onChanged(ListChangeListener.Change<SidebarNavigationMenuGroup> c) {
            while (c.next()) {
                c.getRemoved().forEach(SidebarNavigation.this::removeMenuGroup);
                c.getAddedSubList().forEach(SidebarNavigation.this::addMenuGroup);
            }
        }
    };

    public SidebarNavigation() {
        initialize();
    }

    /**
     * 初始化
     */
    public void initialize() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        getStylesheets().add(USER_AGENT_STYLESHEET);
    }

    public ObservableList<SidebarNavigationMenuGroup> getMenus() {
        return menus;
    }

    public void eachMenu(Consumer<SidebarNavigationMenu> consumer) {
        getMenus().forEach(group -> group.getMenus().forEach(consumer));
    }

    public void setOnNavigationMenuClick(Consumer<SidebarNavigationMenu> onNavigationMenuClick) {
        this.onNavigationMenuClick = onNavigationMenuClick;
    }

    /**
     * 删除一组菜单
     *
     * @param sidebarNavigationMenuGroup 菜单组
     */
    public void removeMenuGroup(SidebarNavigationMenuGroup sidebarNavigationMenuGroup) {
        getChildren().removeAll(sidebarNavigationMenuGroup.getMenus());
        if (sidebarNavigationMenuGroup.getGroupLabel() != null) {
            getChildren().remove(sidebarNavigationMenuGroup.getGroupLabel());
        }
    }

    /**
     * 添加一组菜单
     *
     * @param sidebarNavigationMenuGroup 菜单组
     */
    public void addMenuGroup(SidebarNavigationMenuGroup sidebarNavigationMenuGroup) {
        if (sidebarNavigationMenuGroup.getGroupLabel() != null) {
            getChildren().add(sidebarNavigationMenuGroup.getGroupLabel());
        }
        // 给每个菜单设置事件监听
        sidebarNavigationMenuGroup.getMenus().forEach(sidebarNavigationMenu -> {
            sidebarNavigationMenu.setOnMouseClicked(e -> {
                if (!sidebarNavigationMenu.isSelected()) {
                    // 设置选中状态
                    eachMenu(currentSidebarNavigationMenu -> currentSidebarNavigationMenu.setSelected(currentSidebarNavigationMenu.equals(sidebarNavigationMenu)));
                    // 菜单菜单点击回调
                    if (onNavigationMenuClick != null) {
                        onNavigationMenuClick.accept(sidebarNavigationMenu);
                    }
                }
            });
            getChildren().add(sidebarNavigationMenu);
        });
    }
}
