package com.uncles.novel.app.jfx;

import com.uncles.novel.app.jfx.ui.stage.App;
import javafx.application.Platform;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class Launcher {
    public static void main(String[] args) {
        Platform.startup(() -> {
            App app = new App();
            try {
                app.init();
                app.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
//        LauncherImpl.launchApplication(App.class, PreLoaderApp.class, args);
    }
}
