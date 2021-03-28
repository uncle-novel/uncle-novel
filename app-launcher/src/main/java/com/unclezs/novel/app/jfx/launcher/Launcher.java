package com.unclezs.novel.app.jfx.launcher;

import java.nio.charset.Charset;
import java.util.logging.Logger;
import javafx.application.Application;

/**
 * @author blog.unclezs.com
 * @since 2021/03/26 19:27
 */
public class Launcher {

  private static final Logger LOG = LoggerHelper.get(Launcher.class);


  public static void main(String[] args) {
    //方法一：中文操作系统中打印GBK
    System.out.println(System.getProperty("file.encoding"));
    //方法二：中文操作系统中打印GBK
    System.out.println(Charset.defaultCharset());
    LOG.info("Start FX Launcher...");
    Application.launch(FxLauncher.class, args);
  }

}
