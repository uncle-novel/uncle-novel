package com.unclezs.novel.app.main.ui.app;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 10:37
 */
public class TestStage extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    VBox box = new VBox();
    Button theme1 = new Button("theme1");
    Button theme2 = new Button("theme2");
    box.getChildren().add(theme1);
    box.getChildren().add(theme2);
    box.getChildren().add(new Button("搜索"));
    box.getChildren().add(new Button("搜索"));
    box.getChildren().add(new Button("搜索"));
    box.getChildren().add(new Button("搜索"));
    Scene scene = new Scene(box, 900, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
    JFXAlert<String> alert = new JFXAlert<>();
    alert.initModality(Modality.APPLICATION_MODAL);
    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setBody(new Label("123123"));
    alert.setContent(layout);
    alert.show();
  }
}
