package com.uncles.novel.app.jfx.framework.app;

import com.sun.javafx.css.StyleManager;
import com.uncles.novel.app.jfx.framework.lifecycle.LifeCycle;
import com.uncles.novel.app.jfx.framework.ui.components.decorator.StageDecorator;
import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义 Application
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 10:50
 */
@Getter
public abstract class Application extends javafx.application.Application implements LifeCycle, StageDecorator.ActionHandler {
    private Stage stage;
    private Scene scene;
    private Parent view;
    /**
     * 自定义Application CSS
     */
    private static final String APP_STYLE = ResourceUtils.loadCss("/css/application.css");
    /**
     * 图标
     */
    public static final Image ICON = new Image(ResourceUtils.load("/assets/favicon.png").toString());

    static {
        Application.setUserAgentStylesheet(APP_STYLE);
        // 设置主题
        List<String> theme = Arrays.asList("com/sun/javafx/scene/control/skin/modena/modena.css", APP_STYLE);
        StyleManager.getInstance().setUserAgentStylesheets(theme);
    }

    /**
     * 初始化 preloader 加载
     *
     * @throws Exception 加载失败
     */
    @Override
    public void init() throws Exception {
        this.view = getView();
    }

    /**
     * 入口
     *
     * @param stage 舞台
     */
    @Override
    public final void start(Stage stage) {
        this.stage = stage;
        this.stage.setMinWidth(view.minWidth(-1));
        this.stage.setMinHeight(view.minHeight(-1));
        this.stage.getIcons().setAll(ICON);
        if (this.view instanceof StageDecorator) {
            StageDecorator decorator = (StageDecorator) this.view;
            decorator.setStage(stage, this);
        }
        this.scene = new Scene(view, Color.TRANSPARENT);
        this.stage.setScene(scene);
        onCreated();
        this.stage.onHiddenProperty().addListener(e -> onHidden());
        this.stage.onShowingProperty().addListener(e -> onShow());
        this.stage.show();
    }

    /**
     * 获取试图,只会调用一次
     *
     * @return 视图
     * @throws Exception 异常
     */
    public abstract Parent getView() throws Exception;
}
