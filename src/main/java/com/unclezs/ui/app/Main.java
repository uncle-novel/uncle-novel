package com.unclezs.ui.app;

import com.unclezs.ui.utils.DataManager;
import com.unclezs.ui.utils.HotKeyUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws IOException {
        VBox root = new VBox();
        DataManager.mainStage = mainStage;
        DataManager.root = root;
        mainStage.initStyle(StageStyle.TRANSPARENT);
        mainStage.getIcons().add(new Image("/images/图标/圆角图标.png"));
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/fxml/index.fxml"));
        VBox box = loader.load();
        VBox.setVgrow(box, Priority.ALWAYS);
        root.getChildren().add(box);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/reader.css").toExternalForm());
        mainStage.setOnCloseRequest(e -> System.exit(0));
        mainStage.setTitle("Uncle小说");
        mainStage.setScene(scene);
        mainStage.sizeToScene();
        mainStage.show();
        DataManager.currentStage = mainStage;
        new Thread(HotKeyUtil::bindListener).start();
    }
}
