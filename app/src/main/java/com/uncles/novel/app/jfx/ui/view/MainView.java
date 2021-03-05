package com.uncles.novel.app.jfx.ui.view;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.app.SceneView;
import com.uncles.novel.app.jfx.framework.ui.components.decorator.StageDecorator;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.NavigateBundle;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.SidebarNavigationPane;
import com.uncles.novel.app.jfx.framework.view.BaseView;
import com.uncles.novel.app.jfx.ui.MainViewModel;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:23
 */
@FxView(fxml = "/layout/home.fxml", bundle = "i18n.home")
public class MainView extends BaseView implements SceneView {
    public StageDecorator root;
    public SidebarNavigationPane container;
    private static final MainViewModel VIEW_MODEL = new MainViewModel();

    public void print() {
        System.out.println(localized("Uncle小说"));
    }

//    @Override
//    public void onCreated() {
//        search.setOnMouseClicked(e-> App.redirect(ReaderView.class));
//    }

    @Override
    public void onTheme(StageDecorator view) {
        System.out.println("12312312");
        NavigateBundle bundle = new NavigateBundle().put("data", "我来自: mainView");
        container.navigateTo(SettingView.class, bundle);
    }
}
