package com.unclezs.novel.app.jfx.launcher;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * 这是预加载器界面
 */
public class TestLongInitAppPreloader extends Preloader {
    private Stage stage;
    private ProgressBar bar;
    private Text text;

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane pane = new StackPane();
        VBox box = new VBox();
        bar = new ProgressBar();
        this.text = new Text("启动中");
        box.getChildren().addAll(text, bar);
        pane.getChildren().add(box);
        pane.setPrefSize(400, 400);
        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setScene(scene);
        this.stage = primaryStage;
        primaryStage.show();
    }

    @Override
    public void handleProgressNotification(ProgressNotification info) {
        System.out.println("handleProgressNotification="+info.getProgress());
        if (info.getProgress() != 1.0) {
            bar.setProgress(info.getProgress() / 2);
        }
    }


    /**
     * 重载这个方法可以处理应用通知
     * @param info
     */
    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            //提取应用程序发送过来的进度值
            double v = ((ProgressNotification) info).getProgress();
            System.out.println("handleApplicationNotification="+v);
            bar.setProgress(v);
        } else if (info instanceof StateChangeNotification) {
            //隐藏/或者关闭preloader
//            stage.hide();
            stage.close();
        }
    }

}
