package com.unclezs.novel.app.jfx.launcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author blog.unclezs.com
 * @since 2021/03/26 19:27
 */
public class UpdateViewApp extends Application {

  private static final Logger LOG = LoggerHelper.get(UpdateViewApp.class);


  public static void main(String[] args) throws IOException {
    LOG.info("Start FX Launcher...");
    Application.launch(FxLauncher.class, args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    LauncherView launcherView = new LauncherView();
    primaryStage.setResizable(false);
    launcherView.setLogoName("Uncle小说");
    launcherView.setPhase("正在检测更新...");
    new Thread(() -> {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      launcherView.initUpdateView();
      List<String> news = new ArrayList<>();
      for (int i = 0; i < 105; i++) {
        news.add("更新了什么东西？");
      }
      launcherView.setWhatNew(news);
    }).start();
    Scene scene = new Scene(launcherView, Color.TRANSPARENT);
    primaryStage.setScene(scene);
    primaryStage.setMaxHeight(340);
    primaryStage.initStyle(StageStyle.TRANSPARENT);
    primaryStage.show();
  }
}
