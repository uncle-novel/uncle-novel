package com.uncles.novel.app.jfx.ui.pages.home;

import com.jfoenix.controls.JFXPopup;
import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.ui.appication.SceneView;
import com.uncles.novel.app.jfx.framework.ui.appication.SceneViewNavigateBundle;
import com.uncles.novel.app.jfx.framework.ui.components.button.IconButton;
import com.uncles.novel.app.jfx.framework.ui.components.decorator.StageDecorator;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.NavigateBundle;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.SidebarNavigationPane;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import com.uncles.novel.app.jfx.ui.MainViewModel;
import com.uncles.novel.app.jfx.ui.pages.home.theme.ThemeView;
import com.uncles.novel.app.jfx.ui.pages.home.views.SettingView;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:23
 */
@FxView(fxml = "/layout/home/home.fxml", bundle = "i18n.home")
public class HomeSceneView extends SceneView {
    public StageDecorator root;
    public SidebarNavigationPane container;
    private static final MainViewModel VIEW_MODEL = new MainViewModel();

    public void print() {
        System.out.println(localized("Uncle小说"));
    }

    @Override
    public void onCreated() {
        System.out.println("MainView created");
    }

    @Override
    public void onTheme(StageDecorator view, IconButton themeButton) {
        System.out.println("12312312");
        NavigateBundle bundle = new NavigateBundle().put("data", "我来自: mainView");
        container.navigateTo(SettingView.class, bundle);
    }

    @Override
    public void onSetting(StageDecorator view, IconButton settingButton) {
        ThemeView themeView = FxmlLoader.load(ThemeView.class);
        JFXPopup popup = themeView.getView();
        popup.show(settingButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, 0, 40);
    }

    @Override
    public void onShow(SceneViewNavigateBundle bundle) {
        System.out.println("MainView show");
        String data = bundle.get("data");
        if (data != null) {
            System.out.println("MainView收到数据：" + data);
        }
    }

    @Override
    public void onHidden() {
        System.out.println("MainView hidden");
    }

    @Override
    public void onDestroy() {
        System.out.println("MainView destroy");
    }
}
