package com.unclezs.novel.app.jfx.app.ui.app;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 19:28
 */
public class PreLoaderApp extends Preloader {

  private Stage stage;
  private Text text;
  private ProgressBar bar;

  @Override
  public void start(Stage primaryStage) throws Exception {
    StackPane pane = new StackPane();
    VBox box = new VBox();
    bar = new ProgressBar();
    this.text = new Text("启动中");
    box.getChildren().addAll(text, bar);
    pane.getChildren().add(box);
    pane.setPrefSize(400, 400);
    Scene scene = new Scene(pane, 400, 400);
    primaryStage.setScene(scene);
    this.stage = primaryStage;
    primaryStage.show();
  }

  @Override
  public void handleProgressNotification(ProgressNotification info) {
    bar.setProgress(info.getProgress());
  }

  @Override
  public void handleStateChangeNotification(StateChangeNotification info) {
    switch (info.getType()) {
      case BEFORE_INIT:
        text.setText("初始化中");
        break;
      case BEFORE_LOAD:
        text.setText("加载中");
        break;
      case BEFORE_START:
        text.setText("启动中");
        stage.close();
        break;
      default:
        break;
    }
  }

  @Override
  public void handleApplicationNotification(PreloaderNotification info) {
    System.out.println(info);
  }

  @Override
  public boolean handleErrorNotification(ErrorNotification info) {
    System.out.println(info);
    return super.handleErrorNotification(info);
  }
}
