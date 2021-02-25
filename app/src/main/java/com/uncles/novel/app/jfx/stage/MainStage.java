package com.uncles.novel.app.jfx.stage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class MainStage extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
//        LeftNavigationView node = new LeftNavigationView();
        URL resource = MainStage.class.getResource("/components/left_navigation.fxml");
        BorderPane load = FXMLLoader.load(resource);
        Scene value = new Scene(load);
        primaryStage.setScene(value);
        primaryStage.show();
    }
}
