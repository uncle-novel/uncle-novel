package com.unclezs.gui.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class WebViewContextMenuTest extends Application {

    private final String START_URL = "https://blog.unclezs.com";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TextField locationField = new TextField(START_URL);
        WebView webView = new WebView();
        webView.getEngine().load(START_URL);

        webView.setContextMenuEnabled(false);
        createContextMenu(webView);

        locationField.setOnAction(e -> {
            webView.getEngine().load(getUrl(locationField.getText()));
        });
        BorderPane root = new BorderPane(webView, locationField, null, null, null);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void createContextMenu(WebView webView) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem reload = new MenuItem("Reload");
        reload.setOnAction(e -> webView.getEngine().reload());
        MenuItem savePage = new MenuItem("Save Page");
        savePage.setOnAction(e -> {
            System.out.println(webView.getEngine().getLocation());
        });
        MenuItem hideImages = new MenuItem("Hide Images");
        hideImages.setOnAction(e -> System.out.println("Hide Images..."));
        contextMenu.getItems().addAll(reload, savePage, hideImages);

        webView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(webView, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    private String getUrl(String text) {
        if (text.indexOf("://") == -1) {
            return "http://" + text;
        } else {
            return text;
        }
    }
}
