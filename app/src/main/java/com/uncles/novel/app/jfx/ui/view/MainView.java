package com.uncles.novel.app.jfx.ui.view;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.app.SceneView;
import com.uncles.novel.app.jfx.framework.ui.components.decorator.StageDecorator;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.SidebarNavigationMenu;
import com.uncles.novel.app.jfx.framework.view.BaseView;
import com.uncles.novel.app.jfx.ui.MainViewModel;
import com.uncles.novel.app.jfx.ui.stage.App;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:23
 */
@FxView(fxml = "/layout/main.fxml", bundle = "i18n.home")
public class MainView extends BaseView implements SceneView {
    public SidebarNavigationMenu search;
    private static final MainViewModel VIEW_MODEL = new MainViewModel();

    public void print() {
        System.out.println(localized("Uncle小说"));
    }

    @Override
    public void onCreated() {
        search.setOnMouseClicked(e-> App.redirect(ReaderView.class));
    }

    @Override
    public void onTheme(StageDecorator view) {
        System.out.println("主题被点击");
    }
}
