package com.unclezs.gui.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.unclezs.gui.app.App;
import com.unclezs.gui.app.Reader;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.ContentUtil;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 主页
 *
 * @author unclezs.com
 * @date 2019.06.20 23:46
 */
@Slf4j
@FXController("index")
@SuppressWarnings("unchecked")
public class IndexController implements LifeCycleFxController {
    public BorderPane container;
    public VBox menu;
    private Class<? extends LifeCycleFxController> indexPage = BookShelfController.class;

    @Override
    public void initialize() {
        initMenu();
        loadAll();
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        App.contentContainer = container;
        AtomicBoolean exist = new AtomicBoolean(false);
        menu.getChildren().stream().filter(i -> i instanceof Button).forEach(item -> {
            item.setOnMouseClicked(e -> {
                String classPackage = "";
                try {
                    classPackage = (String) item.getUserData();
                    Class<?> controller = Class.forName(classPackage);
                    ContentUtil.show((Class<? extends LifeCycleFxController>) controller, item);
                } catch (ClassNotFoundException ex) {
                    log.error(
                        "没有找到这个类【{}】,检查菜单按钮里面得UserData是不是写错了，需要对应Controller的全限定类名",
                        classPackage);
                }
            });
            if (item.getUserData().equals(indexPage.getName())) {
                exist.set(true);
                ContentUtil.show(indexPage, item);
            }
        });
        if (!exist.get()) {
            ContentUtil.show(indexPage);
        }

    }

    /**
     * 异步加载全部页面（降低点击延迟）
     */
    private void loadAll() {
        Stage readerStage = new Stage();
        ThreadUtil.execute(() -> {
            Reader reader = new Reader();
            try {
                reader.start(readerStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            ContentUtil.getController(SearchAllSiteController.class);
            ContentUtil.getController(AudioBookSelfController.class);
            ContentUtil.getController(AnalysisController.class);
            ContentUtil.getController(DownloadController.class);
            ContentUtil.getController(SearchController.class);
            ContentUtil.getController(SearchAudioController.class);
            ContentUtil.getController(SettingController.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
