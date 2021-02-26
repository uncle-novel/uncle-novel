package com.uncles.novel.app.jfx.framework.ui.components;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 18:33
 */
public class LeftTab extends JFXButton {
    /**
     * 跳转的视图
     */
    private StringProperty controller = new SimpleStringProperty();

    public LeftTab() {
        this("123", "dasdasd");
    }

    public LeftTab(String text, String controller) {
        super(text);
        setStyle("-fx-border-color: red");
        this.controller.set(controller);
    }

    public LeftTab(StringProperty controller) {
        this.controller = controller;
    }

    public String getController() {
        return controller.get();
    }

    public StringProperty controllerProperty() {
        return controller;
    }

    public void setController(String controller) {
        this.controller.set(controller);
    }
}
