package com.unclezs.novel.app.jfx.app.ui.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
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
    }
}
