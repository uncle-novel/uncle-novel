package com.unclezs.novel.app.main;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.framework.components.JsonEditor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JsonEditorDemo extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    JsonEditor codeArea = new JsonEditor(GsonUtils.NULL_PRETTY.toJson(new AnalyzerRule()));
    Scene scene = new Scene(codeArea, 600, 400);
    primaryStage.setScene(scene);
    primaryStage.setTitle("JSON编辑器");
    primaryStage.show();
  }
}
