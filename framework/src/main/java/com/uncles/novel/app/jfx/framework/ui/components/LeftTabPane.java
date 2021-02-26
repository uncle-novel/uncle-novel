package com.uncles.novel.app.jfx.framework.ui.components;

import com.uncles.novel.app.jfx.framework.annotation.FxController;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * 左侧菜单
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 14:41
 */
@FxController(fxml = "components/left_navigation.fxml", bundle = "basic")
public class LeftTabPane extends BorderPane {
    public MenuPane tabs;

    public LeftTabPane() {
        FXMLLoader fxmlLoader = FxmlLoader.getLoader(getClass());
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public LeftTabPane(MenuPane tabs) {
        this.tabs = tabs;
    }

    public MenuPane getTabs() {
        return tabs;
    }

    public void setTabs(MenuPane tabs) {
        this.tabs = tabs;
    }
}
