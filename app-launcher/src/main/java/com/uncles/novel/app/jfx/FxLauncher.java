package com.uncles.novel.app.jfx;

import com.sun.javafx.application.LauncherImpl;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 19:46
 */
public class FxLauncher {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(TestLongInitApp.class, TestLongInitAppPreloader.class, args);
    }
}
