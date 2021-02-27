package com.uncles.novel.app.jfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * 主应用程序
 */
public class TestLongInitApp extends Application {

    BooleanProperty ready=new SimpleBooleanProperty(false);

    @Override
    public void start(Stage primaryStage) throws Exception {
        //这里可能处理一些耗时操作
        new Thread(longTask()).start();

        primaryStage.setTitle("主界面");
        primaryStage.setScene(new Scene(new Label("Main Application started"),
                400, 400));
        primaryStage.setMaximized(true);

        ready.addListener((observable, oldValue, newValue) -> {
            if(Boolean.TRUE.equals(newValue)){
                Platform.runLater(primaryStage::show);
            }
        });
    }

    private Task longTask(){
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //模拟准备耗时数据
                int max = 10;
                for (int i = 1; i <= max; i++) {
                    Thread.sleep(1000);
                    // 发送进程给预加载器主要通过Application中的notifyPreloader（PreloaderNotification）方法
                    Preloader.ProgressNotification notification=new Preloader.ProgressNotification(((double) i)/max);
                    notifyPreloader(notification);
                }
                // 这里数据已经准备好了
                // 在隐藏预加载程序之前防止应用程序过早退出
                ready.setValue(Boolean.TRUE);

                notifyPreloader(new Preloader.StateChangeNotification(
                        Preloader.StateChangeNotification.Type.BEFORE_START));
                return null;
            }
        };
    }
}
