package com.uncles.novel.app.jfx.framework.ui.components;

import com.sun.javafx.collections.TrackableObservableList;
import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 18:50
 */
public class MenuPane extends VBox {
    /**
     * 样式
     */
    private static final String DEFAULT_STYLE_CLASS = "menu-pane";
    private static final String USER_AGENT_STYLESHEET = ResourceUtils.load("/css/controls/menu-pane.css").toExternalForm();
    private final ObservableList<LeftTab> menus = new TrackableObservableList<>() {
        @Override
        protected void onChanged(ListChangeListener.Change<LeftTab> c) {
            while (c.next()) {
                for (LeftTab tab : c.getRemoved()) {
                    tab.setOnMouseClicked(null);
                    getChildren().remove(tab);
                }
                for (LeftTab tab : c.getAddedSubList()) {
                    tab.setOnMouseClicked(e -> menus.forEach(menu -> menu.setSelected(menu.equals(tab))));
                    getChildren().add(tab);
                }
            }
        }
    };

    public MenuPane() {
        initialize();
    }

    /**
     * 初始化
     */
    public void initialize() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        getStylesheets().add(USER_AGENT_STYLESHEET);
    }

    public ObservableList<LeftTab> getMenus() {
        return menus;
    }

    public MenuPane(LeftTab... menus) {
        if (menus != null) {
            getMenus().addAll(menus);
        }
    }
}
