package com.uncles.novel.app.jfx.ui.view;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.app.SceneView;
import com.uncles.novel.app.jfx.framework.ui.components.decorator.StageDecorator;
import com.uncles.novel.app.jfx.framework.view.BaseView;
import com.uncles.novel.app.jfx.ui.stage.App;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 12:13
 */
@FxView(fxml = "/layout/reader.fxml", bundle = "i18n.framework")
public class ReaderView extends BaseView implements SceneView {
    public HBox box;
    public ToggleButton btn1;
    public ToggleButton btn2;
    public ToggleButton btn3;
    public ToggleButton btn4;

    @Override
    public void onCreated() {

    }

    public void toHome() {
        App.redirect(MainView.class);
    }

    @Override
    public void onTheme(StageDecorator view) {
        System.out.println("主题被点击1");
    }
}
