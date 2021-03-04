package com.uncles.novel.app.jfx.ui.view;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.app.SceneView;
import com.uncles.novel.app.jfx.framework.view.BaseView;
import com.uncles.novel.app.jfx.ui.stage.App;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 12:13
 */
@FxView(fxml = "/layout/reader.fxml", bundle = "framework")
public class ReaderView extends BaseView implements SceneView {
    public void toHome() {
        App.redirect(MainView.class);
    }
}
