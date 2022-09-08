package com.unclezs.gui.utils;

import com.unclezs.gui.app.App;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * @author uncle
 * @date 2020/5/3 15:06
 */
public class NodeUtil {
    /**
     * 创建一个背景绑定主窗口主题的容器
     *
     * @param pane /
     * @return /
     */
    public static StackPane createBgPane(Pane pane) {
        StackPane content = new StackPane();
        Pane background = new Pane();
        pane.getStyleClass().add("bg-color-theme");
        background.backgroundProperty().bind(App.background.backgroundProperty());
        pane.backgroundProperty().bind(App.root.backgroundProperty());
        content.getChildren().addAll(background, pane);
        return content;
    }
}
