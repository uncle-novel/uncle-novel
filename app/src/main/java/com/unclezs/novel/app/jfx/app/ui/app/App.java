package com.unclezs.novel.app.jfx.app.ui.app;

import com.unclezs.novel.app.jfx.app.ui.pages.home.HomeSceneView;
import com.unclezs.novel.app.jfx.app.util.DebugUtils;
import com.unclezs.novel.app.jfx.framework.ui.appication.SceneView;
import com.unclezs.novel.app.jfx.framework.ui.appication.SceneViewNavigateBundle;
import com.unclezs.novel.app.jfx.framework.ui.appication.SsaApplication;
import com.unclezs.novel.app.jfx.framework.ui.components.icon.SvgIcon;
import com.unclezs.novel.app.jfx.framework.util.ResourceUtils;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Random;
import sun.misc.Unsafe;

/**
 * SSA
 *
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class App extends SsaApplication {

  public static App app;

  public App() {
    disableWarning();
    if (app != null) {
      throw new IllegalStateException("不可以创建多个App");
    }
    app = this;
  }

  public static void disableWarning() {
    try {
      Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      Unsafe u = (Unsafe) theUnsafe.get(null);
      Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
      Field logger = cls.getDeclaredField("logger");
      u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
    } catch (Exception ignore) {
      // ignore
    }
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
   */
  public static void redirect(Class<? extends SceneView> viewClass,
    SceneViewNavigateBundle bundle) {
    app.navigate(viewClass, bundle);
  }

  @Override
  public void init() throws Exception {
    DebugUtils.init();
    SvgIcon.load("/assets/icons/svg.properties");
    Random random = new Random();
    int i = random.nextInt(3);
    if (i == 1) {
      Locale.setDefault(Locale.ENGLISH);
    } else if (i == 2) {
      Locale.setDefault(Locale.TAIWAN);
    }
    super.init();
  }

  @Override
  public Class<? extends SceneView> getIndexView() throws Exception {
    return HomeSceneView.class;
  }
}
