package com.uncles.novel.app.jfx.framework.ui.components.sidebar;

import com.sun.javafx.collections.TrackableObservableList;
import javafx.beans.DefaultProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

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
    @Setter
    private Consumer<Node> onNavigate;
    /**
     * 菜单列表
     */
    @Getter
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
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /**
     * 遍历每个菜单
     *
     * @param consumer 处理
     */
    public void eachMenu(Consumer<SidebarNavigationMenu> consumer) {
        getMenus().forEach(group -> group.getMenus().forEach(consumer));
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
                    if (onNavigate != null) {
                        onNavigate.accept(sidebarNavigationMenu.getActionView());
                    }
                }
            });
            getChildren().add(sidebarNavigationMenu);
        });
    }
}
