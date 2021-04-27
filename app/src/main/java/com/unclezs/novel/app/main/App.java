package com.unclezs.novel.app.main;

import cn.hutool.core.util.StrUtil;
import com.jfoenix.utils.JFXUtilities;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.phantomjs.PhantomJsClient;
import com.unclezs.novel.analyzer.util.SystemUtils;
import com.unclezs.novel.app.framework.appication.BaseApplication;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.exception.FxException;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.util.ReflectUtils;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.ui.home.HomeView;
import com.unclezs.novel.app.main.util.DebugUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
   * 初始化
   *
   * @throws Exception
   */
  @Override
  public void init() throws Exception {
    super.init();
    // 预热加载HttpProvider 的 SPI
    ReflectUtils.forName(Http.class.getName());
    DebugUtils.init();
    Random random = new Random();
    int i = random.nextInt(3);
    if (i == 1) {
      Locale.setDefault(Locale.ENGLISH);
    } else if (i == 2) {
      Locale.setDefault(Locale.TAIWAN);
    }

  }

  @Override
  public void start(Stage stage) {
    try {
      System.setProperty(PhantomJsClient.PHANTOMJS_PATH, ResourceManager.binFile("/script/phantomjs").getAbsolutePath().concat(SystemUtils.getExecuteSuffix()));
      System.setProperty(PhantomJsClient.PHANTOMJS_SCRIPT, ResourceManager.binFile("/script/spider.js").getAbsolutePath());
      System.out.println(System.getProperty(PhantomJsClient.PHANTOMJS_SCRIPT));
      System.out.println(System.getProperty(PhantomJsClient.PHANTOMJS_PATH));
      super.start(stage);
      initStage(stage);
      stage.show();
      checkForUpdate(stage);
    } catch (Throwable e) {
      e.printStackTrace();
      throw new FxException(e);
    }
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
  public SceneView<? extends Parent> getIndexView() {
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
