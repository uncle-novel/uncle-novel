package com.uncles.novel.app.jfx.ui.stage;

import com.uncles.novel.app.jfx.framework.app.Application;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import com.uncles.novel.app.jfx.ui.view.MainView;
import javafx.scene.Parent;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class MainApp extends Application {
    @Override
    public Parent getView() throws Exception {
        return FxmlLoader.load(MainView.class);
    }
}
