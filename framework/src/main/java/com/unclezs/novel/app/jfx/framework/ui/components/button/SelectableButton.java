package com.unclezs.novel.app.jfx.framework.ui.components.button;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;

/**
 * 可以选择的按钮
 *
 * @author blog.unclezs.com
 * @since 2021/03/02 18:03
 */
public class SelectableButton extends IconButton {
    /**
     * 设置选中伪类
     */
    private static final PseudoClass SELECTED_PSEUDO_CLASS_STATE = PseudoClass.getPseudoClass("selected");
    private ReadOnlyBooleanWrapper selected;

    public boolean isSelected() {
        return selectedProperty().get();
    }

    public ReadOnlyBooleanProperty selectedProperty() {
        return selectedPropertyImpl().getReadOnlyProperty();
    }

    public ReadOnlyBooleanWrapper selectedPropertyImpl() {
        if (selected == null) {
            selected = new ReadOnlyBooleanWrapper(this, "selected", false) {
                @Override
                protected void invalidated() {
                    pseudoClassStateChanged(SELECTED_PSEUDO_CLASS_STATE, get());
                }
            };
        }
        return selected;
    }

    public void setSelected(boolean selected) {
        selectedPropertyImpl().set(selected);
    }
}
