package com.uncles.novel.app.jfx.ui.components;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

import java.util.Objects;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 18:03
 */
public class LeftNavigationMenuItem extends JFXButton {
    private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    private final BooleanProperty selected;
    private final StringProperty group;
    private static final String DEFAULT_STYLE_CLASS = "left-nav-menu-item";

    public LeftNavigationMenuItem() {
        this("", null);
    }

    public LeftNavigationMenuItem(String text) {
        this(text, null);
    }

    public LeftNavigationMenuItem(String text, Node graphic, BooleanProperty selected, StringProperty group) {
        this(text, graphic, selected, group, null);
    }

    public LeftNavigationMenuItem(String text, Node graphic) {
        this(text, graphic, null, null);
    }

    public LeftNavigationMenuItem(String text, Node graphic, BooleanProperty selected, StringProperty group, EventHandler<ActionEvent> actionEventEventHandler) {
        super(text, graphic);
        this.group = group == null ? new SimpleStringProperty("") : group;
        this.selected = Objects.requireNonNullElseGet(selected, () -> new SimpleBooleanProperty(this, "selected", false) {
            @Override
            protected void invalidated() {
                LeftNavigationMenuItem.this.pseudoClassStateChanged(LeftNavigationMenuItem.PSEUDO_CLASS_SELECTED, this.get());
            }
        });
        this.setOnAction(actionEventEventHandler);
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    public String getGroup() {
        return group.get();
    }

    public StringProperty groupProperty() {
        return group;
    }

    public void setGroup(String group) {
        this.group.set(group);
    }

    public final BooleanProperty selectedProperty() {
        return this.selected;
    }

    public final boolean isSelected() {
        return this.selected.get();
    }

    public final void setSelected(boolean value) {
        this.selected.set(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeftNavigationMenuItem that = (LeftNavigationMenuItem) o;
        return Objects.equals(selected, that.selected) && Objects.equals(group, that.group) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selected, group, textProperty().get());
    }
}
