package com.uncles.novel.app.jfx.framework.ui.appication;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.stage.StageHelper;
import com.uncles.novel.app.jfx.framework.ui.components.decorator.StageDecorator;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;
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
public abstract class SsaApplication extends Application {
    @Getter
    private Stage stage;
    /**
     * 当前view
     */
    @Getter
    private SceneView currentView;
    /**
     * 场景View缓存
     */
    private final Map<Class<?>, SceneView> views = new HashMap<>();
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
        if (stage != null) {
            return;
        }
        stage = new Stage();
        StageHelper.setPrimary(stage, true);
        stage.getIcons().setAll(ICON);
        // 生命周期监听
        stage.onShowingProperty().addListener(e -> currentView.onShow(new SceneViewNavigateBundle()));
        stage.setOnCloseRequest(e -> views.values().forEach(SceneView::onDestroy));
        // 首页
        currentView = loadSceneView(getIndexView());
        stage.setScene(currentView.getScene());
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
     */
    public <T extends SceneView> void navigate(Class<? extends SceneView> viewClass) {
        navigate(viewClass, null);
    }

    /**
     * 场景View切换
     *
     * @param viewClass 继承SceneView 的class
     * @param bundle    数据
     */
    protected void navigate(Class<? extends SceneView> viewClass, SceneViewNavigateBundle bundle) {
        SceneView sceneView = loadSceneView(viewClass);
        if (bundle == null) {
            bundle = new SceneViewNavigateBundle();
            bundle.setFrom(sceneView.getClass().getName());
        }
        // view生命周期回调
        if (currentView != null) {
            currentView.onHidden();
        }
        currentView = sceneView;
        sceneView.onShow(bundle);
        stage.setScene(sceneView.getScene());
        stage.centerOnScreen();
    }


    /**
     * 获取场景view
     *
     * @param viewClass 继承SceneView 的class
     * @return SceneView
     */
    public SceneView loadSceneView(@NonNull Class<? extends SceneView> viewClass) {
        SceneView sceneView = views.get(viewClass);
        if (sceneView == null) {
            sceneView = FxmlLoader.load(viewClass);
            if (sceneView.getView() instanceof StageDecorator) {
                StageDecorator decorator = (StageDecorator) sceneView.getView();
                decorator.setStage(stage, sceneView);
            }
            sceneView.setScene(new Scene(sceneView.getView(), Color.TRANSPARENT));
            // 场景创建完成回调
            sceneView.onSceneCreated(sceneView.getScene());
            views.put(viewClass, sceneView);
        }
        // 宽高绑定
        stage.setMinWidth(sceneView.getView().minWidth(-1));
        stage.setMinHeight(sceneView.getView().minHeight(-1));
        stage.setHeight(stage.getMinHeight());
        stage.setWidth(stage.getMinWidth());
        return sceneView;
    }
}
