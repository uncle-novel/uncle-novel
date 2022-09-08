package com.unclezs.gui.controller;

import cn.hutool.core.lang.Dict;
import com.unclezs.gui.app.App;
import com.unclezs.gui.extra.FXController;
import javafx.scene.web.WebView;

/**
 * 问题反馈
 *
 * @author uncle
 * @date 2020/6/18 22:03
 */
@FXController("feedback")
public class FeedbackController implements LifeCycleFxController {
    public WebView webView;
    private double width = 1400;
    private double height = 1000;

    @Override
    public void initialize() {
        webView.getEngine().load("https://support.qq.com/products/169599");
    }

    @Override
    public void onShow(Dict data) {
        width = App.stage.getWidth();
        height = App.stage.getHeight();
        App.stage.setWidth(1400);
        App.stage.setHeight(1000);
    }

    @Override
    public void onHidden() {
        App.stage.setWidth(width);
        App.stage.setHeight(height);
    }
}
