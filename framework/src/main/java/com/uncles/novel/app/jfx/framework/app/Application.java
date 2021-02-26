package com.uncles.novel.app.jfx.framework.app;

import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 10:50
 */
public abstract class Application extends javafx.application.Application {
    public static final int INIT_WIDTH = 900;
    public static final int INIT_HEIGHT = 600;
    private Stage stage;
    private Scene scene;
    private Parent view;

    static {
        // 设置主题
        javafx.application.Application.setUserAgentStylesheet(ResourceUtils.loadCss("application.css").toExternalForm());
        javafx.application.Application.setUserAgentStylesheet(STYLESHEET_CASPIAN);
    }

    @Override
    public void init() throws Exception {
//        FontUtils.loadFont();
    }

    @Override
    public final void start(Stage stage) throws Exception {
        this.stage = stage;
        this.view = getView();
        this.scene = new Scene(view, INIT_WIDTH, INIT_HEIGHT);
        this.stage.setScene(scene);
        this.stage.show();
    }

    /**
     * 获取试图
     *
     * @return 视图
     * @throws Exception 异常
     */
    public abstract Parent getView() throws Exception;

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }
}
