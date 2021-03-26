package com.unclezs.novel.app.jfx.launcher;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.application.Application;

/**
 * @author blog.unclezs.com
 * @since 2021/03/26 19:27
 */
public class Launcher {

  private static final Logger LOG = LoggerHelper.get(Launcher.class);


  public static void main(String[] args) throws IOException {
    LOG.info("Start FX Launcher...");
    Application.launch(FxLauncher.class, args);
  }

}
