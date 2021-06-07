package com.unclezs.novel.app.main;

import cn.hutool.core.util.StrUtil;
import com.jfoenix.utils.JFXUtilities;
import com.unclezs.novel.app.framework.appication.BaseApplication;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.exception.FxException;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.support.fonts.FontsLoader;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.manager.HotkeyManager;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.util.TrayManager;
import com.unclezs.novel.app.main.views.home.HomeView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  /**
   * jfx-launcher更新参数
   */
  public static final String CHANGE_LOG_KEY = "changeLog";
  public static final String VERSION_KEY = "version";
  public static final String NAME = "Uncle小说";

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
    super.init();
    SettingManager.init();
    // 初始化托盘图标
    TrayManager.init();
    // 初始化
    HotkeyManager.init();
    // 加载字体
    FontsLoader.loadFonts(ResourceManager.FONTS_DIR);
  }

  @Override
  public void start(Stage stage) {
    try {
      super.start(stage);
      initStage(stage);
      stage.show();
      checkForUpdate(stage);
    } catch (Throwable e) {
      e.printStackTrace();
      throw new FxException(e);
    }
  }

  /**
   * App停止事件处理
   */
  @Override
  public void stop() {
    super.stop();
    SettingManager.save();
    Platform.exit();
    System.exit(0);
  }

  private void mockUpdate(Stage stage) {
    Map<String, Object> map = new HashMap<>();
    map.put(CHANGE_LOG_KEY, List.of("我是谁呢", "你又是谁呢"));
    map.put(VERSION_KEY, "5.5.54");
    stage.setUserData(map);
  }

  private void initStage(Stage stage) {
    stage.initStyle(StageStyle.TRANSPARENT);
    // 图标
    String[] icons = {"16", "32", "48", "64", "128"};
    for (String icon : icons) {
      stage.getIcons().add(new Image(ResourceUtils.stream(String.format("assets/logo/icon-%s.png", icon))));
    }
  }

  @Override
  public SceneView<? extends Region> getIndexView() {
//    return AppContext.getView(ReaderView.class);
    return AppContext.getView(HomeView.class);
  }

  /**
   * 检测更新，有新版本则显示更新内容
   *
   * @param stage 舞台
   */
  @SuppressWarnings("unchecked")
  public void checkForUpdate(Stage stage) {
    Object userData = stage.getUserData();
    if (userData == null) {
      return;
    }
    Executor.run(() -> {
      Map<String, Object> data = (Map<String, Object>) userData;
      List<String> changeLog = (List<String>) data.get(CHANGE_LOG_KEY);
      StringBuilder whatNew = new StringBuilder();
      for (String newItem : changeLog) {
        whatNew.append(newItem).append(StrUtil.LF);
      }
      String version = (String) data.get(VERSION_KEY);
      Executor.run(() -> JFXUtilities.runInFX(() -> ModalBox.none().cancel("了解了").message(whatNew.toString()).title("更新内容 - V".concat(version)).show()), 1000);
    });
  }
}
