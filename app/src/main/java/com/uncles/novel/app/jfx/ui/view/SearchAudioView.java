package com.uncles.novel.app.jfx.ui.view;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.app.SceneView;
import com.uncles.novel.app.jfx.framework.view.BaseView;
import com.uncles.novel.app.jfx.ui.stage.App;
import javafx.scene.control.Button;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/search_audio.fxml")
public class SearchAudioView extends BaseView implements SceneView {
    public Button navigate;

    @Override
    public void onCreated() {
        navigate.setOnMouseClicked(e-> App.redirect(SearchNovelView.class));
    }
}
