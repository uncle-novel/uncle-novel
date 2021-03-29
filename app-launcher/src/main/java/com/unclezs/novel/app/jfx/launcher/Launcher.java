package com.unclezs.jfx.launcher;

import java.nio.charset.Charset;
import java.util.logging.Logger;
import javafx.application.Application;

/**
 * 启动器
 *
 * @author blog.unclezs.com
 * @since 2021/03/26 19:27
 */
public class Launcher {

  private static final Logger LOG = LoggerHelper.get(Launcher.class);

  public static void main(String[] args) {
    LOG.info(String.format("当前系统编码格式：file.encoding = %s ; default-charset = %s", System.getProperty("file.encoding"), Charset.defaultCharset()));
    LOG.info("Start FX Launcher...");
    Application.launch(LauncherApp.class, args);
  }

}
