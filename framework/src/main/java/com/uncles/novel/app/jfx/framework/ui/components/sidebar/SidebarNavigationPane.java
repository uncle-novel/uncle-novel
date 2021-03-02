package com.uncles.novel.app.jfx.framework.ui.components.sidebar;

import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;

/**
 * 左侧菜单面板
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 14:41
 */
@Getter
public class SidebarNavigationPane extends HBox {
    private static final String USER_AGENT_STYLESHEET = ResourceUtils.loadCss("/css/components/sidebar-navigation.css");
    public static final String DEFAULT_STYLE_CLASS = "sidebar-nav-pane";
    private SidebarNavigation menus;
    private Node content;


    public SidebarNavigationPane() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        getStylesheets().add(USER_AGENT_STYLESHEET);
    }

    /**
     * 设置菜单
     *
     * @param menus 菜单列表
     */
    public void setMenus(SidebarNavigation menus) {
        getChildren().remove(this.menus);
        getChildren().add(0, menus);
        this.menus = menus;
        this.menus.setOnNavigate(this::setContent);
        this.menus.eachMenu(this::addMenu);
    }

    /**
     * 添加菜单
     *
     * @param menu 菜单
     */
    public void addMenu(SidebarNavigationMenu menu) {
        if (menu.isSelected()) {
            // 如果选中了，则设置为当前显示view
            setContent(menu.getActionView());
        }
    }

    /**
     * 设置面板
     *
     * @param content 面板view
     */
    public void setContent(Node content) {
        getChildren().remove(this.content);
        getChildren().add(content);
        this.content = content;
        HBox.setHgrow(content, Priority.ALWAYS);
    }
}
