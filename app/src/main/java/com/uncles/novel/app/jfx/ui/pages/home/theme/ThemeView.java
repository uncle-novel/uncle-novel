package com.uncles.novel.app.jfx.ui.pages.home.theme;

import com.jfoenix.controls.JFXPopup;
import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.ui.view.BaseView;
import com.uncles.novel.app.jfx.ui.app.App;

/**
 * @author blog.unclezs.com
 * @date 2021/03/06 18:16
 */
@FxView(fxml = "/layout/home/theme/theme.fxml")
public class ThemeView extends BaseView {
    public JFXPopup popup;

    @Override
    public void onCreated() {

    }

    public void show() {
        popup.show(App.stage());
    }
}
