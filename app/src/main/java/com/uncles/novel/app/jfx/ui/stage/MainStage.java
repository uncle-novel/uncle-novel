package com.uncles.novel.app.jfx.ui.stage;

import com.uncles.novel.app.jfx.framework.app.Application;
import com.uncles.novel.app.jfx.framework.ui.components.LeftTabPane;
import javafx.scene.Parent;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class MainStage extends Application {
    @Override
    public Parent getView() throws Exception {
        //        VBox view = FxmlLoader.load(MainController.class);
        LeftTabPane view = new LeftTabPane();
        return view;
    }
}
