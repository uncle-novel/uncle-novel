package com.uncles.novel.app.jfx.framework.ui.components.sidebar;

import com.uncles.novel.app.jfx.framework.exception.FxException;
import com.uncles.novel.app.jfx.framework.ui.components.button.SelectableButton;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import javafx.scene.Node;
import lombok.Getter;

/**
 * 菜单按钮
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 18:33
 */
@Getter
public class SidebarNavigationMenu extends SelectableButton {
    private static final String DEFAULT_STYLE_CLASS = "sidebar-nav-menu";
    /**
     * 跳转的视图
     */
    private Node actionView;
    /**
     * 视图控制器
     */
    private String view;

    public SidebarNavigationMenu() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /**
     * 设置按钮点击跳转的页面
     *
     * @param view view的controller
     */
    public void setView(String view) {
        try {
            this.actionView = FxmlLoader.load(Class.forName(view));
            this.view = view;
            this.actionView.getStyleClass().add("sidebar-nav-content");
        } catch (ClassNotFoundException e) {
            throw new FxException("view controller class not found " + view, e);
        }
    }
}
