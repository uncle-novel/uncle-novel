package com.unclezs.novel.app.main;

import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.app.framework.appication.BaseApplication;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.support.fonts.FontsLoader;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyManager;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.manager.HotkeyManager;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.util.TrayManager;
import com.unclezs.novel.app.main.util.UpdateUtils;
import com.unclezs.novel.app.main.views.home.HomeView;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * _ooOoo_
 * o8888888o
 * 88" . "88
 * (| -_- |)
 *  O\ = /O
 * ___/`---'\____
 * .   ' \\| |// `.
 * / \\||| : |||// \
 * / _||||| -:- |||||- \
 * | | \\\ - /// | |
 * | \_| ''\---/'' | |
 * \ .-\__ `-` ___/-. /
 * ___`. .' /--.--\ `. . __
 * ."" ' `.___\_|_/___.' '"".
 * | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * \ \ `-. \_ __\ /__ _/ .-` / /
 * ======`-.____`-.___\_____/___.-`____.-'======
 * `=---='
 * .............................................
 * 佛曰：bug泛滥，我已瘫痪！
 * </pre>
 *
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
@Slf4j
public class App extends BaseApplication {

  public static final String NAME = "Uncle小说";
  long start;

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * 获取APP的舞台
   *
   * @return 舞台
   */
  public static Stage stage() {
    return AppContext.getInstance().getPrimaryStage();
  }

  /**
   * 提交退出事件
   */
  public static void requestExit() {
    Platform.setImplicitExit(true);
    stage().fireEvent(new WindowEvent(stage(), WindowEvent.WINDOW_CLOSE_REQUEST));
  }

  /**
   * 显示窗口
   */
  public static void requestShow() {
    if (!stage().isShowing()) {
      Platform.setImplicitExit(false);
      stage().show();
    }
    stage().toFront();
  }

  /**
   * 最小化到系统托盘
   */
  public static void tray() {
    Platform.setImplicitExit(false);
    stage().fireEvent(new WindowEvent(stage(), WindowEvent.WINDOW_CLOSE_REQUEST));
  }

  /**
   * 初始化
   *
   * @throws Exception /
   */
  @Override
  public void init() throws Exception {
    start = System.currentTimeMillis();
    super.init();
    SettingManager.init();
    // 初始化托盘图标
    TrayManager.init();
    // 初始化
    HotkeyManager.init();
    // 加载字体
    FontsLoader.loadFonts(ResourceManager.FONTS_DIR);
  }

  /**
   * 启动
   *
   * @param stage 舞台
   * @throws Exception 启动失败
   */
  @Override
  public void start(Stage stage) throws Exception {
    super.init();
    super.start(stage);
    initStage(stage);
    stage.show();
    UpdateUtils.checkForUpdate(stage);
    log.trace("启动耗时：{}ms", (System.currentTimeMillis() - start));
  }

  /**
   * App停止事件处理
   */
  @Override
  public void stop() {
    super.stop();
    SettingManager.save();
    // 释放全局热键
    ThreadUtils.newThread(HotKeyManager::unbind, false).start();
    Platform.exit();
    System.exit(0);
  }

  /**
   * 初始化舞台
   *
   * @param stage 舞台
   */
  private void initStage(Stage stage) {
    stage.initStyle(StageStyle.TRANSPARENT);
    // 图标
    String[] icons = {"16", "32", "48", "64", "128"};
    for (String icon : icons) {
      stage.getIcons().add(new Image(ResourceUtils.stream(String.format("assets/logo/icon-%s.png", icon))));
    }
  }

  /**
   * 首页
   *
   * @return 首页 HomeView
   */
  @Override
  public SceneView<? extends Region> getIndexView() {
    return AppContext.getView(HomeView.class);
  }
}
