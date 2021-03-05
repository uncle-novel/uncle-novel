package com.uncles.novel.app.jfx.ui.view;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.NavigateBundle;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.SidebarNavigationView;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

/**
 * @author blog.unclezs.com
 * @since 2021/03/05 17:26
 */
@FxView(fxml = "/layout/setting.fxml")
public class SettingView extends SidebarNavigationView {
    public Button button;
    public Text text;

    @Override
    public void onShow(NavigateBundle bundle) {
        if (bundle.getData() != null) {
            text.setText("来自：" + bundle.getFrom() + "   携带数据：" + bundle.get("data"));
        }
    }

    @Override
    public void onCreated() {
        button.setOnMouseClicked(event -> {
            NavigateBundle bundle = new NavigateBundle().put("data", "我是SettingView");
            navigate(SearchNetworkView.class, bundle);
        });
    }
}
