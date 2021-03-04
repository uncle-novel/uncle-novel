package com.uncles.novel.app.jfx.ui.stage;

import com.uncles.novel.app.jfx.framework.app.SceneView;
import com.uncles.novel.app.jfx.framework.app.SsaApplication;
import com.uncles.novel.app.jfx.ui.view.MainView;

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
        return MainView.class;
    }

    /**
     * 场景切换
     *
     * @param viewClass View类
     */
    public static void redirect(Class<? extends SceneView> viewClass) {
        app.navigate(viewClass);
    }
}
