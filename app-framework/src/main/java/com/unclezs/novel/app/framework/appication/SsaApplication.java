package com.unclezs.novel.app.framework.appication;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.stage.StageHelper;
import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.factory.ViewFactory;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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

  /**
   * JavaFX 线程名称，原名太长，日志里面不好看
   */
  public static final String FX_THREAD_NAME = "FX";
  /**
   * Application CSS
   */
  private static final String APP_STYLE = ResourceUtils.loadCss("css/application.css");

  static {
    Thread.currentThread().setName(FX_THREAD_NAME);
  }

  /**
   * 场景View缓存
   */
  private final Map<Class<?>, SceneView<? extends Parent>> views = new HashMap<>();
  @Getter
  private Stage stage;
  /**
   * 当前view
   */
  @Getter
  private SceneView<? extends Parent> currentView;

  /**
   * 初始化 preloader 加载
   *
   * @throws Exception 加载失败
   */
  @Override
  public void init() throws Exception {
  }

  /**
   * @param stage null即可
   */
  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;
    Application.setUserAgentStylesheet(APP_STYLE);
    // 设置主题
    List<String> theme = Arrays.asList("com/sun/javafx/scene/control/skin/modena/modena.css", APP_STYLE);
    StyleManager.getInstance().setUserAgentStylesheets(theme);
    StageHelper.setPrimary(stage, true);
    stage.initStyle(StageStyle.TRANSPARENT);
    Image icon = getIcon();
    if (icon != null) {
      stage.getIcons().setAll(icon);
    }
    // 生命周期监听
    stage.onShowingProperty().addListener(e -> currentView.onShow(new SceneViewNavigateBundle()));
    stage.setOnCloseRequest(e -> {
      views.values().forEach(SceneView::onDestroy);
      System.exit(0);
    });
    // 首页
    currentView = getIndexView();
    loadSceneView(currentView);
    Scene scene = currentView.getRoot().getScene();
    scene.getStylesheets().add(APP_STYLE);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * 返回首页 初次自动加载
   *
   * @return 视图
   * @throws Exception 异常
   */
  public abstract SceneView<? extends Parent> getIndexView() throws Exception;


  /**
   * 场景View切换
   *
   * @param viewClass 继承SceneView 的class
   */
  public <V extends Parent> void navigate(Class<? extends SceneView<V>> viewClass) {
    navigate(viewClass, null);
  }

  /**
   * 场景View切换
   *
   * @param viewClass 继承SceneView 的class
   * @param bundle    数据
   */
  protected <V extends Parent> void navigate(Class<? extends SceneView<V>> viewClass, SceneViewNavigateBundle bundle) {
    SceneView<V> sceneView = ViewFactory.me().getController(viewClass);
    loadSceneView(sceneView);
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
   * @param sceneView 场景View
   */
  public void loadSceneView(SceneView<? extends Parent> sceneView) {
    if (sceneView.getScene() == null) {
      if (sceneView.getRoot() instanceof StageDecorator) {
        ((StageDecorator) sceneView.getRoot()).setStage(stage, sceneView);
      }
      sceneView.setScene(new Scene(sceneView.getRoot(), Color.TRANSPARENT));
      // 场景创建完成回调
      sceneView.onSceneCreated(sceneView.getScene());
      views.put(sceneView.getClass(), sceneView);
    }
    // 宽高绑定
    stage.setMinWidth(sceneView.getRoot().minWidth(-1));
    stage.setMinHeight(sceneView.getRoot().minHeight(-1));
    stage.setHeight(stage.getMinHeight());
    stage.setWidth(stage.getMinWidth());
  }

  /**
   * 设置应用图标
   *
   * @return 图标
   */
  protected Image getIcon() {
    return null;
  }
}
