package com.uncles.novel.app.jfx.ui.app;

import com.uncles.novel.app.jfx.framework.ui.appication.SceneView;
import com.uncles.novel.app.jfx.framework.ui.appication.SceneViewNavigateBundle;
import com.uncles.novel.app.jfx.framework.ui.appication.SsaApplication;
import com.uncles.novel.app.jfx.ui.pages.home.HomeSceneView;

/**
 * SSA
 *
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class App extends SsaApplication {
    public static App app;


    public App() {
        if (app != null) {
            throw new IllegalStateException("不可以创建多个App");
        }
        app = this;
    }

    @Override
    public Class<? extends SceneView> getIndexView() throws Exception {
        return HomeSceneView.class;
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
