package com.unclezs.novel.app.framework.appication;

import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
   * 当前view
   */
  protected SceneView<? extends Region> currentView;
  private Stage stage;
  /**
   * 舞台坐标，切换舞台场景时使用
   */
  private double stageX = -1;
  private double stageY = -1;

  /**
   * 返回首页 初次自动加载
   *
   * @return 视图
   */
  public abstract SceneView<? extends Region> getIndexView();

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
    // 启动时候注册舞台
    AppContext.getInstance().setPrimaryStage(stage);
    // 首页
    loadSceneView(getIndexView());
    initScene();
    getIndexView().onShow(new SceneNavigateBundle());
  }

  /**
   * 停止应用，销毁上下文
   */
  @Override
  public void stop() {
    AppContext.getInstance().destroy();
  }

  /**
   * 场景View切换
   *
   * @param viewClass 继承SceneView 的class
   */
  public void navigate(Class<? extends SceneView<? extends Region>> viewClass) {
    navigate(viewClass, null);
  }

  /**
   * 场景View切换
   *
   * @param viewClass 继承SceneView 的class
   * @param bundle    数据
   */
  public void navigate(Class<? extends SceneView<? extends Region>> viewClass, SceneNavigateBundle bundle) {
    // view生命周期回调
    if (currentView != null) {
      currentView.onHidden();
    }
    // 加载sceneView
    SceneView<? extends Region> sceneView = AppContext.getView(viewClass);
    loadSceneView(sceneView);
    // 先隐藏 再显示 过渡效果
    if (stage.isShowing()) {
      stage.hide();
    }
    // 场景间参数传递
    if (bundle == null) {
      bundle = new SceneNavigateBundle();
      bundle.setFrom(sceneView.getClass().getName());
    }
    initScene();
    // 触发生命周期
    sceneView.onShow(bundle);
    stage.show();
  }


  /**
   * 获取场景view
   *
   * @param sceneView 场景View
   */
  private void loadSceneView(SceneView<? extends Region> sceneView) {
    this.currentView = sceneView;
    if (sceneView.getRoot().getScene() == null) {
      // 注入application用于导航
      sceneView.setApp(this);
      if (sceneView.getRoot() instanceof StageDecorator) {
        ((StageDecorator) sceneView.getRoot()).setStage(stage, sceneView);
      }
      Scene scene = new Scene(sceneView.getRoot(), Color.TRANSPARENT);
      scene.getStylesheets().setAll(ResourceUtils.externalForm(APPLICATION_STYLESHEET));
      sceneView.onCreated();
    }
  }

  /**
   * 切换场景 初始化
   */
  private void initScene() {
    stage.setScene(currentView.getRoot().getScene());
    stage.setMinWidth(currentView.getRoot().getMinWidth());
    stage.setMinHeight(currentView.getRoot().getMinHeight());
    if (currentView.getRoot().getWidth() == 0) {
      stage.setHeight(currentView.getRoot().getPrefHeight());
      stage.setWidth(currentView.getRoot().getPrefWidth());
    } else {
      stage.setHeight(currentView.getRoot().getHeight());
      stage.setWidth(currentView.getRoot().getWidth());
    }
    // 舞台位置
    double x = stage.getX();
    double y = stage.getY();
    if (stageX > 0 && stageY > 0) {
      stage.setX(stageX);
      stage.setY(stageY);
    }
    stageX = x;
    stageY = y;
  }
}
