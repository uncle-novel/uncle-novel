package com.uncles.novel.app.jfx.framework.ui.components;

import com.jfoenix.controls.JFXButton;
import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 18:33
 */
public class LeftTab extends JFXButton {
    /**
     * 设置选中伪类
     */
    private static final PseudoClass SELECTED_PSEUDO_CLASS_STATE = PseudoClass.getPseudoClass("selected");
    private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS_STATE, get());
        }
    };
    /**
     * 样式
     */
    private static final String DEFAULT_STYLE_CLASS = "left-tab";
    private static final String USER_AGENT_STYLESHEET = ResourceUtils.load("/css/controls/left-tab.css").toExternalForm();
    /**
     * 跳转的视图
     */
    private final StringProperty controller = new SimpleStringProperty();

    public LeftTab() {
        initialize();
    }

    public LeftTab(String text, String controller) {
        super(text);
        setController(controller);
    }

    /**
     * 初始化
     */
    public void initialize() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        getStylesheets().add(USER_AGENT_STYLESHEET);
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

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
