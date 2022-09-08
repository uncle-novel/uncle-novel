package com.unclezs.gui.app;

import com.tulskiy.keymaster.common.Provider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.swing.*;

public class Domo extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(Font.getDefault().getFamily());
        StackPane root = new StackPane();
        root.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 1);-fx-effect: dropshadow(gaussian, black, 50, 0, 0, 0);-fx-background-insets: 50 !important;");
        root.getChildren().add(new StackPane());
        //设置窗体面板和大小
        Scene scene = new Scene(root, 400, 400);
        scene.setFill(Color.TRANSPARENT);
        //设置窗体样式
        final Provider provider = Provider.getCurrentProvider(false);
        provider.register(KeyStroke.getKeyStroke("ctrl alt D"), System.out::println);
        //设置窗口标题
        primaryStage.setTitle("Demo From");
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add("CSS file path");
        primaryStage.show();
    }
}
