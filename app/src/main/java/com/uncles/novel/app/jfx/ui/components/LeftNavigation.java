package com.uncles.novel.app.jfx.ui.components;

import com.uncles.novel.app.jfx.ui.skin.LeftNavigationViewSkin;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;


/**
 * 左侧菜单栏View
 *
 * @author blog.unclezs.com
 * @since 2021/02/25 17:04
 */
@DefaultProperty("menuItems")
public class LeftNavigation extends Control {
    private final ObservableList<LeftNavigationMenuItem> menuItems;
    private static final String DEFAULT_STYLE_CLASS = "left-nav";
    private Consumer<LeftNavigationMenuItem> onMenuItemSelectedAction = null;

    public LeftNavigation() {
        this.menuItems = FXCollections.observableArrayList();
        this.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    public ObservableList<LeftNavigationMenuItem> getMenuItems() {
        return menuItems;
    }

    public void addMenuItem(LeftNavigationMenuItem menuItem) {
        this.menuItems.add(menuItem);
    }

    public void setOnMenuItemSelectedAction(Consumer<LeftNavigationMenuItem> onMenuItemSelectedAction) {
        this.onMenuItemSelectedAction = onMenuItemSelectedAction;
    }

    public Consumer<LeftNavigationMenuItem> getOnMenuItemSelectedAction() {
        return onMenuItemSelectedAction;
    }

    /**
     * 设置皮肤
     *
     * @return 左侧菜单皮肤
     */
    protected Skin<?> createDefaultSkin() {
        return new LeftNavigationViewSkin(this);
    }
}
