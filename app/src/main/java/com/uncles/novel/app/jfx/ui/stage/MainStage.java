package com.uncles.novel.app.jfx.ui.stage;

import com.uncles.novel.app.jfx.framework.ui.components.LeftTabPane;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import com.uncles.novel.app.jfx.ui.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class MainStage extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
//        VBox load = FxmlLoader.load(MainController.class);
        LeftTabPane load = new LeftTabPane();
        System.out.println(load.getTabs().getMenus());
//        FxmlLoader.("/components/left_navigation.fxml");
//        BorderPane load = FXMLLoader.load(resource);
        Scene value = new Scene(load,400,400);
        primaryStage.setScene(value);
        primaryStage.show();
    }
}
