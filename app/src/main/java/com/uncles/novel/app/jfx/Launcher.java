package com.uncles.novel.app.jfx;

import com.uncles.novel.app.jfx.framework.ui.appication.SsaApplication;
import com.uncles.novel.app.jfx.ui.app.App;
import javafx.application.Platform;

import java.util.Locale;
import java.util.Random;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class Launcher {
    public static void main(String[] args) {
        Random random = new Random();
        int i = random.nextInt(3);
        if (i == 1) {
            Locale.setDefault(Locale.ENGLISH);
        } else if (i == 2) {
            Locale.setDefault(Locale.TAIWAN);
        }
        Platform.startup(() -> {
            SsaApplication app = new App();
            try {
                app.init();
                app.start();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        });
//        LauncherImpl.launchApplication(App.class, PreLoaderApp.class, args);
    }
}
