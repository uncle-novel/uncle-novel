package com.uncles.novel.app.jfx.ui.app;

import com.uncles.novel.app.jfx.framework.ui.appication.SceneView;
import com.uncles.novel.app.jfx.framework.ui.appication.SceneViewNavigateBundle;
import com.uncles.novel.app.jfx.framework.ui.appication.SsaApplication;
import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import com.uncles.novel.app.jfx.ui.pages.home.HomeSceneView;
import javafx.scene.image.Image;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * SSA
 *
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class App extends SsaApplication {
    public static App app;

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

    public App() {
        disableWarning();
        if (app != null) {
            throw new IllegalStateException("不可以创建多个App");
        }
        app = this;
    }

    @Override
    public Class<? extends SceneView> getIndexView() throws Exception {
        getStage().getIcons().clear();
        getStage().getIcons().add(new Image("/assets/images/favicon.png"));
        return HomeSceneView.class;
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
    public static void redirect(Class<? extends SceneView> viewClass, SceneViewNavigateBundle bundle) {
        app.navigate(viewClass, bundle);
    }
}
