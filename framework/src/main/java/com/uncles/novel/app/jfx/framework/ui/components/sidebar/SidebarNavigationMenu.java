package com.uncles.novel.app.jfx.framework.ui.components.sidebar;

import com.jfoenix.controls.JFXButton;
import com.uncles.novel.app.jfx.framework.exception.FxException;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import org.controlsfx.glyphfont.Glyph;

/**
 * 菜单按钮
 * @author blog.unclezs.com
 * @since 2021/02/26 18:33
 */
public class SidebarNavigationMenu extends JFXButton {
    private static final String DEFAULT_STYLE_CLASS = "sidebar-nav-menu";
    /**
     * 设置选中伪类
     */
    private static final PseudoClass SELECTED_PSEUDO_CLASS_STATE = PseudoClass.getPseudoClass("selected");
    private ReadOnlyBooleanWrapper selected;
    /**
     * 跳转的视图
     */
    private Node actionView;
    /**
     * 图标
     */
    private StringProperty icon;
    /**
     * 视图控制器
     */
    private String view;


    public SidebarNavigationMenu() {
        initialize();
    }

    public SidebarNavigationMenu(String text, String view) {
        super(text);
        setView(view);
    }

    /**
     * 初始化
     */
    public void initialize() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    public String getView() {
        return view;
    }

    public Node getActionView() {
        return actionView;
    }

    public void setView(String viewController) {
        try {
            this.actionView = FxmlLoader.load(Class.forName(viewController));
            this.view = viewController;
        } catch (ClassNotFoundException e) {
            throw new FxException("view controller class not found " + viewController, e);
        }
    }

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

    public void setIcon(String icon) {
        iconProperty().set(icon);
        Glyph graphic = new Glyph();
        graphic.setIcon(icon);
        this.setGraphic(graphic);
    }

    public String getIcon() {
        return iconProperty().get();
    }

    public StringProperty iconProperty() {
        if (icon == null) {
            icon = new SimpleStringProperty();
        }
        return icon;
    }

}
