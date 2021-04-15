package com.unclezs.novel.app.framework.appication;

import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 10:50
 */
@Slf4j
public abstract class BaseApplication extends Application {

  /**
   * JavaFX 线程名称，原名太长，日志里面不好看
   */
  public static final String FX_THREAD_NAME = "FX";
  public static final String APPLICATION_STYLESHEET = "css/application.css";
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
    Thread.currentThread().setName(FX_THREAD_NAME);
  }

  /**
   * @param stage 舞台
   */
  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;
    AppContext.setStage(stage);
    // 生命周期监听
    stage.onShowingProperty().addListener(e -> currentView.onShow(new SceneNavigateBundle()));
    // 首页
    loadSceneView(getIndexView());
    stage.setScene(currentView.getRoot().getScene());
  }

  @Override
  public void stop() {
    views.values().forEach(SceneView::onDestroy);
    System.exit(0);
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
  public <V extends Parent> void navigate(Class<? extends SceneView<V>> viewClass, SceneNavigateBundle bundle) {
    // view生命周期回调
    if (currentView != null) {
      currentView.onHidden();
    }
    SceneView<V> sceneView = AppContext.getView(viewClass);
    loadSceneView(sceneView);
    if (bundle == null) {
      bundle = new SceneNavigateBundle();
      bundle.setFrom(sceneView.getClass().getName());
    }
    sceneView.onShow(bundle);
    stage.setScene(sceneView.getRoot().getScene());
    stage.centerOnScreen();
  }


  /**
   * 获取场景view
   *
   * @param sceneView 场景View
   */
  public void loadSceneView(SceneView<? extends Parent> sceneView) {
    this.currentView = sceneView;
    if (sceneView.getRoot().getScene() == null) {
      // 注入application用于导航
      sceneView.setApp(this);
      if (sceneView.getRoot() instanceof StageDecorator) {
        ((StageDecorator) sceneView.getRoot()).setStage(stage, sceneView);
      }
      Scene scene = new Scene(sceneView.getRoot(), Color.TRANSPARENT);
      scene.getStylesheets().setAll(ResourceUtils.externalForm(APPLICATION_STYLESHEET));
      // 场景创建完成回调
      views.put(sceneView.getClass(), sceneView);
      sceneView.onCreated();
    }
    // 宽高绑定
    stage.setMinWidth(sceneView.getRoot().minWidth(-1));
    stage.setMinHeight(sceneView.getRoot().minHeight(-1));
    stage.setHeight(stage.getMinHeight());
    stage.setWidth(stage.getMinWidth());
  }
}
