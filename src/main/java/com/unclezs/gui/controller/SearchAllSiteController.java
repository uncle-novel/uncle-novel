package com.unclezs.gui.controller;

import cn.hutool.core.lang.Dict;
import com.jfoenix.controls.JFXProgressBar;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.AlertUtil;
import com.unclezs.gui.utils.ContentUtil;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;

/**
 * 全网搜书
 *
 * @author uncle
 * @date 2020/7/19 13:14
 */
@Slf4j
@FXController("search_all_site")
public class SearchAllSiteController implements LifeCycleFxController {
    private static final String INDEX_URL = "https://www.baidu.com/s?wd=遮天 小说章节列表";
    public WebView webView;
    public JFXProgressBar progress;
    private WebEngine engine;

    @Override
    public void initialize() {
        engine = webView.getEngine();
        engine.load(INDEX_URL);
        webView.setContextMenuEnabled(false);
        createContextMenu(webView);
        engine.getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> {
            progress.setProgress(newValue.doubleValue() == 0 ? 0.1 : newValue.doubleValue());
            progress.setVisible(progress.getProgress() != 1);
        });
    }

    /**
     * 创建上下文菜单
     *
     * @param webView /
     */
    private void createContextMenu(WebView webView) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem reload = new MenuItem("刷新");
        MenuItem analysis = new MenuItem("解析小说");
        analysis.setOnAction(e -> analysis());
        reload.setOnAction(e -> engine.reload());
        contextMenu.getItems().addAll(reload, analysis);
        webView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(webView, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    /**
     * 解析当前页面
     */
    public void analysis() {
        String location = webView.getEngine().getLocation();
        ContentUtil.show(AnalysisController.class, Dict.create().set("url", location));
    }

    /**
     * 主页
     */
    public void backHome() {
        engine.load(INDEX_URL);
    }

    /**
     * 后退
     */
    public void backPage() {
        if (engine.getHistory().getCurrentIndex() != 0) {
            engine.getHistory().go(-1);
        }
    }

    /**
     * 帮助
     */
    public void help() {
        AlertUtil.alert("怎么使用？", "找到小说的目录页面，点击解析本页目录按钮即可解析下载，绝大部分网站都可以下载的");
    }
}
