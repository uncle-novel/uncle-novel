package com.unclezs.gui.app;

import com.jfoenix.controls.JFXDrawersStack;
import com.unclezs.gui.animation.ScaleLargeTransition;
import com.unclezs.gui.components.StageDecorator;
import com.unclezs.gui.controller.ReaderController;
import com.unclezs.gui.utils.ApplicationUtil;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.ResourceUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 阅读器
 *
 * @author unclezs.com
 * @date 2020.05.12 14:48
 */
public class Reader extends Application {
    public static Stage stage;
    public static StageDecorator root;
    public static JFXDrawersStack container;
    public static ReaderController controller;
    private static ScaleLargeTransition ann;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        ApplicationUtil.initConfig();
    }

    @Override
    public void start(Stage readerStage) throws IOException {
        stage = readerStage;
        stage.setTitle("Uncle小说");
        readerStage.getIcons().add(new Image("images/logo/favicon.ico"));
        readerStage.setMinHeight(400);
        readerStage.setMinWidth(500);
        FXMLLoader loader = ResourceUtil.getFxmlLoader(ReaderController.class);
        container = loader.load();
        controller = loader.getController();
        root = new StageDecorator(stage, container, false, true, true, true, false);
        controller.initLater();
        root.header().getStyleClass().add("bg-transparent");
        root.setOnCloseButtonAction(() -> controller.exit());
        ann = new ScaleLargeTransition(root);
        Scene scene = new Scene(root, DataManager.application.getReaderConfig().getStageWidth().get(),
            DataManager.application.getReaderConfig().getStageHeight().get());
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().addAll(ResourceUtil.loadCss("/css/reader.css"));
        Platform.runLater(() -> readerStage.setScene(scene));
        stage.setOnShowing(e -> {
            DataManager.currentStage = readerStage;
            ann.play();
            readerStage.setWidth(DataManager.application.getReaderConfig().getStageWidth().get());
            readerStage.setHeight(DataManager.application.getReaderConfig().getStageHeight().get());
        });
        stage.setOnHidden(e -> controller.onHidden());
    }
}
