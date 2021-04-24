package com.unclezs.novel.app.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JsonEditorDemo extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Scene scene = new Scene(null, 600, 400);
    primaryStage.setScene(scene);
    primaryStage.setTitle("JSON编辑器");
    primaryStage.show();
  }
}
