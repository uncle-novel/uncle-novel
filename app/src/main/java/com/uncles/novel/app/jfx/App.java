package com.uncles.novel.app.jfx;

import com.sun.javafx.application.LauncherImpl;
import com.uncles.novel.app.jfx.ui.stage.MainApp;
import com.uncles.novel.app.jfx.ui.stage.PreLoaderApp;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class App {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainApp.class, PreLoaderApp.class, args);
    }
}
