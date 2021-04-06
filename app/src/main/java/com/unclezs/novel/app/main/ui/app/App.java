package com.unclezs.novel.app.main.ui.app;

import com.unclezs.jfx.launcher.Manifest;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.appication.SceneViewNavigateBundle;
import com.unclezs.novel.app.framework.appication.SsaApplication;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.ui.pages.home.HomeSceneView;
import com.unclezs.novel.app.main.util.DebugUtils;
import java.util.Locale;
import java.util.Random;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * SSA
 *
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
@Slf4j
public class App extends SsaApplication {

  public static Manifest manifest;
  public static App app;

  public App() {
    if (app != null) {
      throw new IllegalStateException("不可以创建多个App");
    }
    app = this;
  }

  /**
   * 设置主题 css
   *
   * @param styleSheetPath 主题样式表类路径
   */
  public static void changeTheme(String styleSheetPath) {
    String styleSheet = ResourceUtils.loadCss(styleSheetPath);
    app.getStage().getScene().getStylesheets().setAll(styleSheet);
  }

  /**
   * 场景切换
   *
   * @param viewClass View类
   */
  public static void redirect(Class<? extends SceneView> viewClass) {
    app.navigate(viewClass);
  }

  /**
   * 场景切换
   *
   * @param viewClass View类
   * @param bundle    绑定数据
   */
  public static void redirect(Class<? extends SceneView> viewClass, SceneViewNavigateBundle bundle) {
    app.navigate(viewClass, bundle);
  }

  @Override
  public void init() throws Exception {
    DebugUtils.init();
//    SvgIcon.load("/assets/icons/svg.properties");
    Random random = new Random();
    int i = random.nextInt(3);
    if (i == 1) {
      Locale.setDefault(Locale.ENGLISH);
    } else if (i == 2) {
      Locale.setDefault(Locale.TAIWAN);
    }
    Locale.setDefault(Locale.TAIWAN);
    super.init();
//    System.out.println(App.class.getResource("/layout/home/home.fxml"));
//    System.out.println(App.class.getClassLoader().getResource("/layout/home/home.fxml"));
//    System.out.println(ResourceUtils.class.getModule().getResourceAsStream("/fonts/pingfang-simple-bold.ttf"));
//    URL load = ResourceUtils.load("/layout/home/home.fxml");
  }

  @Override
  public void start(Stage stage) throws Exception {
    manifest = (Manifest) stage.getUserData();
    log.error("{}", manifest);
    super.start(stage);
  }

  @Override
  public Class<? extends SceneView> getIndexView() {
    return HomeSceneView.class;
  }


  public static void main(String[] args) {
    launch(args);
  }

}
