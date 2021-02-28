package com.uncles.novel.app.jfx.ui.view;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.view.BaseView;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:23
 */
@FxView(fxml = "/layout/main.fxml", bundle = "basic")
public class MainView extends BaseView {

    public void print() {
        System.out.println(str("app_name"));
    }
}
