package com.uncles.novel.app.jfx.framework.ui.components.sidebar;

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
    private SidebarNavigation menus;
    private Node content;


    public SidebarNavigationPane() {
    }


    public SidebarNavigation getMenus() {
        return menus;
    }

    public void setMenus(SidebarNavigation menus) {
        if (this.menus == null) {
            getChildren().add(0, menus);
        } else {
            this.menus.setOnNavigationMenuClick(null);
            getChildren().set(0, menus);
        }
        this.menus = menus;
        this.menus.setOnNavigationMenuClick(menu -> setContent(menu.getActionView()));
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
