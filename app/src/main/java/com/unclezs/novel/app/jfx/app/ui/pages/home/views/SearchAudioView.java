package com.unclezs.novel.app.jfx.app.ui.pages.home.views;

import com.jfoenix.controls.JFXPopup;
import com.unclezs.novel.app.jfx.framework.annotation.FxView;
import com.unclezs.novel.app.jfx.framework.ui.components.sidebar.NavigateBundle;
import com.unclezs.novel.app.jfx.framework.ui.components.sidebar.SidebarNavigationView;
import com.unclezs.novel.app.jfx.app.ui.app.App;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/home/views/search_audio.fxml")
public class SearchAudioView extends SidebarNavigationView {
    public Text text;
    public Button button;

    @Override
    public void onShow(NavigateBundle bundle) {
        if (bundle.getData() != null) {
            text.setText("来自：" + bundle.getFrom() + "   携带数据：" + bundle.get("data"));
        }
    }

    @Override
    public void onCreated() {
        button.setOnMouseClicked(event -> {
            new JFXPopup().show(App.app.getStage());
        });
    }
}

