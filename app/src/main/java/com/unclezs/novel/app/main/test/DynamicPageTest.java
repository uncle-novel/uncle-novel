package com.unclezs.novel.app.main.test;

import javafx.application.Application;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author blog.unclezs.com
 * @date 2021/5/24 20:48
 */
public class DynamicPageTest extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    WebView webView = new WebView();
    WebEngine engine = webView.getEngine();
    engine.getLoadWorker().stateProperty().addListener(e -> {
      if (engine.getLoadWorker().getState() == State.SUCCEEDED) {
        System.out.println(engine.executeScript("document.documentElement.outerHTML"));
      }
    });
    engine.load("https://book.qidian.com/info/1735921");
    Button button = new Button("打印源码");
    button.setOnAction(e -> {
      System.out.println(engine.executeScript("document.documentElement.outerHTML"));
    });
    VBox box = new VBox(button, webView);
    Scene scene = new Scene(box, 800, 500);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
