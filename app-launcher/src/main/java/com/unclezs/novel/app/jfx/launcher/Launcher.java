package com.unclezs.novel.app.jfx.launcher;

import javafx.application.Application;

/**
 * @author blog.unclezs.com
 * @since 2021/03/26 19:27
 */
public class Launcher {

  public static void main(String[] args) {
//    Logging.getJavaFXLogger().disableLogging();
    Application.launch(FxLauncher.class, args);
  }

}
