package com.uncles.novel.app.jfx;

import com.uncles.novel.app.jfx.framework.app.SsaApplication;
import com.uncles.novel.app.jfx.ui.stage.App;
import javafx.application.Platform;

import java.util.Locale;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class Launcher {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        Platform.startup(() -> {
            SsaApplication app = new App();
            try {
                app.init();
                app.start();
            } catch (Exception e) {
                System.exit(-1);
                e.printStackTrace();
            }
        });
//        LauncherImpl.launchApplication(App.class, PreLoaderApp.class, args);
    }
}
