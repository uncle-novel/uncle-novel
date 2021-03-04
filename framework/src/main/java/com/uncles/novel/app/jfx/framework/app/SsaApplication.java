package com.uncles.novel.app.jfx.framework.app;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.stage.StageHelper;
import com.uncles.novel.app.jfx.framework.lifecycle.LifeCycle;
import com.uncles.novel.app.jfx.framework.ui.components.decorator.StageDecorator;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单舞台 Application
 * <p>
 * Single Stage Application
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 10:50
 */
@Slf4j
public abstract class SsaApplication extends javafx.application.Application implements LifeCycle {
    private static Stage stage;
    private Scene scene;
    /**
     * 场景View缓存
     */
    private final Map<Class<?>, Parent> views = new HashMap<>();
    /**
     * Application CSS
     */
    private static final String APP_STYLE = ResourceUtils.loadCss("/css/application.css");
    /**
     * 图标
     */
    public static final Image ICON = new Image(ResourceUtils.load("/assets/favicon.png").toString());

    static {
        SsaApplication.setUserAgentStylesheet(APP_STYLE);
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
    public final void init() throws Exception {
        stage = new Stage();
        StageHelper.setPrimary(stage, true);
        stage.getIcons().setAll(ICON);
        stage.onHiddenProperty().addListener(e -> onHidden());
        stage.onShowingProperty().addListener(e -> onShow());
        // 首页
        scene = new Scene(navigate(getIndexView()), Color.TRANSPARENT);
        stage.setScene(scene);
    }

    /**
     * 入口，可以先在预启动中调用init，提升用户体验
     *
     * @throws Exception 初始化失败
     */
    public final void start() throws Exception {
        if (stage == null) {
            init();
        }
        stage.show();
    }

    /**
     * @param stage null即可
     */
    @Override
    public final void start(Stage stage) throws Exception {
        start();
    }

    /**
     * 返回首页 初次自动加载
     *
     * @return 视图
     * @throws Exception 异常
     */
    public abstract Class<? extends SceneView> getIndexView() throws Exception;


    /**
     * 场景View切换
     *
     * @param viewClass 继承SceneView 的class
     * @param <T>       类型
     */
    public <T extends SceneView> Parent navigate(Class<T> viewClass) {
        Parent view = views.get(viewClass);
        if (view == null) {
            view = load(viewClass);
            views.put(viewClass, view);
        }
        stage.setMinWidth(view.minWidth(-1));
        stage.setMinHeight(view.minHeight(-1));
        stage.setHeight(stage.getMinHeight());
        stage.setWidth(stage.getMinWidth());
        // 初次加载scene还没有被创建
        if (scene != null) {
            scene.setRoot(view);
        }
        stage.centerOnScreen();
        return view;
    }

    /**
     * 加载view时调用
     *
     * @param viewClass 继承SceneView 的class
     * @param <T>       类型
     * @return view
     */
    public <T extends Parent> T load(@NonNull Class<? extends SceneView> viewClass) {
        FXMLLoader loader = FxmlLoader.loadedLoader(viewClass);
        T view = loader.getRoot();
        if (view instanceof StageDecorator) {
            StageDecorator decorator = (StageDecorator) view;
            decorator.setStage(stage, loader.getController());
        }
        return view;
    }

    /**
     * 获取当前View
     *
     * @return view
     */
    @SuppressWarnings("unchecked")
    public <T extends Parent> T currentView() {
        if (this.scene == null) {
            log.error("需要在创建View之后调用");
            throw new NullPointerException();
        }
        return (T) this.scene.getRoot();
    }

    /**
     * 获取舞台
     *
     * @return 舞台
     */
    public static Stage stage() {
        return stage;
    }
}
