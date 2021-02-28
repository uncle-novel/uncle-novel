package com.uncles.novel.app.jfx.framework.ui.components.sidebar;

import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * 左侧菜单面板
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 14:41
 */
public class SidebarNavigationPane extends HBox {
    private static final String USER_AGENT_STYLESHEET = ResourceUtils.loadCss("/css/components/sidebar-navigation.css");
    public static final String DEFAULT_STYLE_CLASS = "sidebar-nav-pane";
    private SidebarNavigation menus;
    private Node content;


    public SidebarNavigationPane() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        getStylesheets().add(USER_AGENT_STYLESHEET);
    }

    public SidebarNavigation getMenus() {
        return menus;
    }

    public void setMenus(SidebarNavigation menus) {
        if (this.menus == null) {
            getChildren().add(0, menus);
        } else {
            this.menus.setOnNavigate(null);
            getChildren().set(0, menus);
        }
        this.menus = menus;
        this.menus.setOnNavigate(this::setContent);
        this.menus.eachMenu(menu -> {
            if (menu.isSelected()) {
                setContent(menu.getActionView());
            }
        });
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        if (this.content == null) {
            getChildren().add(content);
        } else {
            getChildren().set(1, content);
        }
        this.content = content;
        HBox.setHgrow(content, Priority.ALWAYS);

    }
}
