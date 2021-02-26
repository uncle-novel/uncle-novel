package com.uncles.novel.app.jfx.framework.ui.components;

import com.sun.javafx.collections.TrackableObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 18:50
 */
public class MenuPane extends VBox {
    private ObservableList<LeftTab> menus = new TrackableObservableList<>() {
        @Override
        protected void onChanged(ListChangeListener.Change<LeftTab> c) {
            while (c.next()) {
//                getChildren().removeAll(c.getRemoved());
//                getChildren().addAll(c.getAddedSubList());
            }
        }
    };

    public MenuPane() {
        System.out.println("123");
        this.menus.add(new LeftTab("完美实际", "asdasd"));
        this.getChildren().setAll(menus);
    }

    public MenuPane(ObservableList<LeftTab> menus) {
        this.menus = menus;
    }

    public ObservableList<LeftTab> getMenus() {
        return menus;
    }

    public MenuPane(LeftTab... menus) {
        this();
        System.out.println(1233212312);
        if (menus != null) {
            getMenus().addAll(menus);
        }
    }
}
